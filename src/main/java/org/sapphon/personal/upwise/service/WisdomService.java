package org.sapphon.personal.upwise.service;

import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.presentation.WisdomWithVotesPresentation;
import org.sapphon.personal.upwise.repository.VoteRepository;
import org.sapphon.personal.upwise.repository.WisdomRepository;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WisdomService {

    private WisdomRepository wisdomRepo;

    private VoteService voteService;

    public WisdomService(WisdomRepository wisdomRepository, VoteService voteService){
        this.wisdomRepo = wisdomRepository;
        this.voteService = voteService;
    }

    public List<IWisdom> getAllWisdoms(){
        return this.wisdomRepo.getAll();
    }

    public List<IWisdom> getAllWisdomsBySubmitter(String username){return this.wisdomRepo.getBySubmitter(username);}

    public IWisdom addOrUpdateWisdom(IWisdom wisdom) {
        return this.wisdomRepo.save(wisdom);
    }

    public Optional<IWisdom> findWisdomByContentAndAttribution(String content, String attribution) { return this.wisdomRepo.findWisdom(content, attribution);}

    public List<WisdomWithVotesPresentation> getAllWisdomsWithVotes(){
        return this.getWisdomsWithVotes(this.getAllWisdoms());
    }

    public List<WisdomWithVotesPresentation> getWisdomsWithVotes(List<IWisdom> wisdoms) {
        return wisdoms.stream().map(wisdom -> DomainObjectFactory.createWisdomWithVotes(wisdom, voteService.getByWisdom(wisdom))).collect(Collectors.toList());
    }
}
