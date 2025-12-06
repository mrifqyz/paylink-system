package net.ryzen.paylinksystem.module.dashboard.transaction.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import net.ryzen.paylinksystem.base.request.ServiceRequest;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionGetListRequestDTO implements ServiceRequest {
    @JsonIgnore
    private Integer pageSize;
    @JsonIgnore
    private String sortBy;
    @JsonIgnore
    private String sortType;
    @JsonIgnore
    private Integer pageNo;
    @JsonIgnore
    private String clientEmail;

    private Filter filter;
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Filter {
        private String status;
        private String searchKeyword;

    }
}
