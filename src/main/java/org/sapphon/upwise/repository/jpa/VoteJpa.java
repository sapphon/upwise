package org.sapphon.upwise.repository.jpa;

import org.sapphon.upwise.model.AbstractVote;
import org.sapphon.upwise.model.IWisdom;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class VoteJpa extends AbstractVote {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    protected VoteJpa(){
        super();
    }

    public VoteJpa(IWisdom wisdom, String addedByUsername, Timestamp timeAdded) {
        super(wisdom, addedByUsername, timeAdded);
    }
}
