package com.ironhack.bankapp.service.interfaces;

import com.ironhack.bankapp.controller.accounts.dto.CreditCardDTO;
import com.ironhack.bankapp.model.accounts.CreditCard;

public interface ICreditCardService {
    CreditCard create(CreditCardDTO creditCardDTO);
    CreditCard applyInterest(CreditCard creditCard);
}
