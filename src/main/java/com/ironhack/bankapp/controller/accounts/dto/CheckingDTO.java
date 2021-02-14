package com.ironhack.bankapp.controller.accounts.dto;

import com.ironhack.bankapp.controller.accounts.dto.AccountDTO;

import javax.validation.constraints.*;
import java.math.BigDecimal;

import static com.ironhack.bankapp.utils.RegExp.VALID_PASSWORD;

public class CheckingDTO extends AccountDTO {

    @NotBlank
    @Pattern(regexp = VALID_PASSWORD, message = "Not a valid hash key")
    private String secretKey;

    /**
     * Default class constructor
     **/
    public CheckingDTO() {
    }

    /**
     * class constructor specifying balance, primary owner id (not nullable), secondary owner id (nullable)
     * and secretKey
     **/
    public CheckingDTO(@NotNull @DecimalMin(value = "0", message = "Balance must be above 0") BigDecimal balance,
                       @Min(1) @NotNull Long primaryId,
                       @Min(1) Long secondaryId,
                       @NotBlank @Pattern(regexp = VALID_PASSWORD, message = "Not a valid secret key") String secretKey) {
        super(balance, primaryId, secondaryId);
        this.secretKey = secretKey;
    }


    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
