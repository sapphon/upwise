package org.sapphon.upwise.repository;


import org.sapphon.upwise.model.IUser;
import org.sapphon.upwise.repository.jpa.UserJpa;
import org.sapphon.upwise.time.TimeLord;

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
