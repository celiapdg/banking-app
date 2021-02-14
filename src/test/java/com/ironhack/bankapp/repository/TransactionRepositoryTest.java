package com.ironhack.bankapp.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.bankapp.classes.Address;
import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.users.dto.AccountHolderDTO;
import com.ironhack.bankapp.model.Transaction;
import com.ironhack.bankapp.model.accounts.*;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.users.Admin;
import com.ironhack.bankapp.model.users.Role;
import com.ironhack.bankapp.model.users.ThirdParty;
import com.ironhack.bankapp.repository.accounts.*;
import com.ironhack.bankapp.repository.users.AccountHolderRepository;
import com.ironhack.bankapp.repository.users.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionRepositoryTest {

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
    CreditCardRepository creditCardRepository;
    @Autowired
    StudentCheckingRepository studentCheckingRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Address address = new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63");
        AccountHolder accountHolder1 = new AccountHolder("Celia Lumbreras", "cecece", "cecece",
                LocalDate.of(1995, 9, 27), address, address);
        AccountHolder accountHolder2 = new AccountHolder("Pepa Pig", "pepa", "pig",
                LocalDate.of(1999, 9, 27), address, address);
        AccountHolder accountHolder3 = new AccountHolder("WRONG ACC", "wrongAccount", "IAMSAD",
                LocalDate.of(1999, 9, 27), address, address);
        accountHolderRepository.saveAll(List.of(accountHolder1,accountHolder2,accountHolder3));

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

        Transaction transaction1 = new Transaction(checking, savings, new Money(new BigDecimal("200")), "De mí, pa ti",
                LocalDateTime.now().minusHours(28));
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
        savingsRepository.deleteAll();
        checkingRepository.deleteAll();
        roleRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }


    @Test
    void maxAmountInADayInTheHistoryOfMankindFromTheBeginningOfTime(){
        List<Account> accountList = accountRepository.findAll();
        List<Object[]> maxTotal = transactionRepository.maxAmountInADayInTheHistoryOfMankindFromTheBeginningOfTime(accountList.get(0).getId());

        assertEquals(((BigDecimal) maxTotal.get(0)[1]).compareTo(new BigDecimal(200)), 0);

        maxTotal = transactionRepository.maxAmountInADayInTheHistoryOfMankindFromTheBeginningOfTime(accountList.get(4).getId());
        assertEquals(((BigDecimal) maxTotal.get(0)[1]).compareTo(new BigDecimal(700)), 0);
    }

    @Test
    void totalAmountLastDay(){
        List<Account> accountList = accountRepository.findAll();
        List<Object[]> totalLastDay = transactionRepository.totalAmountLastDay(accountList.get(0).getId());

        assertEquals(((BigDecimal) totalLastDay.get(0)[1]).compareTo(new BigDecimal(130)), 0);

        totalLastDay = transactionRepository.totalAmountLastDay(accountList.get(4).getId());
        assertEquals(totalLastDay.get(0)[1],null);
    }

    @Test
    void totalTransactionsLastSecond(){
        List<Account> accountList = accountRepository.findAll();

        List<Object[]> prueba = transactionRepository.transactionsLastSecond(accountList.get(0).getId());
        assertEquals(2, prueba.size());
    }
}