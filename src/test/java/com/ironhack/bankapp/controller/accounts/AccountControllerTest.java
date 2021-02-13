package com.ironhack.bankapp.controller.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.bankapp.classes.Address;
import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.TransactionDTO;
import com.ironhack.bankapp.controller.accounts.dto.BalanceDTO;
import com.ironhack.bankapp.model.accounts.*;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.users.Role;
import com.ironhack.bankapp.model.users.ThirdParty;
import com.ironhack.bankapp.repository.TransactionRepository;
import com.ironhack.bankapp.repository.accounts.*;
import com.ironhack.bankapp.repository.users.AccountHolderRepository;
import com.ironhack.bankapp.repository.users.RoleRepository;
import com.ironhack.bankapp.repository.users.ThirdPartyRepository;
import com.ironhack.bankapp.service.impl.AccountService;
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
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    CheckingRepository checkingRepository;
    @Autowired
    SavingsRepository savingsRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    ThirdPartyRepository thirdPartyRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    CreditCardRepository creditCardRepository;
    @Autowired
    StudentCheckingRepository studentCheckingRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        Address address = new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63");
        AccountHolder accountHolder1 = new AccountHolder("Celia Lumbreras", "cecece", "cecece",
                LocalDate.of(1995, 9, 27), address, address);
        AccountHolder accountHolder2 = new AccountHolder("Pepa Pig", "pepa", "pig",
                LocalDate.of(1999, 9, 27), address, address);
        accountHolderRepository.saveAll(List.of(accountHolder1,accountHolder2));

        ThirdParty thirdParty1 = new ThirdParty("Fiezta", "fiezta");
        thirdPartyRepository.save(thirdParty1);

        Checking checking = new Checking(new Money(new BigDecimal(2030)),
                accountHolder1, "caca");
        checkingRepository.save(checking);

        Savings savings = new Savings(new Money(new BigDecimal(1600)),
                accountHolder1, "caca", new Money(new BigDecimal(100)),
                new BigDecimal(0.1));
        savings.setLastInterestDate(LocalDate.of(2020,1,1));
        savingsRepository.save(savings);

        StudentChecking studentChecking = new StudentChecking(new Money(new BigDecimal(2030)),
                accountHolder2, "caca");
        studentCheckingRepository.save(studentChecking);

        CreditCard creditCard = new CreditCard(new Money(new BigDecimal(1600)),
                accountHolder2, new BigDecimal(0.12), new Money(new BigDecimal(100)));
        creditCard.setLastInterestDate(LocalDate.of(2020,1,1));
        creditCard.setSecondaryOwner(accountHolder1);
        creditCardRepository.save(creditCard);
    }


    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
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

        System.out.println(accountHolder.getAllAccounts().get(1));

        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accountHolder.getAllAccounts().get(1).getId())
                        .with(user(accountHolder.getUsername())
                                .password(accountHolder.getPassword())
                                .roles("ADMIN")))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
        System.out.println(accountHolderRepository.findAll().get(0).getAllAccounts().get(1));

        assertTrue(result.getResponse().getContentAsString().contains("1760.00"));
    }

    @Test
    void checkBalance_noUser_unauthorized() throws Exception {
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accountHolder.getAllAccounts().get(1).getId()))
                .andExpect(status().isUnauthorized()).andReturn();

        assertThrows(ResponseStatusException.class, () -> accountService.checkBalance(accountHolder.getAllAccounts().get(1).getId(),null));
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

    @Test
    void transfer() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(0).getId(),
                accounts.get(1).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("cecece")
                                .password("cucucu")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isCreated()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void transfer_admin_forbidden() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(0).getId(),
                accounts.get(1).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("cecece")
                                .password("cucucu")
                                .roles("ADMIN")))
                .andExpect(status().isForbidden()).andReturn();
    }

    @Test
    void withdraw_validCredentials() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(0).getId(),
                thirdParties.get(0).getId(), "Fiezta", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/fiezta/caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("cecece")
                                .password("cucucu")
                                .roles("ADMIN")))
                .andExpect(status().isCreated()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void withdraw_notEnoughFunds() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(0).getId(),
                thirdParties.get(0).getId(), "Fiezta", new BigDecimal("100000"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/fiezta/caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("cecece")
                                .password("cucucu")
                                .roles("ADMIN")))
                .andExpect(status().isExpectationFailed()).andReturn();
    }

    @Test
    void withdraw_wrongSecretKey() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(0).getId(),
                thirdParties.get(0).getId(), "Fiezta", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/fiezta/cucu")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("cecece")
                                .password("cucucu")
                                .roles("ADMIN")))
                .andExpect(status().isUnauthorized()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void withdraw_wrongHashedKey() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(0).getId(),
                thirdParties.get(0).getId(), "Fiezta", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/pedozz/caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("cecece")
                                .password("cucucu")
                                .roles("ADMIN")))
                .andExpect(status().isUnauthorized()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void deposit_validCredentials() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId(),
                accounts.get(0).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/fiezta/caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("cecece")
                                .password("cucucu")
                                .roles("ADMIN")))
                .andExpect(status().isCreated()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void deposit_notValidName() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId(),
                accounts.get(0).getId(), "Fiezta", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("cecece")
                                .password("cucucu")
                                .roles("ADMIN")))
                .andExpect(status().isExpectationFailed()).andReturn();
    }

    @Test
    void deposit_wrongSecretKey() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId(),
                accounts.get(0).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/fiezta?secretKey=cucu")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("cecece")
                                .password("cucucu")
                                .roles("ADMIN")))
                .andExpect(status().isUnauthorized()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void deposit_noSecretKey() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId(),
                accounts.get(0).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/fiezta")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("cecece")
                                .password("cucucu")
                                .roles("ADMIN")))
                .andExpect(status().isUnauthorized()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void deposit_wrongHashedKey() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId(),
                accounts.get(0).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/pedozz/caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("cecece")
                                .password("cucucu")
                                .roles("ADMIN")))
                .andExpect(status().isUnauthorized()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }
}