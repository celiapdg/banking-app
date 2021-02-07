package com.ironhack.bankapp.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.ironhack.bankapp.utils.RegExp.VALID_NAME;

// no entiendo bien esta clase, es un usuario de otro banco?
@Entity
public class ThirdParty {
    @NotNull
    @Pattern(regexp = VALID_NAME)
    protected String name;
    @NotNull
    @Size(min = 6, max = 6)
    private String hashKey;

    public ThirdParty() {
    }

    public ThirdParty(@NotNull @Pattern(regexp = VALID_NAME) String name,
                      @NotNull @Size(min = 6, max = 6) String hashKey) {
        this.name = name;
        this.hashKey = hashKey;
    }

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
