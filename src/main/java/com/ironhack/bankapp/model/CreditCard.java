package com.ironhack.bankapp.model;

import com.ironhack.bankapp.classes.Money;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class CreditCard extends Account {
    @NotNull
    @DecimalMax(value = "0.2", message = "Interest rate must be below 0.2")
    @DecimalMin(value = "0.1", message = "Interest rate cannot be less than 0.1")
    private BigDecimal interestRate = new BigDecimal(0.2);

    @NotNull
//    @DecimalMax(value = "100000", message = "Max credit limit is 100000")
//    @DecimalMin(value = "100", message = "Minimum credit limit is 100")
    @Embedded
    @AttributeOverrides(value ={
            @AttributeOverride(name = "amount", column = @Column(name = "credit_limit_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "credit_limit_currency"))
    })
    private Money creditLimit = new Money(new BigDecimal(100));

    public CreditCard() {
    }

    public CreditCard(Money balance,
                      AccountHolder primaryOwner,
                      AccountHolder secondaryOwner) {
        super(balance, primaryOwner, secondaryOwner);
    }

    public CreditCard(Money balance,
                      AccountHolder primaryOwner,
                      AccountHolder secondaryOwner,
                      @NotNull @DecimalMax(value = "0.2", message = "Interest rate must be below 0.2")
                      @DecimalMin(value = "0.1", message = "Interest rate cannot be less than 0.1") BigDecimal interestRate,
                      @NotNull @DecimalMax(value = "100000", message = "Max credit limit is 100000")
                      @DecimalMin(value = "100", message = "Minimum credit limit is 100") Money creditLimit) {
        super(balance, primaryOwner, secondaryOwner);
        this.interestRate = interestRate;
        this.creditLimit = creditLimit;
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
}
