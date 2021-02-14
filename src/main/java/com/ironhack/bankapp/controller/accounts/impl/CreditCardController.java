package com.ironhack.bankapp.controller.accounts.impl;

import com.ironhack.bankapp.controller.accounts.dto.CreditCardDTO;
import com.ironhack.bankapp.model.accounts.CreditCard;
import com.ironhack.bankapp.service.interfaces.ICreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class CreditCardController {

    @Autowired
    ICreditCardService creditCardService;

    /** Create a new credit card account. Only for admins **/
    @PostMapping("/new-credit-card")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCard create(@RequestBody @Valid CreditCardDTO creditCardDTO){
        return creditCardService.create(creditCardDTO);
    }
}
