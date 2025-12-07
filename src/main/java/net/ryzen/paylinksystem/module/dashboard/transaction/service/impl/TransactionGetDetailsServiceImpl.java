package net.ryzen.paylinksystem.module.dashboard.transaction.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.common.ProjectConstant;
import net.ryzen.paylinksystem.common.utils.DateTimeUtils;
import net.ryzen.paylinksystem.common.utils.EncryptorUtils;
import net.ryzen.paylinksystem.entity.Customer;
import net.ryzen.paylinksystem.entity.Transaction;
import net.ryzen.paylinksystem.entity.TransactionHistory;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.enums.TransactionStatusEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.module.checkout.dto.CustomerDTO;
import net.ryzen.paylinksystem.module.dashboard.transaction.dto.request.TransactionGetDetailsRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.transaction.dto.response.TransactionGetDetailsResponseDTO;
import net.ryzen.paylinksystem.module.dashboard.transaction.service.contract.TransactionGetDetailsService;
import net.ryzen.paylinksystem.repository.TransactionHistoryRepository;
import net.ryzen.paylinksystem.repository.TransactionRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionGetDetailsServiceImpl implements TransactionGetDetailsService {
    private final TransactionRepository transactionRepository;
    private final EncryptorUtils encryptorUtils;
    private final TransactionHistoryRepository transactionHistoryRepository;

    @Override
    public TransactionGetDetailsResponseDTO execute(TransactionGetDetailsRequestDTO request) {
        Long transactionId = Long.valueOf(encryptorUtils.decrypt(request.getTransactionId()));

        Transaction transaction = getTransaction(request, transactionId);

        List<TransactionHistory> transactionHistoryList = getTransactionHistory(transaction);

        return buildResponse(request.getTransactionId(), transaction, transactionHistoryList);
    }

    private Transaction getTransaction(TransactionGetDetailsRequestDTO request, Long transactionId) {
        return transactionRepository.findFirstByClient_clientIdAndId(request.getClientId(), transactionId)
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));
    }

    private List<TransactionHistory> getTransactionHistory(Transaction transaction) {
        return transactionHistoryRepository.findTransactionHistoryByTransaction(transaction.getId());
    }

    private TransactionGetDetailsResponseDTO buildResponse(String encryptId,
                                                           Transaction trx,
                                                           List<TransactionHistory> history) {

        return TransactionGetDetailsResponseDTO.builder()
                .transactionId(encryptId)
                .items(trx.getLineItems())
                .createdDate(DateTimeUtils.convertDateToString(trx.getCreatedDate()))
                .expiredDate(!ObjectUtils.isEmpty(trx.getExpiredDate()) ? DateTimeUtils.convertDateToString(trx.getExpiredDate()) : ProjectConstant.NULL_VALUE)
                .updatedDate(!ObjectUtils.isEmpty(trx.getUpdatedDate()) ? DateTimeUtils.convertDateToString(trx.getUpdatedDate()) : ProjectConstant.NULL_VALUE)
                .amount(trx.getAmount())
                .status(trx.getStatus())
                .tokenId(trx.getTokenId())
                .currency(trx.getCurrency())
                .customer(buildCustomerDTO(trx.getCustomer()))
                .transactionDueDate(trx.getTransactionDueDate())
                .amount(trx.getAmount())
                .history(buildTransactionHistoryResponseDTO(history))
                .latestSuccessPaymentMethod(findLastSuccessPaymentMethod(history))
                .build();
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

    private List<TransactionGetDetailsResponseDTO.TransactionHistoryDTO> buildTransactionHistoryResponseDTO(List<TransactionHistory> history) {
        return history.parallelStream().map(data ->
                TransactionGetDetailsResponseDTO.TransactionHistoryDTO.builder()
                        .status(data.getStatus())
                        .uniqueReferenceNumber(data.getUniqueReferenceNumber())
                        .paymentMethod(data.getClientPaymentMethod().getPaymentMethod().getName())
                        .updatedDate(DateTimeUtils.convertDateToString(data.getCreatedDate()))
                        .build())
                .toList();
    }

    private String findLastSuccessPaymentMethod(List<TransactionHistory> history) {
        if (history.isEmpty()) {
            return ProjectConstant.NULL_VALUE;
        }

        TransactionHistory transactionHistory = history.get(0);

        if (!transactionHistory.getStatus().equalsIgnoreCase(TransactionStatusEnum.SUCCESS.name())) {
            return ProjectConstant.NULL_VALUE;
        }
        return transactionHistory.getStatus();
    }
}
