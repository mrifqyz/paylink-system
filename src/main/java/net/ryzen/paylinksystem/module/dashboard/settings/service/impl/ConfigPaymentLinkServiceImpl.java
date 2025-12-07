package net.ryzen.paylinksystem.module.dashboard.settings.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.entity.Client;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.request.ConfigPaymentLinkRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.response.ClientConfigUpdateResponseDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.service.contract.ConfigPaymentLinkService;
import net.ryzen.paylinksystem.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfigPaymentLinkServiceImpl implements ConfigPaymentLinkService {
    private final ClientRepository clientRepository;

    @Override
    public ClientConfigUpdateResponseDTO execute(ConfigPaymentLinkRequestDTO request) {
        Client client = clientRepository.findByClientId(request.getClientId())
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));

        client.setDefaultExpired(request.getTransactionDueDate());
        client.setRedirectUrl(request.getClientRedirectUrl());
        client.setUpdatedDate(new Date());

        clientRepository.save(client);

        return ClientConfigUpdateResponseDTO.builder()
                .message("Payment link configuration updated successfully")
                .build();
    }
}
