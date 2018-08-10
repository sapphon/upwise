package org.sapphon.personal.upwise.controller;


import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.service.WisdomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LeaderboardControllerTest {
    @Autowired
    private MockMvc mvc;

    @Mock
    private WisdomService wisdomService;

    @InjectMocks
    private LeaderboardController underTest;

    @Test
    public void getLeaderboardData() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Leaderboard is up")));
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

    //TODO fix the dependency injection for this class so that this test works.
    @Ignore
    @Test
    public void getAllCanReturnRealValues() throws Exception {
        when(wisdomService.getAllWisdoms()).thenReturn(RandomObjectFactory.makeRandomCollection());

        mvc.perform(MockMvcRequestBuilders.get("/wisdom/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));

        verify(wisdomService).getAllWisdoms();
    }
}
