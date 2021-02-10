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

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class SavingsService implements ISavingsService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private SavingsRepository savingsRepository;

    public Savings create(SavingsDTO savingsDTO){

        Optional<AccountHolder> primaryOwner = accountHolderRepository.findById(savingsDTO.getAccountId());
        if (primaryOwner.isPresent()){
            Savings savings = new Savings(new Money(savingsDTO.getBalance()),
                    primaryOwner.get(), savingsDTO.getSecretKey(),
                    new Money(savingsDTO.getMinimumBalance()), savingsDTO.getInterestRate());

            Optional<AccountHolder> secondaryOwner = accountHolderRepository.findById(savingsDTO.getAccountSecondaryId());

            if (secondaryOwner.isPresent()){
                savings.setSecondaryOwner(secondaryOwner.get());
            }

            return savingsRepository.save(savings);
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Primary account owner not found");
        }
    }

    public Savings applyInterest(Savings savings){
        Integer interestsRemaining = TimeCalc.calculateYears(savings.getLastInterestDate());
        savings.setLastInterestDate(savings.getLastInterestDate().plusYears(interestsRemaining));

        BigDecimal balanceAmount = savings.getBalance().getAmount();
        BigDecimal interestRate = savings.getInterestRate();

        while (interestsRemaining > 0){
            balanceAmount = balanceAmount.add(balanceAmount.multiply(interestRate));
            interestsRemaining--;
        }
        savings.setBalance(new Money(balanceAmount));
        return savingsRepository.save(savings);
    }
}
