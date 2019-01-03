package org.sapphon.personal.upwise.repository.jpa;

import org.sapphon.personal.upwise.model.AbstractAnalyticsEvent;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class AnalyticsEventJpa extends AbstractAnalyticsEvent {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    protected AnalyticsEventJpa(){}

    public AnalyticsEventJpa(String eventDescription, String eventInitiator, Timestamp timeOccurred) {
        super(eventDescription, eventInitiator, timeOccurred);
    }
}
