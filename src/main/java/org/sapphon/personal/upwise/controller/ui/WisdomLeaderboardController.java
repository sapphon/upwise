package org.sapphon.personal.upwise.controller.ui;

import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.presentation.WisdomWithVotesPresentation;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.service.VoteService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WisdomLeaderboardController {

    private final WisdomService wisdomService;

    private final VoteService voteService;

    public WisdomLeaderboardController(WisdomService wisdomService, VoteService voteService){
        this.wisdomService = wisdomService;
        this.voteService = voteService;
    }

    @GetMapping("/wisdomleaderboard")
    public String getWisdomLeaderboardWithVotes(Model model){
        List<WisdomWithVotesPresentation> allWisdoms = getWisdomsWithVotes(wisdomService.getAllWisdoms());
        model.addAttribute("allWisdoms", allWisdoms);
        return "wisdomleaderboard";
    }

    private List<WisdomWithVotesPresentation> getWisdomsWithVotes(List<IWisdom> allWisdoms) {
        return allWisdoms.stream().map(wisdom -> DomainObjectFactory.createWisdomWithVotes(wisdom, voteService.getByWisdom(wisdom))).collect(Collectors.toList());
    }

}
