package org.sapphon.personal.upyougo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaderboardController {


    @RequestMapping("/")
    public String leaderboard(){
        return "Leaderboard test text";
    }
}
