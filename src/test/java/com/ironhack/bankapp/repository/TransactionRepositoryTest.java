package com.ironhack.bankapp.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.bankapp.classes.Address;
import com.ironhack.bankapp.classes.Money;
import com.ironhack.bankapp.controller.users.dto.AccountHolderDTO;
import com.ironhack.bankapp.model.Transaction;
import com.ironhack.bankapp.model.accounts.Account;
import com.ironhack.bankapp.model.accounts.Checking;
import com.ironhack.bankapp.model.accounts.Savings;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.users.Role;
import com.ironhack.bankapp.repository.accounts.AccountRepository;
import com.ironhack.bankapp.repository.accounts.CheckingRepository;
import com.ironhack.bankapp.repository.accounts.SavingsRepository;
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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        AccountHolder accountHolder1 = new AccountHolder("Celia Patata", "cecece", "cecece",
                LocalDate.of(1995, 9, 27),
                new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63"),
                new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63"));
        accountHolder1.addRole(new Role("USER", accountHolder1));
        accountHolderRepository.save(accountHolder1);
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO("cece", "cecece", "cecece",
                LocalDate.of(1995, 9, 27),
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63",
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63");

        Checking checking = new Checking(new Money(new BigDecimal(2030)),
                accountHolder1, "avs4");
        checkingRepository.save(checking);

        Savings savings = new Savings(new Money(new BigDecimal(1600)),
                accountHolder1, "bdg5", new Money(new BigDecimal(100)),
                new BigDecimal(0.1));
        savingsRepository.save(savings);

        Transaction transaction1 = new Transaction(checking, savings, new Money(new BigDecimal("100")),
                LocalDateTime.of(2020, 10, 8, 1, 1, 7));
        Transaction transaction2 = new Transaction(checking, savings, new Money(new BigDecimal("150")),
                LocalDateTime.of(2020, 10, 10, 1, 1, 1));
        Transaction transaction3 = new Transaction(checking, savings, new Money(new BigDecimal("250")),
                LocalDateTime.now());
        Transaction transaction4 = new Transaction(checking, savings, new Money(new BigDecimal("150")),
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
    void findByOriginIdOrderByTransactionDateTimeDesc_yupi() {
        List<Account> accountList = accountRepository.findAll();
        List<Transaction> transactionList = transactionRepository.findByOriginIdOrderByTransactionDateTimeDesc(accountList.get(0).getId());

        Timestamp ts1 = Timestamp.valueOf(transactionList.get(0).getTransactionDateTime());
        Timestamp ts2 = Timestamp.valueOf(transactionList.get(1).getTransactionDateTime());


        System.out.println(ts1.getTime() - ts2.getTime());
        for (Transaction transaction: transactionList){
            System.out.println(transaction);
        }
    }

    @Test
    void groupTransactionsByDate(){
        List<Account> accountList = accountRepository.findAll();
        List<Object[]> prueba = transactionRepository.totalTransactionsLastSecond(accountList.get(0).getId());
        System.out.println(prueba.size());
        System.out.println(prueba.get(0).length);
        System.out.println(prueba.get(0)[0] + " " + prueba.get(0)[1] + " " + prueba.get(0)[2] + " " + prueba.get(0)[3] + " " + prueba.get(0)[4] + " " + prueba.get(0)[5]);
    }
}