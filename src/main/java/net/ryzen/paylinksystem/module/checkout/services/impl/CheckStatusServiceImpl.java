package net.ryzen.paylinksystem.module.checkout.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.entity.Transaction;
import net.ryzen.paylinksystem.entity.TransactionHistory;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.module.checkout.dto.request.CheckStatusRequestDTO;
import net.ryzen.paylinksystem.module.checkout.dto.response.CheckStatusResponseDTO;
import net.ryzen.paylinksystem.module.checkout.services.contract.CheckStatusService;
import net.ryzen.paylinksystem.repository.TransactionHistoryRepository;
import net.ryzen.paylinksystem.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckStatusServiceImpl implements CheckStatusService {
    private final TransactionRepository transactionRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    @Override
    public CheckStatusResponseDTO execute(CheckStatusRequestDTO request) {
        Transaction trxCheckStatus = getTransaction(request.getTokenId());

        String status = trxCheckStatus.getStatus();
        var latestTrxHist = getLatestTrxHist(trxCheckStatus);
        String paymentMethodCode = latestTrxHist == null ?
                null : latestTrxHist.getClientPaymentMethod().getPaymentMethod().getCode();

        return CheckStatusResponseDTO.builder()
                .status(status)
                .paymentMethodCode(paymentMethodCode)
                .build();
    }

    private Transaction getTransaction(String tokenId) {
        return transactionRepository.findFirstByTokenId(tokenId)
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));
    }

    private TransactionHistory getLatestTrxHist(Transaction trxCheckStatus) {
        return transactionHistoryRepository.findById(trxCheckStatus.getId()).orElse(null);
    }
}
