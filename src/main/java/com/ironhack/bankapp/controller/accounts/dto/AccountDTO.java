package com.ironhack.bankapp.controller.accounts.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AccountDTO {
    @NotNull
    @DecimalMin(value = "0", message = "Minimum balance must be above 0")
    private BigDecimal balance;

    @Min(1)
    @NotNull
    private Long accountId; //primary owner

    @Min(1)
    private Long accountSecondaryId; //secondary owner

    public AccountDTO() {
    }

    public AccountDTO(@NotNull @DecimalMin(value = "0", message = "Balance must be above 0") BigDecimal balance,
                      @Min(1) @NotNull Long accountId,
                      @Min(1) Long accountSecondaryId) {
        this.balance = balance;
        this.accountId = accountId;
        this.accountSecondaryId = accountSecondaryId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getAccountSecondaryId() {
        return accountSecondaryId;
    }

    public void setAccountSecondaryId(Long accountSecondaryId) {
        this.accountSecondaryId = accountSecondaryId;
    }
}
