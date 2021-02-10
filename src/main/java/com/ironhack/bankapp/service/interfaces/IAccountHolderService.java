package com.ironhack.bankapp.service.interfaces;

import com.ironhack.bankapp.controller.users.dto.AccountHolderDTO;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.users.User;

public interface IAccountHolderService {
    AccountHolder create(AccountHolderDTO accountHolderDTO);

    void checkBalance(User user);
}
