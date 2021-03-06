package com.ironhack.bankapp.controller.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.bankapp.classes.Address;
import com.ironhack.bankapp.controller.accounts.dto.SavingsDTO;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.users.Role;
import com.ironhack.bankapp.repository.users.AccountHolderRepository;
import com.ironhack.bankapp.repository.users.RoleRepository;
import com.ironhack.bankapp.repository.accounts.SavingsRepository;
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
class SavingsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    AccountHolderRepository accountHolderRepository;
    @Autowired
    SavingsRepository savingsRepository;
    @Autowired
    RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        AccountHolder accountHolder1 = new AccountHolder("Celia Lumbreras", "cecece", "cecece",
                LocalDate.of(1995, 9, 27),
                new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63"),
                new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63"));
        accountHolder1.addRole(new Role("USER", accountHolder1));
        AccountHolder accountHolder2 = new AccountHolder("Celia Lumbreras", "cacito", "cecece",
                LocalDate.of(1995, 9, 27),
                new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63"),
                new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63"));
        accountHolder1.addRole(new Role("USER", accountHolder1));
        accountHolderRepository.saveAll(List.of(accountHolder1, accountHolder2));
    }

    @AfterEach
    void tearDown() {
        savingsRepository.deleteAll();
        roleRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void create_twoOwners() throws Exception {
        List<AccountHolder> accountHolders = accountHolderRepository.findAll();

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal(2000),
                new BigDecimal(200), accountHolders.get(0).getId(),
                accountHolders.get(1).getId(), "a4b5",
                new BigDecimal(0.002));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/new-savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        System.out.println(result.getResponse().getContentAsString());

        assertTrue(result.getResponse().getContentAsString().contains("cecece"));
        assertTrue(result.getResponse().getContentAsString().contains("cacito"));
    }


    @Test
    void create_noSecondaryOwner() throws Exception {
        List<AccountHolder> accountHolders = accountHolderRepository.findAll();

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal(2000),
                new BigDecimal(200), accountHolders.get(0).getId(),
                null, "a4b5",
                new BigDecimal(0.002));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/new-savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        System.out.println(result.getResponse().getContentAsString());

        assertTrue(result.getResponse().getContentAsString().contains("cecece"));
        assertFalse(result.getResponse().getContentAsString().contains("cacito"));
    }


    @Test
    void create_wrongPrimaryOwnerID_notFound() throws Exception {
        List<AccountHolder> accountHolders = accountHolderRepository.findAll();

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal(2000),
                new BigDecimal(200), accountHolders.get(0).getId()+123L,
                null, "a4b5",
                new BigDecimal(0.002));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/new-savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andReturn();

    }


    @Test
    void create_wrongSecondaryOwnerID_accountCreated() throws Exception {
        List<AccountHolder> accountHolders = accountHolderRepository.findAll();

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal(2000),
                new BigDecimal(200), accountHolders.get(0).getId(),
                accountHolders.get(1).getId()+123L, "a4b5",
                new BigDecimal(0.002));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/new-savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        System.out.println(result.getResponse().getContentAsString());

        assertTrue(result.getResponse().getContentAsString().contains("cecece"));
        assertFalse(result.getResponse().getContentAsString().contains("cacito"));
    }


    @Test
    void create_negativeSecondaryOwnerID_badRequest() throws Exception {
        List<AccountHolder> accountHolders = accountHolderRepository.findAll();

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal(2000),
                new BigDecimal(200), accountHolders.get(0).getId(),
                -123L, "a4b5",
                new BigDecimal(0.002));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/new-savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();

    }


    @Test
    void create_negativeBalance_badRequest() throws Exception {
        List<AccountHolder> accountHolders = accountHolderRepository.findAll();

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal(-2000),
                new BigDecimal(200), accountHolders.get(0).getId(),
                null, "a4b5",
                new BigDecimal(0.002));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/new-savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();

    }


    @Test
    void create_nullPrimaryOwnerID_badRequest() throws Exception {
        List<AccountHolder> accountHolders = accountHolderRepository.findAll();

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal(2000),
                new BigDecimal(200), null,
                accountHolders.get(0).getId(), "a4b5",
                new BigDecimal(0.002));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/new-savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();

    }


    @Test
    void create_nullSecretKey_badRequest() throws Exception {
        List<AccountHolder> accountHolders = accountHolderRepository.findAll();

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal(2000),
                new BigDecimal(200), accountHolders.get(0).getId(),
                null, null,
                new BigDecimal(0.002));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/new-savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();

    }

    @Test
    void create_tooLowMinimumBalance_badRequest() throws Exception {
        List<AccountHolder> accountHolders = accountHolderRepository.findAll();

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal(2000),
                new BigDecimal(99), accountHolders.get(0).getId(),
                null, "a4b5",
                new BigDecimal(0.002));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/new-savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();

    }

    @Test
    void create_tooHighMinimumBalance_badRequest() throws Exception {
        List<AccountHolder> accountHolders = accountHolderRepository.findAll();

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal(2000),
                new BigDecimal(1001), accountHolders.get(0).getId(),
                null, "a4b5",
                new BigDecimal(0.002));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/new-savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();

    }


    @Test
    void create_negativeInterestRate_badRequest() throws Exception {
        List<AccountHolder> accountHolders = accountHolderRepository.findAll();

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal(2000),
                new BigDecimal(100), accountHolders.get(0).getId(),
                null, "a4b5",
                new BigDecimal(-0.001));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/new-savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();

    }


    @Test
    void create_tooHighInterestRate_badRequest() throws Exception {
        List<AccountHolder> accountHolders = accountHolderRepository.findAll();

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal(2000),
                new BigDecimal(100), accountHolders.get(0).getId(),
                null, "a4b5",
                new BigDecimal(0.51));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/new-savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();

    }


    @Test
    void create_balanceUnderMinimum_underMinimumBalanceTrue() throws Exception {
        List<AccountHolder> accountHolders = accountHolderRepository.findAll();

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal(200),
                new BigDecimal(500), accountHolders.get(0).getId(),
                null, "a4b5",
                new BigDecimal(0.002));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/new-savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        assertTrue(savingsRepository.findAll().get(0).isBelowMinimumBalance());


    }
}