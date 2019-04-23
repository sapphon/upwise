package org.sapphon.personal.upwise.repository;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Token {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    String token;

    public Token(){

    }
    public Token(String token){
        this.token = token;
    }

}
