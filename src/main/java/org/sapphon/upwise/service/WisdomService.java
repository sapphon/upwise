package org.sapphon.upwise.service;

import org.sapphon.upwise.model.IUser;
import org.sapphon.upwise.model.IWisdom;
import org.sapphon.upwise.factory.DomainObjectFactory;
import org.sapphon.upwise.presentation.WisdomPresentation;
import org.sapphon.upwise.repository.WisdomRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WisdomService {

    private final WisdomRepository wisdomRepo;
    private final VoteService voteService;
    private final UserService userService;

    public WisdomService(WisdomRepository wisdomRepository, VoteService voteService, UserService userService) {
        this.wisdomRepo = wisdomRepository;
        this.voteService = voteService;
        this.userService = userService;
    }

    public Optional<IWisdom> findWisdom(Long wisdomId){
        return wisdomRepo.getById(wisdomId);
    }

    public List<IWisdom> getAllWisdoms() {
        return this.wisdomRepo.getAll();
    }

    public List<IWisdom> getAllWisdomsBySubmitter(String username) {
        return this.wisdomRepo.getBySubmitter(username);
    }

    public IWisdom addOrUpdateWisdom(IWisdom wisdom)
    {
        return this.wisdomRepo.save(wisdom);
    }

    public boolean removeWisdom(Long identifier){
        return this.wisdomRepo.delete(identifier);
    }

    public Optional<IWisdom> findWisdomByContentAndAttribution(String content, String attribution) {
        return this.wisdomRepo.findWisdom(content, attribution);
    }

    public Optional<IWisdom> findWisdom(IWisdom wisdom) {
        return this.findWisdomByContentAndAttribution(wisdom.getWisdomContent(), wisdom.getAttribution());
    }

    public List<WisdomPresentation> getAllWisdomPresentations(){
        return this.getWisdomPresentationsForWisdoms(this.getAllWisdoms());
    }

    public List<WisdomPresentation> getAllWisdomPresentationsSortedByNumberOfVotes() {
        return this.getAllWisdomPresentations().stream().sorted(new Comparator<WisdomPresentation>() {
            @Override
            public int compare(WisdomPresentation o1, WisdomPresentation o2) {
                return Integer.compare(o2.getVotes().size(), o1.getVotes().size());
            }
        }).collect(Collectors.toList());
    }

    public List<WisdomPresentation> getAllWisdomPresentationsSortedByTimeAdded() {
        return this.getAllWisdomPresentations().stream().sorted(new Comparator<WisdomPresentation>() {
            @Override
            public int compare(WisdomPresentation o1, WisdomPresentation o2) {
                return o2.getTimeAdded().compareTo(o1.getTimeAdded());
            }
        }).collect(Collectors.toList());
    }

    public List<WisdomPresentation> getWisdomPresentationsForWisdoms(List<IWisdom> wisdoms) {
        return wisdoms.stream().map(this::getWisdomPresentationForWisdom).collect(Collectors.toList());
    }

    public WisdomPresentation getWisdomPresentationForWisdom(IWisdom wisdom){
        IUser possibleUser = userService.getUserWithLogin(wisdom.getAddedByUsername());
        return DomainObjectFactory.createWisdomPresentation(wisdom, voteService.getVotePresentationForVotes(voteService.getByWisdom(wisdom)), possibleUser == null ? wisdom.getAddedByUsername() : possibleUser.getDisplayName());
    }

    public boolean hasAnyWisdoms() {
        return this.wisdomRepo.getCount() > 0;
    }

    public List<IWisdom> getAllWisdomsByAttribution(String attributionSearch) {
        List<IWisdom> toReturn = new ArrayList<>();

        for (IWisdom wisdom : getAllWisdoms()) {
            if (wisdom.getAttribution().contains(attributionSearch)) {
                toReturn.add(wisdom);
            }
        }
        return toReturn;
    }
}
