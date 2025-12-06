package net.ryzen.paylinksystem.module.dashboard.transaction.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionGetListResponseDTO {
    private Integer pageNo;
    private Integer pageSize;
    private Integer count;
    private List<TransactionListDTO> data;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class TransactionListDTO {
        private String transactionId;
        private String invoiceNumber;
        private String currency;
        private String requestId;
        private String createdDate;
        private String status;
        private String customerName;
        private BigInteger amount;
    }
}
