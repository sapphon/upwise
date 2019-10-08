package org.sapphon.upwise.controller.ui;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sapphon.upwise.TestHelper;
import org.sapphon.upwise.factory.AnalyticsFactory;
import org.sapphon.upwise.model.AnalyticsAction;
import org.sapphon.upwise.model.IAnalyticsEvent;
import org.sapphon.upwise.service.AnalyticsService;
import org.sapphon.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
public class AnalyticsControllerTest {


    @Autowired
    private AnalyticsController underTest;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void getAnalyticsPageReturnsCorrectView() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/allanalytics").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("analytics", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void getAnalyticsPageSetsAnalyticsOnModel() throws Exception {
        final ArrayList<IAnalyticsEvent> expectedEvents = newArrayList(AnalyticsFactory.createAnalyticsEvent("rim", "farge", TimeLord.getNow(), AnalyticsAction.LOGIN), AnalyticsFactory.createAnalyticsEvent("gord", "drog", TimeLord.getNow(), AnalyticsAction.VIEWWISDOM));
        analyticsService.saveEvent(expectedEvents.get(0));
        analyticsService.saveEvent(expectedEvents.get(1));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/allanalytics").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andReturn();
        TestHelper.assertListEquals(expectedEvents.stream().sorted(Comparator.comparing(IAnalyticsEvent::getEventTime).reversed()).collect(Collectors.toList()), (List)mvcResult.getModelAndView().getModel().get("events"));
    }

}