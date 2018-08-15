package org.sapphon.personal.upwise.presentation;

import org.sapphon.personal.upwise.AbstractVote;
import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.service.VoteService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class VoteDto implements DTO<IVote> {
    private final String submittedByUser;
    private final String wisdomContent;
    private final String wisdomAttribution;

    @Autowired
    private VoteService voteService;

    @Autowired
    private WisdomService wisdomService;

    public VoteDto(String submittedByUser, String wisdomContent, String wisdomAttribution){

        this.submittedByUser = submittedByUser;
        this.wisdomContent = wisdomContent;
        this.wisdomAttribution = wisdomAttribution;
    }

    @Override
    public IVote getModelObject() {
        Optional<IWisdom> wisdomFound = wisdomService.findWisdomByContentAndAttribution(this.wisdomContent, this.wisdomAttribution);
        if(!wisdomFound.isPresent()){
            return null;
        }
        return voteService.addOrUpdateVote(DomainObjectFactory.createVote(wisdomFound.get(), submittedByUser, TimeLord.getNow()));
    }
}
