package com.ironhack.bankapp.model.accounts;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.Transaction;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Account {
    @Id
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

    protected static final Money penaltyFee = new Money(new BigDecimal(40));

    @OneToMany(mappedBy = "origin", fetch = FetchType.EAGER)
    @JsonIgnore
    protected List<Transaction> transactionSent;

    @OneToMany(mappedBy = "destination", fetch = FetchType.EAGER)
    @JsonIgnore
    protected List<Transaction> transactionReceived;

    @Transient
    @JsonIgnore
    protected List<Transaction> allTransactions;

    /**
     * Default class constructor
     **/
    public Account() {
    }

    /**
     * Class constructor specifying balance and primary owner.
     **/
    public Account(Money balance, AccountHolder primaryOwner) {
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        // Only primary owner. If there's a 2nd, we'll use its setter after creation
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

    public AccountHolder getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(AccountHolder primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public AccountHolder getSecondaryOwner() {
        return secondaryOwner;
    }

    public void setSecondaryOwner(AccountHolder secondaryOwner) {
        this.secondaryOwner = secondaryOwner;
    }

    public Money getPenaltyFee() {
        return penaltyFee;
    }

    public List<Transaction> getTransactionSent() {
        return transactionSent;
    }

    public void setTransactionSent(List<Transaction> transactionSent) {
        this.transactionSent = transactionSent;
    }

    public List<Transaction> getTransactionReceived() {
        return transactionReceived;
    }

    public void setTransactionReceived(List<Transaction> transactionReceived) {
        this.transactionReceived = transactionReceived;
    }

    /** get all transactions sorted by date (most recent first) */
    public List<Transaction> getAllTransactions() {
        List<Transaction> allTransactions = this.getTransactionReceived();
        allTransactions.addAll(this.getTransactionSent());
        Collections.sort(allTransactions, Comparator.comparing(Transaction::getTransactionDateTime).reversed());
        return allTransactions;
    }

    /** checks if balance is more or equals to the amount provided*/
    public Boolean hasEnoughFunds(Money amount){
        if (this instanceof CreditCard){
            return amount.getAmount().compareTo(this.getBalance().getAmount().add(((CreditCard) this).getCreditLimit().getAmount())) < 0;
        }
        return amount.getAmount().compareTo(this.getBalance().getAmount()) < 0;
    }

    public BigDecimal increaseBalance(Money amount){
        return this.balance.increaseAmount(amount);
    }

    public BigDecimal decreaseBalance(Money amount){
        return this.balance.decreaseAmount(amount);
    }

    public Boolean isFrozen(){
        return this.isFrozen();
    }

}
