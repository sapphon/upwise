package org.sapphon.upwise.repository;

import org.sapphon.upwise.model.IWisdom;
import org.sapphon.upwise.factory.DomainObjectFactory;
import org.sapphon.upwise.repository.jpa.WisdomJpa;
import org.sapphon.upwise.repository.jpa.WisdomRepositoryJpa;
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
        return this.getOrCreate(toSave);
    }

    WisdomJpa getOrCreate(IWisdom toSave){
        Optional<WisdomJpa> wisdomFound = this.findWisdomJpa(toSave.getWisdomContent(), toSave.getAttribution());
        return wisdomFound.orElseGet(() -> jpaWisdomRepo.save(DomainObjectFactory.createWisdomJpa(toSave)));
    }

    public void save(List<IWisdom> toSave){
        toSave.forEach(this::save);
    }

    public void save(String wisdomContent, String attribution, String addedByUsername, Timestamp timeAdded){
        this.save(DomainObjectFactory.createWisdom(wisdomContent, attribution, addedByUsername, timeAdded));
    }

    public boolean delete(Long identifier){
        boolean toReturn = jpaWisdomRepo.existsById(identifier);
        this.jpaWisdomRepo.deleteById(identifier);
        return toReturn;
    }

    public List<IWisdom> getAll(){
        List<IWisdom> toReturn = new ArrayList<>();
        jpaWisdomRepo.findAll().forEach(toReturn::add);
        return toReturn;
    }

    public List<IWisdom> getBySubmitter(String submitter) {
        return new ArrayList<>(jpaWisdomRepo.findByAddedByUsernameOrderByTimeAddedDesc(submitter));
    }

    public Optional<IWisdom> getById(long id) {
        Optional<WisdomJpa> repoCopy = jpaWisdomRepo.findOneById(id);
        return repoCopy.isPresent() ? Optional.of(repoCopy.get()) : Optional.empty();
    }

	void clear() {
		jpaWisdomRepo.deleteAll();
	}

    private Optional<WisdomJpa> findWisdomJpa(String content, String attribution){
        WisdomJpa found = jpaWisdomRepo.findOneByWisdomContentAndAttribution(content, attribution);
        return found == null ? Optional.empty() : Optional.of(found);
    }

    public Optional<IWisdom> findWisdom(String content, String attribution){
        Optional<WisdomJpa> wisdomJpa = findWisdomJpa(content, attribution);
        return wisdomJpa.isPresent() ? Optional.of(wisdomJpa.get()) : Optional.empty();
    }

    public long getCount(){
        return jpaWisdomRepo.count();
    }
}
