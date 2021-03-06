package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.classes.Address;
import com.ironhack.bankapp.controller.users.dto.AccountHolderDTO;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.users.Role;
import com.ironhack.bankapp.model.users.User;
import com.ironhack.bankapp.repository.users.AccountHolderRepository;
import com.ironhack.bankapp.service.interfaces.IAccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountHolderService implements IAccountHolderService {

    @Autowired
    AccountHolderRepository accountHolderRepository;

    /** Creates an account holder **/
    public AccountHolder create(AccountHolderDTO accountHolderDTO) {

        Address primaryAddress = new Address(accountHolderDTO.getPrimaryCountry(),
                accountHolderDTO.getPrimaryCity(),
                accountHolderDTO.getPrimaryPostalCode(),
                accountHolderDTO.getPrimaryStreet());

        Address mailingAddress = new Address(accountHolderDTO.getMailingCountry(),
                accountHolderDTO.getMailingCity(),
                accountHolderDTO.getMailingPostalCode(),
                accountHolderDTO.getMailingStreet());

        AccountHolder accountHolder = new AccountHolder(accountHolderDTO.getName(),
                accountHolderDTO.getUsername(),
                accountHolderDTO.getPassword(),
                accountHolderDTO.getBirth(), primaryAddress, mailingAddress);

        return accountHolderRepository.save(accountHolder);
    }
}
