package com.ironhack.bankapp.model;

import ch.qos.logback.core.status.Status;
import com.ironhack.bankapp.classes.Money;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class Checking extends StudentChecking {

    @Embedded
    @AttributeOverrides(value ={
            @AttributeOverride(name = "amount", column = @Column(name = "monthly_maintenance_fee_amount"))
    })
    private final Money minimumBalance = new Money(new BigDecimal(250));

    @Embedded
    @AttributeOverrides(value ={
            @AttributeOverride(name = "amount", column = @Column(name = "minimum_balance_amount"))
    })
    private final Money monthlyMaintenance = new Money(new BigDecimal(12));

    private boolean belowMinimumBalance;

    public Checking() {
    }

    public Checking(Money balance,
                    AccountHolder primaryOwner,
                    AccountHolder secondaryOwner,
                    @NotBlank @Size(min = 4, max = 4) String secretKey,
                    Status status) {
        super(balance, primaryOwner, secondaryOwner, secretKey, status);
    }

    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public Money getMonthlyMaintenance() {
        return monthlyMaintenance;
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
