package org.sapphon.personal.upwise.model;

import java.sql.Timestamp;

public class Wisdom extends AbstractWisdom {
    public Wisdom(){
        super();
    }

    public Wisdom(String wisdomContent, String attribution, String addedByUsername, Timestamp timeAdded) {
        super(wisdomContent, attribution, addedByUsername, timeAdded);
    }
}
