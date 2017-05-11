package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.sim.SimWorker;
import be.uantwerpen.sc.services.sim.SimWorkerService;
import be.uantwerpen.sc.tools.TypesList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 03/04/2016.
 */
@Controller
public class WorkerController extends GlobalModelController
{
    @Autowired
    private SimWorkerService workerService;

    @Autowired
    TypesList typesList;

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

    @RequestMapping(value="/workers/add", method= RequestMethod.POST)
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

    @RequestMapping(value="/workers/{workerId}", method= RequestMethod.POST)
    @PreAuthorize("hasRole('logon')")
    public String editWorker(@Validated @ModelAttribute("worker") SimWorker worker, BindingResult result, HttpServletRequest request, SessionStatus sessionStatus, ModelMap model)
    {
        if(result.hasErrors())
        {
            return "protected/settings/workers";
        }
        SimWorker w = workerService.findByWorkerId(worker.getWorkerId());
        w.setWorkerName(worker.getWorkerName());
        w.setServerURL(worker.getServerURL());
        System.out.println(w.getWorkerName());
        System.out.println(w.getServerURL());

        if(workerService.save(w))
        {
            return "redirect:/settings/workers?workerEdited";
        }
        else
        {
            return "redirect:" + request.getRequestURI() + "?errorAlreadyExists";
        }
    }

    // Mapping moet /workers/{id}/... worden
    @RequestMapping(value="/workers/management", method= RequestMethod.GET)
    public String manageWorker(ModelMap model, @Validated @ModelAttribute("type") String type)
    {
        List<String> types = new ArrayList<String>();
        try {
            types = typesList.getTypes();
            model.addAttribute("types", types);
        } catch (Exception e) {
            types.add("No types could be loaded!");
            e.printStackTrace();
            model.addAttribute("types", types);
        }
        return "protected/workerManagement";
    }

}
