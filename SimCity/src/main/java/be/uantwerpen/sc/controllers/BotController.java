package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.sim.SimBot;
import be.uantwerpen.sc.models.sim.SimForm;
import be.uantwerpen.sc.models.sim.messages.SimBotStatus;
import be.uantwerpen.sc.services.sim.SimDispatchService;
import be.uantwerpen.sc.services.sim.SimSupervisorService;
import be.uantwerpen.sc.tools.PropertiesList;
import be.uantwerpen.sc.tools.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by Thomas on 5/05/2017.
 */
@Controller
public class BotController extends GlobalModelController{
    @Autowired
    SimDispatchService dispatchService;

    @Autowired
    SimSupervisorService supervisorService;

    private Terminal terminal;

    @RequestMapping(value = {"/bots"})
    @PreAuthorize("hasRole('logon')")
    public String showBotsSettings(ModelMap model) throws Exception {
        SimForm botForm = new SimForm();
        List<String> properties = new PropertiesList().getProperties();

        model.addAttribute("bot", botForm);
        model.addAttribute("properties", properties);

        return "protected/botManagement";
    }

    @RequestMapping(value="/workers/{workerId}/bots/create/{type}")
    @PreAuthorize("hasRole('logon')")
    public String createBot(@Validated @ModelAttribute("type") String type, BindingResult result, ModelMap model)
    {
        if(this.instantiateBot(type))
        {
            return "redirect:/workers/management/?botCreatedSuccess";
        }
        else
        {
            return "redirect:/bots/?botCreatedFailed";
        }

    }

    @RequestMapping(value="/workers/{workerId}/bots/deploy/{type}/{amount}")
    @PreAuthorize("hasRole('logon')")
    public String deployBots(ModelMap model, @PathVariable String type, @PathVariable String amount)
    {
        if(this.instantiateBots(type, Integer.parseInt(amount)))
        {
            return "redirect:/bots/?botsDeployedSuccess";
        }
        else
        {
            return "redirect:/bots/?botsDeployedFailed";
        }

    }

    @RequestMapping(value="/workers/{workerId}/bots/run/{botId}")
    @PreAuthorize("hasRole('logon')")
    public String runBot(@PathVariable int botId, ModelMap model) throws Exception
    {
        if(this.startBot(botId))
        {
            return "redirect:/bots/?botStartedSuccess";
        }
        else
        {
            return "redirect:/bots/?botStartedFailed";
        }
    }
/*
    @RequestMapping(value="/workers/{workerId}/bots/run/{botId1}/{botId2}")
    @PreAuthorize("hasRole('logon')")
    public String runBots(ModelMap model)
    {
        int botId1, botId2;
        botId1 = this.parseInteger(botId1);
        botId2 = this.parseInteger(botId2);

        if(this.startBots(botId1, botId2))
        {
            return "redirect:/workers/{workerId}/bots/?botsStartedSuccess";
        }
        else
        {
            return "redirect:/workers/{workerId}/bots/?botsStartedFailed";
        }
    }
*/
    @RequestMapping(value="/workers/{workerId}/bots/stop/{botId}")
    @PreAuthorize("hasRole('logon')")
    public String stopBot(@PathVariable int botId, ModelMap model) throws Exception
    {
        if(this.stopBot(botId))
        {
            return "redirect:/bots/?botStoppedSuccess";
        }
        else
        {
            return "redirect:/bots/?botStoppedFailed";
        }
    }

    @RequestMapping(value="/workers/{workerId}/bots/restart/{botId}")
    @PreAuthorize("hasRole('logon')")
    public String restartBot(@PathVariable int botId, ModelMap model)
    {
        if(this.restartBot(botId))
        {
            return "redirect:/bots/?botRestartedSuccess";
        }
        else
        {
            return "redirect:/bots/?botRestartedFailed";
        }
    }

    @RequestMapping(value="/workers/{workerId}/bots/delete/{botId}")
    @PreAuthorize("hasRole('logon')")
    public String killBot(@PathVariable int botId, ModelMap model)
    {
        if(this.killBot(botId))
        {
            return "redirect:/bots/?botKilledSuccess";
        }
        else
        {
            return "redirect:/bots/?botKilledFailed";
        }
    }

    @RequestMapping(value="/workers/{workerId}/bots/deleteAll")
    @PreAuthorize("hasRole('logon')")
    public String killAllBots(ModelMap model)
    {
        if(this.killAllBots())
        {
            return "redirect:/bots/?botsDeletedSuccess";
        }
        else
        {
            return "redirect:/bots/?botsDeletedFailed";
        }
    }

    @RequestMapping(value="/workers/{workerId}/bots/set/{botId}/{property}/{value}")
    @PreAuthorize("hasRole('logon')")
    public String setBot(@PathVariable int botId, @PathVariable String property, @PathVariable String value, ModelMap model)
    {
        if(this.setBotProperty(botId, property, String.valueOf(value)))
        {
            return "redirect:/bots/?botEditedSuccess";
        }
        else
        {
            return "redirect:/bots/?botEditedFailed";
        }
    }

