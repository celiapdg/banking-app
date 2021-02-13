package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.accounts.dto.SavingsDTO;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.accounts.Savings;
import com.ironhack.bankapp.repository.users.AccountHolderRepository;
import com.ironhack.bankapp.repository.accounts.SavingsRepository;
import com.ironhack.bankapp.service.interfaces.ISavingsService;
import com.ironhack.bankapp.utils.TimeCalc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class SavingsService implements ISavingsService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private SavingsRepository savingsRepository;

    public Savings create(SavingsDTO savingsDTO){
        AccountHolder primaryOwner = accountHolderRepository.findById(savingsDTO.getPrimaryId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Primary account owner not found"));

        Savings savings = new Savings(new Money(savingsDTO.getBalance()),
                primaryOwner, savingsDTO.getSecretKey(),
                new Money(savingsDTO.getMinimumBalance()), savingsDTO.getInterestRate());

        Optional<AccountHolder> secondaryOwner = accountHolderRepository.findById(savingsDTO.getSecondaryId());
        secondaryOwner.ifPresent(savings::setSecondaryOwner);

        return savingsRepository.save(savings);

    }

    public Savings applyInterest(Savings savings){
        // frozen accounts cannot modify its balance
        if (savings.isFrozen()) return savings;
        // if the account is active, interests can apply:
        Integer interestsRemaining = TimeCalc.calculateYears(savings.getLastInterestDate());
        savings.setLastInterestDate(savings.getLastInterestDate().plusYears(interestsRemaining));

        while (interestsRemaining > 0){
            savings.increaseBalance(new Money(savings.getBalance().getAmount().multiply(savings.getInterestRate())));
            // todo: si quiero poner transferencias a todo, aquí iría una
            interestsRemaining--;
        }

        savings.setBelowMinimumBalance(); // maybe the account was under minimum balance but now it's not
        return savingsRepository.save(savings);
    }
}
