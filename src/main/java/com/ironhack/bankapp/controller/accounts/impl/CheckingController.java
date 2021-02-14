package com.ironhack.bankapp.controller.accounts.impl;

import com.ironhack.bankapp.controller.accounts.dto.CheckingDTO;
import com.ironhack.bankapp.model.accounts.Account;
import com.ironhack.bankapp.service.interfaces.ICheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class CheckingController {

    @Autowired
    ICheckingService checkingService;

    /** Create a new checking or student checking account. Only for admins **/
    @PostMapping("/new-checking")
    @ResponseStatus(HttpStatus.CREATED)
    public Account create(@RequestBody @Valid CheckingDTO checkingDTO){
        return checkingService.create(checkingDTO);
    }
}
