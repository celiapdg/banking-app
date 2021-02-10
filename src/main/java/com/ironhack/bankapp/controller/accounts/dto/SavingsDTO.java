package com.ironhack.bankapp.controller.accounts.dto;

import com.ironhack.bankapp.controller.accounts.dto.AccountDTO;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class SavingsDTO extends AccountDTO {

    @NotNull
    @DecimalMax(value = "1000", message = "Minimum balance must be below 1000")
    @DecimalMin(value = "100", message = "Minimum balance must be above 100")
    private BigDecimal minimumBalance;

    @NotBlank
    private String secretKey;

    @NotNull
    @DecimalMax(value = "0.5", message = "Interest rate must be below 0.5")
    @DecimalMin(value = "0", message = "Interest rate shouldn't be a negative value")
    private BigDecimal interestRate;

    public SavingsDTO() {
    }

    public SavingsDTO(@NotNull @DecimalMin(value = "0", message = "Balance must be above 0") BigDecimal balance,
                      @NotNull @DecimalMax(value = "1000", message = "Minimum balance must be below 1000")
                      @DecimalMin(value = "100", message = "Minimum balance must be above 100") BigDecimal minimumBalance,
                      @Min(1) @NotNull Long accountId,
                      @Min(1) Long accountSecondaryId,
                      @NotBlank @Size(min = 4, max = 4) String secretKey,
                      @NotNull @DecimalMax(value = "0.5", message = "Interest rate must be below 0.5")
                      @DecimalMin(value = "0", message = "Interest rate shouldn't be a negative value") BigDecimal interestRate) {
        super(balance, accountId, accountSecondaryId);
        this.minimumBalance = minimumBalance;
        this.secretKey = secretKey;
        this.interestRate = interestRate;
    }

    public SavingsDTO(@NotNull @DecimalMin(value = "0", message = "Balance must be above 0") BigDecimal balance,
                      @DecimalMax(value = "1000", message = "Minimum balance must be below 1000")
                      @DecimalMin(value = "100", message = "Minimum balance must be above 100") BigDecimal minimumBalance,
                      @Min(1) @NotNull Long accountId,
                      @Min(1) Long accountSecondaryId,
                      @NotBlank @Size(min = 4, max = 4) String secretKey) {
        super(balance, accountId, accountSecondaryId);
        this.minimumBalance = minimumBalance;
        this.secretKey = secretKey;
        this.interestRate = new BigDecimal(0.0025);
    }

    public SavingsDTO(@NotNull @DecimalMin(value = "0", message = "Balance must be above 0") BigDecimal balance,
                      @Min(1) @NotNull Long accountId,
                      @Min(1) Long accountSecondaryId,
                      @NotBlank @Size(min = 4, max = 4) String secretKey,
                      @NotNull @DecimalMax(value = "0.5", message = "Interest rate must be below 0.5")
                      @DecimalMin(value = "0", message = "Interest rate shouldn't be a negative value") BigDecimal interestRate) {
        super(balance, accountId, accountSecondaryId);
        this.minimumBalance = new BigDecimal(1000);
        this.secretKey = secretKey;
        this.interestRate = interestRate;
    }

    public SavingsDTO(@NotNull @DecimalMin(value = "0", message = "Balance must be above 0") BigDecimal balance,
                      @Min(1) @NotNull Long accountId,
                      @Min(1) Long accountSecondaryId,
                      @NotBlank @Size(min = 4, max = 4) String secretKey) {
        super(balance, accountId, accountSecondaryId);
        this.minimumBalance = new BigDecimal(1000);
        this.secretKey = secretKey;
        this.interestRate = new BigDecimal(0.0025);
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
