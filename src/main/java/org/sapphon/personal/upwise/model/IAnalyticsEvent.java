package org.sapphon.personal.upwise.model;

import org.sapphon.personal.upwise.model.IUser;

import java.sql.Timestamp;

public interface IAnalyticsEvent {
    String getEventDescription();
    String getEventInitiator();
    Timestamp getEventOccurrenceTime();
}
