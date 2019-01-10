package org.sapphon.personal.upwise.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.repository.VoteRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.verify;

@SpringBootTest
@RunWith(SpringRunner.class)
public class VoteServiceTest {

    private VoteRepository voteRepository;

    public VoteService underTest;

    @Before
    public void setup(){
        voteRepository = Mockito.mock(VoteRepository.class);
        underTest = new VoteService(voteRepository);
    }

    @Test
    public void getAllVotesAsksTheRepositoryForAllVotes() {
        underTest.getAllVotes();
        verify(voteRepository).getAll();
    }

    @Test
    public void getByWisdomCollaboratesWithTheRepositorySearchMethod() {
        IWisdom expectedWisdom = RandomObjectFactory.makeRandomWisdom();
        underTest.getByWisdom(expectedWisdom);
        verify(voteRepository).getByWisdom(expectedWisdom);
    }

    @Test
    public void getBySubmitterCollaboratesWithTheRepositorySearchMethod() {
        underTest.getAllByVoter("testBoi");
        verify(voteRepository).getBySubmitter("testBoi");
    }

    @Test
    public void getByWisdomAndSubmitterCollaboratesWithTheRepositorySearchMethod() {
        IWisdom wisdom = RandomObjectFactory.makeRandomWisdom();
        underTest.getByWisdomAndVoterUsername(wisdom, "testBoi");
        verify(voteRepository).findByWisdomAndVoterUsername(wisdom,"testBoi");
    }

    @Test
    public void votesPassedToAddOrUpdateVoteGetSentToTheRepositorySaveMethod() {
        IVote expectedResult = RandomObjectFactory.makeRandomWisdomlessVote();
        underTest.addOrUpdateVote(expectedResult);
        verify(voteRepository).save(expectedResult);
    }
}