package org.sapphon.personal.upwise.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.model.IUser;
import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.model.Wisdom;
import org.sapphon.personal.upwise.presentation.VotePresentation;
import org.sapphon.personal.upwise.presentation.WisdomPresentation;
import org.sapphon.personal.upwise.repository.WisdomRepository;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;
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
        underTest.getAllWisdomPresentationsSortedByNumberOfVotes();

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
        List<VotePresentation> expectedVotes = RandomObjectFactory.makeRandomListOfWisdomlessVotePresentations();
        List<IVote> expectedVotesToo = new ArrayList<>(expectedVotes);
        IUser expectedUser = RandomObjectFactory.makeRandomUser();
        when(userService.getUserWithLogin(any())).thenReturn(expectedUser);
        when(voteService.getByWisdom(expectedWisdom)).thenReturn(expectedVotesToo);
        when(voteService.getVotePresentationForVotes(expectedVotesToo)).thenReturn(expectedVotes);

        WisdomPresentation actualWisdomWithVotes = underTest.getWisdomPresentationForWisdom(expectedWisdom);
        assertEquals(expectedVotes, actualWisdomWithVotes.getVotes());
        assertEquals(expectedWisdom, actualWisdomWithVotes);
        assertEquals(expectedUser.getDisplayName(), actualWisdomWithVotes.getAddedByDisplayName());
    }

    @Test
    public void testQueriesUserServiceForDisplayNamesForWisdoms() {
        IUser expectedUser = RandomObjectFactory.makeRandomUser();
        when(userService.getUserWithLogin(expectedUser.getLoginUsername())).thenReturn(expectedUser);

        IWisdom expectedWisdom = RandomObjectFactory.makeRandomWisdom();
        expectedWisdom.setAddedByUsername(expectedUser.getLoginUsername());

        List<IVote> expectedVotes = RandomObjectFactory.makeRandomListOfWisdomlessVotes();
        when(voteService.getByWisdom(expectedWisdom)).thenReturn(expectedVotes);


        WisdomPresentation actualWisdomWithVotes = underTest.getWisdomPresentationForWisdom(expectedWisdom);


        verify(userService, times(1)).getUserWithLogin(expectedUser.getLoginUsername());
        assertEquals(expectedUser.getDisplayName(), actualWisdomWithVotes.getAddedByDisplayName());
    }

    @Test
    public void testGetRecentWisdoms() {
        List<IWisdom> wisdomsOutOfAgeOrder = new ArrayList<>();
        IWisdom oldWisdom = RandomObjectFactory.makeRandomWisdom();
        oldWisdom.setTimeAdded(new Timestamp(0));
        IWisdom newerWisdom = RandomObjectFactory.makeRandomWisdom();
        newerWisdom.setTimeAdded(new Timestamp(1000));
        IWisdom newestWisdom = RandomObjectFactory.makeRandomWisdom();
        newestWisdom.setTimeAdded(TimeLord.getNow());

        wisdomsOutOfAgeOrder.add(newerWisdom);
        wisdomsOutOfAgeOrder.add(oldWisdom);
        wisdomsOutOfAgeOrder.add(newestWisdom);
        when(wisdomRepo.getAll()).thenReturn(wisdomsOutOfAgeOrder);

        List<WisdomPresentation> actual = underTest.getAllWisdomPresentationsSortedByTimeAdded();
        assertEquals(actual.get(0).getTimeAdded(),newestWisdom.getTimeAdded());
        assertEquals(actual.get(1).getTimeAdded(),newerWisdom.getTimeAdded());
        assertEquals(actual.get(2).getTimeAdded(),oldWisdom.getTimeAdded());

    }

    @Test
    public void testUsesLoginNameForDisplayNameIfNoExtantUser() {
        IUser expectedUser = RandomObjectFactory.makeRandomUser();
        when(userService.getUserWithLogin(expectedUser.getLoginUsername())).thenReturn(null);

        IWisdom expectedWisdom = RandomObjectFactory.makeRandomWisdom();
        expectedWisdom.setAddedByUsername(expectedUser.getLoginUsername());
        List<IVote> expectedVotes = RandomObjectFactory.makeRandomListOfWisdomlessVotes();
        when(voteService.getByWisdom(expectedWisdom)).thenReturn(expectedVotes);

        WisdomPresentation actualWisdomWithVotes = null;
        try {
            actualWisdomWithVotes = underTest.getWisdomPresentationForWisdom(expectedWisdom);
        }catch(Exception e){
            fail("Should not explode because user with name not found.");
        }

        verify(userService, times(1)).getUserWithLogin(expectedUser.getLoginUsername());
        assertEquals(expectedUser.getLoginUsername(), actualWisdomWithVotes.getAddedByDisplayName());
    }

    @Test
    public void testGivesBackAllWisdomsWhenYouAskForWisdomsByAttribution(){
        List<IWisdom> expectedResults = RandomObjectFactory.makeRandomListOfWisdoms();

        when(wisdomRepo.getAll()).thenReturn(expectedResults);
        List<IWisdom> actual = underTest.getAllWisdomsByAttribution("doesntmatter");

        verify(wisdomRepo).getAll();

        assertListEquals(expectedResults, actual);
    }
}