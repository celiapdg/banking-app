package com.ironhack.bankapp.controller.users.impl;

import com.ironhack.bankapp.controller.users.dto.AccountHolderDTO;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.users.User;
import com.ironhack.bankapp.service.interfaces.IAccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AccountHolderController {

    @Autowired
    private IAccountHolderService accountHolderService;

    @GetMapping("/check-balance")
    @ResponseStatus(HttpStatus.OK)
    public void checkBalance(@AuthenticationPrincipal User user){
        accountHolderService.checkBalance(user);
    }

    @PostMapping("/new-account-holder")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountHolder create(@RequestBody @Valid AccountHolderDTO accountHolderDTO){
        return accountHolderService.create(accountHolderDTO);
    }
}
