package net.ryzen.paylinksystem.repository;

import net.ryzen.paylinksystem.entity.ClientPaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientPaymentMethodRepository extends JpaRepository<ClientPaymentMethod, Long> {
}
