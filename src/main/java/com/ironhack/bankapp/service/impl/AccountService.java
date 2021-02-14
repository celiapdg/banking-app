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
import com.ironhack.bankapp.repository.accounts.CheckingRepository;
import com.ironhack.bankapp.repository.accounts.SavingsRepository;
import com.ironhack.bankapp.repository.accounts.StudentCheckingRepository;
import com.ironhack.bankapp.repository.users.AccountHolderRepository;
import com.ironhack.bankapp.repository.users.ThirdPartyRepository;
import com.ironhack.bankapp.repository.users.UserRepository;
import com.ironhack.bankapp.service.interfaces.*;
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
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private StudentCheckingRepository studentCheckingRepository;
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private SavingsRepository savingsRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ICheckingService checkingService;
    @Autowired
    private ISavingsService savingsService;
    @Autowired
    private ICreditCardService creditCardService;
    @Autowired
    private IFraudService fraudService;

    /** Check the balance of an account (if the user has permissions).
     * Applies interests and fees if necessary, then returns the resulting balance
     */
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

    /** Modify the balance of an account (if the user has permissions).
     */
    public Money modifyBalance(Long id, String username, BalanceDTO balanceDTO) {
        isAdmin(username); // this will also check if the user exists
        Account account = checkAccountId(id);

        account.setBalance(new Money(balanceDTO.getAmount()));
        account = accountRepository.save(checkIfPenaltyFeeApplies(account));

        return account.getBalance();
    }


    /** Transaction between two accounts **/
    public Transaction transfer(TransactionDTO transactionDTO, Principal principal) {
        if (principal == null){ // The user must be logged in
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must log in to view account balance");
        }
        if (transactionDTO.getOriginId().equals(transactionDTO.getDestinationId())){ // Origin and destination account must be different
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Pa k kieres acer eso jaja saludos");
        }

        String username = principal.getName();

        // The user must be an account holder
        AccountHolder accountHolder = accountHolderRepository.findByUsername(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account holder not found"));
        // Checks if the origin account exists, if it's frozen and if it has enough funds
        Account origin = checkValidOriginAccount(transactionDTO.getOriginId(), transactionDTO.getAmount());
        // This account holder must own the origin account (as primary or secondary owner)
        if (!accountHolder.isOwner(transactionDTO.getOriginId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this account");
        }
        // Checks if the destination account exists, and if the name matches any of its owners
        Account destination = checkValidDestinationAccount(transactionDTO.getDestinationName(), transactionDTO.getDestinationId());

        // Checking for fraud. If any of the accounts commit fraud, the transaction won't be completed and the account(s) is/are frozen
        Boolean originFraud = fraudService.checkFrauds(transactionDTO.getOriginId(), transactionDTO.getAmount());
        Boolean destinationFraud = fraudService.checkFrauds(transactionDTO.getDestinationId(), transactionDTO.getAmount());
        if (originFraud) freeze(origin);
        if (destinationFraud) freeze(destination);
        accountRepository.saveAll(List.of(origin, destination));
        if (originFraud||destinationFraud){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Account frozen due to abnormal activity");
        }

        // Adjusts the balance and applies penalty fees if necessary
        origin.decreaseBalance(new Money(transactionDTO.getAmount()));
        origin = checkIfPenaltyFeeApplies(origin);
        destination.increaseBalance(new Money(transactionDTO.getAmount()));
        destination = checkIfPenaltyFeeApplies(destination);
        accountRepository.saveAll(List.of(origin, destination));

        return transactionRepository.save(new Transaction(origin, destination, new Money(transactionDTO.getAmount()),
                                                          transactionDTO.getConcept(), LocalDateTime.now()));
    }


    /** Transaction from account to third party **/
    public Transaction withdraw(TransactionDTO transactionDTO, String hashedKey, Optional<String> secretKey) {
        // Checks if the destination account exists, and if the name matches any of its owners
        Account origin = checkValidOriginAccount(transactionDTO.getOriginId(), transactionDTO.getAmount());
        checkAccountSecretKey(origin, secretKey);
        // Check if third party exists, and if its key and name matches the provided info
        ThirdParty thirdParty = checkValidThirdParty(hashedKey, transactionDTO.getDestinationId());
        if (!thirdParty.getName().equals(transactionDTO.getDestinationName())){
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Name provided does not match with the destination's name");
        }

        // Checking for fraud. If the origin account commits fraud, the transaction won't be completed and the account is frozen
        Boolean originFraud = fraudService.checkFrauds(transactionDTO.getOriginId(), transactionDTO.getAmount());
        if (originFraud){
            freeze(origin);
            accountRepository.save(origin);
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Account frozen due to abnormal activity");
        }

        // Adjust and save the new balance, apply fees if necessary
        origin.setBalance(new Money(origin.getBalance().decreaseAmount(transactionDTO.getAmount())));
        origin = checkIfPenaltyFeeApplies(origin);
        accountRepository.save(origin);

        return transactionRepository.save(new Transaction(origin, null, new Money(transactionDTO.getAmount()),
                                                          transactionDTO.getConcept(), LocalDateTime.now()));

    }


    /** Transaction from third party to account **/
    public Transaction deposit(TransactionDTO transactionDTO, String hashedKey, Optional<String> secretKey) {
        // Check if third party exists, and if its key matches the provided info
        checkValidThirdParty(hashedKey, transactionDTO.getOriginId());
        // Checks if the destination account exists, and if the name matches any of its owners
        Account destination = checkValidDestinationAccount(transactionDTO.getDestinationName(), transactionDTO.getDestinationId());
        checkAccountSecretKey(destination, secretKey);

        // Checking for fraud. If the destination account commits fraud, the transaction won't be completed and the account is frozen
        Boolean destinationFraud = fraudService.checkFrauds(transactionDTO.getDestinationId(), transactionDTO.getAmount());
        if (destinationFraud){
            freeze(destination);
            accountRepository.save(destination);
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Account frozen due to abnormal activity");
        }

        // Adjust and save the new balance, apply fees if necessary
        destination.setBalance(new Money(destination.getBalance().increaseAmount(transactionDTO.getAmount())));
        destination = checkIfPenaltyFeeApplies(destination);
        accountRepository.save(destination);

        return transactionRepository.save(new Transaction(null, destination,
                new Money(transactionDTO.getAmount()),
                transactionDTO.getConcept(), LocalDateTime.now()));
    }


    /** Checks if the account has just gone under minimum balance and applies penalty fee if so **/
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


    /** Checks if an user exists and is an admin **/
    public boolean isAdmin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        return user.getRoles().contains("ADMIN");
    }


    /** Check if an user owns an account **/
    public boolean hasPermissions(String username, Account account) {
        if (isAdmin(username)) return true;
        if (account.getPrimaryOwner().getUsername().equals(username)) return true;
        if (account.getSecondaryOwner() != null) {
            return account.getSecondaryOwner().getUsername().equals(username);
        }
        return false;
    }


    /** Checks if exists an account with the provided id **/
    public Account checkAccountId(Long id) {
        return accountRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }


    /** Checks if the origin account exists, if it's frozen and has enough funds **/
    public Account checkValidOriginAccount(Long id, BigDecimal amount){
        Account account = checkAccountId(id);

        if (account.isFrozen()) {
            throw new ResponseStatusException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, "Account frozen");
        }else if (!(account.hasEnoughFunds(new Money(amount)))){
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Not enough funds");
        }
        return account;
    }


    /** Checks if the destination account exists and the name provided matches any of its owners' name **/
    public Account checkValidDestinationAccount(String name, Long id){
        Account account = checkAccountId(id);
        if (account.isFrozen()) {
            throw new ResponseStatusException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, "Account frozen");
        }
        checkAccountName(account, name);
        return account;
    }


    /** Check if the third party exists and the has key matches **/
    public ThirdParty checkValidThirdParty(String hashedKey, Long id){
        ThirdParty thirdParty = thirdPartyRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Third party not found"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(hashedKey, thirdParty.getHashKey())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong third party credentials");
        }
        return thirdParty;
    }


    /** Checks if the name provided matches any of the account owners' name **/
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


    /** Checks if the account secret key matches the one provided **/
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


    /** Freezes an account (if not a Credit Card)**/
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


    /** Unfreezes an account (if not a Credit Card)**/
    public void unfreeze(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        if (account.isFrozen()){
            String accountClass = account.getClass().getSimpleName();
            switch (accountClass){
                case "StudentChecking":
                    ((StudentChecking) account).setStatus(Status.ACTIVE);
                    studentCheckingRepository.save((StudentChecking) account);
                    break;
                case "Checking":
                    ((Checking) account).setStatus(Status.ACTIVE);
                    checkingRepository.save((Checking) account);
                    break;
                case "Savings":
                    ((Savings) account).setStatus(Status.ACTIVE);
                    savingsRepository.save((Savings) account);
                    break;
            }
        }

    }

}
