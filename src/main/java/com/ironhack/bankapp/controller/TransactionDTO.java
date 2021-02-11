package com.ironhack.bankapp.controller;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransactionDTO {
    @NotNull
    @Min(1)
    private Long originId;
    @NotNull
    @Min(1)
    private Long destinationId;
    @NotBlank
    private String destinationName;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    public TransactionDTO() {
    }

    public TransactionDTO(@NotNull @Min(1) Long originId, @NotNull @Min(1) Long destinationId, @NotBlank String destinationName, @NotNull @DecimalMin("0.01") BigDecimal amount) {
        this.originId = originId;
        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.amount = amount;
    }

    public Long getOriginId() {
        return originId;
    }

    public void setOriginId(Long originId) {
        this.originId = originId;
    }

    public Long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Long destinationId) {
        this.destinationId = destinationId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
