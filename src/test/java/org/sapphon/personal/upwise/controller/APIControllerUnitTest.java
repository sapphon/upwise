package org.sapphon.personal.upwise.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.service.AnalyticsService;
import org.sapphon.personal.upwise.service.UserService;
import org.sapphon.personal.upwise.service.VoteService;
import org.sapphon.personal.upwise.service.WisdomService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class APIControllerUnitTest {
    private APIController underTest;
    private WisdomService mockWisdomService;
    private VoteService mockVoteService;
    private UserService mockUserService;
    private AnalyticsService mockAnalyticsService;

    @Before
    public void setUp(){
        mockWisdomService = Mockito.mock(WisdomService.class);
        mockVoteService = Mockito.mock(VoteService.class);
        mockUserService = Mockito.mock(UserService.class);
        mockAnalyticsService = Mockito.mock(AnalyticsService.class);
        underTest = new APIController(mockWisdomService, mockVoteService, mockUserService, mockAnalyticsService);
    }

    @Test
    public void testCollaboratesAsExpectedWithVoteService_InOrderToServeRandomWisdomsByVoter() {
        List<IWisdom> wisdoms = RandomObjectFactory.makeRandomListOfWisdoms();
        when(mockWisdomService.getAllWisdoms()).thenReturn(wisdoms);
        underTest.getRandomWisdomEndpoint("Dilly");
        for (IWisdom wisdom : wisdoms) {
            verify(mockVoteService).getByWisdomAndVoterUsername(wisdom, "Dilly");
        }
    }
}
