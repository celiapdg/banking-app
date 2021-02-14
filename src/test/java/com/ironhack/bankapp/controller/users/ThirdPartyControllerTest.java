package com.ironhack.bankapp.controller.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.bankapp.controller.accounts.dto.CheckingDTO;
import com.ironhack.bankapp.controller.users.dto.ThirdPartyDTO;
import com.ironhack.bankapp.model.users.AccountHolder;
import com.ironhack.bankapp.repository.users.ThirdPartyRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ThirdPartyControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    ThirdPartyRepository thirdPartyRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

    }

    @AfterEach
    void tearDown() {
        thirdPartyRepository.deleteAll();
    }

    @Test
    void create() throws Exception {
        ThirdPartyDTO thirdPartyDTO = new ThirdPartyDTO("holiiiiii", "a4b5c2");
        String body = objectMapper.writeValueAsString(thirdPartyDTO);
        MvcResult result = mockMvc.perform(
                post("/new-third-party")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepito")
                                .password("grillito")
                                .roles("ADMIN"))
        ).andExpect(status().isCreated()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("holiiiiii"));
    }

    @Test
    void create_nullName_badRequest() throws Exception {
        ThirdPartyDTO thirdPartyDTO = new ThirdPartyDTO(null, "a4b5c2");
        String body = objectMapper.writeValueAsString(thirdPartyDTO);
        MvcResult result = mockMvc.perform(
                post("/new-third-party")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepito")
                                .password("grillito")
                                .roles("ADMIN"))
        ).andExpect(status().isBadRequest()).andReturn();
    }


    @Test
    void create_nullSecretKey_badRequest() throws Exception {
        ThirdPartyDTO thirdPartyDTO = new ThirdPartyDTO("caracoliiii", null);
        String body = objectMapper.writeValueAsString(thirdPartyDTO);
        MvcResult result = mockMvc.perform(
                post("/new-third-party")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepito")
                                .password("grillito")
                                .roles("ADMIN"))
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void create_noAdmin_forbidden() throws Exception {
        ThirdPartyDTO thirdPartyDTO = new ThirdPartyDTO("holiiiiii", "a4b5c2");
        String body = objectMapper.writeValueAsString(thirdPartyDTO);
        MvcResult result = mockMvc.perform(
                post("/new-third-party")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("pepito")
                                .password("grillito")
                                .roles("NO_SOY_ADMIN_JOOOO"))
        ).andExpect(status().isForbidden()).andReturn();
    }
}