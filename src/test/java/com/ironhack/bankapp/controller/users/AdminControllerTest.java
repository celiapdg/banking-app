package com.ironhack.bankapp.controller.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.bankapp.controller.users.dto.AccountHolderDTO;
import com.ironhack.bankapp.controller.users.dto.AdminDTO;
import com.ironhack.bankapp.repository.users.AdminRepository;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AdminControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AdminRepository adminRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
        adminRepository.deleteAll();
    }

    @Test
    void create_isAdmin_created() throws Exception {
        AdminDTO adminDTO = new AdminDTO("Celia Lumbreras", "cacito", "cecece");
        String body = objectMapper.writeValueAsString(adminDTO);
        MvcResult result = mockMvc.perform(
                post("/new-admin")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepito")
                                .password("grillito")
                                .roles("ADMIN"))
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("cacito"));
    }
}