package net.ryzen.paylinksystem.module.checkout.services.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.checkout.dto.request.GetPaymentLinkDataRequestDTO;
import net.ryzen.paylinksystem.module.checkout.dto.response.GetPaymentLinkDataResponseDTO;

public interface GetPaymentLinkDataService extends Command<GetPaymentLinkDataRequestDTO, GetPaymentLinkDataResponseDTO> {
}
