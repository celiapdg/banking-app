package com.ironhack.bankapp.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

import static com.ironhack.bankapp.utils.RegExp.VALID_NAME;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @NotBlank
    @Pattern(regexp = VALID_NAME, message = "Not a valid name")
    protected String name;
    @NotBlank
    @Size(min = 4, max = 36)    // añadir que no pueda empezar por un número (exp reg)
    protected String username;
    @NotBlank
    @Size(min = 6, max = 20)    // mensaje indicando condiciones (exp regular?)
    protected String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected Set<Role> roles;

    public User() {
    }

    public User(@NotNull @Pattern(regexp = VALID_NAME) String name,
                @NotNull @Size(min = 4, max = 36) String username,
                @NotNull @Size(min = 6, max = 20) String password) {
        this.name = name;
        this.username = username;
        this.password = password;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }
}