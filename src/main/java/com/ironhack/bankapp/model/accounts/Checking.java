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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class Checking extends Account {

    protected String secretKey;
    @Enumerated(EnumType.STRING)
    protected Status status;

    private static final Money minimumBalance = new Money(new BigDecimal(250));

    private static final Money monthlyMaintenance = new Money(new BigDecimal(12));

    @PastOrPresent
    @NotNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate lastMaintenanceDate;

    private boolean belowMinimumBalance;

    public Checking() {
    }

    public Checking(Money balance,
                    AccountHolder primaryOwner,
                    String secretKey) {
        super(balance, primaryOwner);
        setSecretKey(secretKey);
        this.status = Status.ACTIVE;
        setBelowMinimumBalance();
        this.lastMaintenanceDate = LocalDate.now();
    }

    @Override
    public void setBalance(Money balance) {
        super.setBalance(balance);
    }

    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public Money getMonthlyMaintenance() {
        return monthlyMaintenance;
    }

    public String getSecretKey() {
        return secretKey;
    }

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

    public LocalDate getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    @Override
    public Boolean isFrozen(){
        return this.status.equals(Status.FROZEN);
    }

}
