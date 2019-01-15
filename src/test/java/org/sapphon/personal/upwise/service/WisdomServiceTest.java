package org.sapphon.personal.upwise.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sapphon.personal.upwise.model.IUser;
import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.presentation.WisdomPresentation;
import org.sapphon.personal.upwise.repository.WisdomRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sapphon.personal.upwise.TestHelper.assertListEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WisdomServiceTest {

    private WisdomRepository wisdomRepo;
    private UserService userService;
    private VoteService voteService;

    private WisdomService underTest;

    @Before
    public void setup() {
        voteService = Mockito.mock(VoteService.class);
        wisdomRepo = Mockito.mock(WisdomRepository.class);
        userService = Mockito.mock(UserService.class);

        this.underTest = new WisdomService(wisdomRepo, voteService, userService);
    }

    @Test
    public void usesTheWisdomRepositoryToFindAllWisdomsAndReturnsThemWhenAllWisdomsIsCalled() {
        List<IWisdom> expectedResults = RandomObjectFactory.makeRandomListOfWisdoms();

        when(wisdomRepo.getAll()).thenReturn(expectedResults);
        List<IWisdom> actual = underTest.getAllWisdoms();

        verify(wisdomRepo).getAll();

        assertListEquals(expectedResults, actual);
    }

    @Test
    public void wisdomsPassedToAddOrUpdateWisdomGetSentToTheRepositorySaveMethod() {
        IWisdom expectedResult = RandomObjectFactory.makeRandomWisdom();

        underTest.addOrUpdateWisdom(expectedResult);

        verify(wisdomRepo).save(expectedResult);
    }

    @Test
    public void getWisdomsBySubmitterCollaboratesWithWisdomRepo(){
        underTest.getAllWisdomsBySubmitter("bobert");
        verify(wisdomRepo).getBySubmitter("bobert");
    }

    @Test
    public void testGetAllWisdomsWithVotesCallsVoteServiceAndUserServiceOnceForEachWisdom(){
        List<IWisdom> expectedWisdoms = RandomObjectFactory.makeRandomListOfWisdoms();
        when(wisdomRepo.getAll()).thenReturn(expectedWisdoms);
        when(userService.getUserWithLogin(any())).thenReturn(RandomObjectFactory.makeRandomUser());
        underTest.getAllWisdomsWithVotes();

        verify(wisdomRepo).getAll();
        for(IWisdom wisdom : expectedWisdoms){
            verify(voteService).getByWisdom(wisdom);
            verify(userService).getUserWithLogin(wisdom.getAddedByUsername());
        }
    }

    @Test
    public void testFindWisdomTakesAnIWisdomArgumentAndSearchesByUniqueKey(){
        IWisdom theChosenOne = RandomObjectFactory.makeRandomWisdom();
        when(wisdomRepo.findWisdom(theChosenOne.getWisdomContent(), theChosenOne.getAttribution())).thenReturn(Optional.of(theChosenOne));

        assertEquals(theChosenOne, underTest.findWisdom(theChosenOne).get());

        verify(wisdomRepo).findWisdom(theChosenOne.getWisdomContent(), theChosenOne.getAttribution());
    }

    @Test
    public void testSaysHasWisdomsIfRepositoryIsNotEmpty() {
        when(wisdomRepo.getCount()).thenReturn(5L);
        assertTrue(underTest.hasAnyWisdoms());
        verify(wisdomRepo).getCount();
    }

    @Test
    public void testSaysHasNoWisdomsIfRepositoryIsEmpty(){
        when(wisdomRepo.getCount()).thenReturn(0L);
        assertFalse(underTest.hasAnyWisdoms());
        verify(wisdomRepo).getCount();
    }

    @Test
    public void testGetWisdomWithVotes(){
        IWisdom expectedWisdom = RandomObjectFactory.makeRandomWisdom();
        List<IVote> expectedVotes = RandomObjectFactory.makeRandomListOfWisdomlessVotes();
        when(userService.getUserWithLogin(any())).thenReturn(RandomObjectFactory.makeRandomUser());
        when(voteService.getByWisdom(expectedWisdom)).thenReturn(expectedVotes);
        WisdomPresentation actualWisdomWithVotes = underTest.getWisdomWithVotes(expectedWisdom);
        assertEquals(expectedVotes, actualWisdomWithVotes.getVotes());
        assertEquals(expectedWisdom, actualWisdomWithVotes);
    }

    @Test
    public void testQueriesUserServiceForDisplayNamesForWisdoms() {
        IUser expectedUser = RandomObjectFactory.makeRandomUser();
        when(userService.getUserWithLogin(expectedUser.getLoginUsername())).thenReturn(expectedUser);

        IWisdom expectedWisdom = RandomObjectFactory.makeRandomWisdom();
        expectedWisdom.setAddedByUsername(expectedUser.getLoginUsername());

        List<IVote> expectedVotes = RandomObjectFactory.makeRandomListOfWisdomlessVotes();
        when(voteService.getByWisdom(expectedWisdom)).thenReturn(expectedVotes);


        WisdomPresentation actualWisdomWithVotes = underTest.getWisdomWithVotes(expectedWisdom);


        verify(userService, times(1)).getUserWithLogin(expectedUser.getLoginUsername());
        assertEquals(expectedUser.getDisplayName(), actualWisdomWithVotes.getAddedByDisplayName());
    }
}