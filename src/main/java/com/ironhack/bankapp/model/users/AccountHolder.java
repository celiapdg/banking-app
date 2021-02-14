package com.ironhack.bankapp.model.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.ironhack.bankapp.classes.Address;
import com.ironhack.bankapp.model.accounts.Account;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class AccountHolder extends User{
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birth;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "primary_country")),
            @AttributeOverride(name = "city", column = @Column(name = "primary_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "primary_postal_code")),
            @AttributeOverride(name = "street", column = @Column(name = "primary_street"))
    })
    private Address primaryAddress;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "country", column = @Column(name = "mailing_country")),
        @AttributeOverride(name = "city", column = @Column(name = "mailing_city")),
        @AttributeOverride(name = "postalCode", column = @Column(name = "mailing_postal_code")),
        @AttributeOverride(name = "street", column = @Column(name = "mailing_street"))
    })
    private Address mailingAddress;

    @OneToMany(mappedBy = "primaryOwner", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Account> primaryAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "secondaryOwner", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Account> secondaryAccounts = new ArrayList<>();

    @Transient
    @JsonIgnore
    private List<Account> allAccounts = new ArrayList<>();

    /**
     * Default class constructor
     **/
    public AccountHolder() {
        this.addRole(new Role("ACCOUNT_HOLDER", this));
    }

    /**
     * Class constructor specifying name, username, password birth, and primary and mailing address
     **/
    public AccountHolder(String name,
                         String username,
                         String password,
                         LocalDate birth,
                         Address primaryAddress,
                         Address mailingAddress) {
        super(name, username, password);
        this.birth = birth;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = mailingAddress;
        // adds ACCOUNT_HOLDER role on creation
        this.addRole(new Role("ACCOUNT_HOLDER", this));
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public List<Account> getPrimaryAccounts() {
        return primaryAccounts;
    }

    public void setPrimaryAccounts(List<Account> primaryAccounts) {
        this.primaryAccounts = primaryAccounts;
    }

    public List<Account> getSecondaryAccounts() {
        return secondaryAccounts;
    }

    public void setSecondaryAccounts(List<Account> secondaryAccounts) {
        this.secondaryAccounts = secondaryAccounts;
    }

    /** returns all the accounts where this account holder is primary or secondary owner*/
    public List<Account> getAllAccounts() {
        List<Account> allAccounts = new ArrayList<Account>(this.getPrimaryAccounts());
        allAccounts.addAll(this.getSecondaryAccounts());
        return allAccounts;
    }

    /** checks if this account holder owns (as either primary or secondary owner) an account by its ID */
    public Boolean isOwner(Long accountID){
        List<Account> allAccounts = this.getAllAccounts();
        for (Account account: allAccounts){
            if (account.getId().equals(accountID)){
                return true;
            }
        }
        return false;
    }
}
