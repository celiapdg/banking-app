package com.ironhack.bankapp.controller.accounts.impl;

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
        return accountService.checkBalance(id, principal.getName());
    }

    @PatchMapping("/modify-balance/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BalanceDTO modifyBalance(@PathVariable Long id, Principal principal, @RequestBody @Valid BalanceDTO balanceDTO){
        return accountService.modifyBalance(id, principal.getName(), balanceDTO);
    }

    @PostMapping("/account-holder/transference")
    @ResponseStatus(HttpStatus.OK)
    public Transaction transfer(@RequestBody @Valid TransactionDTO transactionDTO, // TODO: dtos de las Keys?
                                @RequestParam Optional<String> hashedKey, @RequestParam Optional<String> secretKey, Principal principal)
    { return accountService.transfer(transactionDTO, principal, hashedKey, secretKey);}
}
