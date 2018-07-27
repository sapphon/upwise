package org.sapphon.personal.upwise;

import org.sapphon.personal.upwise.repository.WisdomRepository;

import java.sql.Timestamp;

public class Vote extends AbstractVote {

    public Vote(IWisdom wisdom, String addedByUsername, Timestamp timeAdded) {
        super(wisdom, addedByUsername, timeAdded);
    }
}
