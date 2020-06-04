package org.sapphon.upwise.presentation;

import org.sapphon.upwise.model.AbstractWisdom;
import org.sapphon.upwise.model.IWisdom;

import java.util.List;

public class WisdomPresentation extends AbstractWisdom {

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

    @Override
    public Long getIdentifier() {
        //If you'd like to treat wisdoms as data, please use the API endpoints
        return -1L;
    }
    //endregion
}
