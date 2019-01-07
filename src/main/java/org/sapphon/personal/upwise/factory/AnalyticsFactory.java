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

    public static IAnalyticsEvent createViewLeaderboardEvent(String user){
        return createAnalyticsEvent("[No details]", user, AnalyticsAction.VIEWLEADERBOARD);
    }

    public static IAnalyticsEvent createAddWisdomEvent(HttpStatus status, IWisdom wisdom){
        return createAnalyticsEvent(formatStatusString(status, wisdom), wisdom.getAddedByUsername(), AnalyticsAction.ADDWISDOM);
    }

    public static IAnalyticsEvent createAddVoteEvent(HttpStatus status, IVote vote){
        return createAnalyticsEvent(formatStatusString(status, vote), vote.getAddedByUsername(), AnalyticsAction.ADDVOTE);
    }

    public static IAnalyticsEvent createAddUserEvent(HttpStatus status, IUser user) {
        return createAnalyticsEvent(formatStatusString(status, user), user.getLoginUsername(), AnalyticsAction.ADDUSER);
    }

    private static String formatStatusString(HttpStatus status, Object responseObject) {
        return String.format("[%s %s]: %s", status.toString(), status.getReasonPhrase(), responseObject.toString());
    }

    public static IAnalyticsEvent createViewWisdomEvent(String viewingUsername, IWisdom wisdom) {
        return createAnalyticsEvent(wisdom == null ? "[None Found]" : wisdom.toString(), viewingUsername, AnalyticsAction.VIEWWISDOM);
    }
}
