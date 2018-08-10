package org.sapphon.personal.upwise.factory;

import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.Vote;
import org.sapphon.personal.upwise.Wisdom;
import org.sapphon.personal.upwise.repository.VoteRepository;
import org.sapphon.personal.upwise.repository.WisdomRepository;
import org.sapphon.personal.upwise.repository.jpa.VoteJpa;
import org.sapphon.personal.upwise.repository.jpa.WisdomJpa;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.context.annotation.Bean;

import java.sql.Timestamp;

public class DomainObjectFactory {

    private WisdomRepository wisdomRepo;

    private VoteRepository voteRepo;

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

    public static IVote createVote(IVote vote) {
        return createVote(vote.getWisdom(), vote.getAddedByUsername(), vote.getTimeAdded());
    }

    public static VoteJpa createVoteJpa(IWisdom wisdom, String submitterUsername, Timestamp timeAdded){
        return new VoteJpa(wisdom, submitterUsername, timeAdded);
    }


    public static IVote createVote(IWisdom wisdom, String addedByUsername, Timestamp timeAdded) {
        return new Vote(wisdom, addedByUsername,timeAdded);
    }


}