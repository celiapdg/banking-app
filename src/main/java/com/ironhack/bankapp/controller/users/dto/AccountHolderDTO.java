package com.ironhack.bankapp.controller.users.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import javax.validation.constraints.*;
import java.time.LocalDate;

import static com.ironhack.bankapp.utils.RegExp.*;

public class AccountHolderDTO {
    @NotBlank
    @Pattern(regexp = VALID_NAME, message = "Not a valid name")
    @Size(max = 100)
    private String name;

    @NotBlank
    @Pattern(regexp = VALID_USERNAME, message = "Not a valid username")
    private String username;

    @NotBlank
    @Pattern(regexp = VALID_PASSWORD, message = "Not a valid password")
    private String password;

    @Past
    @NotNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birth;
    @NotNull
    @Size(max = 40)
    private String primaryCountry;
    @NotNull
    @Size(max = 60)
    private String primaryCity;
    @NotNull
    @Digits(integer = 5, fraction = 0, message = "Not a valid postal code")
    private Integer primaryPostalCode;
    @NotNull
    @Size(max = 100)
    private String primaryStreet;

    @Size(max = 40)
    private String mailingCountry;
    @Size(max = 60)
    private String mailingCity;
    @Digits(integer = 5, fraction = 0, message = "Not a valid postal code")
    private Integer mailingPostalCode;
    @Size(max = 100)
    private String mailingStreet;

    public AccountHolderDTO() {
    }

    public AccountHolderDTO(@NotBlank @Pattern(regexp = VALID_NAME, message = "Not a valid name") @Size(max = 100) String name,
                            @NotBlank @Pattern(regexp = VALID_USERNAME, message = "Not a valid username") String username,
                            @NotBlank @Pattern(regexp = VALID_PASSWORD, message = "Not a valid password") String password,
                            @Past @NotNull LocalDate birth,
                            @NotNull @Size(max = 40) String primaryCountry,
                            @NotNull @Size(max = 60) String primaryCity,
                            @NotNull @Digits(integer = 5, fraction = 0, message = "Not a valid postal code") Integer primaryPostalCode,
                            @NotNull @Size(max = 100) String primaryStreet) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.birth = birth;
        this.primaryCountry = primaryCountry;
        this.primaryCity = primaryCity;
        this.primaryPostalCode = primaryPostalCode;
        this.primaryStreet = primaryStreet;
    }

    public AccountHolderDTO(@NotBlank @Pattern(regexp = VALID_NAME, message = "Not a valid name") @Size(max = 100) String name,
                            @NotBlank @Pattern(regexp = VALID_USERNAME, message = "Not a valid username") String username,
                            @NotBlank @Pattern(regexp = VALID_PASSWORD, message = "Not a valid password") String password,
                            @Past @NotNull LocalDate birth,
                            @NotNull @Size(max = 40) String primaryCountry,
                            @NotNull @Size(max = 60) String primaryCity,
                            @NotNull @Digits(integer = 5, fraction = 0, message = "Not a valid postal code") Integer primaryPostalCode,
                            @NotNull @Size(max = 100) String primaryStreet,
                            @Size(max = 40) String mailingCountry,
                            @Size(max = 60) String mailingCity,
                            @Digits(integer = 5, fraction = 0, message = "Not a valid postal code") Integer mailingPostalCode,
                            @Size(max = 100) String mailingStreet) {

        this.name = name;
        this.username = username;
        this.password = password;
        this.birth = birth;
        this.primaryCountry = primaryCountry;
        this.primaryCity = primaryCity;
        this.primaryPostalCode = primaryPostalCode;
        this.primaryStreet = primaryStreet;
        // mailing address will only be stored if all fields are not null
        if (mailingCity!=null&&mailingCountry!=null&&mailingStreet!=null&&mailingPostalCode!=null){
            this.mailingCountry = mailingCountry;
            this.mailingCity = mailingCity;
            this.mailingPostalCode = mailingPostalCode;
            this.mailingStreet = mailingStreet;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    public String getPrimaryCountry() {
        return primaryCountry;
    }

    public void setPrimaryCountry(String primaryCountry) {
        this.primaryCountry = primaryCountry;
    }

    public String getPrimaryCity() {
        return primaryCity;
    }

    public void setPrimaryCity(String primaryCity) {
        this.primaryCity = primaryCity;
    }

    public Integer getPrimaryPostalCode() {
        return primaryPostalCode;
    }

    public void setPrimaryPostalCode(Integer primaryPostalCode) {
        this.primaryPostalCode = primaryPostalCode;
    }

    public String getPrimaryStreet() {
        return primaryStreet;
    }

    public void setPrimaryStreet(String primaryStreet) {
        this.primaryStreet = primaryStreet;
    }

    public String getMailingCountry() {
        return mailingCountry;
    }

    public void setMailingCountry(String mailingCountry) {
        this.mailingCountry = mailingCountry;
    }

    public String getMailingCity() {
        return mailingCity;
    }

    public void setMailingCity(String mailingCity) {
        this.mailingCity = mailingCity;
    }

    public Integer getMailingPostalCode() {
        return mailingPostalCode;
    }

    public void setMailingPostalCode(Integer mailingPostalCode) {
        this.mailingPostalCode = mailingPostalCode;
    }

    public String getMailingStreet() {
        return mailingStreet;
    }

    public void setMailingStreet(String mailingStreet) {
        this.mailingStreet = mailingStreet;
    }
}
