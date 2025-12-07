package net.ryzen.paylinksystem.module.dashboard.transaction.service.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.dashboard.transaction.dto.request.TransactionGetDetailsRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.transaction.dto.response.TransactionGetDetailsResponseDTO;

public interface TransactionGetDetailsService extends Command<TransactionGetDetailsRequestDTO, TransactionGetDetailsResponseDTO> {
}
