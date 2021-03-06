package com.ironhack.bankapp.repository;

import com.ironhack.bankapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Max amount of money sent in the same day
    @Query(value = "SELECT t.transaction_date, MAX(t.sum) FROM (SELECT DATE(transaction_date_time) AS transaction_date, SUM(transaction_amount) AS sum FROM transaction WHERE origin_id = :id GROUP BY transaction_date) AS t", nativeQuery = true)
    List<Object[]> maxAmountInADayInTheHistoryOfMankindFromTheBeginningOfTime(@Param("id") Long id);

    // Total amount of money sent today
    @Query(value = "SELECT transaction_date_time, SUM(transaction_amount) AS sum FROM transaction WHERE origin_id = :id AND transaction_date_time >= NOW() - INTERVAL 1 DAY", nativeQuery = true)
    List<Object[]> totalAmountLastDay(@Param("id") Long id);

    // Transactions sent or received in the last second
    @Query(value = "SELECT * FROM transaction WHERE (origin_id = :id OR destination_id = :id) AND transaction_date_time >= (NOW() - INTERVAL 1 SECOND)", nativeQuery = true)
    List<Object[]> transactionsLastSecond(@Param("id") Long id);
}
