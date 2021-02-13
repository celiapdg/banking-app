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
    @Autowired
    private AccountService accountService;

    public Account create(CheckingDTO checkingDTO) {
        AccountHolder primaryOwner = accountHolderRepository.findById(checkingDTO.getPrimaryId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Primary account owner not found"));

        Account account;
        // Age check
        if (TimeCalc.calculateYears(primaryOwner.getBirth()) > 23){
            account = new Checking(new Money(checkingDTO.getBalance()),
                    primaryOwner, checkingDTO.getSecretKey());
        }else{
            account = new StudentChecking(new Money(checkingDTO.getBalance()),
                    primaryOwner, checkingDTO.getSecretKey());
        }

        if (checkingDTO.getSecondaryId()!=null){
            Optional<AccountHolder> secondaryOwner = accountHolderRepository.findById(checkingDTO.getSecondaryId());
            secondaryOwner.ifPresent(account::setSecondaryOwner);
        }

        if (account instanceof Checking){
            return checkingRepository.save((Checking) account);
        }else if (account instanceof StudentChecking){
            return studentCheckingRepository.save((StudentChecking) account);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account creation failed. Check parameters");
        }

    }


    public Checking applyMonthlyFee(Checking checking){
        // frozen accounts cannot modify its balance
        if (checking.isFrozen()) return checking;
        // if the account is active, monthly fee can apply:
        Integer feesRemaining = TimeCalc.calculateMonths(checking.getLastMaintenanceDate());
        checking.setLastMaintenanceDate(checking.getLastMaintenanceDate().plusMonths(feesRemaining));

        while (feesRemaining > 0){
            checking.decreaseBalance(checking.getMonthlyMaintenance());
            feesRemaining--;
        }
        checking = (Checking) accountService.checkIfPenaltyFeeApplies(checking);
        return checkingRepository.save(checking);
    }
}
