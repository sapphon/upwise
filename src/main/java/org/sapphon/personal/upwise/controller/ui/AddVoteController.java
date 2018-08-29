package org.sapphon.personal.upwise.controller.ui;


import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.Vote;
import org.sapphon.personal.upwise.Wisdom;
import org.sapphon.personal.upwise.controller.APIController;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Random;

@Controller
public class AddVoteController {

    private APIController apiController;

    public AddVoteController(APIController apiController){
        this.apiController = apiController;
    }

    @GetMapping("/addvote")
    public String wisdomForm(Model model) {
        List<IWisdom> allWisdomsEndpoint = apiController.getAllWisdomsEndpoint();
        model.addAttribute("voteToAdd", new Vote());
        model.addAttribute("wisdomToVoteFor", allWisdomsEndpoint.get(new Random().nextInt(allWisdomsEndpoint.size())));
        return "addvote";
    }

    @PostMapping("/addvote")
    public String voteSubmit(Model model, @ModelAttribute Vote voteToAdd) {
        ResponseEntity<IVote> voteResponseEntity = this.apiController.voteForWisdomEndpoint(voteToAdd.getAddedByUsername(), voteToAdd.getWisdom());
         model.addAttribute("statusCode", voteResponseEntity.getStatusCodeValue());
        return "addvoteresult";
    }

}
