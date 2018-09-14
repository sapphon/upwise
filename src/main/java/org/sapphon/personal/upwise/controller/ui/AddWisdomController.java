package org.sapphon.personal.upwise.controller.ui;


import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.Wisdom;
import org.sapphon.personal.upwise.controller.APIController;
import org.sapphon.personal.upwise.presentation.ColorPicker;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AddWisdomController {

    private APIController apiController;

    public AddWisdomController(APIController apiController){
        this.apiController = apiController;
    }

    @GetMapping("/addwisdom")
    public String wisdomForm(Model model) {
        model.addAttribute("wisdomToAdd", new Wisdom());
        new ColorPicker("#57467b", "#537a5a", "#909590", "#8b85c1", "#9ae19d").setColorPaletteOnModel(model);
        System.out.println("eyy");
        return "addwisdom";
    }

    @PostMapping("/addwisdom")
    public String wisdomSubmit(Model model, @ModelAttribute Wisdom wisdomToAdd) {
        ResponseEntity<IWisdom> wisdomResponseEntity = this.apiController.addWisdomEndpoint(wisdomToAdd);
        model.addAttribute("statusCode", wisdomResponseEntity.getStatusCodeValue());
        return wisdomForm(model);
    }

}
