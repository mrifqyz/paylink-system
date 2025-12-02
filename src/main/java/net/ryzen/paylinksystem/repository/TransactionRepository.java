package net.ryzen.paylinksystem.repository;

import net.ryzen.paylinksystem.entity.Client;
import net.ryzen.paylinksystem.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    Optional<Transaction> findFirstByClientAndInvoiceNumberAndRequestIdAndAmount(Client client, String invoiceNumber, String requestId, BigInteger amount);

    Optional<Transaction> findFirstByTokenId(String tokenId);
}
