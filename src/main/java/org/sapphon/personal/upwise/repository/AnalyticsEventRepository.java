package org.sapphon.personal.upwise.repository;

import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.model.IAnalyticsEvent;
import org.sapphon.personal.upwise.model.IUser;
import org.sapphon.personal.upwise.repository.jpa.AnalyticsEventJpa;
import org.sapphon.personal.upwise.repository.jpa.AnalyticsEventRepositoryJpa;
import org.sapphon.personal.upwise.repository.jpa.UserJpa;
import org.sapphon.personal.upwise.repository.jpa.UserRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("analyticsEventRepo")
public class AnalyticsEventRepository {

    private AnalyticsEventRepositoryJpa jpaAnalyticsEventRepo;

    @Autowired
    public AnalyticsEventRepository(AnalyticsEventRepositoryJpa backingUserRepo) {
        jpaAnalyticsEventRepo = backingUserRepo;
    }

    public IAnalyticsEvent save(IAnalyticsEvent eventToSave) {
        IAnalyticsEvent eventFound = jpaAnalyticsEventRepo.findByEventDescriptionAndEventInitiatorAndEventTime(eventToSave.getEventDescription(), eventToSave.getEventInitiator(), eventToSave.getEventOccurrenceTime());
        if(eventFound != null){
            return eventFound;
        }
        else{
            return jpaAnalyticsEventRepo.save(DomainObjectFactory.createAnalyticsEventJpa(eventToSave));
        }
    }

    public void save(List<IAnalyticsEvent> usersToSave){
        usersToSave.forEach(this::save);
    }

    public IAnalyticsEvent getById(Long eventId) {
        return this.jpaAnalyticsEventRepo.findById(eventId).orElse(null);
    }

    public List<IAnalyticsEvent> getAll() {
        List<IAnalyticsEvent> toReturn = new ArrayList<>();
        jpaAnalyticsEventRepo.findAll().forEach((j) -> toReturn.add(DomainObjectFactory.createAnalyticsEvent(j)));
        return toReturn;
    }

    public Optional<IAnalyticsEvent> find(String action, String user, Timestamp time){
        final AnalyticsEventJpa found = this.jpaAnalyticsEventRepo.findByEventDescriptionAndEventInitiatorAndEventTime(action, user, time);
        return found != null ? Optional.of(found) : Optional.empty();
    }

    public long getCount() {
        return jpaAnalyticsEventRepo.count();
    }

}
