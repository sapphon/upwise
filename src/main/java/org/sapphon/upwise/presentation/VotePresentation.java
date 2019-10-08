package org.sapphon.upwise.presentation;

import org.sapphon.upwise.model.IVote;
import org.sapphon.upwise.model.Vote;

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
