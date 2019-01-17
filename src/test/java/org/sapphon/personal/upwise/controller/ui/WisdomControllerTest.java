package org.sapphon.personal.upwise.controller.ui;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.auth.BasicUserPrincipal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sapphon.personal.upwise.TestHelper;
import org.sapphon.personal.upwise.model.*;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.presentation.VotePresentation;
import org.sapphon.personal.upwise.presentation.WisdomPresentation;
import org.sapphon.personal.upwise.service.AnalyticsService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WisdomControllerTest {

    @Autowired
    private MockMvc mvc;

    private WisdomService mockWisdomService;
    private AnalyticsService mockAnalyticsService;

    private WisdomController underTest;

    private List<IWisdom> exampleWisdoms;
    private List<IVote> exampleVotes;
    private ArrayList<VotePresentation> exampleVotesZeroAndOnePresented;

    @Before
    public void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("templates/");
        viewResolver.setSuffix(".html");

        mockAnalyticsService = Mockito.mock(AnalyticsService.class);
        mockWisdomService = Mockito.mock(WisdomService.class);
        this.underTest = new WisdomController(mockWisdomService, mockAnalyticsService);

        mvc = MockMvcBuilders.standaloneSetup(underTest)
                .setViewResolvers(viewResolver)
                .build();

        this.exampleWisdoms = new ArrayList<>();
        exampleWisdoms.add(new Wisdom("A good programmer is someone who looks both ways before crossing a one-way street.", "Doug Linder", "jcrouc15", TimeLord.getNow()));
        exampleWisdoms.add(new Wisdom("[Javascript] doesn't exactly allow you to fall into a pit of success.", "Nick Reuter", "cshaugh1", TimeLord.getNow()));
        exampleWisdoms.add(new Wisdom("It's done, it just doesn't work.", "Chris Boyer", "tsatam", TimeLord.getNow()));
        exampleWisdoms.add(new Wisdom("May we be judged by the quality of our commits, not by the content of our Google searches.", "Connor Shaughnessy", "awalte35", TimeLord.getNow()));

        this.exampleVotes = new ArrayList<>();
        exampleVotes.add(RandomObjectFactory.makeRandomVoteForWisdom(exampleWisdoms.get(0)));
        exampleVotes.add(RandomObjectFactory.makeRandomVoteForWisdom(exampleWisdoms.get(0)));
        exampleVotes.add(RandomObjectFactory.makeRandomVoteForWisdom(exampleWisdoms.get(1)));

        this.exampleVotesZeroAndOnePresented = newArrayList(DomainObjectFactory.createVotePresentation(exampleVotes.get(0), RandomStringUtils.randomAlphanumeric(4, 64)), DomainObjectFactory.createVotePresentation(exampleVotes.get(1), RandomStringUtils.randomAlphanumeric(4, 64)));

    }

    @Test
    public void getWisdomLeaderboardCollaboratesWithWisdomServiceAndAnalyticsService() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/wisdomleaderboard").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
        verify(mockWisdomService, times(1)).getAllWisdomPresentationsSortedByNumberOfVotes();
        verify(mockAnalyticsService, times(1)).saveEvent(any());
    }

    @Test
    public void testGetLeaderboardSavesCorrectAnalyticsEvent_WhetherUserIsLoggedInOrNot() throws Exception {
        ArgumentCaptor<IAnalyticsEvent> captor = ArgumentCaptor.forClass(IAnalyticsEvent.class);
        mvc.perform(MockMvcRequestBuilders.get("/wisdomleaderboard").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
        verify(mockAnalyticsService, times(1)).saveEvent(captor.capture());
        assertEquals(AnalyticsAction.VIEWLEADERBOARD, captor.getValue().getEventType());
        assertEquals("[No details]", captor.getValue().getEventDescription());
        assertEquals("[anonymous]", captor.getValue().getEventInitiator());
        assertEquals(null, captor.getValue().getEventTime());
    }

    @Test
    public void testGetLeaderboardSavesUsernameOnAnalyticsEventIfUserLoggedIn() throws Exception {
        ArgumentCaptor<IAnalyticsEvent> captor = ArgumentCaptor.forClass(IAnalyticsEvent.class);
        mvc.perform(MockMvcRequestBuilders.get("/wisdomleaderboard").accept(MediaType.TEXT_HTML).principal(new BasicUserPrincipal("myDude")))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
        verify(mockAnalyticsService, times(1)).saveEvent(captor.capture());
        assertEquals("myDude", captor.getValue().getEventInitiator());
    }

    @Test
    public void baseUrlServesYouTheWisdomLeaderboard() throws Exception {
        when(mockWisdomService.getAllWisdomPresentationsSortedByNumberOfVotes()).thenReturn(newArrayList(DomainObjectFactory.createWisdomPresentation(exampleWisdoms.get(0), exampleVotesZeroAndOnePresented, "")));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        verifyLeaderboard(mvcResult);
    }

    private void verifyLeaderboard(MvcResult mvcResult) {
        try {
            assertEquals("wisdomleaderboard", mvcResult.getModelAndView().getViewName());
            List<WisdomPresentation> actualWisdoms = (List<WisdomPresentation>) mvcResult.getModelAndView().getModel().values().iterator().next();
            verifyWisdoms(actualWisdoms);
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }

    private void verifyWisdoms(List<WisdomPresentation> actualWisdoms) {
        assertEquals(exampleWisdoms.get(0), actualWisdoms.get(0));

        assertEquals(exampleVotes.get(0), actualWisdoms.get(0).getVotes().get(0));
        assertEquals(exampleVotesZeroAndOnePresented.get(0).getDisplayName(), actualWisdoms.get(0).getVotes().get(0).getDisplayName());
        assertEquals(exampleVotes.get(1), actualWisdoms.get(0).getVotes().get(1));
        assertEquals(exampleVotesZeroAndOnePresented.get(1).getDisplayName(), actualWisdoms.get(0).getVotes().get(1).getDisplayName());
    }

    private void verifyWisdom(MvcResult mvcResult) {
        WisdomPresentation actualWisdom = (WisdomPresentation) mvcResult.getModelAndView().getModel().get("wisdom");
        verifyWisdoms(newArrayList(actualWisdom));
    }

    @Test
    public void getWisdomLeaderboard_WithWisdomAndVotes_ProducesCorrectOutputOnModel() throws Exception {
        when(mockWisdomService.getAllWisdomPresentationsSortedByNumberOfVotes()).thenReturn(newArrayList(DomainObjectFactory.createWisdomPresentation(exampleWisdoms.get(0), exampleVotesZeroAndOnePresented, "")));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wisdomleaderboard").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        verifyLeaderboard(mvcResult);
    }

    @Test
    public void viewWisdomWithVotesPopulatesModelAndView() throws Exception {
        when(mockWisdomService.getWisdomPresentationForWisdom(exampleWisdoms.get(0))).thenReturn(DomainObjectFactory.createWisdomPresentation(exampleWisdoms.get(0), exampleVotesZeroAndOnePresented, ""));
        when(mockWisdomService.findWisdomByContentAndAttribution(exampleWisdoms.get(0).getWisdomContent(), exampleWisdoms.get(0).getAttribution())).thenReturn(Optional.of(exampleWisdoms.get(0)));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(String.format("/viewwisdom?wisdomContent=%s&wisdomAttribution=%s", exampleWisdoms.get(0).getWisdomContent(), exampleWisdoms.get(0).getAttribution())).accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();

        verifyWisdom(mvcResult);
        assertEquals("viewwisdom", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void testViewWisdomSavesCorrectAnalyticsEvent_WhetherUserIsLoggedInOrNot() throws Exception {
        ArgumentCaptor<IAnalyticsEvent> captor = ArgumentCaptor.forClass(IAnalyticsEvent.class);

        when(mockWisdomService.getWisdomPresentationForWisdom(exampleWisdoms.get(0))).thenReturn(DomainObjectFactory.createWisdomPresentation(exampleWisdoms.get(0), exampleVotesZeroAndOnePresented, ""));
        when(mockWisdomService.findWisdomByContentAndAttribution(exampleWisdoms.get(0).getWisdomContent(), exampleWisdoms.get(0).getAttribution())).thenReturn(Optional.of(exampleWisdoms.get(0)));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(String.format("/viewwisdom?wisdomContent=%s&wisdomAttribution=%s", exampleWisdoms.get(0).getWisdomContent(), exampleWisdoms.get(0).getAttribution())).accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        verify(mockAnalyticsService).saveEvent(captor.capture());
        IAnalyticsEvent actual = captor.getValue();
        assertEquals(AnalyticsAction.VIEWWISDOM, actual.getEventType());
        assertEquals(exampleWisdoms.get(0).toString(), actual.getEventDescription());
        assertEquals("[anonymous]", actual.getEventInitiator());
        assertEquals(null, actual.getEventTime());
    }

    @Test
    public void testViewWisdomSavesUsernameOnAnalytics_IfUserIsLoggedIn() throws Exception {
        ArgumentCaptor<IAnalyticsEvent> captor = ArgumentCaptor.forClass(IAnalyticsEvent.class);

        when(mockWisdomService.getWisdomPresentationForWisdom(exampleWisdoms.get(0))).thenReturn(DomainObjectFactory.createWisdomPresentation(exampleWisdoms.get(0), exampleVotesZeroAndOnePresented, ""));
        when(mockWisdomService.findWisdomByContentAndAttribution(exampleWisdoms.get(0).getWisdomContent(), exampleWisdoms.get(0).getAttribution())).thenReturn(Optional.of(exampleWisdoms.get(0)));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(String.format("/viewwisdom?wisdomContent=%s&wisdomAttribution=%s", exampleWisdoms.get(0).getWisdomContent(), exampleWisdoms.get(0).getAttribution())).accept(MediaType.TEXT_HTML).principal(new BasicUserPrincipal("aKindaLongUserLoginNameWithCaps")))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        verify(mockAnalyticsService).saveEvent(captor.capture());
        IAnalyticsEvent actual = captor.getValue();

        assertEquals("aKindaLongUserLoginNameWithCaps", actual.getEventInitiator());
    }


    @Test
    public void setsANullWisdomOnTheModelForTheRandomWisdomPageAndServesViewWisdom_IfNoWisdomsExist() throws Exception{
        when(mockWisdomService.hasAnyWisdoms()).thenReturn(false);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/randomwisdom").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        try {
            WisdomPresentation actualWisdom = (WisdomPresentation) mvcResult.getModelAndView().getModel().get("wisdom");
            assertEquals("viewwisdom", mvcResult.getModelAndView().getViewName());
            assertNull(actualWisdom);
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }


}