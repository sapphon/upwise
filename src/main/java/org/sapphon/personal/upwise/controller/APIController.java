package org.sapphon.personal.upwise.controller;

import org.sapphon.personal.upwise.model.IAnalyticsEvent;
import org.sapphon.personal.upwise.model.IUser;
import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.IWisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.service.AnalyticsService;
import org.sapphon.personal.upwise.service.UserService;
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

    private final WisdomService wisdomService;

    private final VoteService voteService;
    private final UserService userService;
    private final AnalyticsService analyticsService;

    @Autowired
    public APIController(WisdomService wisdomService, VoteService voteService, UserService userService, AnalyticsService analyticsService) {
        this.wisdomService = wisdomService;
        this.voteService = voteService;
        this.userService = userService;
        this.analyticsService = analyticsService;
    }


    @RequestMapping(value = "/health")
    public String healthEndpoint(){
        return this.getCannedResponse();
    }

    private String getCannedResponse(){
        return "Upwise API is up";
    }


    @RequestMapping(value = "/wisdom/all")
    public List<IWisdom> getAllWisdomsEndpoint(){

        return this.getAllWisdoms();
    }

    @RequestMapping(value="/wisdom/random")
    public IWisdom getRandomWisdomEndpoint(){
        int numberOfWisdoms = getAllWisdoms().size();
        return numberOfWisdoms > 0 ?  this.getAllWisdoms().get(new Random().nextInt(numberOfWisdoms)) : null;
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

    @RequestMapping(value = "/vote/add", method=RequestMethod.POST)
    public ResponseEntity<IVote> voteForWisdomEndpoint(@RequestBody IVote vote){
        Optional<IWisdom> wisdomMaybe = this.wisdomService.findWisdom(vote.getWisdom());
        if(!validateWisdom(vote.getWisdom()) || !validateUsername(vote.getAddedByUsername()) || !wisdomMaybe.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        else if(voteService.getByWisdomAndVoterUsername(wisdomMaybe.get(), vote.getAddedByUsername()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.voteForWisdom(vote, wisdomMaybe.get()));
    }

    private boolean validateUsername(String voterUsername) {
        return voterUsername != null && !voterUsername.equals("");
    }

    private IVote voteForWisdom(IVote prospectiveVote, IWisdom toVoteFor){
        return this.voteService.addOrUpdateVote(DomainObjectFactory.createVote(toVoteFor, prospectiveVote.getAddedByUsername(), prospectiveVote.getTimeAdded() == null ? TimeLord.getNow() : prospectiveVote.getTimeAdded()));

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

    private boolean validateUser(IUser user){
        return user.getLoginUsername() != null && !user.getLoginUsername().isEmpty() &&
                user.getPassword() != null && !user.getPassword().isEmpty();
    }

    private IWisdom addWisdom(IWisdom wisdom){
        return this.wisdomService.addOrUpdateWisdom(wisdom);
    }


    @RequestMapping(value = "/registration/add", method = RequestMethod.POST)
    public ResponseEntity<IUser> addUserEndpoint(@RequestBody IUser user) {
        if(!validateUser(user)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        else if(this.userService.getUserWithLogin(user.getLoginUsername()) != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        else {
            user.setTimeAdded(TimeLord.getNow());
            user.setPassword(this.userService.getPasswordEncoder().encode(user.getPassword()));
            return ResponseEntity.status(HttpStatus.CREATED).body(this.addUser(user));
        }
    }

    @RequestMapping(value = "/analytics/all")
    public List<IAnalyticsEvent> getAllAnalyticsEventsEndpoint(){
        return this.analyticsService.getAllEvents();
    }


    @RequestMapping(value = "/analytics/add", method = RequestMethod.POST)
    public ResponseEntity<IAnalyticsEvent> addAnalyticsEventEndpoint(@RequestBody IAnalyticsEvent event) {
        if(!validateAnalyticsEvent(event)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        else if(this.analyticsService.eventExists(event)){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        else {
            return ResponseEntity.status(HttpStatus.CREATED).body(this.analyticsService.saveEvent(event));
        }
    }

    private boolean validateAnalyticsEvent(IAnalyticsEvent event) {
        return event.getEventDescription() != null && !event.getEventDescription().isEmpty()
                && event.getEventInitiator() != null && !event.getEventInitiator().isEmpty()
                && event.getEventOccurrenceTime() != null;
    }

    private IUser addUser(IUser user) {
        return this.userService.addOrUpdateUser(user);
    }
}
