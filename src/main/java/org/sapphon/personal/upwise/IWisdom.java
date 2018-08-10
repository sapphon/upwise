package org.sapphon.personal.upwise;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.sql.Timestamp;

@JsonDeserialize(as = Wisdom.class)
public interface IWisdom {

    //region SettersGetters

    String getWisdomContent();

    String getAttribution();

    Timestamp getTimeAdded();

    String getAddedByUsername();

    void setAddedByUsername(String addedByUsername);

    void setAttribution(String attribution);

    void setTimeAdded(Timestamp timeAdded);

    void setWisdomContent(String wisdomContent);
    //endregion

}
