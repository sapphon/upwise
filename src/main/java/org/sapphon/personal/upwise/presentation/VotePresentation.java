package org.sapphon.personal.upwise.presentation;

import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.Vote;
import org.sapphon.personal.upwise.model.Wisdom;

public class VotePresentation extends Vote {

    private final String displayName;

    public VotePresentation(String displayName, IVote vote) {
        super(vote.getWisdom(), vote.getAddedByUsername(), vote.getTimeAdded());
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
