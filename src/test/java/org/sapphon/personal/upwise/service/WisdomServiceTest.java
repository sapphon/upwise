package org.sapphon.personal.upwise.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.repository.VoteRepository;
import org.sapphon.personal.upwise.repository.WisdomRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

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
        List<IWisdom> expectedResults = RandomObjectFactory.makeRandomCollection();

        when(wisdomRepo.getAll()).thenReturn(expectedResults);

        List<IWisdom> actual = underTest.getAllWisdoms();

        verify(wisdomRepo).getAll();

        assertListEquals(expectedResults, actual);
    }

    @Test
    public void wisdomsPassedToAddOrUpdateWisdomGetSentToTheRepositorySaveMethod() {
        IWisdom expectedResult = RandomObjectFactory.makeRandom();

        underTest.addOrUpdateWisdom(expectedResult);

        verify(wisdomRepo).save(expectedResult);
    }

    @Test
    public void testGetAllWisdomsWithVotesCallsVoteServiceOnceForEachWisdom(){
        List<IWisdom> expectedWisdoms = RandomObjectFactory.makeRandomCollection();
        when(wisdomRepo.getAll()).thenReturn(expectedWisdoms);

        underTest.getAllWisdomsWithVotes();

        verify(wisdomRepo).getAll();
        for(IWisdom wisdom : expectedWisdoms){
            verify(voteService).getByWisdom(wisdom);
        }
    }
}