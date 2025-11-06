package br.com.fiap.iottu.antenna;

import br.com.fiap.iottu.helper.MessageHelper;
import br.com.fiap.iottu.yard.YardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/antennas")
public class AntennaController {

    @Autowired
    private AntennaService service;

    @Autowired
    private YardService yardService;

    @Autowired
    private MessageHelper messageHelper;

    @GetMapping
    public String listAntennas(Model model) {
        model.addAttribute("antennas", service.findAll());
        return "antenna/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("antenna", new Antenna());
        model.addAttribute("yards", yardService.findAll());
        return "antenna/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Antenna antenna, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("yards", yardService.findAll());
            return "antenna/form";
        }
        try {
            service.validateDuplicate(antenna);
        } catch (IllegalArgumentException e) {
            String key = e.getMessage();
            if (key != null && key.contains("duplicateCode")) {
                bindingResult.rejectValue("code", "DuplicateAntennaCode", messageHelper.getMessage("service.antenna.error.duplicateCode"));
            } else if (key != null && key.contains("duplicateCoordinates")) {
                bindingResult.rejectValue("latitude", "DuplicateAntennaCoordinates", messageHelper.getMessage("service.antenna.error.duplicateCoordinates"));
                bindingResult.rejectValue("longitude", "DuplicateAntennaCoordinates", messageHelper.getMessage("service.antenna.error.duplicateCoordinates"));
            } else {
                bindingResult.reject("AntennaError", messageHelper.getMessage("service.antenna.error.processing"));
            }
            model.addAttribute("yards", yardService.findAll());
            return "antenna/form";
        }
        service.save(antenna);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.getMessage("message.success.antenna.created"));
        return "redirect:/antennas";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        model.addAttribute("antenna", service.findById(id).orElseThrow());
        model.addAttribute("yards", yardService.findAll());
        return "antenna/form";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute Antenna antenna, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("yards", yardService.findAll());
            return "antenna/form";
        }
        antenna.setId(id);
        try {
            service.validateDuplicate(antenna);
        } catch (IllegalArgumentException e) {
            String key = e.getMessage();
            if (key != null && key.contains("duplicateCode")) {
                bindingResult.rejectValue("code", "DuplicateAntennaCode", messageHelper.getMessage("service.antenna.error.duplicateCode"));
            } else if (key != null && key.contains("duplicateCoordinates")) {
                bindingResult.rejectValue("latitude", "DuplicateAntennaCoordinates", messageHelper.getMessage("service.antenna.error.duplicateCoordinates"));
                bindingResult.rejectValue("longitude", "DuplicateAntennaCoordinates", messageHelper.getMessage("service.antenna.error.duplicateCoordinates"));
            } else {
                bindingResult.reject("AntennaError", messageHelper.getMessage("service.antenna.error.processing"));
            }
            model.addAttribute("yards", yardService.findAll());
            return "antenna/form";
        }
        service.save(antenna);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.getMessage("message.success.antenna.updated"));
        return "redirect:/antennas";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        service.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.getMessage("message.success.antenna.deleted"));
        return "redirect:/antennas";
    }
}