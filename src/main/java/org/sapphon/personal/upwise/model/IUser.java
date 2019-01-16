package org.sapphon.personal.upwise.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.sql.Timestamp;

@JsonDeserialize(as=User.class)
public interface IUser {
    //region SettersGetters


    Timestamp getTimeAdded();

    String getDisplayName();

    String getLoginUsername();

    String getPassword();

    IUser setPassword(String password);

    IUser setLoginUsername(String addedByUsername);

    IUser setDisplayName(String displayName);

    IUser setTimeAdded(Timestamp timeAdded);

    //endregion
}
