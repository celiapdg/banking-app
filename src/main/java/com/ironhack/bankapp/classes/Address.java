package com.ironhack.bankapp.classes;

import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Embeddable
public class Address {
    @NotBlank(message = "Country is required")
    private String country;
    @NotBlank(message = "City is required")
    private String city;
    @Digits(integer = 5, fraction = 0, message = "Not a valid postal code")
    private Integer postalCode;
    @NotBlank(message = "Street name is required")
    private String street;
    @Digits(integer = 3, fraction = 0, message = "Not a valid street number")
    @Min(1)
    private Integer streetNumber;

    public Address() {
    }

    public Address(@NotBlank(message = "Country is required") String country,
                   @NotBlank(message = "City is required") String city,
                   @Digits(integer = 5, fraction = 0, message = "Not a valid postal code") Integer postalCode,
                   @NotBlank(message = "Street name is required") String street,
                   @Digits(integer = 3, fraction = 0, message = "Not a valid street number") @Min(1) Integer streetNumber) {
        this.country = country;
        this.city = city;
        this.postalCode = postalCode;
        this.street = street;
        this.streetNumber = streetNumber;
    }

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

    public Integer getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(Integer streetNumber) {
        this.streetNumber = streetNumber;
    }
}
