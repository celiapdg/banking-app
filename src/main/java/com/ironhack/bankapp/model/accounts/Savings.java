package com.ironhack.bankapp.model.accounts;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.enums.Status;
import com.ironhack.bankapp.model.users.AccountHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Savings extends Account{
    @Embedded
    @AttributeOverrides(value ={
            @AttributeOverride(name = "amount", column = @Column(name = "monthly_maintenance_fee_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "monthly_maintenance_fee_currency"))
    })
    private Money minimumBalance;

    protected String secretKey;

    @Enumerated(EnumType.STRING)
    protected Status status;

    private BigDecimal interestRate;
    private boolean belowMinimumBalance;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate lastInterestDate;

    /**------------------------Constructors------------------------**/

    /**
     * Default class constructor
     **/
    public Savings() {
    }

    /**
     * Class constructor specifying balance, primary owner, secret key,
     * minimum balance and interest rate
     **/
    public Savings(Money balance,
                   AccountHolder primaryOwner,
                   String secretKey,
                   Money minimumBalance,
                   BigDecimal interestRate) {
        super(balance, primaryOwner);
        setSecretKey(secretKey);
        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
        // default set-up:
        this.status = Status.ACTIVE;
        setBelowMinimumBalance();
        this.lastInterestDate = LocalDate.now();
    }

    /**------------------------Methods------------------------**/

    @Override
    public Boolean isFrozen(){
        return this.status.equals(Status.FROZEN);
    }

    /**------------------------Getters and Setters------------------------**/

    public String getSecretKey() {
        return secretKey;
    }

    // automatically encrypts the secret key
    public void setSecretKey(String secretKey) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.secretKey = passwordEncoder.encode(secretKey);
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
