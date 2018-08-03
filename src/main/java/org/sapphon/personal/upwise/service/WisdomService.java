package org.sapphon.personal.upwise.service;

import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.repository.VoteRepository;
import org.sapphon.personal.upwise.repository.WisdomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WisdomService {
    @Autowired
    WisdomRepository wisdomRepo;

    @Autowired
    VoteRepository voteRepo;

    public List<IWisdom> getAllWisdoms(){
        return this.wisdomRepo.getAll();
    }

    public void addOrUpdateWisdom(IWisdom wisdom) {
        this.wisdomRepo.save(wisdom);
    }
}
