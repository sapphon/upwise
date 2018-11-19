package org.sapphon.personal.upwise.repository.jpa;

import org.sapphon.personal.upwise.AbstractUser;
import org.sapphon.personal.upwise.AbstractVote;
import org.sapphon.personal.upwise.IWisdom;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class UserJpa extends AbstractUser {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    protected UserJpa(){
        super();
    }

    public UserJpa(String loginUsername, String displayUsername, Timestamp timeAdded) {
        super(loginUsername, displayUsername, timeAdded);
    }
}
