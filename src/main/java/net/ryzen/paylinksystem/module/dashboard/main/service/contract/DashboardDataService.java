package net.ryzen.paylinksystem.module.dashboard.main.service.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.dashboard.main.dto.request.DashboardDataRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.main.dto.response.DashboardDataResponseDTO;

public interface DashboardDataService extends Command<DashboardDataRequestDTO, DashboardDataResponseDTO> {
}
