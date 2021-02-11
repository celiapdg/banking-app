package com.ironhack.bankapp.controller.users.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AdminDTO {
    @NotBlank
    //@Pattern(regexp = VALID_NAME, message = "Not a valid name")
    @Size(max = 100)
    private String name;

    @NotBlank
    //@Size(min = 4, max = 35)    // añadir que no pueda empezar por un número (exp reg)
    private String username;

    @NotBlank
    //@Size(min = 6, max = 20)    // mensaje indicando condiciones (exp regular?)
    private String password;

    public AdminDTO() {
    }

    public AdminDTO(@NotBlank @Size(max = 100) String name,
                    @NotBlank String username,
                    @NotBlank String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
