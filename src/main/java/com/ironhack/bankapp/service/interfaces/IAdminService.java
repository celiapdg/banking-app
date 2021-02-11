package com.ironhack.bankapp.service.interfaces;

import com.ironhack.bankapp.controller.users.dto.AdminDTO;
import com.ironhack.bankapp.model.users.Admin;

public interface IAdminService {
    Admin create(AdminDTO adminDTO);
}
