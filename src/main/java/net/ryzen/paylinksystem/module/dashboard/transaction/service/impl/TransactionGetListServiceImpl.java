package net.ryzen.paylinksystem.module.dashboard.transaction.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.common.ProjectConstant;
import net.ryzen.paylinksystem.common.utils.DateTimeUtils;
import net.ryzen.paylinksystem.common.utils.EncryptorUtils;
import net.ryzen.paylinksystem.entity.Transaction;
import net.ryzen.paylinksystem.module.dashboard.transaction.dto.request.TransactionGetListRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.transaction.dto.response.TransactionGetListResponseDTO;
import net.ryzen.paylinksystem.module.dashboard.transaction.service.contract.TransactionGetListService;
import net.ryzen.paylinksystem.repository.TransactionRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionGetListServiceImpl implements TransactionGetListService {
    private final TransactionRepository transactionRepository;
    private final EncryptorUtils encryptorUtils;
    @Override
    public TransactionGetListResponseDTO execute(TransactionGetListRequestDTO request) {
        Pageable pageRequest = buildPageableRequest(request);
        Specification<Transaction> specification = buildSpecification(request);

        Page<Transaction> transactionPage = transactionRepository.findAll(specification, pageRequest);
        Integer count = getTransactionCount(specification);
        log.debug("count : {}", count);

        return TransactionGetListResponseDTO.builder()
                .count(count)
                .pageNo(pageRequest.getPageNumber())
                .pageSize(pageRequest.getPageSize())
                .data(buildTransactionListDTO(transactionPage))
                .build();
    }

    private int getTransactionCount(Specification<Transaction> specification) {
        return (int) transactionRepository.count(specification);
    }

    private Pageable buildPageableRequest(TransactionGetListRequestDTO request) {
        Sort sort = Sort.unsorted();
        if (request.getSortBy() != null && !request.getSortBy().isEmpty()) {
            sort = "desc".equalsIgnoreCase(request.getSortType())
                    ? Sort.by(request.getSortBy()).descending()
                    : Sort.by(request.getSortBy()).ascending();
        }

        return  PageRequest.of(request.getPageNo(), request.getPageSize(), sort);
    }

    private Specification<Transaction> buildSpecification(TransactionGetListRequestDTO request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getFilter() != null) {
                if (request.getFilter().getSearchKeyword() != null &&
                        !request.getFilter().getSearchKeyword().isEmpty()){
                    predicates.add(criteriaBuilder.like(root.get("invoiceNumber"), request.getFilter().getSearchKeyword()));
                }

                if (request.getFilter().getStatus() != null &&
                        !request.getFilter().getStatus().isEmpty()){
                    predicates.add(criteriaBuilder.like(root.get("status"), request.getFilter().getStatus()));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<TransactionGetListResponseDTO.TransactionListDTO> buildTransactionListDTO(Page<Transaction> transactionPage) {
        return transactionPage.stream()
                .map(data -> TransactionGetListResponseDTO.TransactionListDTO.builder()
                        .transactionId(encryptorUtils.encrypt(data.getId().toString()))
                        .invoiceNumber(data.getInvoiceNumber())
                        .createdDate(DateTimeUtils.convertDateToString(data.getCreatedDate()))
                        .amount(data.getAmount())
                        .requestId(data.getRequestId())
                        .status(data.getStatus())
                        .currency(data.getCurrency())
                        .customerName(ObjectUtils.isEmpty(data.getCustomer()) ? data.getCustomer().getName() : ProjectConstant.NULL_VALUE)
                        .build())
                .toList();
    }
}
