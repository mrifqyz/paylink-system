package net.ryzen.paylinksystem.module.checkout.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import net.ryzen.paylinksystem.module.checkout.dto.CustomerDTO;
import net.ryzen.paylinksystem.module.checkout.dto.ItemDTO;
import net.ryzen.paylinksystem.module.checkout.dto.PaymentMethodDTO;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetPaymentLinkDataResponseDTO {
    private String invoiceNumber;
    private String amount;
    private List<ItemDTO> items;
    private CustomerDTO customer;
    private String expiredDate;
    private String currency;
    private List<PaymentMethodDTO> paymentMethods;
    private String merchantName;
    private String tokenId;
    private String status;
    private String redirectUrl;
}
