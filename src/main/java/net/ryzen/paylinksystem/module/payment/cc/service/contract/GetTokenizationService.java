package net.ryzen.paylinksystem.module.payment.cc.service.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.GetTokenizationRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.response.GetTokenizationResponseDTO;

public interface GetTokenizationService extends Command<GetTokenizationRequestDTO, GetTokenizationResponseDTO> {
}
