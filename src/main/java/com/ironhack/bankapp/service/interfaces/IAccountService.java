package com.ironhack.bankapp.service.interfaces;

import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.TransactionDTO;
import com.ironhack.bankapp.controller.accounts.dto.BalanceDTO;
import com.ironhack.bankapp.enums.Status;
import com.ironhack.bankapp.model.Transaction;
import com.ironhack.bankapp.model.accounts.*;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.users.ThirdParty;
import com.ironhack.bankapp.model.users.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IAccountService {

    public BalanceDTO checkBalance(Long id, Principal principal);

    public Money modifyBalance(Long id, String username, BalanceDTO balanceDTO);

    public Transaction transfer(TransactionDTO transactionDTO, Principal principal);

    public Transaction withdraw(TransactionDTO transactionDTO, String hashedKey, Optional<String> secretKey) ;

    public Transaction deposit(TransactionDTO transactionDTO, String hashedKey, Optional<String> secretKey) ;

    public Account checkIfPenaltyFeeApplies(Account account);

    public boolean isAdmin(String username);

    public boolean hasPermissions(String username, Account account);

    public Account checkAccountId(Long id);

    public Account checkValidOriginAccount(Long id, BigDecimal amount);

    public Account checkValidDestinationAccount(String name, Long id);

    public ThirdParty checkValidThirdParty(String hashedKey, Long id);

    public Account checkAccountName(Account account, String name);

    public boolean checkAccountSecretKey(Account account, Optional<String> secretKey);

    public void freeze(Account account);

    public void unfreeze(Long id);

}
