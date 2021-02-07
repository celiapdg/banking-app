package com.ironhack.bankapp.model;

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

    public Admin(@NotNull @Pattern(regexp = VALID_NAME) String name,
                 @NotNull @Size(min = 4, max = 36) String username,
                 @NotNull @Size(min = 6, max = 20) String password) {
        super(name, username, password);
    }
}
