package org.sapphon.personal.upwise.model;

import java.sql.Timestamp;

public class BasicAnalyticsEvent extends AbstractAnalyticsEvent {
public BasicAnalyticsEvent(){super();}
public BasicAnalyticsEvent(String eventDescription, String eventInitiator, Timestamp eventOccurrenceTime){
    super(eventDescription, eventInitiator,  eventOccurrenceTime);
}
}
