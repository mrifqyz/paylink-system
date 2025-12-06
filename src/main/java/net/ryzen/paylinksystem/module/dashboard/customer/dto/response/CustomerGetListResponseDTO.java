package net.ryzen.paylinksystem.module.dashboard.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import net.ryzen.paylinksystem.module.checkout.dto.CustomerDTO;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomerGetListResponseDTO {
    private Integer pageNo;
    private Integer pageSize;
    private Integer count;
    private List<CustomerDTO> data;
}
