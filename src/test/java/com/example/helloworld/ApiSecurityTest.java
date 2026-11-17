package com.example.helloworld;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiSecurityTest {
    @Autowired MockMvc mvc;

    @Test
    void anonymousHelloIsUnauthorized() throws Exception {
        mvc.perform(get("/api/hello")).andExpect(status().isUnauthorized());
    }

    @Test
    void validLoginCreatesSessionAndAllowsHello() throws Exception {
        MvcResult login = mvc.perform(post("/api/login")
                .contentType("application/json")
                .content("{\"username\":\"test\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        if (session == null) throw new AssertionError("Login did not create a session");
        mvc.perform(get("/api/hello").session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/plain"))
                .andExpect(content().string("Hello World"));
    }

    @Test
    void invalidPasswordIsUnauthorized() throws Exception {
        mvc.perform(post("/api/login")
                .contentType("application/json")
                .content("{\"username\":\"test\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }
}
