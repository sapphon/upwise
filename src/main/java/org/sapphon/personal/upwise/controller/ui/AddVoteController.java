package org.sapphon.personal.upwise.controller.ui;


import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.Vote;
import org.sapphon.personal.upwise.Wisdom;
import org.sapphon.personal.upwise.controller.APIController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class AddVoteController {

    private APIController apiController;

    public AddVoteController(APIController apiController){
        this.apiController = apiController;
    }

    @PostMapping("/addvote")
    public String voteSubmit(Model model, Principal loggedInUser, @RequestParam(required=false) String voterUsername, @RequestParam String wisdomContent, @RequestParam String wisdomAttribution) {
        if(loggedInUser != null && loggedInUser.getName() != null && !loggedInUser.getName().isEmpty()){
            voterUsername = loggedInUser.getName();
        }
        Vote voteToAdd = new Vote(new Wisdom(wisdomContent, wisdomAttribution, null, null), voterUsername, null);
        ResponseEntity<IVote> voteResponseEntity = this.apiController.voteForWisdomEndpoint(voteToAdd);
         model.addAttribute("addVoteStatusCode", voteResponseEntity.getStatusCodeValue());
        return "addvoteresult";
    }

}
