package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.controller.users.dto.ThirdPartyDTO;
import com.ironhack.bankapp.repository.users.ThirdPartyRepository;
import com.ironhack.bankapp.service.interfaces.IThirdPartyService;
import com.ironhack.bankapp.model.users.ThirdParty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;

@Service
public class ThirdPartyService implements IThirdPartyService {

    @Autowired
    ThirdPartyRepository thirdPartyRepository;


    @PostMapping("/new-third-party")
    @ResponseStatus(HttpStatus.CREATED)
    public ThirdParty create(@RequestBody @Valid ThirdPartyDTO thirdPartyDTO){
        return thirdPartyRepository.save(new ThirdParty(thirdPartyDTO.getName(), thirdPartyDTO.getHashKey()));
    }
}
