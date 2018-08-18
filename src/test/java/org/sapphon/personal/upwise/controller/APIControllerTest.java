package org.sapphon.personal.upwise.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.service.VoteService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class APIControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WisdomService wisdomService;

    @Autowired
    private VoteService voteService;

    @Test
    public void getLeaderboardData() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Upwise API is up")));
    }

    @Test
    public void getAllWisdomsEndpoint() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/wisdom/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));
    }


    @Test
    public void getAllVotesEndpoint() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/vote/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));
    }

    @Test
    public void getAllCanReturnRealValues_IntegrationTest() throws Exception {
        IWisdom[] testWisdoms = new IWisdom[2];
        testWisdoms[0] = wisdomService.addOrUpdateWisdom(RandomObjectFactory.makeRandom());
        testWisdoms[1] = wisdomService.addOrUpdateWisdom(RandomObjectFactory.makeRandom());

        ObjectWriter mapper = new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).writer().withDefaultPrettyPrinter();
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/wisdom/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        "[" +
                                mapper.writeValueAsString(testWisdoms[0]) + "," +
                                mapper.writeValueAsString(testWisdoms[1]) +
                                "]"))
                .andReturn();
    }

    @Test
    public void addWisdomEndpoint_SaysBadRequestIfNoWisdomContent() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandom();
        randomWisdom.setWisdomContent(null);
        mvc.perform(MockMvcRequestBuilders.post("/wisdom/add", randomWisdom).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")));

    }
}
