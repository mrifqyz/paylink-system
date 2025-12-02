package net.ryzen.paylinksystem.module.checkout.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.common.utils.DateTimeUtils;
import net.ryzen.paylinksystem.entity.*;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.exception.InvalidDataException;
import net.ryzen.paylinksystem.module.checkout.dto.CustomerDTO;
import net.ryzen.paylinksystem.module.checkout.dto.PaymentMethodDTO;
import net.ryzen.paylinksystem.module.checkout.dto.request.GetPaymentLinkDataRequestDTO;
import net.ryzen.paylinksystem.module.checkout.dto.response.GetPaymentLinkDataResponseDTO;
import net.ryzen.paylinksystem.module.checkout.services.contract.GetPaymentLinkDataService;
import net.ryzen.paylinksystem.repository.ClientPaymentMethodRepository;
import net.ryzen.paylinksystem.repository.TransactionRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetPaymentLinkDataServiceImpl implements GetPaymentLinkDataService {
    private final TransactionRepository transactionRepository;
    private final ClientPaymentMethodRepository clientPaymentMethodRepository;

    @Override
    public GetPaymentLinkDataResponseDTO execute(GetPaymentLinkDataRequestDTO request) {
        String tokenId = request.getTokenId();
        Transaction transaction = getTransaction(tokenId);

        Customer customer = getCustomer(transaction);

        Client client = transaction.getClient();

        List<ClientPaymentMethod> dataPaymentMethod = getListClientPaymentMethod(client);

        if (dataPaymentMethod.isEmpty()) {
            log.debug("payment method is empty");
            throw new InvalidDataException(ResponseMessageEnum.PAYMENT_CHANNEL_INACTIVE.getMessage());
        }

        List<PaymentMethodDTO> paymentMethodList = buildListPaymentMethodDTO(dataPaymentMethod);

        return GetPaymentLinkDataResponseDTO.builder()
                .status(transaction.getStatus())
                .invoiceNumber(transaction.getInvoiceNumber())
                .merchantName(client.getName())
                .amount(transaction.getAmount().toString())
                .items(transaction.getLineItems())
                .currency(transaction.getCurrency())
                .customer(ObjectUtils.isEmpty(customer) ? null : buildCustomerDTO(customer))
                .expiredDate(setExpiredDate(transaction))
                .tokenId(tokenId)
                .redirectUrl(client.getRedirectUrl())
                .paymentMethods(paymentMethodList)
                .build();
    }

    private Transaction getTransaction(String tokenId) {
        return transactionRepository.findFirstByTokenId(tokenId)
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));
    }

    private CustomerDTO buildCustomerDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .address(customer.getAddress())
                .phoneNumber(customer.getPhoneNo())
                .email(customer.getEmail())
                .build();
    }

    private Customer getCustomer(Transaction transaction) {
        if (transaction.getCustomer() == null) {
            log.debug("customer is null for transaction {}", transaction.getInvoiceNumber());
            return null;
        }
        return transaction.getCustomer();
    }

    private String setExpiredDate(Transaction transaction) {
        if (!ObjectUtils.isEmpty(transaction.getExpiredDate())) {
            return DateTimeUtils.convertDateToString(transaction.getExpiredDate());
        }

        Date expiredDate = DateTimeUtils.addMinutes(new Date(), transaction.getTransactionDueDate());
        return DateTimeUtils.convertDateToString(expiredDate);

    }

    private List<ClientPaymentMethod> getListClientPaymentMethod(Client client) {
        return clientPaymentMethodRepository.findAllByClientAndIsActive(client, true);
    }

    private List<PaymentMethodDTO> buildListPaymentMethodDTO(List<ClientPaymentMethod> paymentMethodList) {
        return paymentMethodList.stream().map(data ->
                PaymentMethodDTO.builder()
                        .name(data.getPaymentMethod().getName())
                        .code(data.getPaymentMethod().getCode())
                        .currency(data.getPaymentMethod().getCurrency())
                        .build())
                .collect(Collectors.toList());
    }

}
