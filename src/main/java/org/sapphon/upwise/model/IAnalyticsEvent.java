package org.sapphon.upwise.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.sql.Timestamp;

@JsonDeserialize(as = BasicAnalyticsEvent.class)
public interface IAnalyticsEvent {
    AnalyticsAction getEventType();
    String getEventDescription();
    String getEventInitiator();
    Timestamp getEventTime();

    void setEventTime(Timestamp newTime);
    void setEventInitiator(String newInitiator);
}
