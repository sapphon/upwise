package org.sapphon.personal.upwise.controller.ui;

import org.sapphon.personal.upwise.model.IUser;
import org.sapphon.personal.upwise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class UIControllerAdvice {

    private final UserService userService;

    @Autowired
    UIControllerAdvice(UserService userService){

        this.userService = userService;
    }

    @ModelAttribute("loggedInUser")
    public IUser loggedInUser(Principal principal){
        return principal == null ? null : userService.getUserWithLogin(principal.getName());
    }

}
