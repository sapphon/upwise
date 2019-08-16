package org.sapphon.personal.upwise.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.sapphon.personal.upwise.repository.jpa.WisdomJpa;

import java.sql.Timestamp;

@JsonDeserialize(as = WisdomJpa.class)
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
