package org.sapphon.personal.upyougo.factory;

import org.sapphon.personal.upyougo.IWisdom;
import org.sapphon.personal.upyougo.Wisdom;
import org.sapphon.personal.upyougo.repository.jpa.WisdomJpa;
import org.sapphon.personal.upyougo.time.TimeLord;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

public class WisdomFactory {
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
}
