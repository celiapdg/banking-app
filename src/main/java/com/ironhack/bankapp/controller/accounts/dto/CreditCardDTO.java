package com.ironhack.bankapp.controller.accounts.dto;

import com.ironhack.bankapp.controller.accounts.dto.AccountDTO;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CreditCardDTO extends AccountDTO {

    @NotNull
    @DecimalMax(value = "1", message = "Interest rate must be below 1")
    @DecimalMin(value = "0.1", message = "Interest rate cannot be less than 0.1")
    private BigDecimal interestRate;

    @NotNull
    @DecimalMax(value = "100000", message = "Max credit limit is 100000")
    @DecimalMin(value = "100", message = "Minimum credit limit is 100")
    private BigDecimal creditLimit; // default 100

    public CreditCardDTO() {
    }

    public CreditCardDTO(@NotNull @DecimalMin(value = "0", message = "Balance must be above 0") BigDecimal balance,
                         @Min(1) @NotNull Long accountId,
                         @Min(1) Long accountSecondaryId) {
        super(balance, accountId, accountSecondaryId);
        this.interestRate = new BigDecimal(0.2).setScale(4, RoundingMode.HALF_UP);
        this.creditLimit = new BigDecimal(100);
    }

    public CreditCardDTO(@NotNull @DecimalMin(value = "0", message = "Balance must be above 0") BigDecimal balance,
                         @Min(1) @NotNull Long accountId,
                         @Min(1) Long accountSecondaryId,
                         @NotNull @DecimalMax(value = "1", message = "Interest rate must be below 1")
                         @DecimalMin(value = "0.1", message = "Interest rate cannot be less than 0.1") BigDecimal interestRate,
                         @NotNull @DecimalMax(value = "100000", message = "Max credit limit is 100000")
                         @DecimalMin(value = "100", message = "Minimum credit limit is 100") BigDecimal creditLimit) {
        super(balance, accountId, accountSecondaryId);
        this.interestRate = interestRate;
        this.creditLimit = creditLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }
}
