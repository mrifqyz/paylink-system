package net.ryzen.paylinksystem.module.dashboard.settings.service.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.request.ClientConfigGetRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.response.ClientConfigGetResponseDTO;

public interface ClientConfigGetService extends Command<ClientConfigGetRequestDTO, ClientConfigGetResponseDTO> {
}
