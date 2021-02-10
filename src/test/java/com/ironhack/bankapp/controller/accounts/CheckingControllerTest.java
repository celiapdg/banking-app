package com.ironhack.bankapp.controller.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.bankapp.classes.Address;
import com.ironhack.bankapp.controller.accounts.dto.CheckingDTO;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.users.Role;
import com.ironhack.bankapp.repository.accounts.CheckingRepository;
import com.ironhack.bankapp.repository.accounts.StudentCheckingRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CheckingControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    AccountHolderRepository accountHolderRepository;
    @Autowired
    CheckingRepository checkingRepository;
    @Autowired
    StudentCheckingRepository studentCheckingRepository;
    @Autowired
    RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        AccountHolder accountHolder1 = new AccountHolder("Celia", "cecece", "cecece",
                LocalDate.of(1999, 9, 27),
                new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63"),
                new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63"));
        accountHolder1.addRole(new Role("USER", accountHolder1));
        AccountHolder accountHolder2 = new AccountHolder("Celia", "cacito", "cecece",
                LocalDate.of(1995, 9, 27),
                new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63"),
                new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63"));
        accountHolder1.addRole(new Role("USER", accountHolder1));
        accountHolderRepository.saveAll(List.of(accountHolder1, accountHolder2));
    }

    @AfterEach
    void tearDown() {
        checkingRepository.deleteAll();
        studentCheckingRepository.deleteAll();
        roleRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void create_noSecondaryOwner() throws Exception {
        List<AccountHolder> accounts = accountHolderRepository.findAll();
        CheckingDTO checkingDTO = new CheckingDTO(new BigDecimal(2000),
                accounts.get(0).getId(),
                null, "a4b5");
        String body = objectMapper.writeValueAsString(checkingDTO);
        MvcResult result = mockMvc.perform(
                post("/new-checking")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        System.out.println(result.getResponse().getContentAsString());

        assertTrue(result.getResponse().getContentAsString().contains("cecece"));
        assertFalse(result.getResponse().getContentAsString().contains("cacito"));

    }

    @Test
    void create_twoOwnersprimaryYoung() throws Exception {
        List<AccountHolder> accounts = accountHolderRepository.findAll();
        CheckingDTO checkingDTO = new CheckingDTO(new BigDecimal(2000),
                accounts.get(0).getId(),
                accounts.get(1).getId(), "a4b5");
        String body = objectMapper.writeValueAsString(checkingDTO);
        MvcResult result = mockMvc.perform(
                post("/new-checking")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        System.out.println("kesesto " + result.getResponse().getContentAsString());

        assertTrue(result.getResponse().getContentAsString().contains("cecece"));
        assertTrue(result.getResponse().getContentAsString().contains("cacito"));
        assertFalse(result.getResponse().getContentAsString().contains("monthlyMaintenance"));
    }

    @Test
    void create_twoOwnersprimaryOld() throws Exception {
        List<AccountHolder> accounts = accountHolderRepository.findAll();
        CheckingDTO checkingDTO = new CheckingDTO(new BigDecimal(2000),
                accounts.get(1).getId(),
                accounts.get(0).getId(), "a4b5");
        String body = objectMapper.writeValueAsString(checkingDTO);
        MvcResult result = mockMvc.perform(
                post("/new-checking")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        System.out.println("kesesto " + result.getResponse().getContentAsString());

        assertTrue(result.getResponse().getContentAsString().contains("cecece"));
        assertTrue(result.getResponse().getContentAsString().contains("cacito"));
        assertTrue(result.getResponse().getContentAsString().contains("monthlyMaintenance"));
    }
}