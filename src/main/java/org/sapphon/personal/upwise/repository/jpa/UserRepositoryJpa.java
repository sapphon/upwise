package org.sapphon.personal.upwise.repository.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("jpaUserRepo")
public interface UserRepositoryJpa extends CrudRepository<UserJpa, Long> {
    List<UserJpa> findAll();
    UserJpa findByLoginUsername(String loginUsername);
    List<UserJpa> findByDisplayUsername(String displayUsername);
}
