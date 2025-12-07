package net.ryzen.paylinksystem.module.dashboard.settings.service.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.request.ConfigNotificationRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.response.ClientConfigUpdateResponseDTO;

public interface ConfigNotificationService extends Command<ConfigNotificationRequestDTO, ClientConfigUpdateResponseDTO> {
}
