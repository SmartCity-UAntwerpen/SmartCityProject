package be.uantwerpen.sc.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Thomas on 5/05/2017.
 */
@Controller
public class BotController extends GlobalModelController{
    @RequestMapping(value="/bots/")
    @PreAuthorize("hasRole('logon')")
    public String displayBotPage(ModelMap model)
    {
        return "redirect:/bots/";
    }
}
