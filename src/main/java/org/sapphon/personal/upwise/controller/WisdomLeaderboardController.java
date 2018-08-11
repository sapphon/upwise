package org.sapphon.personal.upwise.controller;

import org.sapphon.personal.upwise.service.WisdomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WisdomLeaderboardController {

    private final WisdomService wisdomService;

    public WisdomLeaderboardController(WisdomService wisdomService){
        this.wisdomService = wisdomService;
    }

    @GetMapping("/wisdomleaderboard")
    public String getWisdomLeaderboard(Model model){
        model.addAttribute("allWisdoms", wisdomService.getAllWisdoms());
        return "wisdomleaderboard";
    }

}
