package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.sim.SimBot;
import be.uantwerpen.sc.models.sim.SimWorker;
import be.uantwerpen.sc.services.sim.SimSupervisorService;
import be.uantwerpen.sc.services.sim.SimWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Thomas on 04/04/2016.
 */
@Controller
public class GlobalModelController
{
    @Autowired
    private SimWorkerService simWorkerService;

    @Autowired
    private SimSupervisorService simSupervisorService;

    @ModelAttribute("numOfWorkers")
    public int getNumberOfWorkers()
    {
        return simWorkerService.getNumberOfWorkers();
    }

    @ModelAttribute("allWorkers")
    public Iterable<SimWorker> getAllWorkers()
    {
        return simWorkerService.findAll();
    }

    @ModelAttribute("numOfBots")
    public int getNumberOfBots()
    {
        return simSupervisorService.getNumberOfBots();
    }

/*    @ModelAttribute("allBots")
    public Iterable<SimBot> getAllBots()
    {
        //return botService.findAll();
        return
    }*/

    @ModelAttribute("serverName")
    public String getServerName()
    {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Server machine name could not be retrieved.";
        }
    }

    @ModelAttribute("serverIp")
    public String getServerIp()
    {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "No IP address assigned to this server machine";
        }
    }
}
