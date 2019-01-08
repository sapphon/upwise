package org.sapphon.personal.upwise.service;

import org.sapphon.personal.upwise.model.IAnalyticsEvent;
import org.sapphon.personal.upwise.repository.AnalyticsEventRepository;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    private AnalyticsEventRepository eventRepository;
    private boolean recordAnonymousAnalytics;

    @Autowired
    public AnalyticsService(AnalyticsEventRepository eventRepository, @Value("${upwise.analytics.record.anonymous:true}") boolean recordAnonymousAnalytics) {
        this.eventRepository = eventRepository;
        this.recordAnonymousAnalytics = recordAnonymousAnalytics;
    }

    public List<IAnalyticsEvent> getAllEvents() {
        return this.eventRepository.getAll();
    }

    public IAnalyticsEvent saveEvent(IAnalyticsEvent eventToSave) {
        if(!isEventValid(eventToSave)){
            return null;
        }
        if (eventToSave.getEventTime() == null) {
            eventToSave.setEventTime(TimeLord.getNow());
        }
        return this.eventRepository.save(eventToSave);
    }

    public boolean eventExists(IAnalyticsEvent event) {
        return this.eventRepository.find(event.getEventDescription(), event.getEventInitiator(), event.getEventTime()).isPresent();
    }

    public boolean isEventValid(IAnalyticsEvent event) {
        return event.getEventType() != null
                && event.getEventInitiator() != null
                && !event.getEventInitiator().isEmpty()
                && (recordAnonymousAnalytics || !event.getEventInitiator().equals("[anonymous]"));
    }
}
