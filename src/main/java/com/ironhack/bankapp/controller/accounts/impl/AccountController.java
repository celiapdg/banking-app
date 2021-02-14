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
    private IAccountService accountService;

    /** Check the balance from one account. Only for admins and account owners **/
    @GetMapping("/check-balance/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BalanceDTO checkBalance(@PathVariable Long id, Principal principal){
        return accountService.checkBalance(id, principal);
    }

    /** Modify the balance from one account. Only for admins **/
    @PatchMapping("/modify-balance/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Money modifyBalance(@PathVariable Long id, Principal principal, @RequestBody @Valid BalanceDTO balanceDTO){
        return accountService.modifyBalance(id, principal.getName(), balanceDTO);
    }

    /** Transfer money from one account to another. Only for account holders (origin account owners) **/
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction transfer(@RequestBody @Valid TransactionDTO transactionDTO, Principal principal){
        return accountService.transfer(transactionDTO, principal);
    }

    /** Send money from an account to a third party. Secret and hash keys are needed **/
    @PostMapping("/withdraw/{hashedKey}")
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction withdraw(@RequestBody @Valid TransactionDTO transactionDTO,
                                @PathVariable String hashedKey, @RequestParam Optional<String> secretKey){
        return accountService.withdraw(transactionDTO, hashedKey, secretKey);
    }

    /** Send money from a third party to an account. Secret and hash keys are needed **/
    @PostMapping("/deposit/{hashedKey}")
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction deposit(@RequestBody @Valid TransactionDTO transactionDTO,
                               @PathVariable String hashedKey, @RequestParam Optional<String> secretKey){
        return accountService.deposit(transactionDTO, hashedKey, secretKey);
    }

    /** Unfreeze an account. Only for admins **/
    @PatchMapping("/unfreeze/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfreeze(@PathVariable Long id){
        accountService.unfreeze(id);
    }
}
