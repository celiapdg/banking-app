package com.ironhack.bankapp.controller.accounts.impl;

import com.ironhack.bankapp.controller.accounts.dto.SavingsDTO;
import com.ironhack.bankapp.model.accounts.Savings;
import com.ironhack.bankapp.service.interfaces.ISavingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class SavingsController {

    @Autowired
    private ISavingsService savingsService;

    /** Create a new savings account. Only for admins **/
    @PostMapping("/new-savings")
    @ResponseStatus(HttpStatus.CREATED)
    public Savings create(@RequestBody @Valid SavingsDTO savingsDTO){
        return savingsService.create(savingsDTO);
    }
}
