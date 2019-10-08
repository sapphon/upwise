package org.sapphon.upwise.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.sql.Timestamp;

@JsonDeserialize(as = Vote.class)
public interface IVote {

    //region SettersGetters

    IWisdom getWisdom();

    String getAddedByUsername();

    Timestamp getTimeAdded();

    IVote setTimeAdded(Timestamp timeAdded);

    IVote setAddedByUsername(String addedByUsername);

    IVote setWisdom(IWisdom wisdom);

    //endregion

}
