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
    @NotNull
    private String concept;

    /**------------------------Constructors------------------------**/

    /**
     * Default class constructor
     **/
    public TransactionDTO() {
    }

    /**
     * Class constructor specifying origin and destination account IDs, destination name,
     * amount and concept
     **/
    public TransactionDTO(@NotNull @Min(1) Long originId,
                          @NotNull @Min(1) Long destinationId,
                          @NotBlank String destinationName,
                          @NotNull @DecimalMin("0.01") BigDecimal amount,
                          @NotNull String concept) {
        this.originId = originId;
        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.amount = amount;
        this.concept = concept;
    }

    /**------------------------Getters and Setters------------------------**/

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

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }
}
