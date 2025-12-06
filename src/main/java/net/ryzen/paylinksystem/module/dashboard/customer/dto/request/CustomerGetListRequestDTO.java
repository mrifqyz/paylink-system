package net.ryzen.paylinksystem.module.dashboard.customer.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import net.ryzen.paylinksystem.base.request.ServiceRequest;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerGetListRequestDTO implements ServiceRequest {
    @JsonIgnore
    private Integer pageSize;
    @JsonIgnore
    private String sortBy;
    @JsonIgnore
    private String sortType;
    @JsonIgnore
    private Integer pageNo;
    @JsonIgnore
    private String clientId;
    @JsonIgnore
    private String searchKeyword;
}
