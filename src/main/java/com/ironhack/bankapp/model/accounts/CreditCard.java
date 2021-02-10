package com.ironhack.bankapp.model.accounts;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.model.users.AccountHolder;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class CreditCard extends Account {

    private BigDecimal interestRate;

    @Embedded
    @AttributeOverrides(value ={
            @AttributeOverride(name = "amount", column = @Column(name = "credit_limit_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "credit_limit_currency"))
    })
    private Money creditLimit;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate lastInterestDate;

    public CreditCard() {
    }

    public CreditCard(Money balance,
                      AccountHolder primaryOwner,
                      @NotNull @DecimalMax(value = "0.2", message = "Interest rate must be below 0.2")
                      @DecimalMin(value = "0.1", message = "Interest rate cannot be less than 0.1") BigDecimal interestRate,
                      @NotNull @DecimalMax(value = "100000", message = "Max credit limit is 100000")
                      @DecimalMin(value = "100", message = "Minimum credit limit is 100") Money creditLimit) {
        super(balance, primaryOwner);
        this.interestRate = interestRate;
        this.creditLimit = creditLimit;
        this.lastInterestDate = LocalDate.now();
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Money getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Money creditLimit) {
        this.creditLimit = creditLimit;
    }

    public LocalDate getLastInterestDate() {
        return lastInterestDate;
    }

    public void setLastInterestDate(LocalDate lastInterestDate) {
        this.lastInterestDate = lastInterestDate;
    }
}