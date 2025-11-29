package net.ryzen.paylinksystem.repository;

import net.ryzen.paylinksystem.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findFirstByEmailAndIsActive(String email, Boolean isActive);
}
