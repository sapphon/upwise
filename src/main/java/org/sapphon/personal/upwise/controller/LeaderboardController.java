package org.sapphon.personal.upwise.controller;

import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.service.VoteService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@CrossOrigin
@RestController
public class LeaderboardController {

    @Autowired
    WisdomService wisdomService;

    @Autowired
    VoteService voteService;


    @RequestMapping(value = "/")
    public String defaultEndpoint(){
        return this.getCannedResponse();
    }

    public String getCannedResponse(){
        return "Leaderboard is up";
    }


    @RequestMapping(value = "/wisdom/all")
    public List<IWisdom> getAllWisdomsEndpoint(){
        return this.getAllWisdoms();
    }

    public List<IWisdom> getAllWisdoms(){
        return wisdomService.getAllWisdoms();
    }

    @RequestMapping(value = "/vote/all")
    public List<IVote> getAllVotesEndpoint(){
        return this.getAllVotes();
    }

    public List<IVote> getAllVotes(){
        return voteService.getAllVotes();
    }


    @RequestMapping(value = "/wisdom/populaterandom")
    public ResponseEntity<IWisdom> populateRandomWisdomEndpoint(){
        int wisdomsBeforeAdd = this.wisdomService.getAllWisdoms().size();
        IWisdom result = this.populateRandomWisdom();
        if(wisdomsBeforeAdd < this.wisdomService.getAllWisdoms().size()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }
        else{
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    public IWisdom populateRandomWisdom(){
        return this.wisdomService.addOrUpdateWisdom(RandomObjectFactory.makeRandom());
    }

    @RequestMapping(value = "/wisdom/voterandom")
    public IVote voteForRandomWisdomEndpoint(){
       return this.voteForRandomWisdom();
    }

    public IVote voteForRandomWisdom(){
        Random random = new Random();
        List<IWisdom> allWisdoms = this.getAllWisdoms();
        if(allWisdoms.size() > 0) {
            IWisdom existingWisdom = allWisdoms.get(random.nextInt(allWisdoms.size()));
            IVote randomVote = RandomObjectFactory.makeRandomWisdomlessVote();
            randomVote.setWisdom(existingWisdom);
            return this.wisdomService.addOrUpdateVote(randomVote);
        }
        return null;
    }

    @RequestMapping(value = "/wisdom/add", method = RequestMethod.POST)
    public ResponseEntity<IWisdom> addWisdomEndpoint(@RequestBody IWisdom wisdom) {
            int wisdomsBeforeAdd = this.wisdomService.getAllWisdoms().size();
        IWisdom result = this.wisdomService.addOrUpdateWisdom(wisdom);
        if(wisdomsBeforeAdd < this.wisdomService.getAllWisdoms().size()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(wisdom);
            }
            else{
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

    }

    public IWisdom addWisdom(IWisdom wisdom){
        return this.wisdomService.addOrUpdateWisdom(wisdom);
    }
}
