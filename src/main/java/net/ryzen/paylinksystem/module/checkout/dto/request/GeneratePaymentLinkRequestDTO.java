package net.ryzen.paylinksystem.module.checkout.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import net.ryzen.paylinksystem.base.request.ServiceRequest;
import net.ryzen.paylinksystem.module.checkout.dto.CustomerDTO;
import net.ryzen.paylinksystem.module.checkout.dto.HeadersDTO;
import net.ryzen.paylinksystem.module.checkout.dto.ItemDTO;
import net.ryzen.paylinksystem.module.checkout.dto.TransactionDTO;

import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GeneratePaymentLinkRequestDTO implements ServiceRequest {
    @JsonIgnore
    private String clientEmail;
    private HeadersDTO headers;
    private TransactionDTO transaction;
    private List<ItemDTO> items;
    private CustomerDTO customer;
    private Map<String, Object> additionalInfo;
}
