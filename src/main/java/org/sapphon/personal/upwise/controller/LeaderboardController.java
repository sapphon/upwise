package org.sapphon.personal.upwise.controller;

import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.service.WisdomService;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class LeaderboardController {

    @Autowired
    WisdomService wisdomService;

    @RequestMapping(value = "/")
    public String getCannedResponse(){
        return "Leaderboard is up";
    }

    @RequestMapping(value = "/wisdom/all")
    public List<IWisdom> getAllWisdoms(){
        return wisdomService.getAllWisdoms();
    }

    @RequestMapping(value = "/wisdom/populaterandom")
    public void populateRandomWisdom(){
        this.wisdomService.addOrUpdateWisdom(DomainObjectFactory.createWisdom("A", "B", "C", TimeLord.getTimestampForMillis(10128L)));
    }

    @RequestMapping(value = "/wisdom/add", method = RequestMethod.POST)

    public ResponseEntity<String> addWisdom(@RequestBody IWisdom wisdom) {
        try {
            wisdomService.addOrUpdateWisdom(wisdom);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build();
        }

    }
}
