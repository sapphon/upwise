package org.sapphon.personal.upwise;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.sql.Timestamp;

@JsonDeserialize(as = Vote.class)
public interface IVote {

    //region SettersGetters

    IWisdom getWisdom();

    String getAddedByUsername();

    Timestamp getTimeAdded();

    void setTimeAdded(Timestamp timeAdded);

    void setAddedByUsername(String addedByUsername);

    void setWisdom(IWisdom wisdom);

    //endregion

}
