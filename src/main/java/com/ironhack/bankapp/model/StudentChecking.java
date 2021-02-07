package com.ironhack.bankapp.model;

import ch.qos.logback.core.status.Status;
import com.ironhack.bankapp.classes.Money;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class StudentChecking extends Account {
    @NotBlank
    @Size(min = 4, max = 4)
    protected String secretKey;
    @Enumerated(EnumType.STRING)
    protected Status status;

    public StudentChecking() {
    }

    public StudentChecking(Money balance,
                           AccountHolder primaryOwner,
                           AccountHolder secondaryOwner,
                           @NotBlank @Size(min = 4, max = 4) String secretKey,
                           Status status) {
        super(balance, primaryOwner, secondaryOwner);
        this.secretKey = secretKey;
        this.status = status;
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
}
