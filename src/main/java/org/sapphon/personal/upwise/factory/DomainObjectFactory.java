package org.sapphon.personal.upwise.factory;

import org.sapphon.personal.upwise.model.*;
import org.sapphon.personal.upwise.presentation.WisdomWithVotesPresentation;
import org.sapphon.personal.upwise.repository.jpa.AnalyticsEventJpa;
import org.sapphon.personal.upwise.repository.jpa.UserJpa;
import org.sapphon.personal.upwise.repository.jpa.VoteJpa;
import org.sapphon.personal.upwise.repository.jpa.WisdomJpa;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class DomainObjectFactory {

    public static IWisdom createWisdom(String content, String utterer, String submitter, Timestamp time){
        return new Wisdom(content, utterer, submitter, time);
    }

    public static WisdomJpa createWisdomJpa(String content, String utterer, String submitter, Timestamp time){
        return new WisdomJpa(content, utterer, submitter, time);
    }

    public static IWisdom createWisdomWithCreatedTimeNow(String content, String utterer, String submitter){
        return createWisdom(content, utterer, submitter, TimeLord.getNow());
    }

    public static WisdomJpa createWisdomJpa(IWisdom wisdom){
        return createWisdomJpa(wisdom.getWisdomContent(), wisdom.getAttribution(), wisdom.getAddedByUsername(), wisdom.getTimeAdded());
    }

    public static IWisdom createWisdom(IWisdom wisdom){
        return createWisdom(wisdom.getWisdomContent(), wisdom.getAttribution(), wisdom.getAddedByUsername(), wisdom.getTimeAdded());
    }

    public static VoteJpa createVoteJpa(IVote vote){
        return createVoteJpa(createWisdomJpa(vote.getWisdom()), vote.getAddedByUsername(), vote.getTimeAdded());
    }

    public static IUser createUser(IUser user){
        return createUser(user.getLoginUsername(), user.getDisplayName(), user.getTimeAdded(), user.getPassword());
    }

    public static IUser createUserWithCreatedTimeNow(String loginUsername, String displayUsername, String password){
        return createUser(loginUsername, displayUsername, TimeLord.getNow(), password);
    }

    public static IUser createUser(String loginUsername, String displayUsername, Timestamp timeAdded, String password) {
        return new User(loginUsername, displayUsername, timeAdded, password);
    }

    public static UserJpa createUserJpa(IUser user){
        return new UserJpa(user.getLoginUsername(), user.getDisplayName(), user.getTimeAdded(), user.getPassword());
    }

    public static IVote createVote(IVote vote) {
        return createVote(vote.getWisdom(), vote.getAddedByUsername(), vote.getTimeAdded());
    }

    public static VoteJpa createVoteJpa(IWisdom wisdom, String submitterUsername, Timestamp timeAdded){
        return new VoteJpa(wisdom, submitterUsername, timeAdded);
    }


    public static IVote createVote(IWisdom wisdom, String addedByUsername, Timestamp timeAdded) {
        return new Vote(wisdom, addedByUsername,timeAdded);
    }

    public static WisdomWithVotesPresentation createWisdomWithVotes(IWisdom wisdom, List<IVote> votes){
        return new WisdomWithVotesPresentation(wisdom, votes);
    }

    public static UserDetails createUserDetailsFromUser(IUser user) {
        return new UserDetailsUserWrapper(user);
    }

    public static AnalyticsEventJpa createAnalyticsEventJpa(IAnalyticsEvent event) {
        return new AnalyticsEventJpa(event.getEventDescription(), event.getEventInitiator(), event.getEventOccurrenceTime());
    }

    public static IAnalyticsEvent createAnalyticsEvent(AnalyticsEventJpa eventJpa) {
        return new BasicAnalyticsEvent(eventJpa.getEventDescription(), eventJpa.getEventInitiator(), eventJpa.getEventOccurrenceTime());
    }

    public static IAnalyticsEvent createAnalyticsEvent(String description, String user, Timestamp time) {
        return new BasicAnalyticsEvent(description, user, time);
    }

}
