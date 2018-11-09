package org.sapphon.personal.upwise.repository.jpa;

import org.sapphon.personal.upwise.AbstractWisdom;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.sql.Timestamp;

@Entity
public class WisdomJpa extends AbstractWisdom {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    protected WisdomJpa(){}

    public WisdomJpa(String wisdomContent, String attribution, String addedByUsername, Timestamp timeAdded) {
        super(wisdomContent, attribution, addedByUsername, timeAdded);
    }
}
