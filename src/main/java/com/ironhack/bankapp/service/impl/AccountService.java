package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.TransactionDTO;
import com.ironhack.bankapp.controller.accounts.dto.BalanceDTO;
import com.ironhack.bankapp.enums.Status;
import com.ironhack.bankapp.model.Transaction;
import com.ironhack.bankapp.model.accounts.*;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.users.ThirdParty;
import com.ironhack.bankapp.model.users.User;
import com.ironhack.bankapp.repository.TransactionRepository;
import com.ironhack.bankapp.repository.accounts.AccountRepository;
import com.ironhack.bankapp.repository.users.AccountHolderRepository;
import com.ironhack.bankapp.repository.users.ThirdPartyRepository;
import com.ironhack.bankapp.repository.users.UserRepository;
import com.ironhack.bankapp.service.interfaces.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements IAccountService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountHolderRepository accountHolderRepository;
    @Autowired
    CheckingService checkingService;
    @Autowired
    SavingsService savingsService;
    @Autowired
    CreditCardService creditCardService;
    @Autowired
    ThirdPartyRepository thirdPartyRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    FraudService fraudService;

    public BalanceDTO checkBalance(Long id, Principal principal) {
        if (principal == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must log in to view account balance");
        }

        String username = principal.getName();

        Account account = accountRepository.findById(id).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (hasPermissions(username, account)){
            // apply interests or fees if necessary
            if (account instanceof Savings){
                account = savingsService.applyInterest((Savings) account);
            }else if(account instanceof CreditCard){
                account = creditCardService.applyInterest((CreditCard) account);
            }else if (account instanceof Checking){
                account = checkingService.applyMonthlyFee((Checking) account);
            }
            return new BalanceDTO(account.getBalance().getAmount());
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot access this account info");
    }


    public Money modifyBalance(Long id, String username, BalanceDTO balanceDTO) {
        isAdmin(username); // this will also check if the user exists
        Account account = checkAccountId(id);

        account.setBalance(new Money(balanceDTO.getAmount()));
        account = accountRepository.save(checkIfPenaltyFeeApplies(account));

        // todo: si quiero añadir la transferencia, tengo que comprobar si el balance ahora es mayor o menor

        return account.getBalance();
    }


    /** TRANSACTION BETWEEN TWO ACCOUNTS **/
    public Transaction transfer(TransactionDTO transactionDTO, Principal principal) {
        if (principal == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must log in to view account balance");
        }
        if (transactionDTO.getOriginId().equals(transactionDTO.getDestinationId())){
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Pa k kieres acer eso jaja saludos");
        }

        String username = principal.getName();

        AccountHolder accountHolder = accountHolderRepository.findByUsername(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account holder not found"));

        Account origin = checkValidOriginAccount(transactionDTO.getOriginId(), transactionDTO.getAmount());
        if (!accountHolder.isOwner(transactionDTO.getOriginId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this account");
        }
        Account destination = checkValidDestinationAccount(transactionDTO.getDestinationName(), transactionDTO.getDestinationId());

        Boolean originFraud = fraudService.checkFrauds(transactionDTO.getOriginId(), transactionDTO.getAmount());
        Boolean destinationFraud = fraudService.checkFrauds(transactionDTO.getDestinationId(), transactionDTO.getAmount());
        if (originFraud) freeze(origin);
        if (destinationFraud) freeze(destination);
        accountRepository.saveAll(List.of(origin, destination));
        if (originFraud||destinationFraud){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Account frozen due to abnormal activity");
        }

        origin.decreaseBalance(new Money(transactionDTO.getAmount()));
        origin = checkIfPenaltyFeeApplies(origin);
        destination.increaseBalance(new Money(transactionDTO.getAmount()));
        destination = checkIfPenaltyFeeApplies(destination);
        accountRepository.saveAll(List.of(origin, destination));

        return transactionRepository.save(new Transaction(origin, destination, new Money(transactionDTO.getAmount()),
                                                          transactionDTO.getConcept(), LocalDateTime.now()));
    }


    /** TRANSACTION FROM ACCOUNT TO THIRD PARTY **/
    public Transaction withdraw(TransactionDTO transactionDTO, String hashedKey, Optional<String> secretKey) {
        Account origin = checkValidOriginAccount(transactionDTO.getOriginId(), transactionDTO.getAmount());
        checkAccountSecretKey(origin, secretKey);
        ThirdParty thirdParty = checkValidThirdParty(hashedKey, transactionDTO.getDestinationId());
        if (!thirdParty.getName().equals(transactionDTO.getDestinationName())){
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Name provided does not match with the destination's name");
        }
        Boolean originFraud = fraudService.checkFrauds(transactionDTO.getOriginId(), transactionDTO.getAmount());
        if (originFraud){
            freeze(origin);
            accountRepository.save(origin);
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Account frozen due to abnormal activity");
        }
        origin.setBalance(new Money(origin.getBalance().decreaseAmount(transactionDTO.getAmount())));
        origin = checkIfPenaltyFeeApplies(origin);
        accountRepository.save(origin);

        return transactionRepository.save(new Transaction(origin, null, new Money(transactionDTO.getAmount()),
                                                          transactionDTO.getConcept(), LocalDateTime.now()));

    }


    /** TRANSACTION FROM THIRD PARTY TO ACCOUNT **/
    public Transaction deposit(TransactionDTO transactionDTO, String hashedKey, Optional<String> secretKey) {

        checkValidThirdParty(hashedKey, transactionDTO.getOriginId());
        Account destination = checkValidDestinationAccount(transactionDTO.getDestinationName(), transactionDTO.getDestinationId());
        checkAccountSecretKey(destination, secretKey);

        Boolean destinationFraud = fraudService.checkFrauds(transactionDTO.getDestinationId(), transactionDTO.getAmount());
        if (destinationFraud){
            freeze(destination);
            accountRepository.save(destination);
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Account frozen due to abnormal activity");
        }

        destination.setBalance(new Money(destination.getBalance().increaseAmount(transactionDTO.getAmount())));
        destination = checkIfPenaltyFeeApplies(destination);
        accountRepository.save(destination);

        return transactionRepository.save(new Transaction(null, destination,
                new Money(transactionDTO.getAmount()),
                transactionDTO.getConcept(), LocalDateTime.now()));
    }


    public Account checkIfPenaltyFeeApplies(Account account){
        Transaction transaction;
        if (account instanceof Savings){
            if (!((Savings) account).isBelowMinimumBalance() &&
                    ((Savings) account).getMinimumBalance().getAmount().compareTo(account.getBalance().getAmount()) > 0){
                account.decreaseBalance(account.getPenaltyFee());
                transaction = new Transaction(account, null, account.getPenaltyFee(), "Penalty fee", LocalDateTime.now());
                transactionRepository.save(transaction);
            }
            ((Savings) account).setBelowMinimumBalance();
        }else if (account instanceof Checking){
            if (!((Checking) account).isBelowMinimumBalance() &&
                    ((Checking) account).getMinimumBalance().getAmount().compareTo(account.getBalance().getAmount()) > 0){
                account.decreaseBalance(account.getPenaltyFee());
                transaction = new Transaction(account, null, account.getPenaltyFee(), "Penalty fee", LocalDateTime.now());
                transactionRepository.save(transaction);
            }
            ((Checking) account).setBelowMinimumBalance();
        }

        return account;
    }


    public boolean isAdmin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        return user.getRoles().contains("ADMIN");
    }


    public boolean hasPermissions(String username, Account account) {
        if (isAdmin(username)) return true;
        if (account.getPrimaryOwner().getUsername().equals(username)) return true;
        if (account.getSecondaryOwner() != null) {
            return account.getSecondaryOwner().getUsername().equals(username);
        }
        return false;
    }


    public Account checkAccountId(Long id) {
        return accountRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }


    public Account checkValidOriginAccount(Long id, BigDecimal amount){
        Account account = checkAccountId(id);

        if (account.isFrozen()) {
            throw new ResponseStatusException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, "Account frozen");
        }else if (!(account.hasEnoughFunds(new Money(amount)))){
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Not enough funds");
        }
        return account;
    }


    public Account checkValidDestinationAccount(String name, Long id){
        Account account = checkAccountId(id);
        if (account.isFrozen()) {
            throw new ResponseStatusException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, "Account frozen");
        }
        checkAccountName(account, name);
        return account;
    }


    public ThirdParty checkValidThirdParty(String hashedKey, Long id){
        ThirdParty thirdParty = thirdPartyRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Third party not found"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(hashedKey, thirdParty.getHashKey())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong third party credentials");
        }
        return thirdParty;
    }


    public Account checkAccountName(Account account, String name){
        if (account.getPrimaryOwner().getName().equals(name)) {
            return account;
        }else if(account.getSecondaryOwner()!=null){
            if (account.getSecondaryOwner().getName().equals(name)) {
                return account;
            }
        }
        throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Name does not match with the destination account's names");
    }


    public boolean checkAccountSecretKey(Account account, Optional<String> secretKey){
        String accountClass = account.getClass().getSimpleName();

        if(accountClass.equals("CreditCard")){
            return true;
        }
        if (!secretKey.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Secret key is needed for non-credit-card accounts");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        switch (accountClass){
            case "StudentChecking":
                if (passwordEncoder.matches(secretKey.get(), ((StudentChecking) account).getSecretKey())) return true;
                break;
            case "Checking":
                if (passwordEncoder.matches(secretKey.get(), ((Checking) account).getSecretKey())) return true;
                break;
            case "Savings":
                if (passwordEncoder.matches(secretKey.get(), ((Savings) account).getSecretKey())) return true;
                break;
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong secret key");
    }

    public void freeze(Account account){
        String accountClass = account.getClass().getSimpleName();
        switch (accountClass){
            case "StudentChecking":
                ((StudentChecking) account).setStatus(Status.FROZEN);
                break;
            case "Checking":
                ((Checking) account).setStatus(Status.FROZEN);
                break;
            case "Savings":
                ((Savings) account).setStatus(Status.FROZEN);
                break;
        }
    }

}
