package org.sapphon.personal.upyougo;

import java.sql.Timestamp;

public class Wisdom extends AbstractWisdom {

    public Wisdom(String wisdomContent, String attribution, String addedByUsername, Timestamp timeAdded) {
        super(wisdomContent, attribution, addedByUsername, timeAdded);
    }
}
