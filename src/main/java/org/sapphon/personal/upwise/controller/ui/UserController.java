package org.sapphon.personal.upwise.controller.ui;

import org.sapphon.personal.upwise.IUser;
import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.controller.APIController;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.service.VoteService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.stream.Collectors;

@Controller
public class UserController {

    private final WisdomService wisdomService;

    private final VoteService voteService;
    private final APIController apiController;

    public UserController(WisdomService wisdomService, VoteService voteService, APIController apiController){
        this.wisdomService = wisdomService;
        this.voteService = voteService;
        this.apiController = apiController;
    }


    @GetMapping(value = "/user/{user}", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getUserDashboard(Model model, @PathVariable String user){
        model.addAttribute("userName", user);
        model.addAttribute("allWisdomsSubmitted", wisdomService.getAllWisdomsBySubmitter(user));
        model.addAttribute("allWisdomsVotedFor", voteService.getAllByVoter(user).stream().map(IVote::getWisdom).collect(Collectors.toList()));
        return "userdashboard";
    }

    @GetMapping("/loggedout")
    public String getLogoutPage(Model model){
        return "loggedout";
    }

    @GetMapping("/register")
    public String registrationForm(Model model){
        model.addAttribute("userToRegister", DomainObjectFactory.createUser(null, null, null, null));
        return "register";
    }

    @PostMapping("/register")
    public String registrationSubmit(Model model, @ModelAttribute IUser userToRegister){
        ResponseEntity<IUser> userRegistrationResponseEntity = this.apiController.addUserEndpoint(userToRegister);
        model.addAttribute("statusCode", userRegistrationResponseEntity.getStatusCodeValue());
        return registrationForm(model);
    }
}
