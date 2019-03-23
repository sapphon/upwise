package org.sapphon.personal.upwise.controller.ui;

import org.sapphon.personal.upwise.controller.APIController;
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
    private APIController apiController;

    public WisdomController(WisdomService wisdomService, AnalyticsService analyticsService, APIController apiController){
        this.wisdomService = wisdomService;
        this.analyticsService = analyticsService;
        this.apiController = apiController;
    }

    @GetMapping(value= "/", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getDefaultPage(Model model, Principal principal){
        return this.getWisdomLeaderboardWithVotes(model, principal);
    }

    @GetMapping(value = "/wisdomleaderboard", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getWisdomLeaderboardWithVotes(Model model, Principal principal){
        analyticsService.saveEvent(AnalyticsFactory.createViewLeaderboardEvent(principal == null ? "[anonymous]" : principal.getName()));
        model.addAttribute("allWisdoms", wisdomService.getAllWisdomPresentationsSortedByNumberOfVotes());
        return "wisdomleaderboard";
    }

    @GetMapping(value = "/randomwisdom", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getRandomWisdom(Model model, Principal loggedInUser, @RequestParam(required=false) String upvotedByUsername)
    {
            IWisdom chosen = apiController.getRandomWisdomEndpoint(upvotedByUsername);
            return chosen == null ? "viewwisdom" : this.viewWisdom(model, loggedInUser, chosen.getWisdomContent(), chosen.getAttribution());
    }

    @GetMapping(value = "/recentwisdom",  produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getRecentWisdom(Model model, Principal loggedInUser){
        analyticsService.saveEvent(AnalyticsFactory.createViewRecentEvent(loggedInUser == null ? "[anonymous]" : loggedInUser.getName()));
        model.addAttribute("allWisdoms", wisdomService.getAllWisdomPresentationsSortedByTimeAdded());
        return "recentwisdom";
    }

    @GetMapping(value = "/viewwisdom", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String viewWisdom(Model model, Principal loggedInUser, @RequestParam("wisdomContent") String wisdomContent, @RequestParam("wisdomAttribution") String wisdomAttribution)
    {
        Optional<IWisdom> wisdomFound = wisdomService.findWisdomByContentAndAttribution(wisdomContent, wisdomAttribution);
        if(wisdomFound.isPresent()) {
            model.addAttribute("wisdom", wisdomService.getWisdomPresentationForWisdom(wisdomFound.get()));
            this.analyticsService.saveEvent(AnalyticsFactory.createViewWisdomEvent(loggedInUser == null ? "[anonymous]" : loggedInUser.getName(), wisdomFound.get()));
        }
        return "viewwisdom";
    }

    private IWisdom chooseRandomWisdom(List<IWisdom> toChooseFrom){
        return toChooseFrom.get(new Random().nextInt(toChooseFrom.size()));
    }
}
