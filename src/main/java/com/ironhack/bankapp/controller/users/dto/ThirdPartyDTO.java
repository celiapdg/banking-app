package com.ironhack.bankapp.controller.users.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.ironhack.bankapp.utils.RegExp.VALID_PASSWORD;
import static com.ironhack.bankapp.utils.RegExp.VALID_USERNAME;

public class ThirdPartyDTO {
    @NotBlank
    @Pattern(regexp = VALID_USERNAME, message = "Not a valid third party name")
    protected String name;
    @NotNull
    @Pattern(regexp = VALID_PASSWORD, message = "Not a valid hash key")
    private String hashKey;

    /**------------------------Constructors------------------------**/

    /**
     * Default class constructor
     **/
    public ThirdPartyDTO() {
    }

    /**
     * Class constructor specifying name and hash key
     **/
    public ThirdPartyDTO(@NotNull String name,
                         @NotNull @Pattern(regexp = VALID_PASSWORD, message = "Not a valid hash key") String hashKey) {
        this.name = name;
        this.hashKey = hashKey;
    }

    /**------------------------Getters and Setters------------------------**/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }
}
