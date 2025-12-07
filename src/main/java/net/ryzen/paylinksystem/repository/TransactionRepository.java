package net.ryzen.paylinksystem.repository;

import net.ryzen.paylinksystem.entity.Client;
import net.ryzen.paylinksystem.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    Optional<Transaction> findFirstByClientAndInvoiceNumberAndRequestIdAndAmount(Client client, String invoiceNumber, String requestId, BigInteger amount);

    Optional<Transaction> findFirstByTokenId(String tokenId);

    Optional<Transaction> findFirstByClient_clientIdAndId(String clientId, Long id);

    List<Transaction> findByClientAndCreatedDateBetween(Client client, Date startDate, Date endDate);
}
