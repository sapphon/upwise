package org.sapphon.personal.upwise.service;

import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.repository.VoteRepository;
import org.sapphon.personal.upwise.repository.WisdomRepository;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WisdomService {
    @Autowired
    WisdomRepository wisdomRepo;

    @Autowired
    VoteRepository voteRepo;

    public List<IWisdom> getAllWisdoms(){
        return this.wisdomRepo.getAll();
    }

    public IWisdom addOrUpdateWisdom(IWisdom wisdom) {
        return this.wisdomRepo.save(wisdom);
    }

    public Optional<IWisdom> findWisdomByContentAndAttribution(String content, String attribution) { return this.wisdomRepo.findWisdom(content, attribution);}
}
