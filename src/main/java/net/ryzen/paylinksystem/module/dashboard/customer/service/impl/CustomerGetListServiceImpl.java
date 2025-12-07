package net.ryzen.paylinksystem.module.dashboard.customer.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.entity.Customer;
import net.ryzen.paylinksystem.module.checkout.dto.CustomerDTO;
import net.ryzen.paylinksystem.module.dashboard.customer.dto.request.CustomerGetListRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.customer.dto.response.CustomerGetListResponseDTO;
import net.ryzen.paylinksystem.module.dashboard.customer.service.contract.CustomerGetListService;
import net.ryzen.paylinksystem.repository.CustomerRepository;
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
public class CustomerGetListServiceImpl implements CustomerGetListService {
    private final CustomerRepository customerRepository;

    @Override
    public CustomerGetListResponseDTO execute(CustomerGetListRequestDTO request) {
        Pageable pageRequest = buildPageableRequest(request);
        Specification<Customer> spec = buildSpecification(request);

        Page<Customer> customerPage = customerRepository.findAll(spec, pageRequest);
        Integer countCustomer = getCountCustomer(spec);

        return CustomerGetListResponseDTO.builder()
                .count(countCustomer)
                .pageNo(request.getPageNo())
                .pageSize(request.getPageSize())
                .data(buildCustomerDTOList(customerPage))
                .build();
    }

    private Pageable buildPageableRequest(CustomerGetListRequestDTO request) {
        Sort sort = Sort.unsorted();
        if (request.getSortBy() != null && !request.getSortBy().isEmpty()) {
            sort = "desc".equalsIgnoreCase(request.getSortType())
                    ? Sort.by(request.getSortBy()).descending()
                    : Sort.by(request.getSortBy()).ascending();
        }

        return  PageRequest.of(request.getPageNo(), request.getPageSize(), sort);
    }

    private Specification<Customer> buildSpecification(CustomerGetListRequestDTO request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.like(root.get("client").get("clientId"), request.getClientId()));

            if (request.getSearchKeyword() != null && !request.getSearchKeyword().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), request.getSearchKeyword()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Integer getCountCustomer(Specification<Customer> specification) {
        return (int) customerRepository.count(specification);
    }

    private List<CustomerDTO> buildCustomerDTOList(Page<Customer> customers) {
        return customers.stream().parallel()
                .map(data -> CustomerDTO.builder()
                        .email(data.getEmail())
                        .name(data.getName())
                        .phoneNumber(data.getPhoneNo())
                        .address(data.getAddress())
                        .build())
                .toList();
    }
}
