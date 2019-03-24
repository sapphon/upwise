package org.sapphon.personal.upwise.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sapphon.personal.upwise.factory.AnalyticsFactory;
import org.sapphon.personal.upwise.model.*;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.service.AnalyticsService;
import org.sapphon.personal.upwise.service.UserService;
import org.sapphon.personal.upwise.service.VoteService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class APIControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WisdomService wisdomService;

    @Autowired
    private VoteService voteService;

    private ObjectWriter inputMapper;

    private ObjectWriter outputMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private AnalyticsService analyticsService;

    @Before
    public void setUp() {
        inputMapper = new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true).writer().withDefaultPrettyPrinter();
        outputMapper = new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).writer().withDefaultPrettyPrinter();
    }

    @Test
    public void getHealthCheckData() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/health").accept(MediaType.APPLICATION_JSON))
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
    public void getAllAnalyticsEndpoint() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/analytics/all").accept(MediaType.APPLICATION_JSON))
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
    public void getAllWisdomsCanReturnRealValues_IntegrationTest() throws Exception {
        IWisdom[] testWisdoms = new IWisdom[2];
        testWisdoms[0] = wisdomService.addOrUpdateWisdom(RandomObjectFactory.makeRandomWisdom());
        testWisdoms[1] = wisdomService.addOrUpdateWisdom(RandomObjectFactory.makeRandomWisdom());

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
    public void getAllAnalyticsCanReturnRealValues_IntegrationTest() throws Exception {
        IAnalyticsEvent[] testAnalytics = new IAnalyticsEvent[2];
        testAnalytics[0] = analyticsService.saveEvent(RandomObjectFactory.makeRandomEvent());
        testAnalytics[1] = analyticsService.saveEvent(RandomObjectFactory.makeRandomEvent());

        mvc.perform(MockMvcRequestBuilders.get("/analytics/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        "[" +
                                outputMapper.writeValueAsString(testAnalytics[0]) + "," +
                                outputMapper.writeValueAsString(testAnalytics[1]) +
                                "]"))
                .andReturn();
    }

    @Test
    public void getRandomByUpvoter_IntegrationTest() throws Exception {
        IWisdom[] testWisdoms = new IWisdom[2];
        testWisdoms[0] = wisdomService.addOrUpdateWisdom(RandomObjectFactory.makeRandomWisdom());
        testWisdoms[1] = wisdomService.addOrUpdateWisdom(RandomObjectFactory.makeRandomWisdom());
        IVote aVoteForWisdomZero = RandomObjectFactory.makeRandomVoteForWisdom(testWisdoms[0]);
        voteService.addOrUpdateVote(aVoteForWisdomZero);

        String json0 = outputMapper.writeValueAsString(testWisdoms[0]);
        String json1 = outputMapper.writeValueAsString(testWisdoms[1]);

        for(int i = 0; i < 10; i++) {
            mvc.perform(MockMvcRequestBuilders.get("/wisdom/random").param("upvotedByUsername", aVoteForWisdomZero.getAddedByUsername()).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andDo(new ResultHandler() {
                        @Override
                        public void handle(MvcResult result) throws Exception {
                            String contentAsString = result.getResponse().getContentAsString();
                            assertTrue(contentAsString.contains(testWisdoms[0].getWisdomContent()));
                        }
                    })
                    .andReturn();
        }
    }

    @Test
    public void getRandomRealValues_IntegrationTest() throws Exception {
        IWisdom[] testWisdoms = new IWisdom[2];
        testWisdoms[0] = wisdomService.addOrUpdateWisdom(RandomObjectFactory.makeRandomWisdom());
        testWisdoms[1] = wisdomService.addOrUpdateWisdom(RandomObjectFactory.makeRandomWisdom());

        String json0 = outputMapper.writeValueAsString(testWisdoms[0]);
        String json1 = outputMapper.writeValueAsString(testWisdoms[1]);

        mvc.perform(MockMvcRequestBuilders.get("/wisdom/random").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult result) throws Exception {
                        String contentAsString = result.getResponse().getContentAsString();
                        assertTrue(contentAsString.contains(testWisdoms[0].getWisdomContent()) || contentAsString.contains(testWisdoms[1].getWisdomContent()));
                    }
                })
                .andReturn();
    }

    @Test
    public void addWisdomEndpoint_SaysBadRequestIfNoWisdomContent() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        randomWisdom.setWisdomContent(null);
        mvc.perform(buildJsonPostRequest(randomWisdom, "/wisdom/add"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")));

    }


    @Test
    public void addWisdomEndpoint_SaysConflictIfWisdomCollidesWithPreexistingWisdom() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        wisdomService.addOrUpdateWisdom(randomWisdom);
        mvc.perform(buildJsonPostRequest(randomWisdom, "/wisdom/add"))
                .andExpect(status().isConflict())
                .andExpect(content().string(equalTo("")));

    }

    @Test
    public void addWisdomEndpoint_SaysCreatedInBaseCase() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        mvc.perform(buildJsonPostRequest(randomWisdom, "/wisdom/add"))
                .andExpect(status().isCreated());
    }

    @Test
    public void addWisdomTriggersAnalyticsCreation() throws Exception {
        assertEquals(0, analyticsService.getAllEvents().size());
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        mvc.perform(buildJsonPostRequest(randomWisdom, "/wisdom/add"))
                .andExpect(status().isCreated());

        assertEquals(1, analyticsService.getAllEvents().size());
        assertEquals(analyticsService.getAllEvents().get(0), AnalyticsFactory.createAddWisdomEvent(HttpStatus.CREATED, randomWisdom));
    }

    @Test
    public void addVoteEndpoint_SaysBadRequestIfNoWisdomContent() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        randomWisdom.setWisdomContent(null);
        wisdomService.addOrUpdateWisdom(randomWisdom);
        IVote randomVote = RandomObjectFactory.makeRandomVoteForWisdom(randomWisdom);
        mvc.perform(buildJsonPostRequest(randomVote, "/vote/add"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")));

    }

    @Test
    public void addVoteEndpoint_SaysBadRequestIfEmptyWisdomContent() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        randomWisdom.setWisdomContent("");
        wisdomService.addOrUpdateWisdom(randomWisdom);
        IVote randomVote = RandomObjectFactory.makeRandomVoteForWisdom(randomWisdom);
        mvc.perform(buildJsonPostRequest(randomVote, "/vote/add"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")));

    }


    @Test
    public void addVoteEndpoint_SaysBadRequestIfWisdomDoesNotExist() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        IVote randomVote = RandomObjectFactory.makeRandomVoteForWisdom(randomWisdom);
        mvc.perform(buildJsonPostRequest(randomVote, "/vote/add"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")));

    }

    @Test
    public void addVoteEndpoint_SaysBadRequestIfNoWisdomAttribution() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        randomWisdom.setAttribution(null);
        wisdomService.addOrUpdateWisdom(randomWisdom);
        IVote randomVote = RandomObjectFactory.makeRandomVoteForWisdom(randomWisdom);
        mvc.perform(buildJsonPostRequest(randomVote, "/vote/add"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")));

    }

    @Test
    public void addVoteEndpoint_SaysBadRequestIfNoVoterName() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        wisdomService.addOrUpdateWisdom(randomWisdom);
        IVote randomVote = RandomObjectFactory.makeRandomVoteForWisdom(randomWisdom);
        randomVote.setAddedByUsername(null);
        mvc.perform(buildJsonPostRequest(randomVote, "/vote/add"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")));

    }


    @Test
    public void addVoteEndpoint_SaysConflictIfVoteCollidesWithPreexistingVote() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        IVote randomVote = RandomObjectFactory.makeRandomVoteForWisdom(randomWisdom);
        wisdomService.addOrUpdateWisdom(randomWisdom);
        voteService.addOrUpdateVote(randomVote);
        mvc.perform(buildJsonPostRequest(randomVote, "/vote/add"))
                .andExpect(status().isConflict())
                .andExpect(content().string(equalTo("")));

    }


    @Test
    public void addVoteEndpoint_SaysCreatedInBaseCase() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        wisdomService.addOrUpdateWisdom(randomWisdom);
        IVote randomVote = RandomObjectFactory.makeRandomVoteForWisdom(randomWisdom);
        mvc.perform(buildJsonPostRequest(randomVote, "/vote/add"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(outputMapper.writeValueAsString(randomVote)));

    }

    @Test
    public void addVoteEndpoint_TriggersAnalyticsCreation() throws Exception {
        assertEquals(0, analyticsService.getAllEvents().size());

        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        wisdomService.addOrUpdateWisdom(randomWisdom);
        IVote randomVote = RandomObjectFactory.makeRandomVoteForWisdom(randomWisdom);
        final MvcResult result = mvc.perform(buildJsonPostRequest(randomVote, "/vote/add"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(outputMapper.writeValueAsString(randomVote)))
                .andReturn();

        assertEquals(1, analyticsService.getAllEvents().size());
        final IAnalyticsEvent expectedEvent = AnalyticsFactory.createAddVoteEvent(HttpStatus.CREATED, randomVote);
        final IAnalyticsEvent actualEvent = analyticsService.getAllEvents().get(0);
        assertEquals(expectedEvent.getEventType(), actualEvent.getEventType());
        assertEquals(expectedEvent.getEventInitiator(), actualEvent.getEventInitiator());
        assertEquals(expectedEvent.getEventDescription(), actualEvent.getEventDescription());
        assertNotNull(actualEvent.getEventTime());
    }

    @Test
    public void addUserEndpointSaysCreatedInBaseCase() throws Exception {
        IUser randomUser = RandomObjectFactory.makeRandomUser();

        mvc.perform(buildJsonPostRequest(randomUser, "/registration/add"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addUserEndpointTriggersAnalyticsCreation() throws Exception {
        assertEquals(0, analyticsService.getAllEvents().size());
        IUser randomUser = RandomObjectFactory.makeRandomUser();
        mvc.perform(buildJsonPostRequest(randomUser, "/registration/add"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        assertEquals(1, analyticsService.getAllEvents().size());
        randomUser.setLoginUsername(randomUser.getLoginUsername().toLowerCase());
        final IAnalyticsEvent expectedEvent = AnalyticsFactory.createAddUserEvent(HttpStatus.CREATED, randomUser);
        final IAnalyticsEvent actualEvent = analyticsService.getAllEvents().get(0);
        assertEquals(expectedEvent.getEventType(), actualEvent.getEventType());
        assertEquals(expectedEvent.getEventInitiator(), actualEvent.getEventInitiator());
        assertEquals(expectedEvent.getEventDescription(), actualEvent.getEventDescription());
        assertNotNull(actualEvent.getEventTime());
    }

    @Test
    public void addUserEndpointSaysBadRequestIfNoUsername() throws Exception {
        IUser userWithBlankOrNullLogin = RandomObjectFactory.makeRandomUser();
        userWithBlankOrNullLogin.setLoginUsername("");

        mvc.perform(buildJsonPostRequest(userWithBlankOrNullLogin, "/registration/add"))
                .andExpect(status().isBadRequest());

        userWithBlankOrNullLogin.setLoginUsername(null);

        mvc.perform(buildJsonPostRequest(userWithBlankOrNullLogin, "/registration/add"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserEndpointSaysBadRequestIfNoPassword() throws Exception {
        IUser userWithBlankOrNullPassword = RandomObjectFactory.makeRandomUser();
        userWithBlankOrNullPassword.setPassword("");

        mvc.perform(buildJsonPostRequest(userWithBlankOrNullPassword, "/registration/add"))
                .andExpect(status().isBadRequest());

        userWithBlankOrNullPassword.setPassword(null);

        mvc.perform(buildJsonPostRequest(userWithBlankOrNullPassword, "/registration/add"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserEndpointSaysConflictIfSameUsernameAlreadyExists() throws Exception {
        IUser randomUser = RandomObjectFactory.makeRandomUser();
        userService.addOrUpdateUser(randomUser);
        mvc.perform(buildJsonPostRequest(randomUser, "/registration/add"))
                .andExpect(status().isConflict());
    }

    @Test
    public void addAnalyticsEventEndpointSaysConflictIfSameEventAlreadyExists() throws Exception {
        IAnalyticsEvent randomEvent = RandomObjectFactory.makeRandomEvent();
        analyticsService.saveEvent(randomEvent);
        mvc.perform(buildJsonPostRequest(randomEvent, "/analytics/add"))
                .andExpect(status().isConflict());
    }

    @Test
    public void addAnalyticsEventEndpointSaysCreatedInBaseCase() throws Exception {
        IAnalyticsEvent randomEvent = RandomObjectFactory.makeRandomEvent();
        mvc.perform(buildJsonPostRequest(randomEvent, "/analytics/add"))
                .andExpect(status().isCreated());
    }

    @Test
    public void addAnalyticsEventEndpointSaysBadRequestIfActionIsMissing() throws Exception {
        IAnalyticsEvent eventWithNoAction = AnalyticsFactory.createAnalyticsEvent("description", "user", TimeLord.getNow(), null);
        mvc.perform(buildJsonPostRequest(eventWithNoAction, "/analytics/add"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addAnalyticsEventEndpointAcceptsEmptyDescriptions() throws Exception {
        IAnalyticsEvent eventWithNoDescription = AnalyticsFactory.createAnalyticsEvent(null, "user", TimeLord.getNow(), AnalyticsAction.LOGIN);
        mvc.perform(buildJsonPostRequest(eventWithNoDescription, "/analytics/add"))
                .andExpect(status().isCreated());
    }

    @Test
    public void addAnalyticsEventEndpointSaysBadRequestIfUsernameIsMissing() throws Exception {
        IAnalyticsEvent eventWithNoUser = AnalyticsFactory.createAnalyticsEvent("description", null, TimeLord.getNow(), AnalyticsAction.ADDUSER);
        mvc.perform(buildJsonPostRequest(eventWithNoUser, "/analytics/add"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addAnalyticsEventEndpointSaysOKIfTimestampIsMissing_ButSetsOne() throws Exception {
        IAnalyticsEvent eventWithNoTimestamp = AnalyticsFactory.createAnalyticsEvent("description", "user", null, AnalyticsAction.ADDVOTE);
        final MvcResult mvcResult = mvc.perform(buildJsonPostRequest(eventWithNoTimestamp, "/analytics/add"))
                .andExpect(status().isCreated())
                .andReturn();

        assertFalse("Should not have any null values after request processing", mvcResult.getResponse().getContentAsString().contains("null"));
    }

    @Test
    public void unvoteEndpointSaysBadRequestIfVoteIsNull() throws Exception {
        mvc.perform(buildJsonPostRequest(null, "/vote/remove")).andExpect(status().isBadRequest());
    }

    @Test
    public void unvoteEndpointSaysBadRequestIfVoteHasNullWisdom() throws Exception{
        IVote vote = RandomObjectFactory.makeRandomWisdomlessVote();
        mvc.perform(buildJsonPostRequest(vote, "/vote/remove")).andExpect(status().isBadRequest());

    }

    @Test
    public void unvoteEndpointSaysConflictIfVoteIsValidButDoesNotExist() throws Exception{
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        IVote randomVote = RandomObjectFactory.makeRandomVoteForWisdom(randomWisdom);
        assertEquals(Optional.empty(), voteService.getByWisdomAndVoterUsername(randomWisdom, randomVote.getAddedByUsername()));
        mvc.perform(buildJsonPostRequest(randomVote, "/vote/remove")).andExpect(status().isConflict());
    }

    @Test
    public void unvoteEndpointSaysOkIfVoteWasValidAndExisted_AlsoItRemovesYourVote() throws Exception {
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        IVote randomVote = RandomObjectFactory.makeRandomVoteForWisdom(randomWisdom);
        wisdomService.addOrUpdateWisdom(randomWisdom);
        voteService.addOrUpdateVote(randomVote);

        assertEquals(randomVote, voteService.getByWisdomAndVoterUsername(randomWisdom, randomVote.getAddedByUsername()).get());
        mvc.perform(buildJsonPostRequest(randomVote, "/vote/remove")).andExpect(status().isOk());
        assertEquals(Optional.empty(), voteService.getByWisdomAndVoterUsername(randomWisdom, randomVote.getAddedByUsername()));
    }


    private MockHttpServletRequestBuilder buildJsonPostRequest(Object postBodyContent, String uri) throws JsonProcessingException {
        return MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(inputMapper.writeValueAsString(postBodyContent));
    }
}
