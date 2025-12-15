package net.ryzen.paylinksystem.module.payment.cc.service.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.ThreeDoSecureCallbackRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.response.ThreeDoSecureCallbackResponseDTO;

public interface ThreeDoSecureCallbackService extends Command<ThreeDoSecureCallbackRequestDTO, ThreeDoSecureCallbackResponseDTO> {
}
