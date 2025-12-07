package net.ryzen.paylinksystem.module.dashboard.settings.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.entity.Client;
import net.ryzen.paylinksystem.entity.ClientPaymentMethod;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.module.checkout.dto.PaymentMethodDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.request.ClientConfigGetRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.response.ClientConfigGetResponseDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.service.contract.ClientConfigGetService;
import net.ryzen.paylinksystem.repository.ClientPaymentMethodRepository;
import net.ryzen.paylinksystem.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientConfigGetServiceImpl implements ClientConfigGetService {
    private final ClientRepository clientRepository;
    private final ClientPaymentMethodRepository clientPaymentMethodRepository;

    @Override
    public ClientConfigGetResponseDTO execute(ClientConfigGetRequestDTO request) {
        Client client = clientRepository.findByClientId(request.getClientId())
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));

        List<ClientPaymentMethod> clientPaymentMethods = clientPaymentMethodRepository
                .findAllByClientAndIsActive(client, true);

        List<PaymentMethodDTO> paymentMethods = clientPaymentMethods.stream()
                .map(cpm -> PaymentMethodDTO.builder()
                        .paymentMethodId(cpm.getPaymentMethod().getId().toString())
                        .name(cpm.getPaymentMethod().getName())
                        .code(cpm.getPaymentMethod().getCode())
                        .currency(cpm.getPaymentMethod().getCurrency())
                        .isActive(cpm.getIsActive())
                        .build())
                .collect(Collectors.toList());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createdDate = client.getCreatedDate() != null
                ? dateFormat.format(client.getCreatedDate())
                : null;

        return ClientConfigGetResponseDTO.builder()
                .clientId(client.getClientId())
                .sharedKey(client.getSharedKey())
                .isActive(client.getIsActive())
                .createdDate(createdDate)
                .name(client.getName())
                .clientRedirectUrl(client.getRedirectUrl())
                .transactionDueDate(client.getDefaultExpired())
                .notifyEmail(client.getIsNotifyEmail())
                .notifySms(false)
                .notifyHttp(client.getIsNotifyHttp())
                .notifyHttpUrl(client.getNotifyUrl())
                .currency(client.getCurrency())
                .paymentMethods(paymentMethods)
                .build();
    }
}
