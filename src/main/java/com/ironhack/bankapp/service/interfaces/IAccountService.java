package com.ironhack.bankapp.service.interfaces;

import com.ironhack.bankapp.controller.TransactionDTO;
import com.ironhack.bankapp.controller.accounts.dto.BalanceDTO;
import com.ironhack.bankapp.model.Transaction;

import java.security.Principal;
import java.util.Optional;

public interface IAccountService {

    BalanceDTO checkBalance(Long id, String username);

    public BalanceDTO modifyBalance(Long id, String username, BalanceDTO balanceDTO);

    Transaction transfer(TransactionDTO transactionDTO, Principal principal, Optional<String> hashedKey, Optional<String> secretKey);
}
