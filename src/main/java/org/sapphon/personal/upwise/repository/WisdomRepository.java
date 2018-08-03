package org.sapphon.personal.upwise.repository;

import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.repository.jpa.WisdomJpa;
import org.sapphon.personal.upwise.repository.jpa.WisdomRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("wisdomRepo")
public class WisdomRepository {

	private final WisdomRepositoryJpa jpaWisdomRepo;

    @Autowired
    protected WisdomRepository(WisdomRepositoryJpa jpaWisdomRepo){
        this.jpaWisdomRepo = jpaWisdomRepo;
    }

    public IWisdom save(IWisdom toSave){
        return this.saveImpl(toSave);
    }

    WisdomJpa saveImpl(IWisdom toSave){
        Optional<WisdomJpa> wisdomFound = this.findWisdom(toSave);
        return wisdomFound.orElseGet(() -> jpaWisdomRepo.save(DomainObjectFactory.createWisdomJpa(toSave)));
    }

    public void save(List<IWisdom> toSave){
        toSave.forEach(this::save);
    }

    public void save(String wisdomContent, String attribution, String addedByUsername, Timestamp timeAdded){
        this.save(DomainObjectFactory.createWisdom(wisdomContent, attribution, addedByUsername, timeAdded));
    }

    public List<IWisdom> getAll(){
        List<IWisdom> toReturn = new ArrayList<>();
        jpaWisdomRepo.findAll().forEach((j) -> toReturn.add(DomainObjectFactory.createWisdom(j)));
        return toReturn;
    }

    public List<IWisdom> getBySubmitter(String submitter) {
        return new ArrayList<>(jpaWisdomRepo.findByAddedByUsernameOrderByTimeAddedDesc(submitter));
    }

    public IWisdom getById(long id) {
        return jpaWisdomRepo.findOneById(id);
    }

	public void clear() {
		jpaWisdomRepo.deleteAll();
	}

    public Optional<WisdomJpa> findWisdom(IWisdom template){
        WisdomJpa found = jpaWisdomRepo.findOneByWisdomContentAndAttributionAndAddedByUsernameAndTimeAdded(template.getWisdomContent(), template.getAttribution(), template.getAddedByUsername(), template.getTimeAdded());
        return found == null ? Optional.empty() : Optional.of(found);
    }
}
