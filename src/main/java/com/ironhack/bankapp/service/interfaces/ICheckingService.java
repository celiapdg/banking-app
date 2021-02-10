package com.ironhack.bankapp.service.interfaces;

import com.ironhack.bankapp.controller.accounts.dto.CheckingDTO;
import com.ironhack.bankapp.model.accounts.Account;

public interface ICheckingService {

    Account create(CheckingDTO checkingDTO);

}
