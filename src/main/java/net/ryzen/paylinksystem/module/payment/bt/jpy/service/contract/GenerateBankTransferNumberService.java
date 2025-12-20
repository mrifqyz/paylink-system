package net.ryzen.paylinksystem.module.payment.bt.jpy.service.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.payment.bt.jpy.dto.request.GenerateBankTransferNumberRequestDTO;
import net.ryzen.paylinksystem.module.payment.bt.jpy.dto.response.GenerateBankTransferNumberResponseDTO;

public interface GenerateBankTransferNumberService extends Command<GenerateBankTransferNumberRequestDTO, GenerateBankTransferNumberResponseDTO> {
}
