package net.ryzen.paylinksystem.module.dashboard.transaction.service.contract;

import net.ryzen.paylinksystem.base.command.Command;
import net.ryzen.paylinksystem.module.dashboard.transaction.dto.request.TransactionGetListRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.transaction.dto.response.TransactionGetListResponseDTO;

public interface TransactionGetListService extends Command<TransactionGetListRequestDTO, TransactionGetListResponseDTO> {
}
