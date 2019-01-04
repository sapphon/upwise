package org.sapphon.personal.upwise.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sapphon.personal.upwise.TestHelper;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.model.IAnalyticsEvent;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AnalyticsEventRepositoryTest {
    @Autowired
    private AnalyticsEventRepository analyticsRepo;

    private IAnalyticsEvent[] testEvents;

    @Before
    public void setUp() throws Exception {
        testEvents = new IAnalyticsEvent[2];
        testEvents[0] = RandomObjectFactory.makeRandomEvent();
        testEvents[1] = RandomObjectFactory.makeRandomEvent();
    }

    @Test
    public void canSaveAndLoadOneRecordByIdWithoutTransformingTheData() {
        IAnalyticsEvent eventWeWantToFind = testEvents[0];
        IAnalyticsEvent eventWeDoNotWant = testEvents[1];

        analyticsRepo.save(eventWeDoNotWant);
        analyticsRepo.save(eventWeWantToFind);

        final IAnalyticsEvent actual = analyticsRepo.getById(2L);
        assertEquals(eventWeWantToFind, actual);
        assertEquals(eventWeWantToFind.getEventDescription(), actual.getEventDescription());
        assertEquals(eventWeWantToFind.getEventInitiator(), actual.getEventInitiator());
        assertEquals(eventWeWantToFind.getEventTime(), actual.getEventTime());
        assertEquals(eventWeWantToFind.getEventType(), actual.getEventType());
    }

    @Test
    public void canSaveAndCount() {
        analyticsRepo.save(Arrays.asList(testEvents));
        assertEquals(2, analyticsRepo.getCount());
    }

    @Test
    public void canSaveAndRetrieveAll() {
        for(IAnalyticsEvent event : testEvents){
            analyticsRepo.save(event);
        }
        final List<IAnalyticsEvent> actual = analyticsRepo.getAll();
        TestHelper.assertListEquals(newArrayList(testEvents), actual);
    }

    @Test
    public void canFindOneRecord() {
        final IAnalyticsEvent expected = analyticsRepo.save(testEvents[0]);
        assertEquals(expected, analyticsRepo.find(testEvents[0].getEventDescription(), testEvents[0].getEventInitiator(), testEvents[0].getEventTime()).get());
        assertEquals(Optional.empty(), analyticsRepo.find("this", "that", TimeLord.getNowWithOffset(-10000)));
    }
}