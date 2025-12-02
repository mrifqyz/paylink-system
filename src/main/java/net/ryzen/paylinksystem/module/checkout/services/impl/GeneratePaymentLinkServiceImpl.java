package net.ryzen.paylinksystem.module.checkout.services.impl;

import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.common.utils.DateTimeUtils;
import net.ryzen.paylinksystem.common.utils.StringUtils;
import net.ryzen.paylinksystem.entity.Client;
import net.ryzen.paylinksystem.entity.ClientPaymentMethod;
import net.ryzen.paylinksystem.entity.Customer;
import net.ryzen.paylinksystem.entity.Transaction;
import net.ryzen.paylinksystem.enums.RedisKeyEnum;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.enums.TransactionStatusEnum;
import net.ryzen.paylinksystem.exception.IdempotentDataException;
import net.ryzen.paylinksystem.exception.InvalidDataException;
import net.ryzen.paylinksystem.module.checkout.config.properties.PaymentGenerateProperties;
import net.ryzen.paylinksystem.module.checkout.dto.CustomerDTO;
import net.ryzen.paylinksystem.module.checkout.dto.HeadersDTO;
import net.ryzen.paylinksystem.module.checkout.dto.PaymentDTO;
import net.ryzen.paylinksystem.module.checkout.dto.request.GeneratePaymentLinkRequestDTO;
import net.ryzen.paylinksystem.module.checkout.dto.response.GeneratePaymentLinkResponseDTO;
import net.ryzen.paylinksystem.module.checkout.services.contract.GeneratePaymentLinkService;
import net.ryzen.paylinksystem.repository.ClientPaymentMethodRepository;
import net.ryzen.paylinksystem.repository.ClientRepository;
import net.ryzen.paylinksystem.repository.CustomerRepository;
import net.ryzen.paylinksystem.repository.TransactionRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneratePaymentLinkServiceImpl implements GeneratePaymentLinkService {
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final ClientPaymentMethodRepository clientPaymentMethodRepository;
    private final ClientRepository clientRepository;
    private final PaymentGenerateProperties paymentGenerateProperties;
    private final RedisTemplate<String, String> redisTemplate;
    @Override
    @Transactional
    public GeneratePaymentLinkResponseDTO execute(GeneratePaymentLinkRequestDTO request) {
        Client requestClient = getClient(request);

        validateAmount(request);
        validateSource(request, requestClient);

        var isIdempotent = checkIdempotent(request, requestClient);

        if (isIdempotent) {
            return getDataRedis(request, requestClient);
        }

        Customer customer = buildNewCustomer(request.getCustomer());
        Transaction newTrx = buildNewTransaction(request, requestClient, customer);

        List<ClientPaymentMethod> clientPaymentMethodList = getListClientPaymentMethod(requestClient);

        if (clientPaymentMethodList.isEmpty()) {
            throw new InvalidDataException(ResponseMessageEnum.PAYMENT_CHANNEL_INACTIVE.getMessage());
        }

        List<String> listPaymentMethods = mapPaymentMethods(clientPaymentMethodList);

        var response = buildResponse(request, listPaymentMethods, requestClient, newTrx);
        storeDataRedis(response, requestClient);
        return response;
    }

    private Client getClient(GeneratePaymentLinkRequestDTO request) {
        if (request.getClientEmail() != null) {
            log.debug("doGeneratePaymentLinkInternal: {}", request);
            return clientRepository.findFirstByEmailAndIsActive(request.getClientEmail(), true)
                    .orElseThrow(() -> new InvalidDataException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));
        } else {
            log.debug("doGeneratePaymentLink: {}", request);
            if (request.getHeaders() != null &&
            request.getHeaders().getClientId() != null) {
                return clientRepository.findFirstByClientIdAndIsActive(request.getHeaders().getClientId(), true)
                        .orElseThrow(() -> new InvalidDataException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));
            }
            throw new InvalidDataException(ResponseMessageEnum.INVALID_DATA.getMessage());
        }
    }

    private boolean checkIdempotent(GeneratePaymentLinkRequestDTO request, Client client) {
        Transaction existingTrx = findExistingTransaction(request, client);

        if (existingTrx == null) {
            return false;
        }

        if (existingTrx.getStatus().equals(TransactionStatusEnum.PENDING.name())) {
            return true;
        } else if (existingTrx.getStatus().equals(TransactionStatusEnum.SUCCESS.name())) {
            throw new IdempotentDataException(ResponseMessageEnum.TRX_ALREADY_SUCCESS.getMessage());
        }
        throw new IdempotentDataException(ResponseMessageEnum.TRX_UNPROCESSABLE.getMessage());
    }

    private Transaction buildNewTransaction(GeneratePaymentLinkRequestDTO request, Client client, Customer customer) {
        Transaction transaction = new Transaction();
        transaction.setCustomer(ObjectUtils.isEmpty(customer) ? null : customer);
        transaction.setClient(client);
        transaction.setInvoiceNumber(request.getTransaction().getInvoiceNumber());
        transaction.setStatus(TransactionStatusEnum.PENDING.name());
        transaction.setAmount(request.getTransaction().getAmount());
        transaction.setLineItems(request.getItems());
        transaction.setCreatedDate(new Date());

        if (request.getTransaction().getFixedExpiredDate() != null) {
            transaction.setExpiredDate(DateTimeUtils.convertStringToDate(request.getTransaction().getFixedExpiredDate()));
        } else {
            transaction.setTransactionDueDate(request.getTransaction().getTransactionDueDate() == null ?
                    client.getDefaultExpired() : request.getTransaction().getTransactionDueDate());
        }
        transactionRepository.save(transaction);
        return transaction;
    }

    private Customer buildNewCustomer(CustomerDTO customerDTO) {
        if (!ObjectUtils.isEmpty(customerDTO)) {
            Customer customer = new Customer();
            customer.setName(customerDTO.getName());
            customer.setAddress(customerDTO.getAddress());
            customer.setCreatedDate(new Date());
            customer.setPhoneNo(customerDTO.getPhoneNumber());
            customerRepository.save(customer);
            return customer;
        }
        return null;
    }

    private void validateAmount(GeneratePaymentLinkRequestDTO request) {
        AtomicReference<BigInteger> amountCalculated = new AtomicReference<>(BigInteger.ZERO);

        request.getItems().forEach(itemDTO -> {
            amountCalculated.set(
                    amountCalculated.get().add(
                            BigInteger.valueOf((long) itemDTO.getPrice() * itemDTO.getQuantity())));
        });

        if (request.getTransaction().getAmount().compareTo(amountCalculated.get()) != 0) {
            throw new InvalidDataException(ResponseMessageEnum.AMOUNT_NOT_MATCH.getMessage());
        }
    }

    private GeneratePaymentLinkResponseDTO buildResponse(GeneratePaymentLinkRequestDTO request,
                                                         List<String> listPaymentMethods,
                                                         Client client,
                                                         Transaction transaction) {
        String tokenId = StringUtils.generateRandString(client.getSessionPrefix(), 12);
        var response = GeneratePaymentLinkResponseDTO.builder()
                .transaction(request.getTransaction())
                .items(request.getItems())
                .customer(request.getCustomer())
                .payment(PaymentDTO.builder()
                        .paymentMethods(listPaymentMethods)
                        .url(paymentGenerateProperties.getBaseFrontendUrl() + tokenId)
                        .tokenId(tokenId)
                        .expiredDate(transaction.getExpiredDate() == null ? null : DateTimeUtils.convertDateToString(transaction.getExpiredDate()))
                        .paymentStatus(TransactionStatusEnum.PENDING.name())
                        .build())
                .status(TransactionStatusEnum.INITIATED.name())
                .additionalInfo(request.getAdditionalInfo())
                .uuid(UUID.randomUUID().toString())
                .headers(request.getHeaders())
                .build();
        return response;
    }

    private List<ClientPaymentMethod> getListClientPaymentMethod(Client client) {
        return clientPaymentMethodRepository.findAllByClientAndIsActive(client, true);
    }

    private List<String> mapPaymentMethods(List<ClientPaymentMethod> listClientPaymentMethod) {
        List<String> paymentMethods = new ArrayList<>();
        listClientPaymentMethod.forEach(clientPaymentMethod -> {
            paymentMethods.add(clientPaymentMethod.getPaymentMethod().getCode());
        });
        return paymentMethods;
    }

    private void storeDataRedis(GeneratePaymentLinkResponseDTO request, Client client) {
        String redisKey = RedisKeyEnum.GENERATE_ORDER.getKey().formatted(client.getClientId(), request.getTransaction().getInvoiceNumber(), request.getHeaders().getRequestId());
        log.debug("Storing Redis data key {}", redisKey);
        redisTemplate.opsForValue().set(redisKey, new Gson().toJson(request), RedisKeyEnum.GENERATE_ORDER.getExpiredSeconds(), TimeUnit.SECONDS);
    }

    private GeneratePaymentLinkResponseDTO getDataRedis(GeneratePaymentLinkRequestDTO request, Client client) {
        String redisKey = RedisKeyEnum.GENERATE_ORDER.getKey().formatted(client.getClientId(), request.getTransaction().getInvoiceNumber(), request.getHeaders().getRequestId());
        String redisValue = redisTemplate.opsForValue().get(redisKey);

        if (redisValue == null) {
            throw new IdempotentDataException(ResponseMessageEnum.TRX_UNPROCESSABLE.getMessage());
        }

        return new Gson().fromJson(redisValue, GeneratePaymentLinkResponseDTO.class);
    }

    private Transaction findExistingTransaction(GeneratePaymentLinkRequestDTO request, Client client) {
        return transactionRepository.findFirstByClientAndInvoiceNumberAndRequestIdAndAmount(client,
                        request.getTransaction().getInvoiceNumber(),
                        request.getHeaders().getRequestId(),
                        request.getTransaction().getAmount())
                .orElse(null);
    }

    private void validateSource(GeneratePaymentLinkRequestDTO request, Client client) {
        if (request.getClientEmail() == null) {
            request.setHeaders(HeadersDTO.builder()
                    .clientId(client.getClientId())
                    .requestId(UUID.randomUUID().toString())
                    .timestamp(new Date().toString())
                    .build());
        }
    }
}
