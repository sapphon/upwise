package org.sapphon.upwise.factory;

import org.apache.commons.lang3.RandomStringUtils;
import org.sapphon.upwise.model.*;
import org.sapphon.upwise.model.datatransfer.UserRegistration;
import org.sapphon.upwise.presentation.VotePresentation;
import org.sapphon.upwise.presentation.WisdomPresentation;
import org.sapphon.upwise.time.TimeLord;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

public class RandomObjectFactory {
    public static IWisdom makeRandomWisdom(){
        Random random = new Random();
        return DomainObjectFactory.createWisdom(randomNonEmptyOfMaxLength(256), randomNonEmptyOfMaxLength(50), randomNonEmptyOfMaxLength(50), TimeLord.getTimestampForMillis(random.nextLong()));
    }

    public static WisdomPresentation makeRandomWisdomPresentation(){
        return makeWisdomPresentationFor(makeRandomWisdom());

    }

    public static WisdomPresentation makeWisdomPresentationFor(IWisdom wisdom){
        return DomainObjectFactory.createWisdomPresentation(wisdom, newArrayList(), randomOfLengthBetween(4, 64));
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

    private static String randomNonEmptyOfMaxLength(int upperBoundExclusive){
        return randomOfLengthBetween(1, upperBoundExclusive);
    }

    private static String randomOfLengthBetween(int lowerBoundInclusive, int upperBoundExclusive){
        return RandomStringUtils.randomAlphanumeric(lowerBoundInclusive, upperBoundExclusive);
    }

    public static IUser makeRandomUser(){
        Random random = new Random();
        return DomainObjectFactory.createUser(randomNonEmptyOfMaxLength(32), randomNonEmptyOfMaxLength(128), TimeLord.getTimestampForMillis(random.nextLong()), randomNonEmptyOfMaxLength(16), randomNonEmptyOfMaxLength(32), new Random().nextBoolean());
    }

    public static IAnalyticsEvent makeRandomEvent(){
        return AnalyticsFactory.createAnalyticsEvent(randomNonEmptyOfMaxLength(32), randomNonEmptyOfMaxLength(16), TimeLord.getTimestampForMillis(new Random().nextLong()), chooseRandomType());
    }

    private static AnalyticsAction chooseRandomType() {
        final int randomIndex = new Random().nextInt(AnalyticsAction.values().length - 1);
        return AnalyticsAction.values()[randomIndex];
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

    public static List<VotePresentation> makeRandomListOfWisdomlessVotePresentations(){
        return makeRandomListOfWisdomlessVotes().stream().map( v -> DomainObjectFactory.createVotePresentation(v, randomNonEmptyOfMaxLength(64))).collect(Collectors.toList());
    }

    public static UserRegistration makeValidButRandomUserRegistration() {
        String password = randomOfLengthBetween(4, 64);
        return new UserRegistration().setDesiredUsername(randomOfLengthBetween(4, 16)).setDisplayName(randomNonEmptyOfMaxLength( 64)).setPassword(password).setConfirmPassword(password).setEmail(randomOfLengthBetween(4, 16));
    }
}
