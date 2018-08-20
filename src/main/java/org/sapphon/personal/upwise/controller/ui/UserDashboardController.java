package org.sapphon.personal.upwise.controller.ui;

import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.service.VoteService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.stream.Collectors;

@Controller
public class UserDashboardController {

    private final WisdomService wisdomService;

    private final VoteService voteService;

    public UserDashboardController(WisdomService wisdomService, VoteService voteService){
        this.wisdomService = wisdomService;
        this.voteService = voteService;
    }


    @GetMapping(value = "/user/{user}", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getUserDashboard(Model model, @PathVariable String user){
        model.addAttribute("userName", user);
        model.addAttribute("allWisdomsSubmitted", wisdomService.getAllWisdomsBySubmitter(user));
        model.addAttribute("allWisdomsVotedFor", voteService.getAllByVoter(user).stream().map(IVote::getWisdom).collect(Collectors.toList()));
        return "userdashboard";
    }
}
