package com.ironhack.bankapp.model.users;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.ironhack.bankapp.utils.RegExp.VALID_NAME;

// no entiendo bien esta clase, es un usuario de otro banco?
@Entity
public class ThirdParty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    protected String name;
    private String hashKey;

    public ThirdParty() {
    }

    public ThirdParty(String name,
                      String hashKey) {
        this.name = name;
        setHashKey(hashKey);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashKey() {
        return hashKey;
    }

    // automatically encrypts the hash key
    public void setHashKey(String hashKey) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.hashKey = passwordEncoder.encode(hashKey);
    }
}
