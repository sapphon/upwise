package org.sapphon.personal.upwise.repository;


import org.sapphon.personal.upwise.model.IUser;
import org.sapphon.personal.upwise.repository.jpa.UserJpa;

import javax.persistence.*;

@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(targetEntity = UserJpa.class)
    private IUser user;
    private String token;


    public Token() {
    }

    public Token(String token, IUser user) {
        this.token = token;
        this.user = user;
    }

    //region SettersGetters

    public IUser getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
    //endregion

}
