package net.ryzen.paylinksystem.module.dashboard.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DashboardDataResponseDTO {
    private String clientId;
    private String sharedKey;
    private String clientName;
    private List<KpiCardDTO> kpiCards;
    private Integer pieChartTrxCount;
    private List<PieCardDataDTO> pieChartData;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class KpiCardDTO {
        private String type;
        private String name;
        private String value;
        private String additionalText;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PieCardDataDTO {
        private String status;
        private Integer count;
    }
}
