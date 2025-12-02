package net.ryzen.paylinksystem.repository;

import net.ryzen.paylinksystem.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    @Query(value = "SELECT * FROM transaction_history " +
            "WHERE transaction_id = ?1 " +
            "ORDER BY updated_date DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<TransactionHistory> findLatestTransaction(Long transactionId);
}
