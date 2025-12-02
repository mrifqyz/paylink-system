package net.ryzen.paylinksystem.module.checkout.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import net.ryzen.paylinksystem.base.request.ServiceRequest;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckStatusRequestDTO implements ServiceRequest {
    private String tokenId;
}
