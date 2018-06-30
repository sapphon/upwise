package org.sapphon.personal.upwise.repository;

import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.WisdomFactory;
import org.sapphon.personal.upwise.repository.jpa.WisdomRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository("wisdomRepo")
public class WisdomRepository {

	@Autowired
    private WisdomRepositoryJpa jpaWisdomRepo;

    protected WisdomRepository(){}

    public void save(IWisdom toSave){
        jpaWisdomRepo.save(WisdomFactory.createWisdomJpa(toSave));
    }

    public void save(List<IWisdom> toSave){
        toSave.forEach(this::save);
    }

    public void save(String wisdomContent, String attribution, String addedByUsername, Timestamp timeAdded){
        jpaWisdomRepo.save(WisdomFactory.createWisdomJpa(wisdomContent, attribution, addedByUsername, timeAdded));
    }

    public List<IWisdom> getAll(){
        List<IWisdom> toReturn = new ArrayList<>();
        jpaWisdomRepo.findAll().forEach((j) -> toReturn.add(WisdomFactory.createWisdom(j)));
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
}
