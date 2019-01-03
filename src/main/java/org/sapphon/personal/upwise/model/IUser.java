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

    void setPassword(String password);

    void setLoginUsername(String addedByUsername);

    void setDisplayName(String displayName);

    void setTimeAdded(Timestamp timeAdded);

    //endregion
}
