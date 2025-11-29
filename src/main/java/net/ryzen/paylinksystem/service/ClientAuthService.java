package net.ryzen.paylinksystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.entity.Client;
import net.ryzen.paylinksystem.repository.ClientRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Slf4j
@Service
public class ClientAuthService implements UserDetailsService {

    private final ClientRepository clientRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Client client = clientRepository.findFirstByEmailAndIsActive(username, true)
                .orElseThrow(() -> {
                    log.warn("User not found or is inactive: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"));
        return new User(client.getEmail(), client.getPassword(), authorities);
    }
}
