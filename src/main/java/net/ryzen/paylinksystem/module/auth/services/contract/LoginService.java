package net.ryzen.paylinksystem.module.auth.services.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.auth.dto.request.LoginRequestDTO;
import net.ryzen.paylinksystem.module.auth.dto.response.LoginResponseDTO;

public interface LoginService extends Command<LoginRequestDTO, LoginResponseDTO> {
}
