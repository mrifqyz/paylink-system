package net.ryzen.paylinksystem.module.dashboard.main.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.entity.Client;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.module.dashboard.main.dto.request.DashboardDataRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.main.dto.response.DashboardDataResponseDTO;
import net.ryzen.paylinksystem.entity.Transaction;
import net.ryzen.paylinksystem.enums.TransactionStatusEnum;
import net.ryzen.paylinksystem.module.dashboard.main.dto.response.DashboardDataResponseDTO.KpiCardDTO;
import net.ryzen.paylinksystem.module.dashboard.main.dto.response.DashboardDataResponseDTO.PieCardDataDTO;
import net.ryzen.paylinksystem.module.dashboard.main.service.contract.DashboardDataService;
import net.ryzen.paylinksystem.repository.ClientRepository;
import net.ryzen.paylinksystem.repository.CustomerRepository;
import net.ryzen.paylinksystem.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardDataServiceImpl implements DashboardDataService {
    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    @Override
    public DashboardDataResponseDTO execute(DashboardDataRequestDTO request) {
        Client client = getDataClient(request);
        List<PieCardDataDTO> pieChartData = getPieChartData(client);
        Integer pieChartTrxCount = pieChartData.stream()
                .mapToInt(PieCardDataDTO::getCount)
                .sum();
        List<KpiCardDTO> kpiCards = getKpiCards(client);

        return DashboardDataResponseDTO.builder()
                .clientId(client.getClientId())
                .clientName(client.getName())
                .sharedKey(client.getSharedKey())
                .kpiCards(kpiCards)
                .pieChartData(pieChartData)
                .pieChartTrxCount(pieChartTrxCount)
                .build();
    }

    private Client getDataClient(DashboardDataRequestDTO request) {
        return clientRepository.findByClientId(request.getClientId())
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));
    }

    private List<PieCardDataDTO> getPieChartData(Client client) {
        Date startOfMonth = getStartOfCurrentMonth();
        Date endOfMonth = getEndOfCurrentMonth();

        Map<String, Long> statusCountMap = transactionRepository
                .findByClientAndCreatedDateBetween(client, startOfMonth, endOfMonth)
                .stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getStatus(),
                        Collectors.counting()
                ));

        return statusCountMap.entrySet().stream()
                .map(entry -> PieCardDataDTO.builder()
                        .status(entry.getKey())
                        .count(entry.getValue().intValue())
                        .build())
                .collect(Collectors.toList());
    }

    private Date getStartOfCurrentMonth() {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        return Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date getEndOfCurrentMonth() {
        LocalDate lastDayOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return Date.from(lastDayOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
    }

    private List<KpiCardDTO> getKpiCards(Client client) {
        Date startOfMonth = getStartOfCurrentMonth();
        Date endOfMonth = getEndOfCurrentMonth();

        List<Transaction> monthlyTransactions = transactionRepository
                .findByClientAndCreatedDateBetween(client, startOfMonth, endOfMonth);

        BigInteger totalAmount = monthlyTransactions.stream()
                .map(Transaction::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigInteger.ZERO, BigInteger::add);

        long totalTransactions = monthlyTransactions.size();

        long failedTransactions = monthlyTransactions.stream()
                .filter(t -> TransactionStatusEnum.EXPIRED.name().equals(t.getStatus()) ||
                        TransactionStatusEnum.VOIDED.name().equals(t.getStatus()))
                .count();

        long newCustomers = customerRepository
                .countByClientAndCreatedDateBetween(client, startOfMonth, endOfMonth);

        String currency = monthlyTransactions.stream()
                .map(Transaction::getCurrency)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("IDR");

        NumberFormat currencyFormatter = NumberFormat.getNumberInstance(Locale.US);
        String formattedAmount = currencyFormatter.format(totalAmount);

        List<KpiCardDTO> kpiCards = new ArrayList<>();

        kpiCards.add(KpiCardDTO.builder()
                .type("total_amount")
                .name("Total Amount")
                .value(formattedAmount)
                .additionalText(currency + " transactions")
                .build());

        kpiCards.add(KpiCardDTO.builder()
                .type("new_customers")
                .name("New Customers")
                .value(String.valueOf(newCustomers))
                .additionalText("customers")
                .build());

        kpiCards.add(KpiCardDTO.builder()
                .type("total_transactions")
                .name("Total Transactions")
                .value(String.valueOf(totalTransactions))
                .additionalText("transactions")
                .build());

        kpiCards.add(KpiCardDTO.builder()
                .type("failed_transactions")
                .name("Failed Transactions")
                .value(String.valueOf(failedTransactions))
                .additionalText("transactions")
                .build());

        return kpiCards;
    }
}
