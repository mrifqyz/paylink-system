package net.ryzen.paylinksystem.module.auth.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.common.jwt.JwtUtils;
import net.ryzen.paylinksystem.common.utils.EncryptorUtils;
import net.ryzen.paylinksystem.common.utils.StringUtils;
import net.ryzen.paylinksystem.dto.JwtUtilsResponseDTO;
import net.ryzen.paylinksystem.entity.Client;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.InvalidDataException;
import net.ryzen.paylinksystem.module.auth.common.ClientCredentialsUtils;
import net.ryzen.paylinksystem.module.auth.common.EmailCheckerUtils;
import net.ryzen.paylinksystem.module.auth.config.properties.DashboardAuthProperties;
import net.ryzen.paylinksystem.module.auth.dto.request.RegisterRequestDTO;
import net.ryzen.paylinksystem.module.auth.dto.response.RegisterResponseDTO;
import net.ryzen.paylinksystem.module.auth.services.contract.RegisterService;
import net.ryzen.paylinksystem.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterServiceImpl implements RegisterService {
    private final JwtUtils jwtUtils;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final DashboardAuthProperties dashboardAuthProperties;
    private final EncryptorUtils encryptorUtils;
    @Override
    @Transactional
    public RegisterResponseDTO execute(RegisterRequestDTO request) {

        validateRequest(request);

        Client newClient = saveToDatabase(request);

        JwtUtilsResponseDTO accessTokenData = jwtUtils.generateToken(newClient);

        return RegisterResponseDTO.builder()
                .accessToken(accessTokenData.getToken())
                .expiredAt(accessTokenData.getExpiryTime())
                .build();
    }

    private Boolean checkExistingClient(String email) {
        return clientRepository.findFirstByEmailAndIsActive(email, true).isPresent();
    }

    private Client saveToDatabase(RegisterRequestDTO request) {
        Client client = new Client();
        client.setEmail(request.getEmail());
        client.setPassword(passwordEncoder.encode(request.getPassword()));
        client.setCreatedDate(new Date());
        client.setName(request.getName());
        client.setDefaultExpired(60);
        client.setRedirectUrl("https://google.com");
        client.setIsActive(true);
        client.setClientId(ClientCredentialsUtils.generateClientId(dashboardAuthProperties.getClientIdPrefix()));
        client.setSharedKey(generateSharedKey());
        clientRepository.save(client);
        log.debug("Client with email {} has been saved", request.getEmail());
        return client;
    }

    private void validateRequest(RegisterRequestDTO request) {
        if (!EmailCheckerUtils.isEmail(request.getEmail())){
            throw new InvalidDataException(ResponseMessageEnum.INVALID_DATA.getMessage().formatted("email"));
        }

        if (checkExistingClient(request.getEmail())) {
            throw new InvalidDataException(ResponseMessageEnum.USER_ALREADY_EXISTS.getMessage());
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException(ResponseMessageEnum.REGISTER_PASSWORD_NOT_MATCH.getMessage());
        }
    }

    private String generateSharedKey() {
        return encryptorUtils.encrypt(StringUtils.generateRandString(dashboardAuthProperties.getSharedKeyPrefix(), 20));
    }
}
