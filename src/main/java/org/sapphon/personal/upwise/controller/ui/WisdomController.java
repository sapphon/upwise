package org.sapphon.personal.upwise.controller.ui;

import org.sapphon.personal.upwise.factory.AnalyticsFactory;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.service.AnalyticsService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
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
        analyticsService.saveEvent(AnalyticsFactory.createViewLeaderboardEvent(principal == null ? "[anonymous]" : principal.getName()));
        model.addAttribute("allWisdoms", wisdomService.getAllWisdomsWithVotes());
        return "wisdomleaderboard";
    }

    @GetMapping(value = "/randomwisdom", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getRandomWisdom(Model model, Principal loggedInUser)
    {
        if(wisdomService.hasAnyWisdoms()) {
            IWisdom chosen = chooseRandomWisdom(wisdomService.getAllWisdoms());
            return this.viewWisdom(model, loggedInUser, chosen.getWisdomContent(), chosen.getAttribution());
        }
        return "viewwisdom";
    }

    @GetMapping(value = "/viewwisdom", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String viewWisdom(Model model, Principal loggedInUser, @RequestParam("wisdomContent") String wisdomContent, @RequestParam("wisdomAttribution") String wisdomAttribution)
    {
        Optional<IWisdom> wisdomFound = wisdomService.findWisdomByContentAndAttribution(wisdomContent, wisdomAttribution);
        if(wisdomFound.isPresent()) {
            model.addAttribute("wisdom", wisdomService.getWisdomPresentation(wisdomFound.get()));
            this.analyticsService.saveEvent(AnalyticsFactory.createViewWisdomEvent(loggedInUser == null ? "[anonymous]" : loggedInUser.getName(), wisdomFound.get()));
        }
        return "viewwisdom";
    }

    private IWisdom chooseRandomWisdom(List<IWisdom> toChooseFrom){
        return toChooseFrom.get(new Random().nextInt(toChooseFrom.size()));
    }
}
