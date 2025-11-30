package net.ryzen.paylinksystem.module.checkout.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import net.ryzen.paylinksystem.module.checkout.dto.CustomerDTO;
import net.ryzen.paylinksystem.module.checkout.dto.HeadersDTO;
import net.ryzen.paylinksystem.module.checkout.dto.ItemDTO;
import net.ryzen.paylinksystem.module.checkout.dto.TransactionDTO;

import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GeneratePaymentLinkResponseDTO {
    private String status;
    private TransactionDTO transaction;
    private List<ItemDTO> items;
    private CustomerDTO customer;
    private Map<String, Object> additionalInfo;
    private String uuid;
    private HeadersDTO headers;
}
