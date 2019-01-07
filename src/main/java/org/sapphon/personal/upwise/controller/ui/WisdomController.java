package org.sapphon.personal.upwise.controller.ui;

import org.sapphon.personal.upwise.factory.AnalyticsFactory;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.model.AnalyticsAction;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.service.AnalyticsService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;
import java.util.Random;

@Controller
public class WisdomController {

    private final WisdomService wisdomService;
    private final AnalyticsService analyticsService;

    public WisdomController(WisdomService wisdomService, AnalyticsService analyticsService){
        this.wisdomService = wisdomService;
        this.analyticsService = analyticsService;
    }

    @GetMapping(value= "/", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getDefaultPage(Model model, Principal principal){
        return this.getWisdomLeaderboardWithVotes(model, principal);
    }

    @GetMapping(value = "/wisdomleaderboard", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getWisdomLeaderboardWithVotes(Model model, Principal principal){
        analyticsService.saveEvent(AnalyticsFactory.createAnalyticsEvent("[none]", principal == null ? "[anonymous]" : principal.getName(), AnalyticsAction.VIEWLEADERBOARD));
        model.addAttribute("allWisdoms", wisdomService.getAllWisdomsWithVotes());
        return "wisdomleaderboard";
    }

    @GetMapping(value = "/wisdomsearch", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getWisdomSearchForm(Model model){
        return "wisdomsearch";
    }

    @GetMapping(value = "/randomwisdom", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getRandomWisdom(Model model)
    {
        if(wisdomService.hasAnyWisdoms()) {
            model.addAttribute("wisdom", wisdomService.getAllWisdomsWithVotes().get(new Random().nextInt(wisdomService.getAllWisdomsWithVotes().size())));
        }
        return "viewwisdom";
    }

    @GetMapping(value = "/viewwisdom", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String viewWisdom(Model model, @RequestParam("wisdomContent") String wisdomContent, @RequestParam("wisdomAttribution") String wisdomAttribution )
    {
        Optional<IWisdom> wisdomFound = wisdomService.findWisdomByContentAndAttribution(wisdomContent, wisdomAttribution);
        wisdomFound.ifPresent(iWisdom -> model.addAttribute("wisdom", wisdomService.getWisdomWithVotes(iWisdom)));
        return "viewwisdom";
    }
}
