package org.sapphon.personal.upwise.controller.ui;

import org.sapphon.personal.upwise.controller.APIController;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.model.IUser;
import org.sapphon.personal.upwise.model.IVote;
import org.sapphon.personal.upwise.model.datatransfer.NewPasswordRequest;
import org.sapphon.personal.upwise.model.datatransfer.PasswordResetRequest;
import org.sapphon.personal.upwise.model.datatransfer.UserRegistration;
import org.sapphon.personal.upwise.service.UserService;
import org.sapphon.personal.upwise.service.VoteService;
import org.sapphon.personal.upwise.service.WisdomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@Controller
public class UserController {

    private final WisdomService wisdomService;
    private final VoteService voteService;
    private final APIController apiController;
    private final UserService userService;

    @Autowired
    public UserController(WisdomService wisdomService, VoteService voteService, APIController apiController, UserService userService) {
        this.wisdomService = wisdomService;
        this.voteService = voteService;
        this.apiController = apiController;
        this.userService = userService;
    }


    @GetMapping(value = "/user/{user}", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String getUserDashboard(Model model, @PathVariable String user) {
        model.addAttribute("userLoginName", user);
        IUser userWithLogin = userService.getUserWithLogin(user);
        model.addAttribute("userDisplayName", userWithLogin == null ? user : userWithLogin.getDisplayName());
        model.addAttribute("allWisdomsSubmitted", wisdomService.getWisdomPresentationsForWisdoms(wisdomService.getAllWisdomsBySubmitter(user)));
        model.addAttribute("allWisdomsVotedFor", wisdomService.getWisdomPresentationsForWisdoms(voteService.getAllByVoter(user).stream().map(IVote::getWisdom).collect(Collectors.toList())));
        return "userdashboard";
    }

    @GetMapping("/register")
    public String registrationForm(Model model) {
        model.addAttribute("userToRegister", DomainObjectFactory.createUserRegistration());
        return "register";
    }

    @PostMapping(value = "/register", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String registrationSubmit(Model model, @ModelAttribute UserRegistration userToRegister) {
        ResponseEntity<IUser> userRegistrationResponseEntity = this.apiController.addUserEndpoint(userToRegister.convertToModelObject());
        model.addAttribute("registrationStatusCode", userRegistrationResponseEntity.getStatusCodeValue());
        if (userRegistrationResponseEntity.getStatusCodeValue() == 201) {
            logRegisteredUserIn(model, userRegistrationResponseEntity.getBody().getLoginUsername());
            return getUserDashboard(model, userRegistrationResponseEntity.getBody().getLoginUsername());
        }
        return registrationForm(model);
    }

    private void logRegisteredUserIn(Model model, String loginUsername) {
        IUser actualUser = userService.getUserWithLogin(loginUsername);
        UserDetails details = DomainObjectFactory.createUserDetailsFromUser(actualUser);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(details.getUsername(), details.getPassword(), details.getAuthorities()));
        model.addAttribute("loggedInUser", actualUser);
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, @RequestParam(required = false) String error, @RequestParam(required = false) String loggedout) {
        if (error != null) {
            model.addAttribute("loginStatusCode", 400);
        } else if (loggedout != null) {
            model.addAttribute("logoutStatusCode", 200);
        }
        return "login";
    }

    @GetMapping("/forgotpassword")
    public String resetPasswordForm(Model model) {
        model.addAttribute("passwordResetRequest", new PasswordResetRequest());
        return "forgotpassword";
    }

    @PostMapping(value = "/forgotpassword", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String resetPasswordSubmit(Model model, @ModelAttribute PasswordResetRequest passwordResetRequest) {
        if (userService.hasUserWithEmail(passwordResetRequest.getEmail())) {
            userService.enablePasswordResetForUser(passwordResetRequest.getEmail());
        }
        return "forgotpasswordresult";
    }

    @GetMapping(value = "/choosenewpassword{token}")
    public String chooseNewPasswordForm(Model model, @PathVariable String token) {
        if (!userService.isValidToken(token)) {
            return this.resetPasswordForm(model);
        }
        else{
            model.addAttribute("newPasswordRequest", new NewPasswordRequest());
            model.addAttribute("passwordResetToken", token);
            return "choosenewpassword";
        }
    }

    @PostMapping(value = "/choosenewpassword{token}", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.ALL_VALUE)
    public String chooseNewPasswordSubmit(Model model, @PathVariable String token, @ModelAttribute NewPasswordRequest newPasswordRequest) {
        boolean successful = userService.resetPasswordForUser(newPasswordRequest.getLoginUsername(), token, newPasswordRequest.getDesiredNewPassword());
        model.addAttribute("chooseNewPasswordStatusCode", successful ? 200 : 400);
        return "wisdomleaderboard";
    }
}
