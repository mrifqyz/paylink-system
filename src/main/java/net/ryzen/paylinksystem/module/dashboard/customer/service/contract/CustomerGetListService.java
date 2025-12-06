package net.ryzen.paylinksystem.module.dashboard.customer.service.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.dashboard.customer.dto.request.CustomerGetListRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.customer.dto.response.CustomerGetListResponseDTO;

public interface CustomerGetListService extends Command<CustomerGetListRequestDTO, CustomerGetListResponseDTO> {
}
