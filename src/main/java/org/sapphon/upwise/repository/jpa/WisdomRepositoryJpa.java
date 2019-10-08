package org.sapphon.upwise.repository.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jpaWisdomRepo")
public interface WisdomRepositoryJpa extends CrudRepository<WisdomJpa, Long>{
    List<WisdomJpa> findByAddedByUsernameOrderByTimeAddedDesc(String username);
    Optional<WisdomJpa> findOneById(long id);
    WisdomJpa findOneByWisdomContentAndAttribution(String wisdomContent, String attribution);
}
