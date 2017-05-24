package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.security.User;
import be.uantwerpen.sc.services.security.RoleService;
import be.uantwerpen.sc.services.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Thomas on 03/04/2016.
 */
@Controller
public class UserController extends GlobalModelController
{
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    // Allowed user fields
    @InitBinder
    private void allowFields(WebDataBinder webDataBinder)
    {
        webDataBinder.setAllowedFields("username", "firstName", "lastName", "password", "roles");
    }

    // Shows certain user information
    @RequestMapping(value="/users/{username}/", method=RequestMethod.GET)
    @PreAuthorize("hasRole('logon')")
    public String showUserForm(@PathVariable String username, HttpServletRequest request, ModelMap model)
    {
        User user = userService.findByUsername(username);

        if(user != null)
        {
            model.addAttribute("user", user);
            model.addAttribute("allRoles", roleService.findAll());
        }
        else
        {
            model.clear();
            return "redirect:/settings/users?errorUserNotFound";
        }

        return "protected/forms/userForm";
    }

    // Edit user information
    @RequestMapping(value="/users/{username}", method=RequestMethod.POST)
    @PreAuthorize("hasRole('logon')")
    public String editUser(@Validated @ModelAttribute("user") User user, BindingResult result, HttpServletRequest request, SessionStatus sessionStatus, ModelMap model)
    {
        if(result.hasErrors())
        {
            return "protected/forms/userForm";
        }

        String[] path = request.getServletPath().split("/");

        User retrievedUser = userService.findByUsername(user.getUsername());
        if(retrievedUser != null)
        {
            return "redirect:/settings/users?errorAlreadyExists";
        }
        else
        {
            retrievedUser = userService.findByUsername(path[2]);
            retrievedUser.setFirstName(user.getFirstName());
            retrievedUser.setLastName(user.getLastName());
            retrievedUser.setUsername(user.getUsername());
            retrievedUser.setPassword(user.getPassword());
            retrievedUser.setRoles(user.getRoles());
            userService.save(retrievedUser);
            return "redirect:/settings/users?userEdited";
        }
    }

    // Create new user
    @RequestMapping(value="/user/create", method=RequestMethod.POST)
    @PreAuthorize("hasRole('logon')")
    public String createUser(@Validated @ModelAttribute("user") User user, BindingResult result, HttpServletRequest request, SessionStatus sessionStatus, ModelMap model)
    {
        if(result.hasErrors())
        {
            return "protected/settings/users";
        }

        if(userService.add(user))
        {
            return "redirect:/settings/users?userAdded";
        }
        else
        {
            return "redirect:/settings/users?errorAlreadyExists";
        }
    }

    // Delete user
    @RequestMapping(value="/users/{username}/delete")
    @PreAuthorize("hasRole('logon')")
    public String deleteUser(@PathVariable String username, HttpServletRequest request, ModelMap model)
    {
        if(!userService.getPrincipalUser().getUsername().equals(username))
        {
            if(userService.delete(username))
            {
                model.clear();
                return "redirect:/settings/users?userRemoved";
            }
            else
            {
                return "redirect:/settings/users?errorUserRemove";
            }
        }
        else
        {
            return "redirect:/settings/users?errorUserActive";
        }
    }
}
