package com.ironhack.bankapp.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.enums.Status;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Savings extends Account{
    @NotBlank
    @Size(min = 4, max = 4)
    protected String secretKey;
    @Enumerated(EnumType.STRING)
    protected Status status;

    @Embedded
    @AttributeOverrides(value ={
            @AttributeOverride(name = "amount", column = @Column(name = "monthly_maintenance_fee_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "monthly_maintenance_fee_currency"))
    })
    @DecimalMax(value = "1000", message = "Minimum balance must be below 1000")
    @DecimalMin(value = "100", message = "Minimum balance must be above 100")
    private Money minimumBalance = new Money(new BigDecimal(1000));

    @NotNull
    @DecimalMax(value = "0.5", message = "Interest rate must be below 0.5")
    @DecimalMin(value = "0", message = "Interest rate shouldn't be a negative value")
    private BigDecimal interestRate = new BigDecimal(0.0025);
    private boolean belowMinimumBalance;

    @PastOrPresent
    @NotNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate lastInterestDate;

    public Savings() {
    }

    public Savings(Money balance,
                   AccountHolder primaryOwner,
                   AccountHolder secondaryOwner,
                   @NotBlank @Size(min = 4, max = 4) String secretKey,
                   Status status) {
        super(balance, primaryOwner, secondaryOwner);
        this.secretKey = secretKey;
        this.status = status;
    }

    public Savings(Money balance, AccountHolder primaryOwner,
                   AccountHolder secondaryOwner,
                   @NotBlank @Size(min = 4, max = 4) String secretKey,
                   Status status,
                   @DecimalMax(value = "1000", message = "Minimum balance must be below 1000")
                   @DecimalMin(value = "100", message = "Minimum balance must be above 100") Money minimumBalance,
                   @DecimalMax(value = "0.5", message = "Interest rate must be below 0.5")
                   @DecimalMin(value = "0", message = "Interest rate shouldn't be a negative value") BigDecimal interestRate,
                   boolean belowMinimumBalance) {
        super(balance, primaryOwner, secondaryOwner);
        this.secretKey = secretKey;
        this.status = status;
        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
        setBelowMinimumBalance();
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(Money minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public boolean isBelowMinimumBalance() {
        return belowMinimumBalance;
    }

    public void setBelowMinimumBalance() {
        if (this.balance.getAmount().compareTo(this.minimumBalance.getAmount())<0) {
            this.belowMinimumBalance = true;
        }else{
            this.belowMinimumBalance = false;
        }
    }

    public LocalDate getLastInterestDate() {
        return lastInterestDate;
    }

    public void setLastInterestDate(LocalDate lastInterestDate) {
        this.lastInterestDate = lastInterestDate;
    }
}
