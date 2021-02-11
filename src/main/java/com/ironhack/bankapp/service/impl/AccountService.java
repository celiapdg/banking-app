package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.TransactionDTO;
import com.ironhack.bankapp.controller.accounts.dto.BalanceDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
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

    public BalanceDTO checkBalance(Long id, String username) {
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

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You cannot access this account info");
    }

    public BalanceDTO modifyBalance(Long id, String username, BalanceDTO balanceDTO) {
        Account account = checkAccountId(id);

        account.setBalance(new Money(balanceDTO.getAmount()));
        if (account instanceof Savings){
            if (((Savings) account).getMinimumBalance().getAmount().compareTo(balanceDTO.getAmount()) > 0){
                account = applyPenaltyFee(account);
                ((Savings) account).setBelowMinimumBalance();
            }
        }else if (account instanceof Checking){
            if (((Checking) account).getMinimumBalance().getAmount().compareTo(balanceDTO.getAmount()) > 0){
                account = applyPenaltyFee(account);
                ((Checking) account).setBelowMinimumBalance();
            }
        }

        return new BalanceDTO(account.getBalance().getAmount());
    }

    /***************************** TRANSFERENCE *******************************/
    public Transaction transfer(TransactionDTO transactionDTO, Principal principal,
                                Optional<String> hashedKey, Optional<String> secretKey) {

        Object origin = checkTransactionOrigin(transactionDTO, principal, hashedKey);
        Object destination = checkTransactionDestination(transactionDTO, origin, hashedKey,  secretKey);

        System.out.println(origin);
        System.out.println(destination);

        System.out.println("LLEGA HASTA AQUIIII");
        // todo: aplicar cambio de saldo y penalty fee if necessary
        if (origin instanceof Account && destination instanceof Account){
            System.out.println(new Transaction((Account) origin, (Account) destination,
                    new Money(transactionDTO.getAmount()),
                    LocalDateTime.now()));
            return transactionRepository.save(new Transaction((Account) origin, (Account) destination,
                                                              new Money(transactionDTO.getAmount()),
                                                              LocalDateTime.now()));
        }else if (origin instanceof Account && destination instanceof ThirdParty){
            return transactionRepository.save(new Transaction((Account) origin, null,
                                                              new Money(transactionDTO.getAmount()),
                                                              LocalDateTime.now()));
        }else if (origin instanceof ThirdParty && destination instanceof Account){
            return transactionRepository.save(new Transaction(null, (Account) destination,
                                                              new Money(transactionDTO.getAmount()),
                                                              LocalDateTime.now()));
        }else{
            System.out.println("Algo salió mal");
            return null;
        }

    }

    /****************************** TRANSFERENCE ORIGIN ******************************/
    public Object checkTransactionOrigin(TransactionDTO transactionDTO, Principal principal,
                                         Optional<String> hashedKey){
        Long originId = transactionDTO.getOriginId();
        BigDecimal amount = transactionDTO.getAmount();

        Object origin;
        // unauthenticated user = third party
        if (principal == null && hashedKey.isPresent()){
            origin = thirdPartyRepository.findById(originId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Third party not found"));

            if (!checkThirdPartyCredentials((ThirdParty) origin, hashedKey.get(), ((ThirdParty) origin).getName())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Origin account info is wrong");
            }

        // authenticated user. check if it is a valid AccountHolder
        } else if (principal != null) {
            AccountHolder accountHolder = accountHolderRepository.findByUsername(principal.getName()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Account holder not found"));

            origin = accountRepository.findById(originId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

            checkValidOriginAccount(accountHolder, (Account) origin, amount); // checks valid owner, funds, and status

        // principal == null and no hashed key provided
        }else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong origin account. Try logging in or check " +
                    "provided credentials");
        }
        return origin;
    }

    /****************************** TRANSFERENCE DESTINATION ******************************/
    public Object checkTransactionDestination(TransactionDTO transactionDTO, Object origin,
                                              Optional<String> hashedKey, Optional<String> secretKey) {
        Long destinationId = transactionDTO.getDestinationId();
        String destinationName = transactionDTO.getDestinationName();

        Object destination;
        if (origin instanceof ThirdParty){
            destination = accountRepository.findById(destinationId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination account not found"));

            checkAccountCredentials((Account) destination, secretKey, destinationName);
            checkIfFrozen((Account) destination);

        }else if (origin instanceof Account && !hashedKey.isPresent()){
            destination = accountRepository.findById(destinationId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination account not found"));

            checkAccountName((Account) destination, destinationName);
            checkIfFrozen((Account) destination);
        }else if (origin instanceof Account && hashedKey.isPresent()) {

            destination = thirdPartyRepository.findById(destinationId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Third party destination not found"));

            checkThirdPartyCredentials((ThirdParty) destination, hashedKey.get(), destinationName);

        }else{
            System.out.println("EXPECTATIVAS FALLIDAS HAMIJO");
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "baia");
        }

        System.out.println("CARACOL COL COOOOOOOOOOOOL " + !origin.equals(destination));

        // todo: comprobar que esto funsione
        if (!origin.equals(destination)){
            return destination;
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pa k kieres acer eso jaja saludos");
        }
    }


    public Account applyPenaltyFee(Account account){
        BigDecimal newBalance = account.getBalance().getAmount();
        newBalance = newBalance.subtract(new BigDecimal(40));
        account.setBalance(new Money(newBalance));
        return account;
    }


    public Account checkAccountId(Long id) {
        return accountRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }


    public boolean isAdmin(String username) {
        User user = userRepository.findByUsername(username).get();
        return user.getRoles().contains("ADMIN");
    }


    public boolean hasPermissions(String username, Account account) {
        return (isAdmin(username) || account.getPrimaryOwner().getUsername().equals(username) ||
                (account.getSecondaryOwner() != null && account.getSecondaryOwner().getUsername().equals(username)));
    }


    public boolean checkThirdPartyCredentials(ThirdParty thirdParty, String hashedKey, String name){
        if (!(thirdParty.getHashKey().equals(hashedKey) && thirdParty.getName().equals(name))){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong third party credentials");
        }
        return true;
    }

    public boolean checkValidOriginAccount(AccountHolder accountHolder, Account account, BigDecimal amount){
        if (!accountHolder.isOwner(account.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this account");
        }else if (checkIfFrozen(account)){
        }else if (!(account.hasEnoughFunds(new Money(amount)))){
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Not enough funds");
        }
        return true;
    }

    public boolean checkIfFrozen(Account account){
        if (account.isFrozen()) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Account frozen");
        }
        return false;
    }


    public boolean checkAccountCredentials(Account account, Optional<String> secretKey, String name){
        return (checkAccountName(account, name) && checkAccountSecretKey(account, secretKey));
    }


    public boolean checkAccountName(Account account, String name){
        if (account.getPrimaryOwner().getName().equals(name)) {
            return true;
        }else if(account.getSecondaryOwner()!=null){
            if (account.getSecondaryOwner().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }


    public boolean checkAccountSecretKey(Account account, Optional<String> secretKey){
        String accountClass = account.getClass().getSimpleName();
        if (secretKey.isPresent()){
            switch (accountClass){
                case "StudentChecking":
                    if (((StudentChecking) account).getSecretKey().equals(secretKey.get())){
                        return true;
                    }
                    break;
                case "Checking":
                    if (((Checking) account).getSecretKey().equals(secretKey.get())){
                        return true;
                    }
                    break;
                case "Savings":
                    if (((Savings) account).getSecretKey().equals(secretKey.get())){
                        return true;
                    }
                    break;
                case "CreditCard":
                    return true;
            }
        }else if(!secretKey.isPresent() && accountClass.equals("CreditCard")){
            return true;
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong secret key");

    }

}