    private boolean instantiateBot(String type)
    {
        SimBot bot;

        switch(type.toLowerCase().trim())
        {
            case "car":
                bot = dispatchService.instantiateBot(type);
                break;
            case "drone":
                bot = dispatchService.instantiateBot(type);
                break;
            case "f1":
                bot = dispatchService.instantiateBot(type);
                break;
            default:
                terminal.printTerminalInfo("Bottype: '" + type + "' is unknown!");
                terminal.printTerminalInfo("Known types: {car | drone | f1}");
                return false;
        }

        if(bot != null)
        {
            terminal.printTerminalInfo("New bot of type: '" + bot.getType() + "' and name: '" + bot.getName() + "' instantiated.");
            return true;
        }
        else
        {
            terminal.printTerminalError("Could not instantiate bot of type: " + type + "!");
            return false;
        }
    }

    private boolean instantiateBots(String type, int amount)
    {
        SimBot bot = null;
        boolean existingType = true;

        switch(type.toLowerCase().trim()) {
            case "car":
                bot = dispatchService.instantiateBot(type);
                break;
            case "drone":
                bot = dispatchService.instantiateBot(type);
                break;
            case "f1":
                bot = dispatchService.instantiateBot(type);
                break;
            default:
                existingType = false;
                terminal.printTerminalInfo("Bottype: '" + type + "' is unknown!");
                terminal.printTerminalInfo("Known types: {car | drone | f1}");
        }
        if(bot == null)
        {
            terminal.printTerminalError("Could not instantiate bot of type: " + type + "!");
            return false;
        }
        else
        {
            terminal.printTerminalInfo("New bot of type: '" + bot.getType() + "' and name: '" + bot.getName() + "' instantiated.");
        }

        int i = 1;
        while(i < amount && bot != null)
        {
            bot = dispatchService.instantiateBot(type);
            if(bot == null)
            {
                terminal.printTerminalError("Could not instantiate bot of type: " + type + "!");
                return false;
            }
            else
            {
                terminal.printTerminalInfo("New bot of type: '" + bot.getType() + "' and name: '" + bot.getName() + "' instantiated.");
            }
            i++;
        }
        return existingType;
    }

    private boolean startBot(int botId)
    {
        if(supervisorService.startBot(botId))
        {
            terminal.printTerminalInfo("Bot started with id: " + botId + ".");
            return true;
        }
        else
        {
            terminal.printTerminalError("Could not start bot with id: " + botId + "!");
            return false;
        }
    }

    private boolean startBots(int botId1, int botId2)
    {
        boolean success = true;
        int i = botId1;
        while(success && i <= botId2)
        {
            success = supervisorService.startBot(i);
            if(success)
            {
                terminal.printTerminalInfo("Bot started with id: " + i + ".");
            }
            else
            {
                terminal.printTerminalError("Could not start bot with id: " + i + "!");
            }
            i++;
        }
        if(success)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean stopBot(int botId)
    {
        if(supervisorService.stopBot(botId))
        {
            terminal.printTerminalInfo("Bot stopped with id: " + botId + ".");
            return true;
        }
        else
        {
            terminal.printTerminalError("Could not stop bot with id: " + botId + "!");
            return false;
        }
    }

    private boolean restartBot(int botId)
    {
        if(supervisorService.restartBot(botId))
        {
            terminal.printTerminalInfo("Bot restarted with id: " + botId + ".");
            return true;
        }
        else
        {
            terminal.printTerminalError("Could not restart bot with id: " + botId + "!");
            return false;
        }
    }

    private boolean killBot(int botId)
    {
        if(supervisorService.removeBot(botId))
        {
            terminal.printTerminalInfo("Bot killed with id: " + botId + ".");
            return true;
        }
        else
        {
            terminal.printTerminalError("Could not kill bot with id: " + botId + "!");
            return false;
        }
    }

    private boolean killAllBots()
    {
        SimBot bot;
        int i = 0;
        boolean success = true;
        List<SimBot> botList = supervisorService.findAll();

        while(i < botList.size() && success)
        {
            bot = botList.get(i);
            if(bot != null)
            {
                success = supervisorService.removeBot(bot.getId());
                if(success)
                {
                    terminal.printTerminalInfo("Bot killed with id: " + bot.getId() + ".");
                    i--;
                }
                else
                {
                    terminal.printTerminalInfo("Could not kill bot with id: " + bot.getId() + "!");
                }
            }
            i++;
        }
        return success;
    }

    private boolean setBotProperty(int botId, String property, String value)
    {
        if(supervisorService.setBotProperty(botId, property, value))
        {
            terminal.printTerminalInfo("Property set for bot with id: " + botId + ".");
            return true;
        }
        else
        {
            terminal.printTerminalError("Could not set property for bot with id: " + botId + "!");
            return false;
        }
    }

    private int parseInteger(String value) throws Exception
    {
        int parsedInt;

        try
        {
            parsedInt = Integer.parseInt(value);
        }
        catch(NumberFormatException e)
        {
            throw new Exception("'" + value + "' is not an integer value!");
        }

        return parsedInt;
    }
}
