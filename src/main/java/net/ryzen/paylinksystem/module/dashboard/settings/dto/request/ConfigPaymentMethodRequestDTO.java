package net.ryzen.paylinksystem.module.dashboard.settings.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import net.ryzen.paylinksystem.base.request.ServiceRequest;
import net.ryzen.paylinksystem.module.checkout.dto.PaymentMethodDTO;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ConfigPaymentMethodRequestDTO implements ServiceRequest {
    @JsonIgnore
    private String clientId;
    private String currency;
    private List<PaymentMethodDTO> paymentMethods;
}
