package org.sapphon.upwise.service;

import org.sapphon.upwise.model.IUser;
import org.sapphon.upwise.model.IVote;
import org.sapphon.upwise.model.IWisdom;
import org.sapphon.upwise.presentation.VotePresentation;
import org.sapphon.upwise.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoteService {
    private VoteRepository voteRepository;
    private final UserService userService;

    public VoteService(VoteRepository voteRepository, UserService userService) {
        this.voteRepository = voteRepository;
        this.userService = userService;
    }

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

    public List<VotePresentation> getVotePresentationForVotes(List<IVote> votes){
        return votes.stream().map(this::getVotePresentationForVote).collect(Collectors.toList());
    }

    public VotePresentation getVotePresentationForVote(IVote vote) {
        IUser voter = userService.getUserWithLogin(vote.getAddedByUsername());
        String displayName = voter != null ? voter.getDisplayName() : vote.getAddedByUsername();
        VotePresentation votePresentation = new VotePresentation(displayName, vote);
        return votePresentation;
    }

    public void removeVote(IVote voteToRemove) {
        this.voteRepository.delete(voteToRemove);
    }
}
