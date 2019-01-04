package org.sapphon.personal.upwise.controller.ui;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sapphon.personal.upwise.TestHelper;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.model.AnalyticsAction;
import org.sapphon.personal.upwise.model.IAnalyticsEvent;
import org.sapphon.personal.upwise.service.AnalyticsService;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

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
        final ArrayList<IAnalyticsEvent> expectedEvents = newArrayList(DomainObjectFactory.createAnalyticsEvent("rim", "farge", TimeLord.getNow(), AnalyticsAction.LOGIN), DomainObjectFactory.createAnalyticsEvent("gord", "drog", TimeLord.getNow(), AnalyticsAction.VIEWWISDOM));
        analyticsService.saveEvent(expectedEvents.get(0));
        analyticsService.saveEvent(expectedEvents.get(1));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/allanalytics").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andReturn();
        TestHelper.assertListEquals(expectedEvents, (List)mvcResult.getModelAndView().getModel().get("events"));
    }

}