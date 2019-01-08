package org.sapphon.personal.upwise.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sapphon.personal.upwise.TestHelper;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.model.IAnalyticsEvent;
import org.sapphon.personal.upwise.repository.AnalyticsEventRepository;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AnalyticsServiceTest {

    private AnalyticsEventRepository mockAnalyticsRepo;

    private AnalyticsService underTest;


    private IAnalyticsEvent[] testEvents;

    @Before
    public void setUp() throws Exception {

    }
    @Before
    public void beforeEach(){

        testEvents = new IAnalyticsEvent[1];
        testEvents[0] = RandomObjectFactory.makeRandomEvent();


        mockAnalyticsRepo = Mockito.mock(AnalyticsEventRepository.class);
        underTest = new AnalyticsService(mockAnalyticsRepo, false);
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

    @Test
    public void saveEventReturnsSavedBoi() throws Exception {
        when(mockAnalyticsRepo.save(testEvents[0])).thenReturn(testEvents[0]);
        final IAnalyticsEvent actualEvent = underTest.saveEvent(testEvents[0]);
        assertEquals(testEvents[0], actualEvent);
    }

    @Test
    public void testAnalyticsServiceSetsCurrentTimeFromTimeLordOnEventWhenSaving_IfNoTimePopulated() {
        Timestamp timeBefore = TimeLord.getNow();
        ArgumentCaptor<IAnalyticsEvent> captor = ArgumentCaptor.forClass(IAnalyticsEvent.class);
        testEvents[0].setEventTime(null);
        underTest.saveEvent(testEvents[0]);
        Timestamp timeAfter = TimeLord.getNow();
        verify(mockAnalyticsRepo).save(captor.capture());
        assertNotNull(captor.getValue().getEventTime());
        TestHelper.assertTimestampBetweenInclusive(captor.getValue().getEventTime(), timeBefore, timeAfter);
    }

    @Test
    public void testAnalyticsServiceReliesOnPropertyToDecideWhetherToRecordAnonymousEventsAndPropertyDefaultsTrue() {
        final Value recordAnonymousAnalytics = TestHelper.assertConstructorHasAnnotationOfTypeAndGet(AnalyticsService.class, newArrayList(AnalyticsEventRepository.class, boolean.class), 1, Value.class);
        final String[] propertyAndDefault = recordAnonymousAnalytics.value().split(":");
        assertEquals(2, propertyAndDefault.length);
        assertEquals("${upwise.analytics.record.anonymous", propertyAndDefault[0]);
        assertEquals("true}", propertyAndDefault[1]);
    }

    @Test
    public void testAnalyticsServiceWillNotSaveAnonymousEventsIfPropertyConfiguringSuchSaysNotTo() {
        ArgumentCaptor<IAnalyticsEvent> captor = ArgumentCaptor.forClass(IAnalyticsEvent.class);
        testEvents[0].setEventInitiator(null);
        underTest.saveEvent(testEvents[0]);
        verify(mockAnalyticsRepo, times(0)).save(captor.capture());
    }

}