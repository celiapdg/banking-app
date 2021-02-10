package com.ironhack.bankapp.controller.accounts.dto;

import com.ironhack.bankapp.controller.accounts.dto.AccountDTO;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CheckingDTO extends AccountDTO {

    @NotBlank
    private String secretKey;

    public CheckingDTO() {
    }

    public CheckingDTO(@NotNull @DecimalMin(value = "0", message = "Balance must be above 0") BigDecimal balance,
                       @Min(1) @NotNull Long accountId,
                       @Min(1) Long accountSecondaryId,
                       @NotBlank String secretKey) {
        super(balance, accountId, accountSecondaryId);
        this.secretKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
