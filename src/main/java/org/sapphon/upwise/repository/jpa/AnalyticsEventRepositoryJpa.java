package org.sapphon.upwise.repository.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository("jpaAnalyticsEventRepo")
public interface AnalyticsEventRepositoryJpa extends CrudRepository<AnalyticsEventJpa, Long> {
    List<AnalyticsEventJpa> findAllByOrderByEventTimeDesc();
    AnalyticsEventJpa findByEventDescriptionAndEventInitiatorAndEventTime(String description, String initiator, Timestamp time);
}
