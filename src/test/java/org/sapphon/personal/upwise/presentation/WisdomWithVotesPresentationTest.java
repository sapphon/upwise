package org.sapphon.personal.upwise.presentation;

import org.junit.Test;
import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;

public class WisdomWithVotesPresentationTest {

    @Test
    public void isVotedForByUser() {
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        List<IVote> listOfVotes = newArrayList( RandomObjectFactory.makeRandomVoteForWisdom(randomWisdom));

        WisdomWithVotesPresentation underTest = new WisdomWithVotesPresentation(randomWisdom, listOfVotes);

        assertTrue(underTest.isVotedForBy(listOfVotes.get(0).getAddedByUsername()));
        assertFalse(underTest.isVotedForBy(RandomObjectFactory.makeRandomWisdomlessVote().getAddedByUsername()));
    }
}