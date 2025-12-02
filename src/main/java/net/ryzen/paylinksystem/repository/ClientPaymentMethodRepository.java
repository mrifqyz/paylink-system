package net.ryzen.paylinksystem.repository;

import net.ryzen.paylinksystem.entity.Client;
import net.ryzen.paylinksystem.entity.ClientPaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientPaymentMethodRepository extends JpaRepository<ClientPaymentMethod, Long> {
    List<ClientPaymentMethod> findAllByClientAndIsActive(Client client, Boolean isActive);
    List<ClientPaymentMethod> findAllByClientAndIsActiveAndPaymentMethod_Currency(Client client, Boolean isActive, String currency);
}
