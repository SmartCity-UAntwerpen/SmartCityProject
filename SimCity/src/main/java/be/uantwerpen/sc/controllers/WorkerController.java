package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.sim.SimWorker;
import be.uantwerpen.sc.services.sim.SimWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Thomas on 03/04/2016.
 */
@Controller
public class WorkerController extends GlobalModelController
{
    @Autowired
    private SimWorkerService workerService;

    @RequestMapping(value="/workers/{workerName}/delete")
    @PreAuthorize("hasRole('logon')")
    public String deleteUser(@PathVariable String workerName, HttpServletRequest request, ModelMap model)
    {
        if(workerService.delete(workerName))
        {
            model.clear();
            return "redirect:/settings/workers?workerRemoved";
        }
        else
        {
            return "redirect:/settings/workers?errorWorkerRemove";
        }
    }

    @RequestMapping(value="/worker/add", method= RequestMethod.POST)
    @PreAuthorize("hasRole('logon')")
    public String addWorker(@Validated @ModelAttribute("worker") SimWorker worker, BindingResult result, HttpServletRequest request, SessionStatus sessionStatus, ModelMap model)
    {
        if(result.hasErrors())
        {
            return "protected/settings/workers";
        }

        if(workerService.add(worker))
        {
            return "redirect:/settings/workers?workerAdded";
        }
        else
        {
            return "redirect:/settings/workers?errorAlreadyExists";
        }
    }

}
