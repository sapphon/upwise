package org.sapphon.personal.upwise.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.repository.VoteRepository;
import org.sapphon.personal.upwise.repository.WisdomRepository;
import org.sapphon.personal.upwise.service.VoteService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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

    private ObjectWriter inputMapper;

    private ObjectWriter outputMapper;

    @Before
    public void setUp(){
        inputMapper = new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true).writer().withDefaultPrettyPrinter();
        outputMapper = new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).writer().withDefaultPrettyPrinter();
    }

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
    public void getRandomWisdomEndpoint() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/wisdom/random").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
    }



    @Test
    public void getAllVotesEndpoint() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/vote/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));
    }

    @Test
    @DirtiesContext
    public void getAllCanReturnRealValues_IntegrationTest() throws Exception {
        IWisdom[] testWisdoms = new IWisdom[2];
        testWisdoms[0] = wisdomService.addOrUpdateWisdom(RandomObjectFactory.makeRandom());
        testWisdoms[1] = wisdomService.addOrUpdateWisdom(RandomObjectFactory.makeRandom());

        mvc.perform(MockMvcRequestBuilders.get("/wisdom/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        "[" +
                                outputMapper.writeValueAsString(testWisdoms[0]) + "," +
                                outputMapper.writeValueAsString(testWisdoms[1]) +
                                "]"))
                .andReturn();
    }

    @Test
    @DirtiesContext
    public void getRandomRealValues_IntegrationTest() throws Exception {
        IWisdom[] testWisdoms = new IWisdom[2];
        testWisdoms[0] = wisdomService.addOrUpdateWisdom(RandomObjectFactory.makeRandom());
        testWisdoms[1] = wisdomService.addOrUpdateWisdom(RandomObjectFactory.makeRandom());

        String json0 = outputMapper.writeValueAsString(testWisdoms[0]);
        String json1 = outputMapper.writeValueAsString(testWisdoms[1]);

        mvc.perform(MockMvcRequestBuilders.get("/wisdom/random").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andDo(new ResultHandler() {
                @Override
                public void handle(MvcResult result) throws Exception {
                    String contentAsString = result.getResponse().getContentAsString();
                    assertTrue(contentAsString.contains(testWisdoms[0].getWisdomContent()) ||  contentAsString.contains(testWisdoms[1].getWisdomContent()));
                }
            })
                    .andReturn();
    }

    @Test
    public void addWisdomEndpoint_SaysBadRequestIfNoWisdomContent() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandom();
        randomWisdom.setWisdomContent(null);
        mvc.perform(buildJsonPostRequest(randomWisdom, "/wisdom/add"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")));

    }


    @Test
    @DirtiesContext
    public void addWisdomEndpoint_SaysConflictIfWisdomCollidesWithPreexistingWisdom() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandom();
        wisdomService.addOrUpdateWisdom(randomWisdom);
        mvc.perform(buildJsonPostRequest(randomWisdom, "/wisdom/add"))
                .andExpect(status().isConflict())
                .andExpect(content().string(equalTo("")));

    }

    @Test
    public void addVoteEndpoint_SaysBadRequestIfNoWisdomContent() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandom();
        randomWisdom.setWisdomContent(null);
        mvc.perform(buildJsonPostRequest(randomWisdom, "/vote/add").param("voterUsername", "trollface"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")));

    }

    @Test
    public void addVoteEndpoint_SaysBadRequestIfNoWisdomAttribution() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandom();
        randomWisdom.setAttribution(null);
        mvc.perform(buildJsonPostRequest(randomWisdom, "/vote/add").param("voterUsername", "trollface"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")));

    }

    private MockHttpServletRequestBuilder buildJsonPostRequest(Object postBodyContent, String uri) throws JsonProcessingException {
        return MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(inputMapper.writeValueAsString(postBodyContent));
    }
}
