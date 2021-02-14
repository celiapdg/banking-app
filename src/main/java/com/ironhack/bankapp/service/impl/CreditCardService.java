package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.accounts.dto.CreditCardDTO;
import com.ironhack.bankapp.model.Transaction;
import com.ironhack.bankapp.model.accounts.CreditCard;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.repository.TransactionRepository;
import com.ironhack.bankapp.repository.accounts.CreditCardRepository;
import com.ironhack.bankapp.repository.users.AccountHolderRepository;
import com.ironhack.bankapp.service.interfaces.ICreditCardService;
import com.ironhack.bankapp.utils.TimeCalc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CreditCardService implements ICreditCardService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    /** Creates a Credit Card account **/
    public CreditCard create(CreditCardDTO creditCardDTO) {
        AccountHolder primaryOwner = accountHolderRepository.findById(creditCardDTO.getPrimaryId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Primary account owner not found"));

        CreditCard account = new CreditCard(new Money(creditCardDTO.getBalance()),
                primaryOwner, creditCardDTO.getInterestRate(),
                new Money(creditCardDTO.getCreditLimit()));

        // secondary owner check
        if (creditCardDTO.getSecondaryId()!=null){
            Optional<AccountHolder> secondaryOwner = accountHolderRepository.findById(creditCardDTO.getSecondaryId());
            secondaryOwner.ifPresent(account::setSecondaryOwner);
        }
        return creditCardRepository.save(account);

    }

    /** Applies interests if necessary **/
    public CreditCard applyInterest(CreditCard creditCard){
        Integer interestsRemaining = TimeCalc.calculateMonths(creditCard.getLastInterestDate());

        BigDecimal interestRate = creditCard.getInterestRate()
                                            .divide(new BigDecimal(12))
                                            .setScale(4, RoundingMode.HALF_UP);
        List<Transaction> monthlyInterests = new ArrayList<>();
        while (interestsRemaining > 0){
            creditCard.setLastInterestDate(creditCard.getLastInterestDate().plusMonths(1));
            monthlyInterests.add(new Transaction(null, creditCard,
                    new Money(creditCard.getBalance().getAmount().multiply(interestRate)),
                    "Monthly interests", creditCard.getLastInterestDate().atTime(0,0)));
            creditCard.increaseBalance(new Money(creditCard.getBalance().getAmount().multiply(interestRate)));
            interestsRemaining--;
        }
        transactionRepository.saveAll(monthlyInterests);
        return creditCardRepository.save(creditCard);
    }
}
