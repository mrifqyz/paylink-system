package net.ryzen.paylinksystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.repository.TransactionHistoryRepository;
import net.ryzen.paylinksystem.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentProcessorService {
    private final TransactionRepository transactionRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
}
