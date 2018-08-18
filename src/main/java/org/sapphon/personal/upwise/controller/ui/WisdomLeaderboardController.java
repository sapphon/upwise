package org.sapphon.personal.upwise.controller.ui;

import org.sapphon.personal.upwise.service.WisdomService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WisdomLeaderboardController {

    private final WisdomService wisdomService;

    public WisdomLeaderboardController(WisdomService wisdomService){
        this.wisdomService = wisdomService;
    }

    @GetMapping(value = "/wisdomleaderboard", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getWisdomLeaderboardWithVotes(Model model){
        model.addAttribute("allWisdoms", wisdomService.getAllWisdomsWithVotes());
        return "wisdomleaderboard";
    }

}
