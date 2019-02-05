package org.sapphon.personal.upwise.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sapphon.personal.upwise.model.IUser;
import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.presentation.VotePresentation;
import org.sapphon.personal.upwise.repository.VoteRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class VoteServiceTest {

    private VoteRepository voteRepository;
    private UserService userService;

    public VoteService underTest;

    @Before
    public void setup(){
        userService = Mockito.mock(UserService.class);
        voteRepository = Mockito.mock(VoteRepository.class);
        underTest = new VoteService(voteRepository, userService);
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

    @Test
    public void testShouldNotBlowUpIfVoterDoesNotExist_UseUsernameForDisplayNameInThisCase() {
        IVote vote = RandomObjectFactory.makeRandomWisdomlessVote();
        VotePresentation actual = null;
        try {
            actual = underTest.getVotePresentationForVote(vote);
        }catch(Exception e){
            fail("Should not explode if voter is not an actual user");
        }
        verify(userService).getUserWithLogin(vote.getAddedByUsername());
        assertEquals(vote.getAddedByUsername(), actual.getDisplayName());
    }

    @Test
    public void testGetVotePresentationCollaboratesWithUserServiceAppropriately() {
        IVote vote = RandomObjectFactory.makeRandomWisdomlessVote();
        underTest.getVotePresentationForVote(vote);
        verify(userService).getUserWithLogin(vote.getAddedByUsername());
    }

    @Test
    public void testCanAccuratelyGenerateVotePresentationFromVote() {
        IVote vote = RandomObjectFactory.makeRandomWisdomlessVote();
        IUser user = RandomObjectFactory.makeRandomUser();
        when(userService.getUserWithLogin(user.getLoginUsername())).thenReturn(user);
        vote.setAddedByUsername(user.getLoginUsername());

        VotePresentation votePresentation = underTest.getVotePresentationForVote(vote);

        assertEquals(user.getLoginUsername(), votePresentation.getAddedByUsername());
        assertEquals(user.getDisplayName(), votePresentation.getDisplayName());
    }

    @Test
    public void testDeleteVoteIsAPassthroughToTheRepository() {
        IVote mockVote = Mockito.mock(IVote.class);
        underTest.removeVote(mockVote);
        verify(voteRepository).delete(mockVote);
    }
}