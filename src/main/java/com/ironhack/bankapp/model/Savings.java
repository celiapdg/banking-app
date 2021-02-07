package com.ironhack.bankapp.model;

import ch.qos.logback.core.status.Status;
import com.ironhack.bankapp.classes.Money;

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
            @AttributeOverride(name = "amount", column = @Column(name = "monthly_maintenance_fee_amount"))
    })
    @DecimalMax(value = "1000", message = "Minimum balance must be below 1000")
    @DecimalMin(value = "100", message = "Minimum balance must be above 100")
    private final Money minimumBalance = new Money(new BigDecimal(1000));

    @DecimalMax(value = "0.5", message = "Interest rate must be below 0.5")
    @DecimalMin(value = "0", message = "Interest rate shouldn't be a negative value")
    private BigDecimal interestRate = new BigDecimal(0.0025);
    private boolean belowMinimumBalance;
    @PastOrPresent
    private LocalDate lastInterestDate;

    public Savings() {
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

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public boolean isBelowMinimumBalance() {
        return belowMinimumBalance;
    }

    public void setBelowMinimumBalance(boolean belowMinimumBalance) {
        if (this.balance.getAmount().compareTo(this.minimumBalance.getAmount())<0) {
            this.belowMinimumBalance = true;
        }else{
            this.belowMinimumBalance = false;
        }
    }
}
