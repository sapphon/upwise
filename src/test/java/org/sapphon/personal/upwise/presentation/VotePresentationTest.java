package org.sapphon.personal.upwise.presentation;

import org.junit.Test;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.Vote;

import static org.junit.Assert.*;

public class VotePresentationTest {

    @Test
    public void testImplementsVoteInterface() {
        assertTrue(new VotePresentation("", RandomObjectFactory.makeRandomWisdomlessVote()) instanceof Vote);
    }

    @Test
    public void doesNotPermuteConstructorArguments() {
        String displayName = "Katline Mcgrew";
        IVote vote = RandomObjectFactory.makeRandomWisdomlessVote();

        VotePresentation votePresentation = new VotePresentation(displayName, vote);

        assertEquals(vote.getWisdom(), votePresentation.getWisdom());
        assertEquals(vote.getAddedByUsername(), votePresentation.getAddedByUsername());
        assertEquals(vote.getTimeAdded(), votePresentation.getTimeAdded());
        assertEquals(displayName, votePresentation.getDisplayName());
    }
}