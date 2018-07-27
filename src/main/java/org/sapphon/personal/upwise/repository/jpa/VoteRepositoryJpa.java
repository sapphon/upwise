package org.sapphon.personal.upwise.repository.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository("jpaVoteRepo")
public interface VoteRepositoryJpa extends CrudRepository<VoteJpa, Long>{
    List<VoteJpa> findByAddedByUsernameOrderByTimeAddedDesc(String username);
    VoteJpa findOneById(long id);
    List<VoteJpa> findByWisdom(WisdomJpa wisdom);
    VoteJpa findOneByWisdomAndAddedByUsernameAndTimeAdded(WisdomJpa wisdom, String username, Timestamp timeAdded);
}
