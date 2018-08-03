package org.sapphon.personal.upwise.repository.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository("jpaWisdomRepo")
public interface WisdomRepositoryJpa extends CrudRepository<WisdomJpa, Long>{
    List<WisdomJpa> findByAddedByUsernameOrderByTimeAddedDesc(String username);
    WisdomJpa findOneById(long id);
    WisdomJpa findOneByWisdomContentAndAttribution(String wisdomContent, String attribution);
}
