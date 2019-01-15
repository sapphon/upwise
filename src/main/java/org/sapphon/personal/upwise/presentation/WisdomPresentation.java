package org.sapphon.personal.upwise.presentation;

import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.model.Wisdom;

import java.util.List;

public class WisdomPresentation extends Wisdom {

    private List<IVote> votes;
    private String addedByDisplayName;

    public WisdomPresentation(IWisdom wisdom, List<IVote> votes, String submitterDisplayName){
        super(wisdom.getWisdomContent(), wisdom.getAttribution(), wisdom.getAddedByUsername(), wisdom.getTimeAdded());
        this.addedByDisplayName = submitterDisplayName;
        this.votes = votes;
    }

    public boolean isVotedForBy(String username){
        return getVotes().stream().anyMatch(vote -> vote.getAddedByUsername().equalsIgnoreCase(username));
    }

    public String getAddedByDisplayName(){
        return this.addedByDisplayName;
    }

    //region GettersSetters
    public List<IVote> getVotes() {
        return votes;
    }
    //endregion
}
