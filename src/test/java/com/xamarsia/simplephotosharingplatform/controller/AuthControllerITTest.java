package com.xamarsia.simplephotosharingplatform.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.xamarsia.simplephotosharingplatform.service.AuthService;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthControllerITTest {

    @Mock
    private Authentication authentication;

    @Mock
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
    }

    @Test
    void testIsAuthUsed() throws Exception {
        Boolean isAuthUsed = true;
        when(authService.isAuthUsed(authentication)).thenReturn(isAuthUsed);

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/isUsed", authentication))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }
}
