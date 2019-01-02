package org.sapphon.personal.upwise.controller.ui;

import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.service.WisdomService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.Random;

@Controller
public class WisdomController {

    private final WisdomService wisdomService;

    public WisdomController(WisdomService wisdomService){
        this.wisdomService = wisdomService;
    }

    @GetMapping(value= "/", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getDefaultPage(Model model){
        return this.getWisdomLeaderboardWithVotes(model);
    }

    @GetMapping(value = "/wisdomleaderboard", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getWisdomLeaderboardWithVotes(Model model){
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
        if(wisdomFound.isPresent()) {
            model.addAttribute("wisdom", wisdomService.getWisdomWithVotes(wisdomFound.get()));
        }
        return "viewwisdom";
    }
}
