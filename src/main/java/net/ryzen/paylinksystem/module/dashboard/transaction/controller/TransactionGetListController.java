package net.ryzen.paylinksystem.module.dashboard.transaction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.dashboard.transaction.dto.request.TransactionGetListRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.transaction.service.contract.TransactionGetListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/dashboard/")
@RequiredArgsConstructor
@CrossOrigin("*")
public class TransactionGetListController {
    private final ServiceExecutor serviceExecutor;

    @PostMapping("/transactions")
    public ResponseEntity<?> doGetListTransaction(@Validated @RequestParam("page_no") Integer pageNo,
                                                  @Validated @RequestParam("page_size") Integer pageSize,
                                                  @Validated @RequestParam("sort_by") String sortBy,
                                                  @Validated @RequestParam("sort_type") String sortType,
                                                  @RequestAttribute String clientEmail,
                                                  @RequestBody TransactionGetListRequestDTO request) {
        request.setPageNo(pageNo);
        request.setPageSize(pageSize);
        request.setSortBy(sortBy);
        request.setSortType(sortType);
        request.setClientEmail(clientEmail);

        log.info("doGetListTransaction request: {}", request);

        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(TransactionGetListService.class, request));

    }
}
