package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.accounts.dto.CheckingDTO;
import com.ironhack.bankapp.model.accounts.Account;
import com.ironhack.bankapp.model.accounts.Checking;
import com.ironhack.bankapp.model.accounts.StudentChecking;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.repository.accounts.CheckingRepository;
import com.ironhack.bankapp.repository.accounts.StudentCheckingRepository;
import com.ironhack.bankapp.repository.users.AccountHolderRepository;
import com.ironhack.bankapp.service.interfaces.ICheckingService;
import com.ironhack.bankapp.utils.TimeCalc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CheckingService implements ICheckingService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    public Account create(CheckingDTO checkingDTO) {
        if (checkingDTO.getAccountId() != null){
            Optional<AccountHolder> primaryOwner = accountHolderRepository.findById(checkingDTO.getAccountId());

            if (primaryOwner.isPresent()){
                Account account;

                if (TimeCalc.calculateYears(primaryOwner.get().getBirth()) > 23){
                    account = new Checking(new Money(checkingDTO.getBalance()),
                            primaryOwner.get(), checkingDTO.getSecretKey());
                }else{
                    account = new StudentChecking(new Money(checkingDTO.getBalance()),
                            primaryOwner.get(), checkingDTO.getSecretKey());
                }

                if (checkingDTO.getAccountSecondaryId()!=null){
                    Optional<AccountHolder> secondaryOwner = accountHolderRepository.findById(checkingDTO.getAccountSecondaryId());
                    if (secondaryOwner.isPresent()){
                        account.setSecondaryOwner(secondaryOwner.get());
                    }
                }

                if (account instanceof Checking){
                    return checkingRepository.save( (Checking) account);

                }else if (account instanceof StudentChecking){
                    return studentCheckingRepository.save( (StudentChecking) account);
                }else{
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account creation failed. Check parameters");
                }
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Primary account owner not found");
            }
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Primary account owner id cannot be null");
        }

    }

    public Checking applyMonthlyFee(Checking checking){
        Integer feesRemaining = TimeCalc.calculateMonths(checking.getLastMaintenanceDate());
        checking.setLastMaintenanceDate(checking.getLastMaintenanceDate().plusMonths(feesRemaining));

        BigDecimal balanceAmount = checking.getBalance().getAmount();
        while (feesRemaining > 0){
            balanceAmount = balanceAmount.subtract(checking.getMonthlyMaintenance().getAmount());
            feesRemaining--;
        }
        // if the account wasn't below minimum balance, but now is
        if (!checking.isBelowMinimumBalance()&&(balanceAmount.compareTo(checking.getMinimumBalance().getAmount()) < 0)){
            balanceAmount.subtract(checking.getPenaltyFee().getAmount());
        }
        checking.setBalance(new Money(balanceAmount));
        checking.setBelowMinimumBalance();
        return checkingRepository.save(checking);
    }
}
