package com.ironhack.bankapp.model.accounts;

import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.enums.Status;
import com.ironhack.bankapp.model.users.AccountHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@PrimaryKeyJoinColumn(name = "id")
@Inheritance(strategy = InheritanceType.JOINED)
public class StudentChecking extends Account {

    protected String secretKey;
    @Enumerated(EnumType.STRING)
    protected Status status;

    /**
     * Default class constructor
     **/
    public StudentChecking() {
    }

    /**
     * Class constructor specifying balance, primary owner and secret key
     **/
    public StudentChecking(Money balance,
                           AccountHolder primaryOwner,
                           String secretKey) {
        super(balance, primaryOwner);
        setSecretKey(secretKey);
        // default set-up:
        this.status = Status.ACTIVE;
    }

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

    @Override
    public Boolean isFrozen(){
        return this.status.equals(Status.FROZEN);
    }
}
