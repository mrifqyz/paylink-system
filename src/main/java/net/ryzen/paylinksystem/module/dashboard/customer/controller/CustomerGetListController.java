package net.ryzen.paylinksystem.module.dashboard.customer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.dashboard.customer.dto.request.CustomerGetListRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.customer.service.contract.CustomerGetListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/dashboard/")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CustomerGetListController {
    private final ServiceExecutor serviceExecutor;

    @GetMapping("/customer")
    public ResponseEntity<?> doGetCustomerData(@Validated @RequestParam("page_no") Integer pageNo,
                                               @Validated @RequestParam("page_size") Integer pageSize,
                                               @Validated @RequestParam("sort_by") String sortBy,
                                               @Validated @RequestParam("sort_type") String sortType,
                                               @Validated @RequestParam(value = "search_keyword", required = false) String searchKeyword,
                                               @RequestAttribute String clientId){
        CustomerGetListRequestDTO request = CustomerGetListRequestDTO.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortType(sortType)
                .clientId(clientId)
                .build();
        log.info("doGetCustomerData: {}", request);
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(CustomerGetListService.class, request));
    }
}
