package net.ryzen.paylinksystem.module.auth.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.common.jwt.JwtUtils;
import net.ryzen.paylinksystem.common.utils.EncryptorUtils;
import net.ryzen.paylinksystem.dto.JwtUtilsResponseDTO;
import net.ryzen.paylinksystem.entity.Client;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.InvalidDataException;
import net.ryzen.paylinksystem.module.auth.common.EmailCheckerUtils;
import net.ryzen.paylinksystem.module.auth.dto.request.LoginRequestDTO;
import net.ryzen.paylinksystem.module.auth.dto.response.LoginResponseDTO;
import net.ryzen.paylinksystem.module.auth.services.contract.LoginService;
import net.ryzen.paylinksystem.repository.ClientRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {
    private final JwtUtils jwtUtils;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptorUtils encryptorUtils;
    @Override
    public LoginResponseDTO execute(LoginRequestDTO request) {

        if (!EmailCheckerUtils.isEmail(request.getEmail())){
            throw new InvalidDataException(ResponseMessageEnum.INVALID_DATA.getMessage().formatted("email"));
        }

        Client client = getClient(request.getEmail());
        log.info(encryptorUtils.decrypt(client.getSharedKey()));

        validatePassword(request, client);
        try {

            JwtUtilsResponseDTO accessTokenData = jwtUtils.generateToken(client);

            return LoginResponseDTO.builder()
                    .accessToken(accessTokenData.getToken())
                    .expiredAt(accessTokenData.getExpiryTime())
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Credentials error caught during authentication: {}", e.getMessage());
            throw new InvalidDataException(ResponseMessageEnum.WRONG_USER_CREDENTIALS.getMessage());
        }  catch (Exception e) {
            log.warn("Error caught during authentication: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Client getClient(String email) {
        return clientRepository.findFirstByEmailAndIsActive(email, true)
                .orElseThrow(() -> new InvalidDataException(ResponseMessageEnum.WRONG_USER_CREDENTIALS.getMessage()));
    }

    private void validatePassword(LoginRequestDTO request, Client user) {
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Current password is incorrect for user: {}", request.getEmail());
            throw new InvalidDataException(ResponseMessageEnum.WRONG_USER_CREDENTIALS.getMessage());
        }
    }
}
