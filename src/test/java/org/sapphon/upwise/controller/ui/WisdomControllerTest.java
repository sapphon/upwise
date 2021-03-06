package org.sapphon.upwise.controller.ui;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.auth.BasicUserPrincipal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sapphon.upwise.controller.APIController;
import org.sapphon.upwise.factory.DomainObjectFactory;
import org.sapphon.upwise.factory.RandomObjectFactory;
import org.sapphon.upwise.model.AnalyticsAction;
import org.sapphon.upwise.model.IAnalyticsEvent;
import org.sapphon.upwise.model.IVote;
import org.sapphon.upwise.model.IWisdom;
import org.sapphon.upwise.presentation.VotePresentation;
import org.sapphon.upwise.presentation.WisdomPresentation;
import org.sapphon.upwise.repository.jpa.WisdomJpa;
import org.sapphon.upwise.service.AnalyticsService;
import org.sapphon.upwise.service.WisdomService;
import org.sapphon.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private APIController mockApiController;

    private WisdomController underTest;

    private List<IWisdom> exampleWisdoms;
    private List<IVote> exampleVotes;
    private ArrayList<VotePresentation> exampleVotesZeroAndOnePresented;

    @Before
    public void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("templates/");
        viewResolver.setSuffix(".html");

        mockApiController = Mockito.mock(APIController.class);
        mockAnalyticsService = Mockito.mock(AnalyticsService.class);
        mockWisdomService = Mockito.mock(WisdomService.class);
        this.underTest = new WisdomController(mockWisdomService, mockAnalyticsService, mockApiController);

        mvc = MockMvcBuilders.standaloneSetup(underTest)
                .setViewResolvers(viewResolver)
                .build();

        this.exampleWisdoms = new ArrayList<>();
        exampleWisdoms.add(new WisdomJpa("A good programmer is someone who looks both ways before crossing a one-way street.", "Doug Linder", "jcrouc15", TimeLord.getNowWithOffset(-5)));
        exampleWisdoms.add(new WisdomJpa("[Javascript] doesn't exactly allow you to fall into a pit of success.", "Nick Reuter", "cshaugh1", TimeLord.getNowWithOffset(-2)));
        exampleWisdoms.add(new WisdomJpa("It's done, it just doesn't work.", "Chris Boyer", "tsatam", TimeLord.getNowWithOffset(-1)));
        exampleWisdoms.add(new WisdomJpa("May we be judged by the quality of our commits, not by the content of our Google searches.", "Connor Shaughnessy", "awalte35", TimeLord.getNow()));

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
    public void testGetRecentSavesCorrectAnalyticsEvent_WhenUserIsNotLoggedIn() throws Exception {
        ArgumentCaptor<IAnalyticsEvent> captor = ArgumentCaptor.forClass(IAnalyticsEvent.class);
        mvc.perform(MockMvcRequestBuilders.get("/recentwisdom").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
        verify(mockAnalyticsService, times(1)).saveEvent(captor.capture());
        assertEquals(AnalyticsAction.VIEWRECENT, captor.getValue().getEventType());
        assertEquals("[No details]", captor.getValue().getEventDescription());
        assertEquals("[anonymous]", captor.getValue().getEventInitiator());
        assertEquals(null, captor.getValue().getEventTime());
    }

    @Test
    public void testGetRecentSavesLoggedInUsernameOnAnalyticsEvent() throws Exception {
        ArgumentCaptor<IAnalyticsEvent> captor = ArgumentCaptor.forClass(IAnalyticsEvent.class);
        mvc.perform(MockMvcRequestBuilders.get("/recentwisdom").accept(MediaType.TEXT_HTML).principal(new BasicUserPrincipal("theDude")))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
        verify(mockAnalyticsService, times(1)).saveEvent(captor.capture());
        assertEquals(AnalyticsAction.VIEWRECENT, captor.getValue().getEventType());
        assertEquals("[No details]", captor.getValue().getEventDescription());
        assertEquals("theDude", captor.getValue().getEventInitiator());
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
        verifyLeaderboard(mvcResult, "wisdomleaderboard");
    }

    private void verifyLeaderboard(MvcResult mvcResult, String expectedViewName) {
        try {
            assertEquals(expectedViewName, mvcResult.getModelAndView().getViewName());
            List<WisdomPresentation> actualWisdoms = (List<WisdomPresentation>) mvcResult.getModelAndView().getModel().values().iterator().next();
            verifyLeaderboardWisdoms(actualWisdoms);
        } catch (Exception e) {
            Assert.fail("Model not as expected.");
        }
    }

    private void verifyLeaderboardWisdoms(List<WisdomPresentation> actualWisdoms) {
        assertEquals(exampleWisdoms.get(0), actualWisdoms.get(0));

        assertEquals(exampleVotes.get(0), actualWisdoms.get(0).getVotes().get(0));
        assertEquals(exampleVotesZeroAndOnePresented.get(0).getDisplayName(), actualWisdoms.get(0).getVotes().get(0).getDisplayName());
        assertEquals(exampleVotes.get(1), actualWisdoms.get(0).getVotes().get(1));
        assertEquals(exampleVotesZeroAndOnePresented.get(1).getDisplayName(), actualWisdoms.get(0).getVotes().get(1).getDisplayName());
    }


    private void verifyRecentWisdoms(List<WisdomPresentation> actualWisdoms) {
        assertEquals(exampleWisdoms.get(3), (IWisdom)actualWisdoms.get(0));
        assertEquals(exampleWisdoms.get(2), (IWisdom)actualWisdoms.get(1));
        assertEquals(exampleWisdoms.get(1), (IWisdom)actualWisdoms.get(2));
        assertEquals(exampleWisdoms.get(0), (IWisdom)actualWisdoms.get(3));

    }

    private void verifyWisdom(MvcResult mvcResult) {
        WisdomPresentation actualWisdom = (WisdomPresentation) mvcResult.getModelAndView().getModel().get("wisdom");
        verifyLeaderboardWisdoms(newArrayList(actualWisdom));
    }

    @Test
    public void getWisdomLeaderboard_WithWisdomAndVotes_ProducesCorrectOutputOnModel() throws Exception {
        when(mockWisdomService.getAllWisdomPresentationsSortedByNumberOfVotes()).thenReturn(newArrayList(DomainObjectFactory.createWisdomPresentation(exampleWisdoms.get(0), exampleVotesZeroAndOnePresented, "")));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wisdomleaderboard").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        verifyLeaderboard(mvcResult, "wisdomleaderboard");
    }

    @Test
    public void getWisdomLeadermatrix_WithWisdomAndVotes_ProducesCorrectOutputOnModel() throws Exception {
        when(mockWisdomService.getAllWisdomPresentationsSortedByNumberOfVotes()).thenReturn(newArrayList(DomainObjectFactory.createWisdomPresentation(exampleWisdoms.get(0), exampleVotesZeroAndOnePresented, "")));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wisdomleadermatrix").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        verifyLeaderboard(mvcResult, "wisdomleadermatrix");
    }


    @Test
    public void viewWisdomWithVotesPopulatesModelAndView() throws Exception {
        when(mockWisdomService.getWisdomPresentationForWisdom(exampleWisdoms.get(0))).thenReturn(DomainObjectFactory.createWisdomPresentation(exampleWisdoms.get(0), exampleVotesZeroAndOnePresented, ""));
        when(mockApiController.getWisdomEndpoint(exampleWisdoms.get(0).getWisdomContent(), exampleWisdoms.get(0).getAttribution())).thenReturn(new ResponseEntity<IWisdom>(exampleWisdoms.get(0), HttpStatus.OK));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(String.format("/viewwisdom?wisdomContent=%s&wisdomAttribution=%s", exampleWisdoms.get(0).getWisdomContent(), exampleWisdoms.get(0).getAttribution())).accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();

        verifyWisdom(mvcResult);
        assertEquals("viewwisdom", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void viewWisdomWithVotesPopulatesModelAndView_IdStyle() throws Exception {
        when(mockWisdomService.getWisdomPresentationForWisdom(exampleWisdoms.get(0))).thenReturn(DomainObjectFactory.createWisdomPresentation(exampleWisdoms.get(0), exampleVotesZeroAndOnePresented, ""));
        //when(mockWisdomService.findWisdomByContentAndAttribution(exampleWisdoms.get(0).getWisdomContent(), exampleWisdoms.get(0).getAttribution())).thenReturn(Optional.of(exampleWisdoms.get(0)));
        when(mockWisdomService.findWisdom(99354L)).thenReturn(Optional.of(exampleWisdoms.get(0)));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(String.format("/viewwisdomnew?wisdomId=%s", 99354L)).accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();

        verifyWisdom(mvcResult);
        assertEquals("viewwisdom", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void testWisdomsWithVotesByRecentPopulatesModelAndView() throws Exception{
        when(mockWisdomService.getAllWisdomPresentationsSortedByTimeAdded()).thenReturn(newArrayList(
                DomainObjectFactory.createWisdomPresentation(exampleWisdoms.get(3), new ArrayList<>(), ""),
        DomainObjectFactory.createWisdomPresentation(exampleWisdoms.get(2), new ArrayList<>(), ""),
        DomainObjectFactory.createWisdomPresentation(exampleWisdoms.get(1), new ArrayList<>(), ""),
        DomainObjectFactory.createWisdomPresentation(exampleWisdoms.get(0), new ArrayList<>(), "")));


        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/recentwisdom").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
        assertEquals("recentwisdom", mvcResult.getModelAndView().getViewName());
        verifyRecentWisdoms((List<WisdomPresentation>)mvcResult.getModelAndView().getModel().get("allWisdoms"));
    }


    @Test
    public void testViewWisdomSavesCorrectAnalyticsEvent_WhetherUserIsLoggedInOrNot() throws Exception {
        ArgumentCaptor<IAnalyticsEvent> captor = ArgumentCaptor.forClass(IAnalyticsEvent.class);

        when(mockWisdomService.getWisdomPresentationForWisdom(exampleWisdoms.get(0))).thenReturn(DomainObjectFactory.createWisdomPresentation(exampleWisdoms.get(0), exampleVotesZeroAndOnePresented, ""));
        when(mockApiController.getWisdomEndpoint(exampleWisdoms.get(0).getWisdomContent(), exampleWisdoms.get(0).getAttribution())).thenReturn(new ResponseEntity<IWisdom>(exampleWisdoms.get(0), HttpStatus.OK));
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
        when(mockApiController.getWisdomEndpoint(exampleWisdoms.get(0).getWisdomContent(), exampleWisdoms.get(0).getAttribution())).thenReturn(new ResponseEntity<IWisdom>(exampleWisdoms.get(0), HttpStatus.OK));
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
        when(mockApiController.getRandomWisdomEndpoint(any())).thenReturn(null);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/randomwisdom").accept(MediaType.TEXT_HTML))
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

    @Test
    public void testNoParametersIsNoProblem_ShouldAllBeOptionalSo200_OK() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/randomwisdom").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }


    @Test
    public void testPassesRandomWisdomArgumentsToApiControllerProperly() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/randomwisdom").param("upvotedByUsername", "PeggyHeel").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
        verify(mockApiController).getRandomWisdomEndpoint("PeggyHeel");
    }
}