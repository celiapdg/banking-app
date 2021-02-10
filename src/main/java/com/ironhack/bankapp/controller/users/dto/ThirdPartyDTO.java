package com.ironhack.bankapp.controller.users.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ThirdPartyDTO {
    @NotNull
    protected String name;
    @NotNull
    @Size(min = 6, max = 6)
    private String hashKey;

    public ThirdPartyDTO() {
    }

    public ThirdPartyDTO(@NotNull String name,
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
