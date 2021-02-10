package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.controller.users.dto.ThirdPartyDTO;
import com.ironhack.bankapp.repository.users.ThirdPartyRepository;
import com.ironhack.bankapp.service.interfaces.IThirdPartyService;
import com.ironhack.bankapp.model.users.ThirdParty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThirdPartyService implements IThirdPartyService {

    @Autowired
    ThirdPartyRepository thirdPartyRepository;

    public ThirdParty create(ThirdPartyDTO thirdPartyDTO){
        return thirdPartyRepository.save(new ThirdParty(thirdPartyDTO.getName(), thirdPartyDTO.getHashKey()));
    }
}
