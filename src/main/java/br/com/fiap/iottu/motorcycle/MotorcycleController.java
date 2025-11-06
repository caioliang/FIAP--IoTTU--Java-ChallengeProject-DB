package br.com.fiap.iottu.motorcycle;

import br.com.fiap.iottu.helper.MessageHelper;
import br.com.fiap.iottu.motorcyclestatus.MotorcycleStatusService;
import br.com.fiap.iottu.tag.Tag;
import br.com.fiap.iottu.tag.TagService;
import br.com.fiap.iottu.yard.YardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/motorcycles")
public class MotorcycleController {

    @Autowired
    private MotorcycleService service;

    @Autowired
    private YardService yardService;

    @Autowired
    private MotorcycleStatusService statusService;

    @Autowired
    private TagService tagService;

    @Autowired
    private MessageHelper messageHelper;

    private void addFormData(Model model) {
        model.addAttribute("yards", yardService.findAll());
        model.addAttribute("statuses", statusService.findAll());

        List<Tag> availableTags = tagService.findAvailableTags();
        model.addAttribute("availableTags", availableTags);
    }

    private void addFormData(Model model, Motorcycle motorcycle) {
        addFormData(model);

        Tag currentOrSelectedTag = null;

        if (motorcycle.getTags() != null && !motorcycle.getTags().isEmpty()) {
            currentOrSelectedTag = motorcycle.getTags().get(0);
        } else if (motorcycle.getSelectedTagId() != null) {
            currentOrSelectedTag = tagService.findById(motorcycle.getSelectedTagId()).orElse(null);
        }

        if (currentOrSelectedTag != null) {
            List<Tag> tagsForForm = (List<Tag>) model.getAttribute("availableTags");
            if (!tagsForForm.contains(currentOrSelectedTag)) {
                tagsForForm.add(currentOrSelectedTag);
            }
            tagsForForm.sort((t1, t2) -> t1.getRfidCode().compareTo(t2.getRfidCode()));
            model.addAttribute("availableTags", tagsForForm);
        }
    }

    @GetMapping
    public String listMotorcycles(Model model) {
        model.addAttribute("motorcycles", service.findAll());
        return "motorcycle/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Motorcycle newMotorcycle = new Motorcycle();
        model.addAttribute("motorcycle", newMotorcycle);
        addFormData(model);
        return "motorcycle/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Motorcycle motorcycle, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (motorcycle.getSelectedTagId() == null) {
            bindingResult.rejectValue("selectedTagId", "NotNull", messageHelper.getMessage("validation.motorcycle.selectedTag.notNull"));
        }
        if (bindingResult.hasErrors()) {
            addFormData(model, motorcycle);
            return "motorcycle/form";
        }
        Map<String, String> dupErrors = service.validateDuplicate(motorcycle);
        if (!dupErrors.isEmpty()) {
            dupErrors.forEach((field, msgKey) -> bindingResult.rejectValue(field, "Duplicate", messageHelper.getMessage(msgKey)));
            addFormData(model, motorcycle);
            return "motorcycle/form";
        }
        try {
            service.saveOrUpdateWithTag(motorcycle, motorcycle.getSelectedTagId());
        } catch (IllegalArgumentException | IllegalStateException e) {
            bindingResult.rejectValue("selectedTagId", "TagError", e.getMessage());
            addFormData(model, motorcycle);
            redirectAttributes.addFlashAttribute("failureMessage", messageHelper.getMessage("message.error.motorcycle.createFailed") + e.getMessage());
            return "motorcycle/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.getMessage("message.success.motorcycle.created"));
        return "redirect:/motorcycles";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Motorcycle motorcycle = service.findById(id).orElseThrow();
        if (motorcycle.getTags() != null && !motorcycle.getTags().isEmpty()) {
            motorcycle.setSelectedTagId(motorcycle.getTags().get(0).getId());
        }
        model.addAttribute("motorcycle", motorcycle);

        addFormData(model, motorcycle);
        return "motorcycle/form";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute Motorcycle motorcycle, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        motorcycle.setId(id);

        if (motorcycle.getSelectedTagId() == null) {
            bindingResult.rejectValue("selectedTagId", "NotNull", messageHelper.getMessage("validation.motorcycle.selectedTag.notNull"));
        }

        if (bindingResult.hasErrors()) {
            addFormData(model, motorcycle);
            return "motorcycle/form";
        }
        Map<String, String> dupErrors = service.validateDuplicate(motorcycle);
        if (!dupErrors.isEmpty()) {
            dupErrors.forEach((field, msgKey) -> bindingResult.rejectValue(field, "Duplicate", messageHelper.getMessage(msgKey)));
            addFormData(model, motorcycle);
            return "motorcycle/form";
        }
        try {
            service.saveOrUpdateWithTag(motorcycle, motorcycle.getSelectedTagId());
        } catch (IllegalArgumentException | IllegalStateException e) {
            bindingResult.rejectValue("selectedTagId", "TagError", e.getMessage());
            addFormData(model, motorcycle);
            redirectAttributes.addFlashAttribute("failureMessage", messageHelper.getMessage("message.error.motorcycle.updateFailed") + e.getMessage());
            return "motorcycle/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.getMessage("message.success.motorcycle.updated"));
        return "redirect:/motorcycles";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        service.deleteByIdWithTagUnbinding(id);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.getMessage("message.success.motorcycle.deleted"));
        return "redirect:/motorcycles";
    }
}