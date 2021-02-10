package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.TransactionDTO;
import com.ironhack.bankapp.controller.accounts.dto.BalanceDTO;
import com.ironhack.bankapp.model.Transaction;
import com.ironhack.bankapp.model.accounts.Account;
import com.ironhack.bankapp.model.accounts.Checking;
import com.ironhack.bankapp.model.accounts.CreditCard;
import com.ironhack.bankapp.model.accounts.Savings;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.users.ThirdParty;
import com.ironhack.bankapp.model.users.User;
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

    @Override
    public BalanceDTO checkBalance(Long id, String username) {
        Account account = accountRepository.findById(id).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        User user = userRepository.findByUsername(username).get();
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));

        if (isAdmin || account.getPrimaryOwner().getUsername().equals(username) ||
           (account.getSecondaryOwner() != null && account.getSecondaryOwner().getUsername().equals(username))){

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

    public Transaction transfer(TransactionDTO transactionDTO, Principal principal,
                                Optional<String> hashedKey, Optional<String> secretKey) {

        Long originId = transactionDTO.getOriginId();
        Long destinationId = transactionDTO.getDestinationId();
        String destinationName = transactionDTO.getDestinationName();
        BigDecimal amount = transactionDTO.getAmount();

        Object origin;
        // unauthenticated user = third party
        if (principal == null && hashedKey.isPresent()){
            origin = thirdPartyRepository.findById(originId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Third party not found"));

            if (checkThirdPartyCredentials((ThirdParty) origin, hashedKey.get(), ((ThirdParty) origin).getName())){
                // todo: origin ok, no hay que restarle nada, pero luego hay que comprobar la secretKey
            }else{
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Origin account info is wrong");
            }
        // authenticated user. check if it is an AccountHolder
        } else if (principal != null) {
            AccountHolder accountHolder = accountHolderRepository.findByUsername(principal.getName()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Account holder not found"));

            Account account = accountRepository.findById(originId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
            // todo: es probable que haya que añadir el equals y el hashcode
            if (accountHolder.getAllAccounts().contains(account)) {
                // todo: origin ok, habría que restarle la cantidad en caso de que el destino también esté ok
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this account");
            }
            /** principal null (unauthenticated user) and no hashed key provided **/
        }else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong origin account. Try logging in or check " +
                    "provided credentials");

        }

        return null;
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
        return true;
    }

    public boolean checkAccountCredentials(ThirdParty thirdParty, String secretKey, String name){
        return true;
    }
}
