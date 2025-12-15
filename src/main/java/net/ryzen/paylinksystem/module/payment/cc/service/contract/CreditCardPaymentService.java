package net.ryzen.paylinksystem.module.payment.cc.service.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.CreditCardPaymentRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.response.CreditCardPaymentResponseDTO;

public interface CreditCardPaymentService extends Command<CreditCardPaymentRequestDTO, CreditCardPaymentResponseDTO> {
}
