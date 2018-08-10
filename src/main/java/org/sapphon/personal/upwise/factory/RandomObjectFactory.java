package org.sapphon.personal.upwise.factory;

import org.apache.commons.lang3.RandomStringUtils;
import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.time.TimeLord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;

public class RandomObjectFactory {
    public static IWisdom makeRandom(){
        Random random = new Random();
        return DomainObjectFactory.createWisdom(RandomStringUtils.random(random.nextInt(255),true,true), RandomStringUtils.random(random.nextInt(50),true,true), RandomStringUtils.random(random.nextInt(50),true,true), TimeLord.getTimestampForMillis(random.nextLong()));
    }

    public static List<IWisdom> makeRandomCollection() {
        List<IWisdom> toReturn = new ArrayList<>();
        for(int i = 0; i < new Random().nextInt(20) + 1; i++){
            toReturn.add(makeRandom());
        }
        return toReturn;
    }

    public static IVote makeRandomWisdomlessVote(){
        Random random = new Random();
        return DomainObjectFactory.createVote(null, RandomStringUtils.random(random.nextInt(50),true,true), TimeLord.getTimestampForMillis(random.nextLong()));
    }
}
