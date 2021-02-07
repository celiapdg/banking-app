package com.ironhack.bankapp.model;

import javax.persistence.*;
import com.ironhack.bankapp.classes.Money;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Account {
    @Id
    @Size(min = 20, max = 20)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @Embedded
    @AttributeOverrides(value ={
            @AttributeOverride(name = "amount", column = @Column(name = "balance_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "balance_currency"))
    })
    protected Money balance;

    @ManyToOne(optional = false)
    protected AccountHolder primaryOwner;

    @ManyToOne(optional = true)
    protected AccountHolder secondaryOwner;

    @Embedded
    @AttributeOverrides(value ={
            @AttributeOverride(name = "amount", column = @Column(name = "penalty_fee_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "penalty_fee_currency"))
    })
    protected final Money penaltyFee = new Money(new BigDecimal(40));

    @OneToMany(mappedBy = "origin")
    private List<Transaction> transactionSent;

    @OneToMany(mappedBy = "destination")
    private List<Transaction> transactionReceived;

    public Account() {
    }

    public Account(Money balance, AccountHolder primaryOwner) {
        this.balance = balance;
        this.primaryOwner = primaryOwner;
    }

    public Account(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
    }

    // todo: añadir getters y setters cuando esté la lista de transferencias

}
