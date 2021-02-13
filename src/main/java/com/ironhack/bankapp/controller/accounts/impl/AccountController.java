package com.ironhack.bankapp.controller.accounts.impl;

import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.TransactionDTO;
import com.ironhack.bankapp.controller.accounts.dto.BalanceDTO;
import com.ironhack.bankapp.model.Transaction;
import com.ironhack.bankapp.service.interfaces.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RestController
public class AccountController {

    @Autowired
    IAccountService accountService;

    @GetMapping("/check-balance/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BalanceDTO checkBalance(@PathVariable Long id, Principal principal){
        return accountService.checkBalance(id, principal);
    }

    @PatchMapping("/modify-balance/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Money modifyBalance(@PathVariable Long id, Principal principal, @RequestBody @Valid BalanceDTO balanceDTO){
        return accountService.modifyBalance(id, principal.getName(), balanceDTO);
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction transfer(@RequestBody @Valid TransactionDTO transactionDTO, Principal principal){
        return accountService.transfer(transactionDTO, principal);
    }

    @PostMapping("/withdraw/{hashedKey}")
    @ResponseStatus(HttpStatus.CREATED) // origin: account // destination: third party
    public Transaction withdraw(@RequestBody @Valid TransactionDTO transactionDTO,
                                @PathVariable String hashedKey, @RequestParam Optional<String> secretKey){
        return accountService.withdraw(transactionDTO, hashedKey, secretKey);
    }

    @PostMapping("/deposit/{hashedKey}")
    @ResponseStatus(HttpStatus.CREATED) // origin: third party // destination: account
    public Transaction deposit(@RequestBody @Valid TransactionDTO transactionDTO,
                                @PathVariable String hashedKey, @RequestParam Optional<String> secretKey){
        return accountService.deposit(transactionDTO, hashedKey, secretKey);
    }
}
