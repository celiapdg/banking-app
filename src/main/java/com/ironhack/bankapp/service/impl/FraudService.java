package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.repository.TransactionRepository;
import com.ironhack.bankapp.service.interfaces.IFraudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FraudService implements IFraudService {

    @Autowired
    TransactionRepository transactionRepository;

    /** Fraud check. Two ways of commiting fraud **/
    public Boolean checkFrauds(Long id, BigDecimal nextTransactionAmount){
        // More than 2 transaction in one second
        List<Object[]> lastSecondTransactions = transactionRepository.transactionsLastSecond(id);
        if (lastSecondTransactions.size() >= 2){
            return true;
        }

        // Amount today bigger than 1.5*(Max amount in a day)
        List<Object[]> maxAmountInADay = transactionRepository.maxAmountInADayInTheHistoryOfMankindFromTheBeginningOfTime(id);
        List<Object[]> totalAmountLastDat = transactionRepository.totalAmountLastDay(id);

        if (maxAmountInADay.get(0)[1]!=null){ // if no transactions today, the amount is set to zero
            BigDecimal lastAmount = BigDecimal.ZERO;
            if (totalAmountLastDat.get(0)[1]!=null) lastAmount = (BigDecimal) totalAmountLastDat.get(0)[1];
            BigDecimal maxAmount = (BigDecimal) maxAmountInADay.get(0)[1];

            if (maxAmount.multiply(new BigDecimal(1.5)).compareTo(lastAmount.add(nextTransactionAmount)) < 0){
                return true;
            }
        }
        return false;
    }

}
