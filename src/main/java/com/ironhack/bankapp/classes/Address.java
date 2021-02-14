package com.ironhack.bankapp.classes;

import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

@Embeddable
public class Address {
    private String country;
    private String city;
    private Integer postalCode;
    private String street;

    /**------------------------Constructors------------------------**/

    /**
     * Default class constructor
     **/
    public Address() {
    }

    /**
     * class constructor specifying country, city, postal code and street
     **/
    public Address(String country,
                   String city,
                   Integer postalCode,
                   String street) {
        this.country = country;
        this.city = city;
        this.postalCode = postalCode;
        this.street = street;
    }


    /**------------------------Getters and Setters------------------------**/

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

}
