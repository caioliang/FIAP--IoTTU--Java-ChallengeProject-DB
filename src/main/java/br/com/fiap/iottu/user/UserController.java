package br.com.fiap.iottu.user;

import br.com.fiap.iottu.helper.MessageHelper;
import br.com.fiap.iottu.validation.OnUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private MessageHelper messageHelper;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", service.findAll());
        return "user/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("user", service.findById(id).orElseThrow());
        return "user/profile";
    }

    @GetMapping("/edit/{id}")
    public String formForUpdate(@PathVariable Integer id, Model model) {
        model.addAttribute("user", service.findById(id).orElseThrow());
        return "user/form";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Integer id, @Validated(OnUpdate.class) @ModelAttribute User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "user/form";
        }
        try {
            service.updateWithPasswordPreservation(id, user);
            redirectAttributes.addFlashAttribute("successMessage", messageHelper.getMessage("message.success.user.updated"));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("failureMessage", messageHelper.getMessage("message.error.user.updateFailed") + e.getMessage());
            return "redirect:/users";
        }
        return "redirect:/users";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        service.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.getMessage("message.success.user.deleted"));
        return "redirect:/users";
    }

    @PostMapping("/{id}/promote")
    public String promoteToAdmin(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            service.promoteToAdmin(id);
            redirectAttributes.addFlashAttribute("successMessage", messageHelper.getMessage("message.success.user.promoted"));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("failureMessage", messageHelper.getMessage("message.error.user.updateFailed") + e.getMessage());
        }
        return "redirect:/users";
    }
}