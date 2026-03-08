package com.paypal.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paypal.entity.Transaction;
import com.paypal.enums.TransactionType;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
	 Page<Transaction> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);
	 Optional<Transaction> findByTransactionRef(String transactionRef);
	 boolean existsByTransactionRefAndTransactionType(String transactionRef, TransactionType transactionType);
}

/**
#SQL being used:

SELECT *
FROM transactions
WHERE user_id = ?
ORDER BY timestamp DESC
LIMIT ?, ?

**/