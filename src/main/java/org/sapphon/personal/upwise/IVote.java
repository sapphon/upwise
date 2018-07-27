package org.sapphon.personal.upwise;

import java.sql.Timestamp;

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
