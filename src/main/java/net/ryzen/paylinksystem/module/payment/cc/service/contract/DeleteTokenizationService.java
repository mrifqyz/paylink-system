package net.ryzen.paylinksystem.module.payment.cc.service.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.DeleteTokenizationRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.response.DeleteTokenizationResponseDTO;

public interface DeleteTokenizationService extends Command<DeleteTokenizationRequestDTO, DeleteTokenizationResponseDTO> {
}
