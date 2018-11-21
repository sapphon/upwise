package org.sapphon.personal.upwise.repository.jpa;

import org.sapphon.personal.upwise.IWisdom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jpaUserRepo")
public interface UserRepositoryJpa extends CrudRepository<UserJpa, Long> {
    List<UserJpa> findAll();
    List<UserJpa> findAllByDisplayUsernameOrderByTimeAddedDesc(String displayUsername);
    List<UserJpa> findAllByLoginUsernameOrderByTimeAddedDesc(String loginUsername);
    Optional<UserJpa> findTopByLoginUsernameAndDisplayUsernameOrderByTimeAddedDesc(String loginUsername, String displayUsername);
}
