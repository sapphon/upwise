package org.sapphon.upwise.controller;

import org.sapphon.upwise.factory.AnalyticsFactory;
import org.sapphon.upwise.factory.DomainObjectFactory;
import org.sapphon.upwise.model.IAnalyticsEvent;
import org.sapphon.upwise.model.IUser;
import org.sapphon.upwise.model.IVote;
import org.sapphon.upwise.model.IWisdom;
import org.sapphon.upwise.service.AnalyticsService;
import org.sapphon.upwise.service.UserService;
import org.sapphon.upwise.service.VoteService;
import org.sapphon.upwise.service.WisdomService;
import org.sapphon.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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
    public String healthEndpoint() {
        return this.getCannedResponse();
    }

    private String getCannedResponse() {
        return "Upwise API is up";
    }


    @RequestMapping(value = "/wisdom/all")
    public List<IWisdom> getAllWisdomsEndpoint() {

        return this.getAllWisdoms();
    }

    @RequestMapping(value = "/wisdom/random")
    public IWisdom getRandomWisdomEndpoint(@RequestParam(required = false) String upvotedByUsername) {
        if (upvotedByUsername == null || upvotedByUsername.isEmpty()) {
            return randomWisdomOrNull(getAllWisdoms());
        } else {
            return randomWisdomOrNull(getAllWisdoms()
                    .stream()
                    .filter(x -> voteService.getByWisdomAndVoterUsername(x, upvotedByUsername).isPresent())
                    .collect(Collectors.toList()));
        }
    }

    private IWisdom randomWisdomOrNull(List<IWisdom> toChooseFrom) {
        int numberOfWisdoms = toChooseFrom.size();
        return numberOfWisdoms > 0 ? toChooseFrom.get(new Random().nextInt(numberOfWisdoms)) : null;
    }

    private List<IWisdom> getAllWisdoms() {
        return wisdomService.getAllWisdoms();
    }

    @RequestMapping(value = "/vote/all")
    public List<IVote> getAllVotesEndpoint() {
        return this.getAllVotes();
    }

    private List<IVote> getAllVotes() {
        return voteService.getAllVotes();
    }

    @RequestMapping(value = "/vote/add", method = RequestMethod.POST)
    public ResponseEntity<IVote> voteForWisdomEndpoint(@RequestBody IVote vote) {
        ResponseEntity<IVote> toReturn;
        Optional<IWisdom> wisdomMaybe = this.wisdomService.findWisdom(vote.getWisdom());

        if (!validateWisdom(vote.getWisdom()) || !validateUsername(vote.getAddedByUsername()) || !wisdomMaybe.isPresent()) {
            toReturn = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (voteService.getByWisdomAndVoterUsername(wisdomMaybe.get(), vote.getAddedByUsername()).isPresent()) {
            toReturn = ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            toReturn = ResponseEntity.status(HttpStatus.CREATED).body(this.voteForWisdom(vote, wisdomMaybe.get()));
        }
        addAnalyticsEventEndpoint(AnalyticsFactory.createAddVoteEvent(toReturn.getStatusCode(), vote));
        return toReturn;
    }

    @RequestMapping(value = "/vote/remove", method = RequestMethod.POST)
    public ResponseEntity unvoteForWisdomEndpoint(@RequestBody IVote vote) {
        ResponseEntity toReturn;

        if (!validateWisdom(vote.getWisdom()) || !validateUsername(vote.getAddedByUsername())) {
            toReturn = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (!this.wisdomService.findWisdom(vote.getWisdom()).isPresent() || !voteService.getByWisdomAndVoterUsername(this.wisdomService.findWisdom(vote.getWisdom()).get(), vote.getAddedByUsername()).isPresent()) {
            toReturn = ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            this.voteService.removeVote(voteService.getByWisdomAndVoterUsername(this.wisdomService.findWisdom(vote.getWisdom()).get(), vote.getAddedByUsername()).get());
            toReturn = ResponseEntity.status(HttpStatus.OK).build();
        }
        return toReturn;
    }

    private boolean validateUsername(String voterUsername) {
        return voterUsername != null && !voterUsername.equals("");
    }

    private IVote voteForWisdom(IVote prospectiveVote, IWisdom toVoteFor) {
        return this.voteService.addOrUpdateVote(DomainObjectFactory.createVote(toVoteFor, prospectiveVote.getAddedByUsername(), prospectiveVote.getTimeAdded() == null ? TimeLord.getNow() : prospectiveVote.getTimeAdded()));

    }


    @RequestMapping(value = "/wisdom/add", method = RequestMethod.POST)
    public ResponseEntity<IWisdom> addWisdomEndpoint(@RequestBody IWisdom wisdom) {
        ResponseEntity<IWisdom> toReturn;
        if (!validateWisdom(wisdom)) {
            toReturn = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (this.wisdomService.findWisdomByContentAndAttribution(wisdom.getWisdomContent(), wisdom.getAttribution()).isPresent()) {
            toReturn = ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            wisdom.setTimeAdded(TimeLord.getNow());
            toReturn = ResponseEntity.status(HttpStatus.CREATED).body(this.addWisdom(wisdom));
        }
        addAnalyticsEventEndpoint(AnalyticsFactory.createAddWisdomEvent(toReturn.getStatusCode(), wisdom));
        return toReturn;
    }

    @RequestMapping(value = "/wisdom/remove", method = RequestMethod.DELETE)
    public ResponseEntity removeWisdomEndpoint(@RequestParam Long identifier) {
        if(wisdomService.findWisdom(identifier).isPresent()){
            wisdomService.removeWisdom(identifier);
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean validateWisdom(IWisdom wisdom) {
        return wisdom != null && wisdom.getWisdomContent() != null &&
                !wisdom.getWisdomContent().isEmpty() &&
                wisdom.getAttribution() != null &&
                !wisdom.getAttribution().isEmpty();
    }

    private boolean validateUser(IUser user) {
        return user != null && user.getLoginUsername() != null && !user.getLoginUsername().isEmpty() &&
                user.getPassword() != null && !user.getPassword().isEmpty();
    }


    private IWisdom addWisdom(IWisdom wisdom) {
        return this.wisdomService.addOrUpdateWisdom(wisdom);
    }


    @RequestMapping(value = "/registration/add", method = RequestMethod.POST)
    public ResponseEntity<IUser> addUserEndpoint(@RequestBody IUser user) {
        ResponseEntity<IUser> toReturn;
        if (!validateUser(user)) {
            toReturn = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (this.userService.getUserWithLogin(user.getLoginUsername()) != null) {
            toReturn = ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            user.setTimeAdded(TimeLord.getNow());
            user.setPassword(this.userService.getPasswordEncoder().encode(user.getPassword()));
            toReturn = ResponseEntity.status(HttpStatus.CREATED).body(this.addUser(user));
        }
        addAnalyticsEventEndpoint(AnalyticsFactory.createAddUserEvent(toReturn.getStatusCode(), user));
        return toReturn;
    }

    @RequestMapping(value = "/analytics/all")
    public List<IAnalyticsEvent> getAllAnalyticsEventsEndpoint() {
        return this.analyticsService.getAllEvents();
    }


    @RequestMapping(value = "/analytics/add", method = RequestMethod.POST)
    public ResponseEntity<IAnalyticsEvent> addAnalyticsEventEndpoint(@RequestBody IAnalyticsEvent event) {
        if (!analyticsService.isEventValid(event)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (this.analyticsService.eventExists(event)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            event.setEventTime(TimeLord.getNow());
            return ResponseEntity.status(HttpStatus.CREATED).body(this.analyticsService.saveEvent(event));
        }
    }

    private IUser addUser(IUser user) {
        return this.userService.addOrUpdateUser(user);
    }

    public List<IWisdom> getAllWisdomsByAttributionEndpoint(String attributionSearch) {
        return wisdomService.getAllWisdomsByAttribution(attributionSearch);
    }
}
