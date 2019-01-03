package org.sapphon.personal.upwise.model;

import java.sql.Timestamp;

public class Vote extends AbstractVote {

    public Vote(){
        super();
    }

    public Vote(IWisdom wisdom, String addedByUsername, Timestamp timeAdded) {
        super(wisdom, addedByUsername, timeAdded);
    }
}
