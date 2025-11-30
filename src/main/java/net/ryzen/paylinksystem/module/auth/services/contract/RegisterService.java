package net.ryzen.paylinksystem.module.auth.services.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.auth.dto.request.RegisterRequestDTO;
import net.ryzen.paylinksystem.module.auth.dto.response.RegisterResponseDTO;

public interface RegisterService extends Command<RegisterRequestDTO, RegisterResponseDTO> {
}
