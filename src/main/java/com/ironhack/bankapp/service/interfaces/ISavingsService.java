package com.ironhack.bankapp.service.interfaces;

import com.ironhack.bankapp.controller.accounts.dto.SavingsDTO;
import com.ironhack.bankapp.model.accounts.Savings;

public interface ISavingsService {
    Savings create(SavingsDTO savingsDTO);
}
