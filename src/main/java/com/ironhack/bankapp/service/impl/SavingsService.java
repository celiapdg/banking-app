package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.accounts.dto.SavingsDTO;
import com.ironhack.bankapp.model.Transaction;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.accounts.Savings;
import com.ironhack.bankapp.repository.TransactionRepository;
import com.ironhack.bankapp.repository.users.AccountHolderRepository;
import com.ironhack.bankapp.repository.accounts.SavingsRepository;
import com.ironhack.bankapp.service.interfaces.ISavingsService;
import com.ironhack.bankapp.utils.TimeCalc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SavingsService implements ISavingsService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private SavingsRepository savingsRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    /** Creates a Savings account **/
    public Savings create(SavingsDTO savingsDTO){
        AccountHolder primaryOwner = accountHolderRepository.findById(savingsDTO.getPrimaryId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Primary account owner not found"));

        Savings savings = new Savings(new Money(savingsDTO.getBalance()),
                primaryOwner, savingsDTO.getSecretKey(),
                new Money(savingsDTO.getMinimumBalance()), savingsDTO.getInterestRate());

        if (savingsDTO.getSecondaryId()!=null) {
            Optional<AccountHolder> secondaryOwner = accountHolderRepository.findById(savingsDTO.getSecondaryId());
            secondaryOwner.ifPresent(savings::setSecondaryOwner);
        }
        return savingsRepository.save(savings);

    }

    /** Applies interests if necessary **/
    public Savings applyInterest(Savings savings){
        // frozen accounts cannot modify its balance
        if (savings.isFrozen()) return savings;
        // if the account is active, interests can apply:
        Integer interestsRemaining = TimeCalc.calculateYears(savings.getLastInterestDate());

        List<Transaction> annualInterests = new ArrayList<>();
        while (interestsRemaining > 0){
            savings.setLastInterestDate(savings.getLastInterestDate().plusYears(1));
            annualInterests.add(new Transaction(null ,savings,
                    new Money(savings.getBalance().getAmount().multiply(savings.getInterestRate())),
                    "Annual interests", savings.getLastInterestDate().atTime(0,0)));
            savings.increaseBalance(new Money(savings.getBalance().getAmount().multiply(savings.getInterestRate())));
            interestsRemaining--;
        }
        transactionRepository.saveAll(annualInterests);
        savings.setBelowMinimumBalance(); // maybe the account was under minimum balance but now it's not
        return savingsRepository.save(savings);
    }
}
