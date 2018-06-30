package org.sapphon.personal.upyougo.repository.jpa;

import org.sapphon.personal.upyougo.AbstractWisdom;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class WisdomJpa extends AbstractWisdom {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    protected WisdomJpa(){}

    public WisdomJpa(String wisdomContent, String attribution, String addedByUsername, Timestamp timeAdded) {
        super(wisdomContent, attribution, addedByUsername, timeAdded);
    }
}
