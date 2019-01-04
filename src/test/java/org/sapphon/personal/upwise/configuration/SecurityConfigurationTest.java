package org.sapphon.personal.upwise.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigurationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testAPIRedirectsUnauthenticatedRequestsToPostEndpoints() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/wisdom/add").accept(MediaType.APPLICATION_JSON)).andExpect(status().is3xxRedirection());
        mvc.perform(MockMvcRequestBuilders.post("/vote/add").accept(MediaType.APPLICATION_JSON)).andExpect(status().is3xxRedirection());
        mvc.perform(MockMvcRequestBuilders.post("/registration/add").accept(MediaType.APPLICATION_JSON)).andExpect(status().is3xxRedirection());
        mvc.perform(MockMvcRequestBuilders.post("/analytics/add").accept(MediaType.APPLICATION_JSON)).andExpect(status().is3xxRedirection());
    }

    @Test
    public void testAPIAcceptsUnauthenticatedRequestsToGetEndpoints() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/wisdom/all").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders.get("/vote/all").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders.get("/wisdom/random").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders.get("/health").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

}