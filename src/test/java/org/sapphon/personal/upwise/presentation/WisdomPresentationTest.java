package org.sapphon.personal.upwise.presentation;

import org.junit.Test;
import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;

public class WisdomPresentationTest {

    @Test
    public void isVotedForByUser() {
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        List<IVote> listOfVotes = newArrayList( RandomObjectFactory.makeRandomVoteForWisdom(randomWisdom));

        WisdomPresentation underTest = new WisdomPresentation(randomWisdom, listOfVotes, "dontcare");

        assertTrue(underTest.isVotedForBy(listOfVotes.get(0).getAddedByUsername()));
        assertFalse(underTest.isVotedForBy(RandomObjectFactory.makeRandomWisdomlessVote().getAddedByUsername()));
    }

    @Test
    public void doesNotPermuteDisplayName() {
        IWisdom randomWisdom = RandomObjectFactory.makeRandomWisdom();
        List<IVote> listOfVotes = newArrayList( RandomObjectFactory.makeRandomVoteForWisdom(randomWisdom));

        WisdomPresentation underTest = new WisdomPresentation(randomWisdom, listOfVotes, "YANANANA");

        assertTrue(underTest.isVotedForBy(listOfVotes.get(0).getAddedByUsername()));
        assertFalse(underTest.isVotedForBy(RandomObjectFactory.makeRandomWisdomlessVote().getAddedByUsername()));
    }
}