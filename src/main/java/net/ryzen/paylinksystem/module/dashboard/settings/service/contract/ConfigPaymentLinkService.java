package net.ryzen.paylinksystem.module.dashboard.settings.service.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.request.ConfigPaymentLinkRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.response.ClientConfigUpdateResponseDTO;

public interface ConfigPaymentLinkService extends Command<ConfigPaymentLinkRequestDTO, ClientConfigUpdateResponseDTO> {
}
