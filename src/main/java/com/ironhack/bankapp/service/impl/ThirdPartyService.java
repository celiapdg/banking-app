package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.controller.users.dto.ThirdPartyDTO;
import com.ironhack.bankapp.repository.users.ThirdPartyRepository;
import com.ironhack.bankapp.service.interfaces.IThirdPartyService;
import com.ironhack.bankapp.model.users.ThirdParty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Service
public class ThirdPartyService implements IThirdPartyService {

    @Autowired
    ThirdPartyRepository thirdPartyRepository;

    /** Creates a Credit Card account **/
    public ThirdParty create(ThirdPartyDTO thirdPartyDTO){
        return thirdPartyRepository.save(new ThirdParty(thirdPartyDTO.getName(), thirdPartyDTO.getHashKey()));
    }
}
