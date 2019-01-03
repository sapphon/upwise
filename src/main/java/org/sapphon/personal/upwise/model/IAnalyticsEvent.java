package org.sapphon.personal.upwise.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.sapphon.personal.upwise.model.IUser;

import java.sql.Timestamp;

@JsonDeserialize(as = BasicAnalyticsEvent.class)
public interface IAnalyticsEvent {
    String getEventDescription();
    String getEventInitiator();
    Timestamp getEventOccurrenceTime();

    void setEventOccurrenceTime(Timestamp newTime);
}
