package com.ironhack.bankapp.controller.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.bankapp.classes.Address;
import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.TransactionDTO;
import com.ironhack.bankapp.controller.accounts.dto.BalanceDTO;
import com.ironhack.bankapp.enums.Status;
import com.ironhack.bankapp.model.Transaction;
import com.ironhack.bankapp.model.accounts.*;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.users.Admin;
import com.ironhack.bankapp.model.users.ThirdParty;
import com.ironhack.bankapp.repository.TransactionRepository;
import com.ironhack.bankapp.repository.accounts.*;
import com.ironhack.bankapp.repository.users.AccountHolderRepository;
import com.ironhack.bankapp.repository.users.AdminRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Autowired
    AdminRepository adminRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        Address address = new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63");
        AccountHolder accountHolder1 = new AccountHolder("Celia Lumbreras", "cecece", "cecece",
                LocalDate.of(1995, 9, 27), address, address);
        AccountHolder accountHolder2 = new AccountHolder("Pepa Pig", "pepa", "pig",
                LocalDate.of(1999, 9, 27), address, address);
        AccountHolder accountHolder3 = new AccountHolder("WRONG ACC", "wrongAccount", "IAMSAD",
                LocalDate.of(1999, 9, 27), address, address);
        accountHolderRepository.saveAll(List.of(accountHolder1,accountHolder2,accountHolder3));

        Admin admin = new Admin("Cece", "cece", "cece");
        adminRepository.save(admin);

        ThirdParty thirdParty1 = new ThirdParty("Fiezta", "fiezta");
        thirdPartyRepository.save(thirdParty1);

        Checking checking = new Checking(new Money(new BigDecimal(350)),
                accountHolder1, "caca");
        checking.setLastMaintenanceDate(LocalDate.now().minusMonths(13));
        checkingRepository.save(checking);

        Savings savings = new Savings(new Money(new BigDecimal(1600)),
                accountHolder1, "caca", new Money(new BigDecimal(100)),
                new BigDecimal("0.1"));
        savings.setLastInterestDate(LocalDate.now().minusMonths(13));
        savingsRepository.save(savings);

        StudentChecking studentChecking = new StudentChecking(new Money(new BigDecimal(2030)),
                accountHolder2, "caca");
        studentCheckingRepository.save(studentChecking);

        CreditCard creditCard = new CreditCard(new Money(new BigDecimal(1600)),
                accountHolder2, new BigDecimal("0.12"), new Money(new BigDecimal(100)));
        creditCard.setLastInterestDate(LocalDate.now().minusMonths(13));
        creditCard.setSecondaryOwner(accountHolder1);
        creditCardRepository.save(creditCard);

        Savings savings2 = new Savings(new Money(new BigDecimal(1600)),
                accountHolder2, "caca", new Money(new BigDecimal(1000)),
                new BigDecimal("0.1"));
        savingsRepository.save(savings2);

        Transaction transaction1 = new Transaction(checking, savings, new Money(new BigDecimal("100")), "De mí, pa ti",
                LocalDateTime.now().minusHours(24));
        Transaction transaction2 = new Transaction(savings2, checking, new Money(new BigDecimal("700")), "De mí, pa ti",
                LocalDateTime.now().minusHours(28));
        Transaction transaction3 = new Transaction(checking, savings, new Money(new BigDecimal("90")), "De mí, pa ti",
                LocalDateTime.now());
        Transaction transaction4 = new Transaction(checking, savings2, new Money(new BigDecimal("40")), "De mí, pa ti",
                LocalDateTime.now());
        transactionRepository.saveAll(List.of(transaction1, transaction2, transaction3, transaction4));
    }


    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        creditCardRepository.deleteAll();
        studentCheckingRepository.deleteAll();
        savingsRepository.deleteAll();
        checkingRepository.deleteAll();
        accountRepository.deleteAll();
        roleRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        accountHolderRepository.deleteAll();
        adminRepository.deleteAll();
    }

    /****************** CHECK BALANCE *******************/
    @Test
    void checkBalance_userAccount_applyMonthlyMaintenance() throws Exception {
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accountHolder.getAllAccounts().get(0).getId())
                        .with(user(accountHolder.getUsername())
                                .password(accountHolder.getPassword())
                                .roles("ACCOUNT_HOLDER")))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

        assertTrue(result.getResponse().getContentAsString().contains("154.00"));
    }

    @Test
    void checkBalance_admin_appliedSavingsInterest() throws Exception {
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

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
    void checkBalance_secondaryOwner_appliedInterest() throws Exception {
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accountHolder.getAllAccounts().get(2).getId())
                        .with(user(accountHolder.getUsername())
                                .password(accountHolder.getPassword())
                                .roles("ACCOUNT_HOLDER")))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

        assertTrue(result.getResponse().getContentAsString().contains("1820.94"));
    }


    @Test
    void checkBalance_wrongAccountID_notFound() throws Exception {
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accountHolder.getAllAccounts().get(2).getId()+25L)
                        .with(user(accountHolder.getUsername())
                                .password(accountHolder.getPassword())
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isNotFound()).andReturn();
    }


    @Test
    void checkBalance_noAuth_unauthorized() throws Exception {
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accountHolder.getAllAccounts().get(1).getId()))
                .andExpect(status().isUnauthorized()).andReturn();
    }


    @Test
    void checkBalance_wrongAccountHolder_unauthorized() throws Exception {
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accountHolder.getAllAccounts().get(2).getId())
                        .with(user("wrongAccount")
                                .password("IAMSAD")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isForbidden()).andReturn();
    }


    @Test
    void checkBalance_fakeUser_userNotFound() throws Exception {
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                get("/check-balance/"+accountHolder.getAllAccounts().get(2).getId())
                        .with(user("kgjsdkjgh")
                                .password("IDONOTEXIST")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isNotFound()).andReturn();
    }

    /****************** MODIFY BALANCE *******************/
    @Test
    void modifyBalance_balanceChanges() throws Exception {
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
    void modifyBalance_wrongAccountID_notFound() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO(new BigDecimal(1234));
        String body = objectMapper.writeValueAsString(balanceDTO);
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                patch("/modify-balance/"+accountHolder.getAllAccounts().get(0).getId()+234L)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(accountHolder.getUsername())
                                .password(accountHolder.getPassword())
                                .roles("ADMIN")))
                .andExpect(status().isNotFound()).andReturn();
    }


    @Test
    void modifyBalance_belowMinimum_penaltyFeeApplied() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO(new BigDecimal(200));
        String body = objectMapper.writeValueAsString(balanceDTO);
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);
        Admin admin = adminRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                patch("/modify-balance/"+accountHolder.getAllAccounts().get(0).getId())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(admin.getUsername())
                                .password(admin.getPassword())
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
    void modifyBalance_fakeUser_notFound() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO(new BigDecimal(1234));
        String body = objectMapper.writeValueAsString(balanceDTO);
        AccountHolder accountHolder = accountHolderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                patch("/modify-balance/"+accountHolder.getAllAccounts().get(0).getId())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("kgjsdkjgh")
                        .password("IDONOTEXIST")
                        .roles("ADMIN")))
                .andExpect(status().isNotFound()).andReturn();
    }


    /****************** TRANSFER *******************/
    @Test
    void transfer() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                accounts.get(3).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepa")
                                .password("pig")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isCreated()).andReturn();

        assertEquals(new BigDecimal(1930).compareTo(accountRepository.findAll().get(2).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(1700).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
        assertTrue(result.getResponse().getContentAsString().contains("1930.00"));
        assertTrue(result.getResponse().getContentAsString().contains("100.00"));
        assertTrue(result.getResponse().getContentAsString().contains("Pepa Pig"));
    }

    @Test
    void transfer_notEnoughBalance_expectationFailed() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                accounts.get(3).getId(), "Celia Lumbreras", new BigDecimal("3000"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepa")
                                .password("pig")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isExpectationFailed()).andReturn();

        assertEquals(new BigDecimal(2030).compareTo(accountRepository.findAll().get(2).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(1600).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
    }


    @Test
    void transfer_underMinimumBalance_penaltyFeeApplied() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(4).getId(),
                accounts.get(3).getId(), "Celia Lumbreras", new BigDecimal("700"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepa")
                                .password("pig")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isCreated()).andReturn();

        assertTrue(((Savings) accountRepository.findAll().get(4)).isBelowMinimumBalance());
        assertEquals(new BigDecimal(860).compareTo(accountRepository.findAll().get(4).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(2300).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
    }


    @Test
    void transfer_frozenAccount_unavailableForLegalReasons() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        accountService.freeze(accounts.get(0));
        accountRepository.save(accounts.get(0));
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(0).getId(),
                accounts.get(3).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("cecece")
                                .password("cucucu")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isUnavailableForLegalReasons()).andReturn();

        assertEquals(new BigDecimal(350).compareTo(accountRepository.findAll().get(0).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(1600).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
    }


    @Test
    void transfer_noAuth_unauthorized() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                accounts.get(3).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();

        assertEquals(new BigDecimal(2030).compareTo(accountRepository.findAll().get(2).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(1600).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
    }


    @Test
    void transfer_admin_forbidden() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                accounts.get(3).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

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

        assertEquals(new BigDecimal(2030).compareTo(accountRepository.findAll().get(2).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(1600).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
    }


    @Test
    void transfer_wrongUser_notFound() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                accounts.get(3).getId(), "Pepa Pig", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("ETOTAMAL")
                                .password("pig")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isNotFound()).andReturn();

        assertEquals(new BigDecimal(2030).compareTo(accountRepository.findAll().get(2).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(1600).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
    }


    @Test
    void transfer_wrongAccountHolder_forbidden() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                accounts.get(3).getId(), "Pepa Pig", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("wrongAccount")
                                .password("pig")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isForbidden()).andReturn();

        assertEquals(new BigDecimal(2030).compareTo(accountRepository.findAll().get(2).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(1600).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
    }


    @Test
    void transfer_notEnoughFunds_ExpectationFailed() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                accounts.get(3).getId(), "Celia Lumbreras", new BigDecimal("1000000"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepa")
                                .password("pig")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isExpectationFailed()).andReturn();

        assertEquals(new BigDecimal(2030).compareTo(accountRepository.findAll().get(2).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(1600).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
    }

    @Test
    void transfer_wrongName_ExpectationFailed() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                accounts.get(3).getId(), "ETO TA MAL", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepa")
                                .password("pig")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isExpectationFailed()).andReturn();

        assertEquals(new BigDecimal(2030).compareTo(accountRepository.findAll().get(2).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(1600).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
    }


    @Test
    void transfer_wrongOriginAccount_notFound() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId()+10L,
                accounts.get(3).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepa")
                                .password("pig")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isNotFound()).andReturn();

        assertEquals(new BigDecimal(2030).compareTo(accountRepository.findAll().get(2).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(1600).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
    }


    @Test
    void transfer_wrongDestinationAccount_notFound() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                accounts.get(3).getId()+10L, "Pepa Pig", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepa")
                                .password("pig")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isNotFound()).andReturn();

        assertEquals(new BigDecimal(2030).compareTo(accountRepository.findAll().get(2).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(1600).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
    }


    @Test
    void transfer_fraudOrigin_notAcceptable() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(4).getId(),
                accounts.get(3).getId(), "Pepa Pig", new BigDecimal("1500"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepa")
                                .password("pig")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isNotAcceptable()).andReturn();

        System.out.println("\u001B[31m\n\nSEÑORAS Y SEÑORES, \n\nPEPA PIG \n\nHA COMETIDO FRAUDE\n\n\u001B[39m");
        assertEquals(Status.FROZEN, ((Savings) accountRepository.findAll().get(4)).getStatus());
        assertEquals(new BigDecimal(1600).compareTo(accountRepository.findAll().get(4).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(1600).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
    }


    @Test
    void transfer_fraudDestination_notAcceptable() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(3).getId(),
                accounts.get(0).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("cecece")
                                .password("cucucu")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isNotAcceptable()).andReturn();

        assertEquals(Status.FROZEN, ((Checking) accountRepository.findAll().get(0)).getStatus());
        assertEquals(new BigDecimal(1600).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(350).compareTo(accountRepository.findAll().get(0).getBalance().getAmount()), 0);
    }


    @Test
    void transfer_originEqualsDestination_IAmATeapot() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(3).getId(),
                accounts.get(3).getId(), "Celia Lumbreras", new BigDecimal("10"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/transfer")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepa")
                                .password("pig")
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isIAmATeapot()).andReturn();

        assertEquals(new BigDecimal(2030).compareTo(accountRepository.findAll().get(2).getBalance().getAmount()), 0);
        assertEquals(new BigDecimal(1600).compareTo(accountRepository.findAll().get(3).getBalance().getAmount()), 0);
    }


    /****************** WITHDRAW *******************/
    @Test
    void withdraw_validCredentials() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                thirdParties.get(0).getId(), "Fiezta", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void withdraw_wrongAccountId_notFound() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId()+20L,
                thirdParties.get(0).getId(), "Fiezta", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();
    }


    @Test
    void withdraw_wrongThirdPartyId_notFound() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                thirdParties.get(0).getId()+10L, "Fiezta", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();
    }

    @Test
    void withdraw_wrongName_expectationFailed() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                thirdParties.get(0).getId(), "ETO TA MAL", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isExpectationFailed()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }


    @Test
    void withdraw_notEnoughFunds_ExpectationFailed() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                thirdParties.get(0).getId(), "Fiezta", new BigDecimal("100000"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isExpectationFailed()).andReturn();
    }


    @Test
    void withdraw_frozenOrigin_UnavailableForLegalReasons() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        accountService.freeze(accounts.get(0));
        accountRepository.save(accounts.get(0));
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(0).getId(),
                thirdParties.get(0).getId(), "Fiezta", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnavailableForLegalReasons()).andReturn();
    }


    @Test
    void withdraw_fraudOrigin_NotAcceptable() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(0).getId(),
                thirdParties.get(0).getId(), "Fiezta", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable()).andReturn();
    }


    @Test
    void withdraw_underMinimumBalance_penaltyFeeApplied() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(4).getId(),
                thirdParties.get(0).getId(), "Fiezta", new BigDecimal("700"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        assertTrue(((Savings) accountRepository.findAll().get(4)).isBelowMinimumBalance());
        assertEquals(new BigDecimal(860).compareTo(accountRepository.findAll().get(4).getBalance().getAmount()), 0);
    }


    @Test
    void withdraw_wrongSecretKey() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                thirdParties.get(0).getId(), "Fiezta", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/fiezta?secretKey=cucu")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }


    @Test
    void withdraw_noSecretKey() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                thirdParties.get(0).getId(), "Fiezta", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/fiezta")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void withdraw_noSecretKeyWithCreditCard_Created() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(3).getId(),
                thirdParties.get(0).getId(), "Fiezta", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/fiezta")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }


    @Test
    void withdraw_wrongHashedKey() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(accounts.get(2).getId(),
                thirdParties.get(0).getId(), "Fiezta", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/withdraw/pedozz?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }


    /****************** DEPOSIT *******************/
    @Test
    void deposit_validCredentials() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId(),
                accounts.get(2).getId(), "Pepa Pig", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }


    @Test
    void deposit_secondaryOwnerName() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId(),
                accounts.get(3).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();
    }

    @Test
    void deposit_wrongccountID_notFound() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId(),
                accounts.get(3).getId()+234, "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();
    }

    @Test
    void deposit_wrongThirdPartyID_notFound() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId()+234,
                accounts.get(3).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();
    }


    @Test
    void deposit_notValidName() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId(),
                accounts.get(3).getId(), "ETO TA MAL", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isExpectationFailed()).andReturn();
    }


    @Test
    void deposit_wrongSecretKey() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId(),
                accounts.get(2).getId(), "Pepa Pig", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/fiezta?secretKey=malmalmal")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void deposit_creditCard_ignoresSecretKeyAndCreatesTransaction() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId(),
                accounts.get(3).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/fiezta?secretKey=malmalmal")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void deposit_noSecretKey_Unauthorized() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId(),
                accounts.get(1).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/fiezta")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }


    @Test
    void deposit_fraudDestination_notAcceptable() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId(),
                accounts.get(0).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable()).andReturn();
    }


    @Test
    void deposit_frozenDestination_UnavailableForLegalReasons() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        accountService.freeze(accounts.get(0));
        accountRepository.save(accounts.get(0));
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        TransactionDTO transactionDTO = new TransactionDTO(thirdParties.get(0).getId(),
                accounts.get(0).getId(), "Celia Lumbreras", new BigDecimal("100"), "Un regalito");

        String body = objectMapper.writeValueAsString(transactionDTO);
        System.out.println(body);

        MvcResult result = mockMvc.perform(
                post("/deposit/fiezta?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnavailableForLegalReasons()).andReturn();
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
                post("/deposit/pedozz?secretKey=caca")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }


    @Test
    void unfreeze() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        accountService.freeze(accounts.get(0));
        accountRepository.save(accounts.get(0));
        Admin admin = adminRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                patch("/unfreeze/"+accounts.get(0).getId())
                        .with(user(admin.getUsername())
                                .password(admin.getPassword())
                                .roles("ADMIN")))
                .andExpect(status().isNoContent()).andReturn();

        assertFalse(accountRepository.findAll().get(0).isFrozen());
    }

    @Test
    void unfreeze_noAdmin_forbidden() throws Exception {
        List<Account> accounts = accountRepository.findAll();
        accountService.freeze(accounts.get(0));
        accountRepository.save(accounts.get(0));
        Admin admin = adminRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                patch("/unfreeze/"+accounts.get(0).getId())
                        .with(user(admin.getUsername())
                                .password(admin.getPassword())
                                .roles("ACCOUNT_HOLDER")))
                .andExpect(status().isForbidden()).andReturn();

        assertTrue(accountRepository.findAll().get(0).isFrozen());
    }



}