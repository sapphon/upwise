package org.sapphon.personal.upwise.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sapphon.personal.upwise.TestHelper;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.model.IAnalyticsEvent;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.model.User;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AnalyticsEventRepositoryTest {
    @Autowired
    private AnalyticsEventRepository analyticsRepo;

    private IAnalyticsEvent[] testEvents;

    @Before
    public void setUp() throws Exception {
        testEvents = new IAnalyticsEvent[4];
        testEvents[0] = DomainObjectFactory.createAnalyticsEvent("WISDOM VIEW", "garbage", TimeLord.getNowWithOffset(-1000));
        testEvents[1] = DomainObjectFactory.createAnalyticsEvent("LOG IN", "trash", TimeLord.getNowWithOffset(-900));
        testEvents[2] = DomainObjectFactory.createAnalyticsEvent("WISDOM SUBMIT", "waste", TimeLord.getNowWithOffset(-1));
        testEvents[3] = DomainObjectFactory.createAnalyticsEvent("WISDOM VIEW", "refuse", TimeLord.getNowWithOffset(10));
    }

    @Test
    public void canSaveAndLoadOneRecordByIdWithoutTransformingTheData() {
        IAnalyticsEvent eventWeWantToFind = testEvents[0];
        IAnalyticsEvent eventWeDoNotWant = testEvents[1];

        analyticsRepo.save(eventWeDoNotWant);
        analyticsRepo.save(eventWeWantToFind);

        final IAnalyticsEvent actual = analyticsRepo.getById(2L);
        assertEquals(eventWeWantToFind, actual);
        assertEquals("WISDOM VIEW", actual.getEventDescription());
        assertEquals("garbage", actual.getEventInitiator());
        assertEquals(eventWeWantToFind.getEventOccurrenceTime(), actual.getEventOccurrenceTime());
    }

    @Test
    public void canSaveAndCount() {
        analyticsRepo.save(Arrays.asList(testEvents));
        assertEquals(4, analyticsRepo.getCount());
    }

    @Test
    public void canSaveAndRetrieveAll() {
        for(IAnalyticsEvent event : testEvents){
            analyticsRepo.save(event);
        }
        final List<IAnalyticsEvent> actual = analyticsRepo.getAll();
        TestHelper.assertListEquals(newArrayList(testEvents), actual);
    }
}