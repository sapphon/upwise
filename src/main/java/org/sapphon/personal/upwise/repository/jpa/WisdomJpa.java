package org.sapphon.personal.upwise.repository.jpa;

import org.sapphon.personal.upwise.model.AbstractWisdom;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class WisdomJpa extends AbstractWisdom {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    public WisdomJpa(){
        super();
    }

    public WisdomJpa(String wisdomContent, String attribution, String addedByUsername, Timestamp timeAdded) {
        super(wisdomContent, attribution, addedByUsername, timeAdded);
    }
}
