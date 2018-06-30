package org.sapphon.personal.upyougo;

import java.sql.Timestamp;

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
