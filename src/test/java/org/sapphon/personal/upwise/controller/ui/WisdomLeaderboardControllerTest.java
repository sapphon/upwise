package org.sapphon.personal.upwise.controller.ui;

import ch.qos.logback.classic.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.Wisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.presentation.WisdomWithVotesPresentation;
import org.sapphon.personal.upwise.service.VoteService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WisdomLeaderboardControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WisdomService wisdomService;

    @InjectMocks
    private WisdomLeaderboardController underTest;

    private List<IWisdom> exampleWisdoms;
    private List<IVote> exampleVotes;

    @Before
    public void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("templates/");
        viewResolver.setSuffix(".html");

        this.underTest = new WisdomLeaderboardController(wisdomService);

        mvc = MockMvcBuilders.standaloneSetup(underTest)
                .setViewResolvers(viewResolver)
                .build();

        this.exampleWisdoms = new ArrayList<IWisdom>();
        exampleWisdoms.add(new Wisdom("A good programmer is someone who looks both ways before crossing a one-way street.", "Doug Linder", "jcrouc15", TimeLord.getNow()));
        exampleWisdoms.add(new Wisdom("[Javascript] doesn't exactly allow you to fall into a pit of success.", "Nick Reuter", "cshaugh1", TimeLord.getNow()));
        exampleWisdoms.add(new Wisdom("It's done, it just doesn't work.", "Chris Boyer", "tsatam", TimeLord.getNow()));
        exampleWisdoms.add(new Wisdom("May we be judged by the quality of our commits, not by the content of our Google searches.", "Connor Shaughnessy", "awalte35", TimeLord.getNow()));

        this.exampleVotes = new ArrayList<IVote>();
        exampleVotes.add(RandomObjectFactory.makeRandomVoteForWisdom(exampleWisdoms.get(0)));
        exampleVotes.add(RandomObjectFactory.makeRandomVoteForWisdom(exampleWisdoms.get(0)));
        exampleVotes.add(RandomObjectFactory.makeRandomVoteForWisdom(exampleWisdoms.get(1)));

    }

    @Test
    public void getWisdomLeaderboardCollaboratesWithWisdomService() throws Exception {
        when(wisdomService.getAllWisdoms()).thenReturn(this.exampleWisdoms);
        mvc.perform(MockMvcRequestBuilders.get("/wisdomleaderboard").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
        verify(wisdomService,times(1)).getAllWisdomsWithVotes();
    }

    @Test
    public void getWisdomLeaderboard_WithWisdomAndVotes_ProducesCorrectOutput() throws Exception {
        when(wisdomService.getAllWisdomsWithVotes()).thenReturn(newArrayList(DomainObjectFactory.createWisdomWithVotes(exampleWisdoms.get(0), newArrayList(exampleVotes.get(0), exampleVotes.get(1)))));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/wisdomleaderboard").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
    try {
        List<WisdomWithVotesPresentation> actualWisdoms = (List<WisdomWithVotesPresentation>) mvcResult.getModelAndView().getModel().values().iterator().next();
        assertEquals(exampleWisdoms.get(0), actualWisdoms.get(0));
        assertEquals(exampleVotes.get(0), actualWisdoms.get(0).getVotes().get(0));
        assertEquals(exampleVotes.get(1), actualWisdoms.get(0).getVotes().get(1));
    }
    catch(Exception e){
        Assert.fail("Model not as expected.");
    }
}
}