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
import java.util.Optional;
import java.util.Random;

@CrossOrigin
@RestController
public class APIController {

    private WisdomService wisdomService;

    private VoteService voteService;

    @Autowired
    public APIController(WisdomService wisdomService, VoteService voteService) {
        this.wisdomService = wisdomService;
        this.voteService = voteService;
    }


    @RequestMapping(value = "/")
    public String defaultEndpoint(){
        return this.getCannedResponse();
    }

    private String getCannedResponse(){
        return "Upwise API is up";
    }


    @RequestMapping(value = "/wisdom/all")
    public List<IWisdom> getAllWisdomsEndpoint(){

        return this.getAllWisdoms();
    }

    private List<IWisdom> getAllWisdoms(){
        return wisdomService.getAllWisdoms();
    }

    @RequestMapping(value = "/vote/all")
    public List<IVote> getAllVotesEndpoint(){
        return this.getAllVotes();
    }

    private List<IVote> getAllVotes(){
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
            return this.voteService.addOrUpdateVote(randomVote);
        }
        return null;
    }

    @RequestMapping(value = "/vote/add")
    public ResponseEntity<IVote> voteForWisdomEndpoint(@RequestParam String voterUsername, @RequestBody IWisdom wisdom){
        Optional<IWisdom> wisdomMaybe = this.wisdomService.findWisdom(wisdom);
        if(!validateWisdom(wisdom) || !validateUsername(voterUsername) || !wisdomMaybe.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        else if(voteService.getByWisdomAndVoterUsername(wisdomMaybe.get(), voterUsername).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.voteForWisdom(voterUsername, wisdomMaybe.get()));
    }

    private boolean validateUsername(String voterUsername) {
        return voterUsername != null && !voterUsername.equals("");
    }

    private IVote voteForWisdom(String userName, IWisdom toVoteFor){
        Optional<IWisdom> wisdomMaybe = this.wisdomService.findWisdomByContentAndAttribution(toVoteFor.getWisdomContent(), toVoteFor.getAttribution());
        return wisdomMaybe.map(iWisdom -> this.voteService.addOrUpdateVote(DomainObjectFactory.createVote(iWisdom, userName, TimeLord.getNow()))).orElse(null);

    }


    @RequestMapping(value = "/wisdom/add", method = RequestMethod.POST)
    public ResponseEntity<IWisdom> addWisdomEndpoint(@RequestBody IWisdom wisdom) {
        if(!validateWisdom(wisdom)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        else if(this.wisdomService.findWisdomByContentAndAttribution(wisdom.getWisdomContent(), wisdom.getAttribution()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        wisdom.setTimeAdded(TimeLord.getNow());
        return ResponseEntity.status(HttpStatus.CREATED).body(this.addWisdom(wisdom));
    }

    private boolean validateWisdom(IWisdom wisdom) {
        return wisdom != null && wisdom.getWisdomContent() != null &&
                !wisdom.getWisdomContent().isEmpty() &&
                wisdom.getAttribution() != null &&
                !wisdom.getAttribution().isEmpty();
    }

    public IWisdom addWisdom(IWisdom wisdom){
        return this.wisdomService.addOrUpdateWisdom(wisdom);
    }
}
