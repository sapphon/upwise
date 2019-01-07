package org.sapphon.personal.upwise.service;

import org.sapphon.personal.upwise.model.IAnalyticsEvent;
import org.sapphon.personal.upwise.repository.AnalyticsEventRepository;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    private AnalyticsEventRepository eventRepository;

    @Autowired
    public AnalyticsService(AnalyticsEventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    public List<IAnalyticsEvent> getAllEvents(){
        return this.eventRepository.getAll();
    }

    public IAnalyticsEvent saveEvent(IAnalyticsEvent eventToSave){
        if(eventToSave.getEventTime() == null){
            eventToSave.setEventTime(TimeLord.getNow());
        }
        return this.eventRepository.save(eventToSave);
    }

    public boolean eventExists(IAnalyticsEvent event){
        return this.eventRepository.find(event.getEventDescription(), event.getEventInitiator(), event.getEventTime()).isPresent();
    }
}
