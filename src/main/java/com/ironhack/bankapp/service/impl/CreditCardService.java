package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.accounts.dto.CreditCardDTO;
import com.ironhack.bankapp.model.accounts.CreditCard;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.repository.accounts.CreditCardRepository;
import com.ironhack.bankapp.repository.users.AccountHolderRepository;
import com.ironhack.bankapp.service.interfaces.ICreditCardService;
import com.ironhack.bankapp.utils.TimeCalc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CreditCardService implements ICreditCardService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    public CreditCard create(CreditCardDTO creditCardDTO) {
        if (creditCardDTO.getAccountId() != null){
            Optional<AccountHolder> primaryOwner = accountHolderRepository.findById(creditCardDTO.getAccountId());

            if (primaryOwner.isPresent()){
                CreditCard account = new CreditCard(new Money(creditCardDTO.getBalance()),
                        primaryOwner.get(), creditCardDTO.getInterestRate(),
                        new Money(creditCardDTO.getCreditLimit()));

                if (creditCardDTO.getAccountSecondaryId()!=null){
                    Optional<AccountHolder> secondaryOwner = accountHolderRepository.findById(creditCardDTO.getAccountSecondaryId());
                    if (secondaryOwner.isPresent()){
                        account.setSecondaryOwner(secondaryOwner.get());
                    }
                }
                return creditCardRepository.save(account);
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Primary account owner not found");
            }
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Primary account owner id cannot be null");
        }
    }

    public CreditCard applyInterest(CreditCard creditCard){
        Integer interestsRemaining = TimeCalc.calculateMonths(creditCard.getLastInterestDate());
        creditCard.setLastInterestDate(creditCard.getLastInterestDate().plusMonths(interestsRemaining));

        BigDecimal balanceAmount = creditCard.getBalance().getAmount();
        BigDecimal interestRate = creditCard.getInterestRate().divide(new BigDecimal(12));

        while (interestsRemaining > 0){
            balanceAmount = balanceAmount.add(balanceAmount.multiply(interestRate));
            interestsRemaining--;
        }
        creditCard.setBalance(new Money(balanceAmount));
        return creditCardRepository.save(creditCard);
    }
}
