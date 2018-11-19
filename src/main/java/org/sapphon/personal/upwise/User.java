package org.sapphon.personal.upwise;

import java.sql.Timestamp;

public class User extends AbstractUser {
    public User(String loginUsername, String displayUsername, Timestamp timeAdded) {
        super(loginUsername, displayUsername, timeAdded);
    }

    public User(){
        super();
    }
}
