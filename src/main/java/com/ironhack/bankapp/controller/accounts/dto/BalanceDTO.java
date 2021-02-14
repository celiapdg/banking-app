package com.ironhack.bankapp.controller.accounts.dto;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BalanceDTO {
    @NotNull
    private BigDecimal amount;

    /**------------------------Constructors------------------------**/

    /**
     * Default class constructor
     **/
    public BalanceDTO() {
    }

    /**
     * Class constructor specifying amount
     **/
    public BalanceDTO(@NotNull @DecimalMin(value = "0", message = "Minimum balance is 0") BigDecimal amount) {
        this.amount = amount;
    }


    /**------------------------Getters and Setters------------------------**/

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
