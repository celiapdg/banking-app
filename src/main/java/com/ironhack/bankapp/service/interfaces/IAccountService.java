package com.ironhack.bankapp.service.interfaces;

import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.TransactionDTO;
import com.ironhack.bankapp.controller.accounts.dto.BalanceDTO;
import com.ironhack.bankapp.model.Transaction;

import java.security.Principal;
import java.util.Optional;

public interface IAccountService {

    BalanceDTO checkBalance(Long id, Principal principal);

    public Money modifyBalance(Long id, String username, BalanceDTO balanceDTO);

    Transaction transfer(TransactionDTO transactionDTO, Principal principal);

    Transaction withdraw(TransactionDTO transactionDTO, String hashedKey, Optional<String> secretKey);

    Transaction deposit(TransactionDTO transactionDTO, String hashedKey, Optional<String> secretKey);
}
