package org.sapphon.personal.upwise.presentation;

import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.Wisdom;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WisdomWithVotesPresentation extends Wisdom {

    private List<IVote> votes;
    public WisdomWithVotesPresentation(String wisdomContent, String attribution, String addedByUsername, Timestamp timeAdded) {
        super(wisdomContent, attribution, addedByUsername, timeAdded);
        this.votes = new ArrayList<IVote>();
    }

    public WisdomWithVotesPresentation(IWisdom wisdom, List<IVote> votes){
        this(wisdom.getWisdomContent(), wisdom.getAttribution(), wisdom.getAddedByUsername(), wisdom.getTimeAdded());
        this.votes = votes;
    }


    //region GettersSetters
    public List<IVote> getVotes() {
        return votes;
    }

    public WisdomWithVotesPresentation setVotes(List<IVote> votes) {
        this.votes = votes;
        return this;
    }
    //endregion
}