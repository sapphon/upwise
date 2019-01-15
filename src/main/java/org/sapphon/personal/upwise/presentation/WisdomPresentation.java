package org.sapphon.personal.upwise.presentation;

import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.model.Wisdom;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WisdomPresentation extends Wisdom {

    private List<IVote> votes;

    public WisdomPresentation(IWisdom wisdom, List<IVote> votes){
        super(wisdom.getWisdomContent(), wisdom.getAttribution(), wisdom.getAddedByUsername(), wisdom.getTimeAdded());
        this.votes = votes;
    }

    public boolean isVotedForBy(String username){
        return getVotes().stream().anyMatch(vote -> vote.getAddedByUsername().equalsIgnoreCase(username));
    }

    //region GettersSetters
    public List<IVote> getVotes() {
        return votes;
    }
    //endregion
}
