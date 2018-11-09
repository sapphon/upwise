package org.sapphon.personal.upwise.repository.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.sapphon.personal.upwise.AbstractVote;
import org.sapphon.personal.upwise.AbstractWisdom;
import org.sapphon.personal.upwise.IWisdom;

import javax.persistence.*;
import javax.transaction.Transactional;
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
