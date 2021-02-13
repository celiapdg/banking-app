package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FraudService {

    @Autowired
    TransactionRepository transactionRepository;

    public Boolean checkFrauds(Long id, BigDecimal nextTransactionAmount){
        List<Object[]> lastSecondTransactions = transactionRepository.totalTransactionsLastSecond(id);
        if (lastSecondTransactions.size() >= 2){
            return true;
        }

        List<Object[]> maxAmountInADay = transactionRepository.maxAmountInADay(id);
        List<Object[]> totalAmountLastDat = transactionRepository.totalAmountLastDay(id);

        if (maxAmountInADay.get(0)[1]!=null && totalAmountLastDat.get(0)[1]!= null){
            BigDecimal maxAmount = (BigDecimal) maxAmountInADay.get(0)[1];
            BigDecimal lastAmount = (BigDecimal) totalAmountLastDat.get(0)[1];

            if (maxAmount.multiply(new BigDecimal(1.5)).compareTo(lastAmount.add(nextTransactionAmount)) < 0){
                return true;
            }
        }
        return false;
    }

}
