package org.sapphon.personal.upwise.model;

import java.sql.Timestamp;

public class User extends AbstractUser {

    public User(){
        super();
    }

    public User(String loginUsername, String displayUsername, Timestamp timeAdded, String password, String email) {
        super(loginUsername, displayUsername, timeAdded, password, email);
    }

}
