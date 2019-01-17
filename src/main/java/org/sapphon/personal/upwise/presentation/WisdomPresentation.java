package org.sapphon.personal.upwise.presentation;

import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.model.Wisdom;

import java.util.List;

public class WisdomPresentation extends Wisdom {

    private List<VotePresentation> votes;
    private String addedByDisplayName;

    public WisdomPresentation(IWisdom wisdom, List<VotePresentation> votes, String submitterDisplayName){
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
    public List<VotePresentation> getVotes() {
        return votes;
    }
    //endregion
}
