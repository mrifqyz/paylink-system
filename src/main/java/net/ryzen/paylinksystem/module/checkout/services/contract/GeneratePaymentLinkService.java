package net.ryzen.paylinksystem.module.checkout.services.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.checkout.dto.request.GeneratePaymentLinkRequestDTO;
import net.ryzen.paylinksystem.module.checkout.dto.response.GeneratePaymentLinkResponseDTO;

public interface GeneratePaymentLinkService extends Command<GeneratePaymentLinkRequestDTO, GeneratePaymentLinkResponseDTO> {
}
