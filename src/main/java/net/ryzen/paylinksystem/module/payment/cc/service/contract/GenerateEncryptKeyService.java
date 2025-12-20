package net.ryzen.paylinksystem.module.payment.cc.service.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.GenerateEncryptKeyRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.response.GenerateEncryptKeyResponseDTO;

public interface GenerateEncryptKeyService extends Command<GenerateEncryptKeyRequestDTO, GenerateEncryptKeyResponseDTO> {
}
