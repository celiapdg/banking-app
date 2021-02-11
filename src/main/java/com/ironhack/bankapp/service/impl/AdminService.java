package com.ironhack.bankapp.service.impl;

import com.ironhack.bankapp.controller.users.dto.AdminDTO;
import com.ironhack.bankapp.model.users.Admin;
import com.ironhack.bankapp.repository.users.AdminRepository;
import com.ironhack.bankapp.service.interfaces.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService implements IAdminService {

    @Autowired
    AdminRepository adminRepository;

    public Admin create(AdminDTO adminDTO) {
        return adminRepository.save(new Admin(adminDTO.getName(),
                                              adminDTO.getUsername(),
                                              adminDTO.getPassword()));
    }
}
