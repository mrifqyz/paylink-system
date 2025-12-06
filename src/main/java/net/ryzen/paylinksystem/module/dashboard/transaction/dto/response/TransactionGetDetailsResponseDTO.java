package net.ryzen.paylinksystem.module.dashboard.transaction.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import net.ryzen.paylinksystem.module.checkout.dto.CustomerDTO;
import net.ryzen.paylinksystem.module.checkout.dto.ItemDTO;
import net.ryzen.paylinksystem.module.checkout.dto.PaymentMethodDTO;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionGetDetailsResponseDTO {
    private String transactionId;
    private String createdDate;
    private String updatedDate;
    private BigInteger amount;
    private CustomerDTO customer;
    private String status;
    private String tokenId;
    private String requestId;
    private String currency;
    private Integer transactionDueDate;
    private String expiredDate;
    private List<ItemDTO> items;
    private List<TransactionHistoryDTO> history;
    private String latestSuccessPaymentMethod;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class TransactionHistoryDTO {
        private String updatedDate;
        private String paymentMethod;
        private String status;
        private String uniqueReferenceNumber;
    }
}
