package org.sapphon.personal.upwise.factory;

import org.apache.commons.lang3.RandomStringUtils;
import org.sapphon.personal.upwise.model.*;
import org.sapphon.personal.upwise.time.TimeLord;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomObjectFactory {
    public static IWisdom makeRandomWisdom(){
        Random random = new Random();
        return DomainObjectFactory.createWisdom(randomNonEmptyOfMaxLength(256), randomNonEmptyOfMaxLength(50), randomNonEmptyOfMaxLength(50), TimeLord.getTimestampForMillis(random.nextLong()));
    }

    public static List<IWisdom> makeRandomListOfWisdoms() {
        List<IWisdom> toReturn = new ArrayList<>();
        for(int i = 0; i < new Random().nextInt(20) + 1; i++){
            toReturn.add(makeRandomWisdom());
        }
        return toReturn;
    }

    public static IVote makeRandomWisdomlessVote(){
        Random random = new Random();
        return DomainObjectFactory.createVote(null, randomNonEmptyOfMaxLength(16), TimeLord.getTimestampForMillis(random.nextLong()));
    }

    private static String randomNonEmptyOfMaxLength(int length){
        return RandomStringUtils.randomAlphanumeric(1, length);
    }

    public static IUser makeRandomUser(){
        Random random = new Random();
        return DomainObjectFactory.createUser(randomNonEmptyOfMaxLength(32), randomNonEmptyOfMaxLength(128), TimeLord.getTimestampForMillis(random.nextLong()), randomNonEmptyOfMaxLength(16));
    }

    public static IAnalyticsEvent makeRandomEvent(){
        return DomainObjectFactory.createAnalyticsEvent(randomNonEmptyOfMaxLength(32), randomNonEmptyOfMaxLength(16), TimeLord.getTimestampForMillis(new Random().nextLong()));
    }

    public static IVote makeRandomVoteForWisdom(IWisdom wisdom) {
        return makeRandomWisdomlessVote().setWisdom(wisdom);
    }

    public static List<IVote> makeRandomListOfWisdomlessVotes(){
        List<IVote> toReturn = new ArrayList<>();
        for(int i = 0; i < new Random().nextInt(20) + 1; i++){
            toReturn.add(makeRandomWisdomlessVote());
        }
        return toReturn;
    }
}
