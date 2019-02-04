package org.sapphon.personal.upwise.controller.ui;


import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.Vote;
import org.sapphon.personal.upwise.model.Wisdom;
import org.sapphon.personal.upwise.controller.APIController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class AddVoteController {

    private APIController apiController;
    private WisdomController wisdomController;

    public AddVoteController(APIController apiController, WisdomController wisdomController){
        this.apiController = apiController;
        this.wisdomController = wisdomController;
    }

    @PostMapping("/addvote")
    public String voteSubmit(Model model, Principal loggedInUser, @RequestParam(required=false) String voterUsername, @RequestParam String wisdomContent, @RequestParam String wisdomAttribution, @RequestParam(required=false) String destinationViewName) {
        if(loggedInUser != null && loggedInUser.getName() != null && !loggedInUser.getName().isEmpty()){
            voterUsername = loggedInUser.getName();
        }

        Vote voteToAdd = new Vote(new Wisdom(wisdomContent, wisdomAttribution, null, null), voterUsername, null);

        ResponseEntity<IVote> voteResponseEntity = this.apiController.voteForWisdomEndpoint(voteToAdd);
        model.addAttribute("addVoteStatusCode", voteResponseEntity.getStatusCodeValue());

        if(destinationViewName == null || destinationViewName.isEmpty() || destinationViewName.equalsIgnoreCase("viewwisdom")){
            return wisdomController.viewWisdom(model, loggedInUser, wisdomContent, wisdomAttribution);
        }
        else return wisdomController.getWisdomLeaderboardWithVotes(model, loggedInUser);
    }

    @PostMapping("/removevote")
    public String removeVote(Model model, Principal loggedInUser, @RequestParam(required=false) String voterUsername, @RequestParam String wisdomContent, @RequestParam String wisdomAttribution, @RequestParam(required=false) String destinationViewName) {

        ResponseEntity apiResponse = apiController.unvoteForWisdomEndpoint(null);
        int notAlwaysAccurate = 400;
        model.addAttribute("removeVoteStatusCode", apiResponse.getStatusCodeValue());
        return "removevote";
    }
}
