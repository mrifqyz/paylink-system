package net.ryzen.paylinksystem.module.checkout.services.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.checkout.dto.request.CheckStatusRequestDTO;
import net.ryzen.paylinksystem.module.checkout.dto.response.CheckStatusResponseDTO;

public interface CheckStatusService extends Command<CheckStatusRequestDTO, CheckStatusResponseDTO> {
}
