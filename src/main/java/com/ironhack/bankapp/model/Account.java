package com.ironhack.bankapp.model;

import javax.persistence.*;
import com.ironhack.bankapp.classes.Money;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Account {
    @Id
    @Size(min = 20, max = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    @Embedded
    @AttributeOverrides(value ={
        @AttributeOverride(name = "amount", column = @Column(name = "balance_amount")),
    })
    protected Money balance;
    @ManyToOne(optional = false)
    protected AccountHolder primaryOwner;
    @ManyToOne(optional = true)
    protected AccountHolder secondaryOwner;
    @Embedded
    @AttributeOverrides(value ={
        @AttributeOverride(name = "amount", column = @Column(name = "penalty_fee_amount")),
    })
    protected final Money penaltyFee = new Money(new BigDecimal(40));

    // todo: me gustaría añadir una entity de transferencias y que todas las cuentas tuvieran una lista
    // todo: debe haber una de transferencias enviadas y otra de recibidas??? no guta
    // todo: yo creo que no, que se podrían juntar

    public Account() {
    }

    public Account(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
    }

    // todo: añadir getters y setters cuando esté la lista de transferencias

}
