package org.sapphon.personal.upwise.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.model.IAnalyticsEvent;
import org.sapphon.personal.upwise.repository.AnalyticsEventRepository;
import org.sapphon.personal.upwise.time.TimeLord;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnalyticsServiceTest {

    private AnalyticsEventRepository mockAnalyticsRepo;
    private AnalyticsService underTest;


    private IAnalyticsEvent[] testEvents;

    @Before
    public void setUp() throws Exception {

    }
    @Before
    public void beforeEach(){

        testEvents = new IAnalyticsEvent[4];
        testEvents[0] = DomainObjectFactory.createAnalyticsEvent("WISDOM VIEW", "garbage", TimeLord.getNowWithOffset(-1000));
        testEvents[1] = DomainObjectFactory.createAnalyticsEvent("LOG IN", "trash", TimeLord.getNowWithOffset(-900));
        testEvents[2] = DomainObjectFactory.createAnalyticsEvent("WISDOM SUBMIT", "waste", TimeLord.getNowWithOffset(-1));
        testEvents[3] = DomainObjectFactory.createAnalyticsEvent("WISDOM VIEW", "refuse", TimeLord.getNowWithOffset(10));


        mockAnalyticsRepo = Mockito.mock(AnalyticsEventRepository.class);
        underTest = new AnalyticsService(mockAnalyticsRepo);
    }

    @Test
    public void canGetAllEventsCollaboratesWithRepository() throws Exception {
        final ArrayList<IAnalyticsEvent> expectedEvents = newArrayList(testEvents);
        when(mockAnalyticsRepo.getAll()).thenReturn(expectedEvents);
        final List<IAnalyticsEvent> actual = underTest.getAllEvents();
        verify(mockAnalyticsRepo).getAll();
        assertSame(expectedEvents, actual);
    }

    @Test
    public void saveEventCallsSaveOnRepository() throws Exception {
        underTest.saveEvent(testEvents[0]);
        verify(mockAnalyticsRepo).save(testEvents[0]);
    }

}