package org.sapphon.personal.upwise.service;

import org.sapphon.personal.upwise.model.IAnalyticsEvent;
import org.sapphon.personal.upwise.repository.AnalyticsEventRepository;
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

    public void saveEvent(IAnalyticsEvent eventToSave){
        this.eventRepository.save(eventToSave);
    }
}
