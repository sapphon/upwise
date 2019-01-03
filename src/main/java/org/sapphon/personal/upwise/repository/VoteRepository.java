package org.sapphon.personal.upwise.repository;

import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.repository.jpa.VoteJpa;
import org.sapphon.personal.upwise.repository.jpa.VoteRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("voteRepo")
public class VoteRepository {

	private final VoteRepositoryJpa jpaVoteRepo;

	private final WisdomRepository wisdomRepository;

    @Autowired
    protected VoteRepository(VoteRepositoryJpa jpaVoteRepo, WisdomRepository wisdomRepository){
        this.jpaVoteRepo = jpaVoteRepo;
        this.wisdomRepository = wisdomRepository;
    }

    public IVote save(IVote toSave){
        return this.getOrCreate(toSave);
    }

    IVote getOrCreate(IVote toPersist){
        Optional<IVote> voteFound = this.findVote(toPersist);
        return voteFound.orElseGet(() -> jpaVoteRepo.save(DomainObjectFactory.createVoteJpa(wisdomRepository.save(toPersist.getWisdom()), toPersist.getAddedByUsername(), toPersist.getTimeAdded())));
    }

    public void save(List<IVote> toSave){
        toSave.forEach(this::save);
    }

    public IVote save(IWisdom wisdom, String addedByUsername, Timestamp timeAdded){
        VoteJpa toSave = DomainObjectFactory.createVoteJpa(wisdomRepository.save(wisdom), addedByUsername, timeAdded);
        return this.save(toSave);
    }

    public List<IVote> getAll(){
        List<IVote> toReturn = new ArrayList<>();
        jpaVoteRepo.findAll().forEach((j) -> toReturn.add(DomainObjectFactory.createVote(j)));
        return toReturn;
    }

    public List<IVote> getBySubmitter(String submitter) {
        return new ArrayList<>(jpaVoteRepo.findByAddedByUsernameOrderByTimeAddedDesc(submitter));
    }

    public List<IVote> getByWisdom(IWisdom wisdom){
        return new ArrayList<>(jpaVoteRepo.findByWisdom(wisdomRepository.getOrCreate(wisdom)));
    }

    public IVote getById(long id) {
        return jpaVoteRepo.findOneById(id);
    }

	public void clear() {
        jpaVoteRepo.deleteAll();
	}

	public Optional<IVote> findByWisdomAndVoterUsername(IWisdom wisdom, String userName){
        Optional<IWisdom> wisdomFound = this.wisdomRepository.findWisdom(wisdom.getWisdomContent(), wisdom.getAttribution());
        if(!wisdomFound.isPresent()){
            return Optional.empty();
        }
        VoteJpa found = jpaVoteRepo.findOneByWisdomAndAddedByUsername(wisdomRepository.getOrCreate(wisdom), userName);
        return found == null ? Optional.empty() : Optional.of(found);
    }

    public Optional<IVote> findVote(IVote template){
        return this.findByWisdomAndVoterUsername(template.getWisdom(), template.getAddedByUsername());
    }
}
