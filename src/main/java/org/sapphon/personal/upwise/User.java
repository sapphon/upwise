package org.sapphon.personal.upwise;

import java.sql.Timestamp;

public class User extends AbstractUser {

    public User(){
        super();
    }

    public User(String loginUsername, String displayUsername, Timestamp timeAdded, String password) {
        super(loginUsername, displayUsername, timeAdded, password);
    }

}
