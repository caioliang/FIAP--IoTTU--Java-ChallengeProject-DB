package br.com.fiap.iottu.yard;

import br.com.fiap.iottu.helper.MessageHelper;
import br.com.fiap.iottu.user.User;
import br.com.fiap.iottu.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/yards")
public class YardController {

    @Autowired
    private YardService service;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageHelper messageHelper;

    @GetMapping
    public String listYards(Model model) {
        model.addAttribute("yards", service.findAll());
        return "yard/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Yard yard = new Yard();
        if (userDetails != null && userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
            yard.setUser(user);
        }
        model.addAttribute("yard", yard);
        model.addAttribute("users", userService.findAll());
        return "yard/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Yard yard, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            return "yard/form";
        }
        if (userDetails != null && userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
            yard.setUser(user);
        }
        try {
            service.validateDuplicate(yard);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("zipCode", "DuplicateYard", messageHelper.getMessage("message.error.yard.duplicate"));
            model.addAttribute("users", userService.findAll());
            return "yard/form";
        }
        service.save(yard);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.getMessage("message.success.yard.created"));
        return "redirect:/yards";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        model.addAttribute("yard", service.findById(id).orElseThrow());
        model.addAttribute("users", userService.findAll());
        return "yard/form";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute Yard yard, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            return "yard/form";
        }
        yard.setId(id);
        if (userDetails != null && userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
            yard.setUser(user);
        }
        try {
            service.validateDuplicate(yard);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("zipCode", "DuplicateYard", messageHelper.getMessage("message.error.yard.duplicate"));
            model.addAttribute("users", userService.findAll());
            return "yard/form";
        }
        service.save(yard);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.getMessage("message.success.yard.updated"));
        return "redirect:/yards";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", messageHelper.getMessage("message.success.yard.deleted"));
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("failureMessage", messageHelper.getMessage("message.error.yard.deleteHasMotorcycles"));
        }
        return "redirect:/yards";
    }
    @GetMapping("/{id}/map")
    public String showYardMap(@PathVariable Integer id, Model model) {
        Yard yard = service.findById(id).orElseThrow(() -> new IllegalArgumentException("{message.error.yard.invalid}" + id));
        YardMapDTO mapData = service.prepareYardMapData(id);
        model.addAttribute("yard", yard);
        model.addAttribute("mapData", mapData);
        boolean canShowMap = mapData.getAntennas() != null && mapData.getAntennas().size() >= 3;
        model.addAttribute("canShowMap", canShowMap);

        return "yard/map";
    }
}