package net.ryzen.paylinksystem.module.payment.cc.service.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.CheckCardRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.response.CheckCardResponseDTO;

public interface CheckCardService extends Command<CheckCardRequestDTO, CheckCardResponseDTO> {
}
