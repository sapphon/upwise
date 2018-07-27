package org.sapphon.personal.upwise.repository;

import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.IWisdom;
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

	@Autowired
    private VoteRepositoryJpa jpaVoteRepo;

	@Autowired
    private WisdomRepository wisdomRepository;

    protected VoteRepository(){}

    public IVote save(IVote toSave){
        return this.save(toSave.getWisdom(), toSave.getAddedByUsername(), toSave.getTimeAdded());
    }

    public void save(List<IVote> toSave){
        toSave.forEach(this::save);
    }

    public IVote save(IWisdom wisdom, String addedByUsername, Timestamp timeAdded){
        VoteJpa toSave = DomainObjectFactory.createVoteJpa(wisdomRepository.save(wisdom), addedByUsername, timeAdded);
        return jpaVoteRepo.save(toSave);
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
        return new ArrayList<>(jpaVoteRepo.findByWisdom(wisdomRepository.saveImpl(wisdom)));
    }

    public IVote getById(long id) {
        return jpaVoteRepo.findOneById(id);
    }

	public void clear() {
        jpaVoteRepo.deleteAll();
	}

    public Optional<VoteJpa> findVote(IVote template){
        VoteJpa found = jpaVoteRepo.findOneByWisdomAndAddedByUsernameAndTimeAdded(wisdomRepository.saveImpl(template.getWisdom()), template.getAddedByUsername(), template.getTimeAdded());
        return found == null ? Optional.empty() : Optional.of(found);
    }

}
