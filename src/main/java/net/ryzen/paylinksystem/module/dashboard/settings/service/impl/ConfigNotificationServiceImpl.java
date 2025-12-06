package net.ryzen.paylinksystem.module.dashboard.settings.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.entity.Client;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.request.ConfigNotificationRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.response.ClientConfigUpdateResponseDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.service.contract.ConfigNotificationService;
import net.ryzen.paylinksystem.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfigNotificationServiceImpl implements ConfigNotificationService {
    private final ClientRepository clientRepository;

    @Override
    public ClientConfigUpdateResponseDTO execute(ConfigNotificationRequestDTO request) {
        Client client = clientRepository.findByClientId(request.getClientId())
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));

        client.setIsNotifyEmail(request.getNotifyEmail());
        client.setIsNotifyHttp(request.getNotifyHttp());
        client.setNotifyUrl(request.getNotifyHttpUrl());
        client.setUpdatedDate(new Date());

        clientRepository.save(client);

        return ClientConfigUpdateResponseDTO.builder()
                .message("Notification configuration updated successfully")
                .build();
    }
}
