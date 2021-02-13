package com.ironhack.bankapp.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.model.accounts.Account;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account origin;

    @ManyToOne
    private Account destination;

    @Embedded
    @AttributeOverrides(value ={
            @AttributeOverride(name = "amount", column = @Column(name = "transaction_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "transaction_currency"))
    })

    private Money amount;

    private String concept;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @PastOrPresent
    private LocalDateTime transactionDateTime;

    public Transaction() {
    }

    public Transaction(Account origin,
                       Account destination,
                       Money amount,
                       String concept,
                       @PastOrPresent LocalDateTime transactionDateTime) {
        this.origin = origin;
        this.destination = destination;
        this.amount = amount;
        this.concept = concept;
        this.transactionDateTime = transactionDateTime;
    }

    public Transaction(Account origin,
                       Account destination,
                       BigDecimal amount,
                       String concept,
                       @PastOrPresent LocalDateTime transactionDateTime) {
        this.origin = origin;
        this.destination = destination;
        this.amount = new Money(amount);
        this.concept = concept;
        this.transactionDateTime = transactionDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getOrigin() {
        return origin;
    }

    public void setOrigin(Account origin) {
        this.origin = origin;
    }

    public Account getDestination() {
        return destination;
    }

    public void setDestination(Account destination) {
        this.destination = destination;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public LocalDateTime getTransactionDateTime() {
        return transactionDateTime;
    }

    public void setTransactionDateTime(LocalDateTime transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", origin=" + origin +
                ", destination=" + destination +
                ", amount=" + amount +
                ", concept='" + concept + '\'' +
                ", transactionDateTime=" + transactionDateTime +
                '}';
    }
}
