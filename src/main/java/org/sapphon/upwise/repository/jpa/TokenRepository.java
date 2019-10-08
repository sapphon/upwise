package org.sapphon.upwise.repository.jpa;

import org.sapphon.upwise.repository.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("jpaTokenRepo")
public interface TokenRepository extends CrudRepository<Token, Long> {
    Token getByToken(String token);
}

