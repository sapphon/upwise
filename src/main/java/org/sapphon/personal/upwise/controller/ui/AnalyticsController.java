package org.sapphon.personal.upwise.controller.ui;

import org.sapphon.personal.upwise.model.IAnalyticsEvent;
import org.sapphon.personal.upwise.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService){

        this.analyticsService = analyticsService;
    }

    @GetMapping("/allanalytics")
    public String getAnalyticsPage(Model model, Principal principal){
        final List<IAnalyticsEvent> allEvents = this.analyticsService.getAllEvents();
        model.addAttribute("events", allEvents);
        return "analytics";
    }
}
