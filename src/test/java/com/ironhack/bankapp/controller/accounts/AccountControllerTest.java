package com.ironhack.bankapp.controller.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.bankapp.classes.Address;
import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.accounts.dto.BalanceDTO;
import com.ironhack.bankapp.model.accounts.Checking;
import com.ironhack.bankapp.model.accounts.Savings;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.users.Role;
import com.ironhack.bankapp.repository.accounts.CheckingRepository;
import com.ironhack.bankapp.repository.accounts.SavingsRepository;
import com.ironhack.bankapp.repository.users.AccountHolderRepository;
import com.ironhack.bankapp.repository.users.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    AccountHolderRepository accountHolderRepository;
    @Autowired
    RoleRepository roleRepository;
//    @Autowired
//    AccountRepository accountRepository;
    @Autowired
    CheckingRepository checkingRepository;
    @Autowired
    SavingsRepository savingsRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        AccountHolder accountHolder1 = new AccountHolder("Celia Lumbreras", "cecece", "cecece",
                LocalDate.of(1995, 9, 27),
                new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63"),
                new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63"));
        accountHolder1.addRole(new Role("USER", accountHolder1));
        accountHolderRepository.save(accountHolder1);

        Checking checking = new Checking(new Money(new BigDecimal(2030)),
                accountHolder1, "avs4");
        checkingRepository.save(checking);

        Savings savings = new Savings(new Money(new BigDecimal(1600)),
                accountHolder1, "bdg5", new Money(new BigDecimal(100)),
                new BigDecimal(0.1));
        savings.setLastInterestDate(LocalDate.of(2020,1,1));
        savingsRepository.save(savings);
    }


    @AfterEach
    void tearDown() {
        savingsRepository.deleteAll();
        checkingRepository.deleteAll();
        roleRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void checkBalance_userAccount() throws Exception {
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accountHolder.getAllAccounts().get(0).getId())
                        .with(user(accountHolder.getUsername())
                                .password(accountHolder.getPassword())
                                .roles("ACCOUNT_HOLDER")))
                        .andReturn();

        System.out.println(result.getResponse().getContentAsString());

        assertTrue(result.getResponse().getContentAsString().contains("2030.00"));
    }

    @Test
    void checkBalance_admin_appliedInterest() throws Exception {
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accountHolder.getAllAccounts().get(1).getId())
                        .with(user(accountHolder.getUsername())
                                .password(accountHolder.getPassword())
                                .roles("ADMIN")))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

        assertTrue(result.getResponse().getContentAsString().contains("1760.00"));
    }

    @Test
    void checkBalance_noUser_unauthorized() throws Exception {
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accountHolder.getAllAccounts().get(1).getId()))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void modifyBalance() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO(new BigDecimal(1234));
        String body = objectMapper.writeValueAsString(balanceDTO);
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                patch("/modify-balance/"+accountHolder.getAllAccounts().get(0).getId())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(accountHolder.getUsername())
                                .password(accountHolder.getPassword())
                                .roles("ADMIN")))
                .andExpect(status().isNoContent()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("1234.00"));
    }

    @Test
    void modifyBalance_belowMinimum() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO(new BigDecimal(200));
        String body = objectMapper.writeValueAsString(balanceDTO);
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                patch("/modify-balance/"+accountHolder.getAllAccounts().get(0).getId())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(accountHolder.getUsername())
                                .password(accountHolder.getPassword())
                                .roles("ADMIN")))
                .andExpect(status().isNoContent()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("160.00"));
    }

    @Test
    void modifyBalance_noAdmin_forbidden() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO(new BigDecimal(1234));
        String body = objectMapper.writeValueAsString(balanceDTO);
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                patch("/modify-balance/"+accountHolder.getAllAccounts().get(0).getId())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(accountHolder.getUsername())
                                .password(accountHolder.getPassword())
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isForbidden()).andReturn();
    }

    @Test
    void modifyBalance_noAuth_unauthorized() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO(new BigDecimal(1234));
        String body = objectMapper.writeValueAsString(balanceDTO);
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                patch("/modify-balance/"+accountHolder.getAllAccounts().get(0).getId())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();
    }
}