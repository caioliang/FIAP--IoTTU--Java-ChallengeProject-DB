package br.com.fiap.iottu.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    @Autowired
    private TagRepository repository;

    public List<Tag> findAll() {
        return repository.findAll();
    }
    
    public List<Tag> findByUserId(Integer userId) {
        return repository.findByUserId(userId);
    }

    public Optional<Tag> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public void save(Tag tag) {
        repository.save(tag);
    }

    @Transactional
    public void deleteById(Integer id) {
        Tag tag = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("{service.tag.error.notFoundById}" + id));
        if (tag.getMotorcycles() != null && !tag.getMotorcycles().isEmpty()) {
            throw new IllegalStateException("{message.error.tag.deleteHasMotorcycles}" + id);
        }
        repository.deleteById(id);
    }

    public List<Tag> findAvailableTags() {
        return repository.findAvailableTags();
    }

    @Transactional
    public Tag findOrCreateTag(String rfidCode, String wifiSsid, Double latitude, Double longitude) {
        Optional<Tag> existingTag = repository.findByRfidCode(rfidCode);
        Tag tag;
        if (existingTag.isPresent()) {
            tag = existingTag.get();
        } else {
            tag = new Tag();
            tag.setRfidCode(rfidCode);
        }
        tag.setWifiSsid(wifiSsid);
        if (latitude != null) {
            tag.setLatitude(BigDecimal.valueOf(latitude));
        }
        if (longitude != null) {
            tag.setLongitude(BigDecimal.valueOf(longitude));
        }
        return repository.save(tag);
    }

    public void validateDuplicate(Tag tag) {
        String rfid = tag.getRfidCode() != null ? tag.getRfidCode().trim() : null;
        if (rfid == null) return;
        Optional<Tag> existing = repository.findByRfidCode(rfid);
        if (existing.isPresent()) {
            Tag found = existing.get();
            if (tag.getId() == null || !found.getId().equals(tag.getId())) {
                throw new IllegalArgumentException("{service.tag.error.duplicate}");
            }
        }
    }
}
