package com.ironhack.bankapp.model.users;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.ironhack.bankapp.utils.RegExp.VALID_NAME;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Admin extends User {

    public Admin() {
    }

    public Admin(String name, String username, String password) {
        super(name, username, password);
    }
}
