package com.ironhack.bankapp.controller.users.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.ironhack.bankapp.utils.RegExp.*;

public class AdminDTO {
    @NotBlank
    @Pattern(regexp = VALID_NAME, message = "Not a valid name")
    @Size(max = 100)
    private String name;

    @NotBlank
    @Pattern(regexp = VALID_USERNAME, message = "Not a valid username")
    private String username;

    @NotBlank
    @Pattern(regexp = VALID_PASSWORD, message = "Not a valid password")
    private String password;

    public AdminDTO() {
    }

    public AdminDTO(@NotBlank @Size(max = 100) @Pattern(regexp = VALID_NAME, message = "Not a valid name") String name,
                    @NotBlank @Pattern(regexp = VALID_USERNAME, message = "Not a valid username") String username,
                    @NotBlank @Pattern(regexp = VALID_PASSWORD, message = "Not a valid hash key") String password) {
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
