package org.sapphon.personal.upwise.factory;

import org.sapphon.personal.upwise.model.*;
import org.sapphon.personal.upwise.repository.jpa.AnalyticsEventJpa;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

public class AnalyticsFactory {
    public static AnalyticsEventJpa createAnalyticsEventJpa(IAnalyticsEvent event) {
        return new AnalyticsEventJpa(event.getEventDescription(), event.getEventInitiator(), event.getEventTime(), event.getEventType());
    }

    public static IAnalyticsEvent createAnalyticsEvent(AnalyticsEventJpa eventJpa) {
        return new BasicAnalyticsEvent(eventJpa.getEventDescription(), eventJpa.getEventInitiator(), eventJpa.getEventTime(), eventJpa.getEventType());
    }

    public static IAnalyticsEvent createAnalyticsEvent(String description, String user, Timestamp time, AnalyticsAction eventType) {
        return new BasicAnalyticsEvent(description, user, time, eventType);
    }

    public static IAnalyticsEvent createAnalyticsEvent(String description, String user, AnalyticsAction eventType) {
        return createAnalyticsEvent(description, user, null, eventType);
    }

    public static IAnalyticsEvent createAddWisdomEvent(HttpStatus status, IWisdom wisdom){
        return createAnalyticsEvent(String.format("[%s %s]: %s", status.toString(), status.getReasonPhrase(), wisdom.toString()), wisdom.getAddedByUsername(), AnalyticsAction.ADDWISDOM);
    }

    public static IAnalyticsEvent createAddVoteEvent(HttpStatus status, IVote vote){
        return createAnalyticsEvent(String.format("[%s %s]: %s", status.toString(), status.getReasonPhrase(), vote.toString()), vote.getAddedByUsername(), AnalyticsAction.ADDVOTE);
    }
}
