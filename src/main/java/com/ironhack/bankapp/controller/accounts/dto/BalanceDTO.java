package com.ironhack.bankapp.controller.accounts.dto;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BalanceDTO {
    @NotNull
    private BigDecimal amount;

    public BalanceDTO() {
    }

    public BalanceDTO(@NotNull @DecimalMax(value = "100000", message = "Max credit limit is 100000")
                      @DecimalMin(value = "100", message = "Minimum credit limit is 100") BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
