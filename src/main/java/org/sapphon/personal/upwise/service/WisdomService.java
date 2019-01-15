package org.sapphon.personal.upwise.service;

import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.presentation.WisdomPresentation;
import org.sapphon.personal.upwise.repository.WisdomRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WisdomService {

    private WisdomRepository wisdomRepo;

    private VoteService voteService;

    public WisdomService(WisdomRepository wisdomRepository, VoteService voteService) {
        this.wisdomRepo = wisdomRepository;
        this.voteService = voteService;
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

    public Optional<IWisdom> findWisdomByContentAndAttribution(String content, String attribution) {
        return this.wisdomRepo.findWisdom(content, attribution);
    }

    public Optional<IWisdom> findWisdom(IWisdom wisdom) {
        return this.findWisdomByContentAndAttribution(wisdom.getWisdomContent(), wisdom.getAttribution());
    }

    public List<WisdomPresentation> getAllWisdomsWithVotes() {
        return this.getWisdomsWithVotes(this.getAllWisdoms()).stream().sorted(new Comparator<WisdomPresentation>() {
            @Override
            public int compare(WisdomPresentation o1, WisdomPresentation o2) {
                return Integer.compare(o2.getVotes().size(), o1.getVotes().size());
            }
        }).collect(Collectors.toList());
    }

    public List<WisdomPresentation> getWisdomsWithVotes(List<IWisdom> wisdoms) {
        return wisdoms.stream().map(this::getWisdomWithVotes).collect(Collectors.toList());
    }

    public WisdomPresentation getWisdomWithVotes(IWisdom wisdom){
        return DomainObjectFactory.createWisdomWithVotes(wisdom, voteService.getByWisdom(wisdom));
    }

    public boolean hasAnyWisdoms() {
        return this.wisdomRepo.getCount() > 0;
    }
}
