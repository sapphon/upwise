package org.sapphon.personal.upwise.service;

import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoteService {
    @Autowired
    private VoteRepository voteRepository;


    public List<IVote> getAllVotes(){
        return this.voteRepository.getAll();
    }

    public List<IVote> getAllByVoter(String username){
        return this.voteRepository.getBySubmitter(username);
    }

    public List<IVote> getByWisdom(IWisdom wisdom){return voteRepository.getByWisdom(wisdom); }

    public Optional<IVote> getByWisdomAndVoterUsername(IWisdom wisdom, String userName){return voteRepository.findByWisdomAndVoterUsername(wisdom, userName); }

    public IVote addOrUpdateVote(IVote vote){
        return this.voteRepository.save(vote);
    }
}
