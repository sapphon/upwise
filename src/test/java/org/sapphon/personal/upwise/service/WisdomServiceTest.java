package org.sapphon.personal.upwise.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.presentation.WisdomWithVotesPresentation;
import org.sapphon.personal.upwise.repository.WisdomRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sapphon.personal.upwise.TestHelper.assertListEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WisdomServiceTest {

    @Mock
    private WisdomRepository wisdomRepo;

    @Mock
    private VoteService voteService;

    @InjectMocks
    private WisdomService underTest;

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
    public void testGetAllWisdomsWithVotesCallsVoteServiceOnceForEachWisdom(){
        List<IWisdom> expectedWisdoms = RandomObjectFactory.makeRandomListOfWisdoms();
        when(wisdomRepo.getAll()).thenReturn(expectedWisdoms);

        underTest.getAllWisdomsWithVotes();

        verify(wisdomRepo).getAll();
        for(IWisdom wisdom : expectedWisdoms){
            verify(voteService).getByWisdom(wisdom);
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
        when(voteService.getByWisdom(expectedWisdom)).thenReturn(expectedVotes);

        WisdomWithVotesPresentation actualWisdomWithVotes = underTest.getWisdomWithVotes(expectedWisdom);

        assertEquals(expectedVotes, actualWisdomWithVotes.getVotes());
        assertEquals(expectedWisdom, actualWisdomWithVotes);

    }
}