package net.ryzen.paylinksystem.repository;

import net.ryzen.paylinksystem.entity.Client;
import net.ryzen.paylinksystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    long countByClientAndCreatedDateBetween(Client client, Date startDate, Date endDate);
}
