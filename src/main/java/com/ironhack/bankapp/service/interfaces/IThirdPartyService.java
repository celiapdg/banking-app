package com.ironhack.bankapp.service.interfaces;

import com.ironhack.bankapp.controller.users.dto.ThirdPartyDTO;
import com.ironhack.bankapp.model.users.ThirdParty;

public interface IThirdPartyService {

    ThirdParty create(ThirdPartyDTO thirdPartyDTO);
}
