package com.ironhack.bankapp.controller.users.impl;

import com.ironhack.bankapp.controller.users.dto.AdminDTO;
import com.ironhack.bankapp.model.users.Admin;
import com.ironhack.bankapp.service.interfaces.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AdminController {

    @Autowired
    IAdminService adminService;

    /** Create a new admin. Only for admins **/
    @PostMapping("/new-admin")
    @ResponseStatus(HttpStatus.CREATED)
    public Admin create(@RequestBody @Valid AdminDTO adminDTO){
        return adminService.create(adminDTO);
    }
}
