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
    private Long primaryId; //primary owner

    @Min(1)
    private Long secondaryId; //secondary owner


    /**------------------------Constructors------------------------**/

    /**
     * Default class constructor
     **/
    public AccountDTO() {
    }

    /**
     * class constructor specifying balance, primary owner id (not nullable) and secondary owner id (nullable)
     **/
    public AccountDTO(@NotNull @DecimalMin(value = "0", message = "Balance must be above 0") BigDecimal balance,
                      @Min(1) @NotNull Long primaryId,
                      @Min(1) Long secondaryId) {
        this.balance = balance;
        this.primaryId = primaryId;
        this.secondaryId = secondaryId;
    }


    /**------------------------Getters and Setters------------------------**/

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(Long primaryId) {
        this.primaryId = primaryId;
    }

    public Long getSecondaryId() {
        return secondaryId;
    }

    public void setSecondaryId(Long secondaryId) {
        this.secondaryId = secondaryId;
    }
}
