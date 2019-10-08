package org.sapphon.upwise.controller.ui;


import org.sapphon.upwise.model.IWisdom;
import org.sapphon.upwise.controller.APIController;
import org.sapphon.upwise.repository.jpa.WisdomJpa;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class AddWisdomController {

    private APIController apiController;

    public AddWisdomController(APIController apiController){
        this.apiController = apiController;
    }

    @GetMapping("/addwisdom")
    public String wisdomForm(Model model) {
        model.addAttribute("wisdomToAdd", new WisdomJpa(null, null, null, null));
        return "addwisdom";
    }

    @PostMapping("/addwisdom")
    public String wisdomSubmit(Model model, Principal loggedInUser, @ModelAttribute WisdomJpa wisdomToAdd) {
        if(loggedInUser != null && loggedInUser.getName() != null && !loggedInUser.getName().isEmpty()){
            wisdomToAdd.setAddedByUsername(loggedInUser.getName());
        }

        ResponseEntity<IWisdom> wisdomResponseEntity = this.apiController.addWisdomEndpoint(wisdomToAdd);
        model.addAttribute("addWisdomStatusCode", wisdomResponseEntity.getStatusCodeValue());
        return wisdomForm(model);
    }
}
