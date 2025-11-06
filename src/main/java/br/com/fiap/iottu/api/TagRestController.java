package br.com.fiap.iottu.api;

import br.com.fiap.iottu.tag.Tag;
import br.com.fiap.iottu.tag.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@CacheConfig(cacheNames = "tags")
public class TagRestController {

    private static final Logger log = LoggerFactory.getLogger(TagRestController.class);

    @Autowired
    private TagService tagService;

    @GetMapping
    @Cacheable
    public List<Tag> list(@RequestParam(required = false) Integer userId) {
        List<Tag> tags;
        if (userId != null) {
            tags = tagService.findByUserId(userId);
        } else {
            tags = tagService.findAll();
        }
        log.info("GET /api/v1/tags?userId={} -> {} registros", userId, tags.size());
        return tags;
    }

    @GetMapping("/available")
    @Cacheable
    public List<Tag> listAvailable() {
        return tagService.findAvailableTags();
    }

    @GetMapping("/{id}")
    public Tag getById(@PathVariable Integer id) {
        log.info("GET /api/v1/tags/{}", id);
        return tagService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag não encontrada, id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(allEntries = true)
    public Tag create(@Valid @RequestBody Tag tag) {
        try {
            tagService.validateDuplicate(tag);
            tagService.save(tag);
            return tag;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @CacheEvict(allEntries = true)
    public Tag update(@PathVariable Integer id, @Valid @RequestBody Tag tag) {
        tagService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag não encontrada, id=" + id));
        try {
            tag.setId(id);
            tagService.validateDuplicate(tag);
            tagService.save(tag);
            return tag;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void delete(@PathVariable Integer id) {
        try {
            tagService.deleteById(id);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
