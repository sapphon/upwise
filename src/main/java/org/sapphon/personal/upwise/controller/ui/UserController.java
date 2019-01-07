package org.sapphon.personal.upwise.controller.ui;

import org.sapphon.personal.upwise.model.IUser;
import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.User;
import org.sapphon.personal.upwise.controller.APIController;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.service.UserService;
import org.sapphon.personal.upwise.service.VoteService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
public class UserController {

    private final WisdomService wisdomService;
    private final VoteService voteService;
    private final APIController apiController;
    private final UserService userService;

    @Autowired
    public UserController(WisdomService wisdomService, VoteService voteService, APIController apiController, UserService userService){
        this.wisdomService = wisdomService;
        this.voteService = voteService;
        this.apiController = apiController;
        this.userService = userService;
    }


    @GetMapping(value = "/user/{user}", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getUserDashboard(Model model, @PathVariable String user){
        model.addAttribute("userName", user);
        model.addAttribute("allWisdomsSubmitted", wisdomService.getAllWisdomsBySubmitter(user));
        model.addAttribute("allWisdomsVotedFor", voteService.getAllByVoter(user).stream().map(IVote::getWisdom).collect(Collectors.toList()));
        return "userdashboard";
    }

    @GetMapping("/register")
    public String registrationForm(Model model){
        model.addAttribute("userToRegister", DomainObjectFactory.createUser(null, null, null, null));
        return "register";
    }

    @PostMapping(value="/register",  produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String registrationSubmit(Model model, @ModelAttribute User userToRegister){
        ResponseEntity<IUser> userRegistrationResponseEntity = this.apiController.addUserEndpoint(userToRegister);
        model.addAttribute("registrationStatusCode", userRegistrationResponseEntity.getStatusCodeValue());
        if(userRegistrationResponseEntity.getStatusCodeValue() == 201){
            UserDetails details = DomainObjectFactory.createUserDetailsFromUser(userService.getUserWithLogin(userToRegister.getLoginUsername()));
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(details.getUsername(), details.getPassword(), details.getAuthorities()));
            return getUserDashboard(model, details.getUsername());
        }
        return registrationForm(model);
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, @RequestParam(required=false) String error, @RequestParam(required=false) String loggedout){
        if(error != null){
            model.addAttribute("loginStatusCode", 400);
        }
        else if(loggedout != null){
            model.addAttribute("logoutStatusCode", 200);
        }
        return "login";
    }
}
