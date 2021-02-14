package com.ironhack.bankapp.controller.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.bankapp.classes.Address;
import com.ironhack.bankapp.controller.users.dto.AccountHolderDTO;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.model.users.Role;
import com.ironhack.bankapp.repository.users.AccountHolderRepository;
import com.ironhack.bankapp.repository.users.RoleRepository;
import com.ironhack.bankapp.repository.users.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountHolderControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    AccountHolderRepository accountHolderRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        AccountHolder accountHolder1 = new AccountHolder("Celia Lumbreras", "cecece", "cecece",
                LocalDate.of(1995, 9, 27),
                new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63"),
                new Address("Ezpania", "aqui", 23456, "wiwiwiwiwwi 63"));
        accountHolder1.addRole(new Role("USER", accountHolder1));
        accountHolderRepository.save(accountHolder1);
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
        accountHolderRepository.deleteAll();
        userRepository.deleteAll();

    }

    @Test
    void create_isAdmin_created() throws Exception {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO("Celia Lumbreras", "cacito", "cecece",
                LocalDate.of(1995, 9, 27),
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63",
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63");
        String body = objectMapper.writeValueAsString(accountHolderDTO);
        MvcResult result = mockMvc.perform(
                post("/new-account-holder")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepito")
                                .password("grillito")
                                .roles("ADMIN"))
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("cacito"));
    }

    @Test
    void create_noAdmin_forbidden() throws Exception {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO("Celia Lumbreras", "cacito", "cecece",
                LocalDate.of(1995, 9, 27),
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63",
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63");
        String body = objectMapper.writeValueAsString(accountHolderDTO);
        MvcResult result = mockMvc.perform(
                post("/new-account-holder")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepito")
                                .password("grillito")
                                .roles("ACCOUNT_HOLDER"))
        ).andExpect(status().isForbidden()).andReturn();
    }

    @Test
    void create_nullName_badRequest() throws Exception {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO(null, "cacito", "cecece",
                LocalDate.of(1995, 9, 27),
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63",
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63");
        String body = objectMapper.writeValueAsString(accountHolderDTO);
        MvcResult result = mockMvc.perform(
                post("/new-account-holder")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepito")
                                .password("grillito")
                                .roles("ADMIN"))
        ).andExpect(status().isBadRequest()).andReturn();
    }


    @Test
    void create_nullUsername_badRequest() throws Exception {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO("Celia", null, "cecece",
                LocalDate.of(1995, 9, 27),
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63",
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63");
        String body = objectMapper.writeValueAsString(accountHolderDTO);
        MvcResult result = mockMvc.perform(
                post("/new-account-holder")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepito")
                                .password("grillito")
                                .roles("ADMIN"))
        ).andExpect(status().isBadRequest()).andReturn();
    }


    @Test
    void create_nullPassword_badRequest() throws Exception {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO("Celia", "cacito", null,
                LocalDate.of(1995, 9, 27),
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63",
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63");
        String body = objectMapper.writeValueAsString(accountHolderDTO);
        MvcResult result = mockMvc.perform(
                post("/new-account-holder")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepito")
                                .password("grillito")
                                .roles("ADMIN"))
        ).andExpect(status().isBadRequest()).andReturn();
    }


    @Test
    void create_nullBirth_badRequest() throws Exception {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO("Celia", "cacito", "cecece",
                null,
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63",
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63");
        String body = objectMapper.writeValueAsString(accountHolderDTO);
        MvcResult result = mockMvc.perform(
                post("/new-account-holder")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepito")
                                .password("grillito")
                                .roles("ADMIN"))
        ).andExpect(status().isBadRequest()).andReturn();
    }


    @Test
    void create_nullPrimaryAddressField_badRequest() throws Exception {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO("Celia", "cacito", "cecece",
                LocalDate.of(1995, 9, 27),
                null, "aqui", 23456, "wiwiwiwiwwi 63",
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63");
        String body = objectMapper.writeValueAsString(accountHolderDTO);
        MvcResult result = mockMvc.perform(
                post("/new-account-holder")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepito")
                                .password("grillito")
                                .roles("ADMIN"))
        ).andExpect(status().isBadRequest()).andReturn();
    }


    @Test
    void create_nullMailingAddressField_createdWithoutMailingAddress() throws Exception {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO("Celia", "cacito", "cecece",
                LocalDate.of(1995, 9, 27),
                "Ezpania", "aqui", 23456, "wiwiwiwiwwi 63",
                "Paisdeprueba", null, 12345, "wewewewewe 63");
        String body = objectMapper.writeValueAsString(accountHolderDTO);
        MvcResult result = mockMvc.perform(
                post("/new-account-holder")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepito")
                                .password("grillito")
                                .roles("ADMIN"))
        ).andExpect(status().isCreated()).andReturn();

        System.out.println(result.getResponse().getContentAsString());

        assertTrue(result.getResponse().getContentAsString().contains("Ezpania"));
        assertFalse(result.getResponse().getContentAsString().contains("Paisdeprueba"));

    }

}