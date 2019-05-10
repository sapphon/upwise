package org.sapphon.personal.upwise.repository.jpa;

import org.sapphon.personal.upwise.model.IUser;
import org.sapphon.personal.upwise.repository.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("jpaTokenRepo")
public interface TokenRepository extends CrudRepository<Token, Long> {
    Token getByToken(String token);
}

