package org.sapphon.personal.upyougo.repository.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("jpaWisdomRepo")
public interface WisdomRepositoryJpa extends CrudRepository<WisdomJpa, Long>{
    List<WisdomJpa> findByAddedByUsernameOrderByTimeAddedDesc(String username);
    WisdomJpa findOneById(long id);
}
