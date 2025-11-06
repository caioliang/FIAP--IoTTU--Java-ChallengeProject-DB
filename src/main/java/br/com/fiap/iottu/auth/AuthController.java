package br.com.fiap.iottu.auth;

import br.com.fiap.iottu.helper.MessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fiap.iottu.user.User;
import br.com.fiap.iottu.user.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private MessageHelper messageHelper;

    @GetMapping("/login")
    public String loginPage(Authentication authentication, Model model, @RequestParam(value = "logout", required = false) String logout) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }

        if (logout != null) {
            model.addAttribute("successMessage", messageHelper.getMessage("message.success.auth.loggedOut"));
        }

        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Validated @ModelAttribute("user") User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "error.user", messageHelper.getMessage("validation.user.email.inUse"));
            return "auth/register";
        }

        if (userService.count() == 0) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }

        userService.save(user);

        redirectAttributes.addFlashAttribute("successMessage", messageHelper.getMessage("message.success.auth.registered"));
        return "redirect:/login";
    }
}