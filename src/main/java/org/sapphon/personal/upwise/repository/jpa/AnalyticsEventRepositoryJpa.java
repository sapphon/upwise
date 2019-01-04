package org.sapphon.personal.upwise.repository.jpa;

import org.sapphon.personal.upwise.model.IUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository("jpaAnalyticsEventRepo")
public interface AnalyticsEventRepositoryJpa extends CrudRepository<AnalyticsEventJpa, Long> {
    List<AnalyticsEventJpa> findAllByOrderByEventTimeDesc();
    AnalyticsEventJpa findByEventDescriptionAndEventInitiatorAndEventTime(String description, String initiator, Timestamp time);
}
