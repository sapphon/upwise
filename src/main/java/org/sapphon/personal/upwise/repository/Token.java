package org.sapphon.personal.upwise.repository;


import org.sapphon.personal.upwise.model.IUser;
import org.sapphon.personal.upwise.repository.jpa.UserJpa;
import org.sapphon.personal.upwise.time.TimeLord;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(targetEntity = UserJpa.class)
    private IUser user;
    private String token;


    private Timestamp timeCreated;

    public Token(){
    }

    public Token(String token, IUser user) {
        this.token = token;
        this.user = user;
        this.timeCreated = TimeLord.getNow();
    }

    //region SettersGetters

    public IUser getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public Timestamp getTimeCreated(){
        return this.timeCreated;
    }
    //endregion

}
