package net.ryzen.paylinksystem.module.dashboard.settings.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.entity.Client;
import net.ryzen.paylinksystem.entity.ClientPaymentMethod;
import net.ryzen.paylinksystem.entity.PaymentMethod;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.module.checkout.dto.PaymentMethodDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.request.ConfigPaymentMethodRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.response.ClientConfigUpdateResponseDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.service.contract.ConfigPaymentMethodService;
import net.ryzen.paylinksystem.repository.ClientPaymentMethodRepository;
import net.ryzen.paylinksystem.repository.ClientRepository;
import net.ryzen.paylinksystem.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfigPaymentMethodServiceImpl implements ConfigPaymentMethodService {
    private final ClientRepository clientRepository;
    private final ClientPaymentMethodRepository clientPaymentMethodRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Override
    public ClientConfigUpdateResponseDTO execute(ConfigPaymentMethodRequestDTO request) {
        Client client = clientRepository.findByClientId(request.getClientId())
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));

        client.setCurrency(request.getCurrency());
        client.setUpdatedDate(new Date());
        clientRepository.save(client);

        List<ClientPaymentMethod> existingClientPaymentMethods = clientPaymentMethodRepository
                .findAllByClientAndIsActive(client, true);

        for (ClientPaymentMethod cpm : existingClientPaymentMethods) {
            cpm.setIsActive(false);
            cpm.setUpdatedDate(new Date());
        }
        clientPaymentMethodRepository.saveAll(existingClientPaymentMethods);

        for (PaymentMethodDTO pmDto : request.getPaymentMethods()) {
            if (pmDto.getIsActive() != null && pmDto.getIsActive()) {
                PaymentMethod paymentMethod = paymentMethodRepository.findById(Long.parseLong(pmDto.getPaymentMethodId()))
                        .orElseThrow(() -> new DataNotFoundException("Payment method not found"));

                ClientPaymentMethod clientPaymentMethod = new ClientPaymentMethod();
                clientPaymentMethod.setClient(client);
                clientPaymentMethod.setPaymentMethod(paymentMethod);
                clientPaymentMethod.setIsActive(true);
                clientPaymentMethod.setCreatedDate(new Date());
                clientPaymentMethodRepository.save(clientPaymentMethod);
            }
        }

        return ClientConfigUpdateResponseDTO.builder()
                .message("Payment method configuration updated successfully")
                .build();
    }
}
