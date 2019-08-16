package org.sapphon.personal.upwise.controller.ui;


import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.Vote;
import org.sapphon.personal.upwise.controller.APIController;
import org.sapphon.personal.upwise.repository.jpa.WisdomJpa;
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

        Vote voteToAdd = new Vote(new WisdomJpa(wisdomContent, wisdomAttribution, null, null), voterUsername, null);

        ResponseEntity<IVote> voteResponseEntity = this.apiController.voteForWisdomEndpoint(voteToAdd);
        model.addAttribute("addVoteStatusCode", voteResponseEntity.getStatusCodeValue());

        if(destinationViewName == null || destinationViewName.isEmpty() || destinationViewName.equalsIgnoreCase("viewwisdom")){
            return wisdomController.viewWisdom(model, loggedInUser, wisdomContent, wisdomAttribution);
        }
        else return "redirect:/wisdomleaderboard#"+wisdomContent;
    }

    @PostMapping("/removevote")
    public String removeVote(Model model, Principal loggedInUser, @RequestParam(required=false) String voterUsername, @RequestParam String wisdomContent, @RequestParam String wisdomAttribution, @RequestParam(required=false) String destinationViewName) {
        voterUsername = loggedInUser.getName();
        ResponseEntity apiResponse = apiController.unvoteForWisdomEndpoint(new Vote(new WisdomJpa(wisdomContent, wisdomAttribution, null, null), voterUsername, null));

        model.addAttribute("removeVoteStatusCode", apiResponse.getStatusCodeValue());

        if(destinationViewName == null || destinationViewName.isEmpty() || destinationViewName.equalsIgnoreCase("viewwisdom")){
            return wisdomController.viewWisdom(model, loggedInUser, wisdomContent, wisdomAttribution);
        }
        else return wisdomController.getWisdomLeaderboardWithVotes(model, loggedInUser);
    }
}
