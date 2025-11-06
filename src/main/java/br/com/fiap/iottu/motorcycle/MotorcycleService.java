package br.com.fiap.iottu.motorcycle;

import br.com.fiap.iottu.dto.MotorcycleDataDTO;
import br.com.fiap.iottu.motorcyclestatus.MotorcycleStatus;
import br.com.fiap.iottu.motorcyclestatus.MotorcycleStatusRepository;
import br.com.fiap.iottu.tag.Tag;
import br.com.fiap.iottu.tag.TagService;
import br.com.fiap.iottu.yard.Yard;
import br.com.fiap.iottu.yard.YardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintViolationException;
import jakarta.persistence.RollbackException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;

@Service
public class MotorcycleService {

    private static final Logger logger = LoggerFactory.getLogger(MotorcycleService.class);

    @Autowired
    private MotorcycleRepository repository;

    @Autowired
    private MotorcycleStatusRepository motorcycleStatusRepository;

    @Autowired
    private YardRepository yardRepository;

    @Autowired
    private TagService tagService;

    public List<Motorcycle> findAll() {
        return repository.findAll();
    }
    
    public List<Motorcycle> findByUserId(Integer userId) {
        return repository.findByYardUserId(userId);
    }

    public Optional<Motorcycle> findById(Integer id) {
        return repository.findById(id);
    }

    public List<Motorcycle> findByYardId(Integer yardId) {
        return repository.findByYardId(yardId);
    }

    public void save(Motorcycle motorcycle) {
        repository.save(motorcycle);
    }

    public Map<String, String> validateDuplicate(Motorcycle motorcycle) {
        Map<String, String> errors = new HashMap<>();

        String chassi = motorcycle.getChassi() != null ? motorcycle.getChassi().trim() : null;
        if (chassi != null && !chassi.isBlank()) {
            List<Motorcycle> matches = repository.findAllByChassi(chassi);
            boolean duplicate = matches.stream().anyMatch(m -> motorcycle.getId() == null || !m.getId().equals(motorcycle.getId()));
            if (duplicate) {
                errors.put("chassi", "service.motorcycle.error.duplicateChassi");
            }
        }

        String plate = motorcycle.getLicensePlate() != null ? motorcycle.getLicensePlate().trim() : null;
        if (plate != null && !plate.isBlank()) {
            List<Motorcycle> matches = repository.findAllByLicensePlate(plate);
            boolean duplicate = matches.stream().anyMatch(m -> motorcycle.getId() == null || !m.getId().equals(motorcycle.getId()));
            if (duplicate) {
                errors.put("licensePlate", "service.motorcycle.error.duplicateLicensePlate");
            }
        }

        String engine = motorcycle.getEngineNumber() != null ? motorcycle.getEngineNumber().trim() : null;
        if (engine != null && !engine.isBlank()) {
            List<Motorcycle> matches = repository.findAllByEngineNumber(engine);
            boolean duplicate = matches.stream().anyMatch(m -> motorcycle.getId() == null || !m.getId().equals(motorcycle.getId()));
            if (duplicate) {
                errors.put("engineNumber", "service.motorcycle.error.duplicateEngineNumber");
            }
        }

        return errors;
    }

    @Transactional
    public Motorcycle saveOrUpdateWithTag(Motorcycle motorcycle, Integer selectedTagId) {
        if (selectedTagId == null) {
            throw new IllegalArgumentException("{validation.motorcycle.selectedTag.notNull}");
        }
        Optional<Tag> tagOptional = tagService.findById(selectedTagId);

        if (tagOptional.isEmpty()) {
            throw new IllegalArgumentException("{service.motorcycle.error.tagNotFound}");
        }
        Tag newTag = tagOptional.get();

        logger.debug("Saving motorcycle - id: {}, selectedTagId: {}", motorcycle.getId(), selectedTagId);

         if (newTag.getMotorcycles() != null && !newTag.getMotorcycles().isEmpty()) {
             boolean isAssignedToThisMotorcycle = motorcycle.getId() != null &&
                     newTag.getMotorcycles().stream()
                             .anyMatch(m -> Objects.equals(m.getId(), motorcycle.getId()));

             if (!isAssignedToThisMotorcycle) {
                 throw new IllegalArgumentException("{service.motorcycle.error.tagAlreadyAssigned}");
             }
         }

         if (motorcycle.getId() != null) {
             Optional<Motorcycle> existingMotorcycleOptional = repository.findById(motorcycle.getId());
             if (existingMotorcycleOptional.isPresent()) {
                 Motorcycle existingMotorcycle = existingMotorcycleOptional.get();

                 if (existingMotorcycle.getTags() != null && !existingMotorcycle.getTags().isEmpty()) {
                     Tag oldTag = existingMotorcycle.getTags().get(0);

                     logger.debug("Existing motorcycle {} has oldTag id: {}", existingMotorcycle.getId(), oldTag.getId());
                     if (!Objects.equals(oldTag.getId(), newTag.getId())) {
                         logger.debug("Old tag id {} differs from new tag id {}", oldTag.getId(), newTag.getId());
                     }
                 }
             }
         }
        motorcycle.setTags(new ArrayList<>());
        motorcycle.getTags().add(newTag);
        logger.debug("About to persist motorcycle (id before save): {}", motorcycle.getId());
        Motorcycle saved = repository.save(motorcycle);
        logger.debug("Motorcycle persisted (id after save): {}", saved.getId());
        try {
            repository.flush();
        } catch (ConstraintViolationException | org.springframework.dao.DataIntegrityViolationException | RollbackException e) {
            logger.error("Falha ao salvar Motorcycle (flush): {}", e.getMessage(), e);
            throw new IllegalStateException("Falha ao persistir a motocicleta: " + e.getMessage(), e);
        }
        return saved;
    }

    @Transactional
    public void processMotorcyclesData(List<MotorcycleDataDTO> motorcycleDataDTOs) {
        for (MotorcycleDataDTO dto : motorcycleDataDTOs) {
            Motorcycle motorcycle = repository.findByChassi(dto.getChassiMoto()).orElse(new Motorcycle());

            MotorcycleStatus status = motorcycleStatusRepository.findById(dto.getIdStatus())
                    .orElseThrow(() -> new IllegalArgumentException("{service.motorcycle.error.statusNotFound}" + dto.getIdStatus()));
            motorcycle.setStatus(status);

            Yard yard = yardRepository.findById(dto.getIdPatio())
                    .orElseThrow(() -> new IllegalArgumentException("{service.motorcycle.error.yardNotFound}" + dto.getIdPatio()));
            motorcycle.setYard(yard);

            motorcycle.setLicensePlate(dto.getPlacaMoto());
            motorcycle.setChassi(dto.getChassiMoto());
            motorcycle.setEngineNumber(dto.getNrMotorMoto());
            motorcycle.setModel(dto.getModeloMoto());

            if (dto.getCodigoRfidTag() != null && !dto.getCodigoRfidTag().isEmpty()) {
                Tag tag = tagService.findOrCreateTag(
                        dto.getCodigoRfidTag(),
                        dto.getSsidWifiTag(),
                        dto.getLatitude(),
                        dto.getLongitude()
                );
                if (motorcycle.getTags() == null) {
                    motorcycle.setTags(new ArrayList<>());
                }
                if (!motorcycle.getTags().contains(tag)) {
                    motorcycle.getTags().add(tag);
                }
            }
            repository.save(motorcycle);
        }
    }

    @Transactional
    public void deleteByIdWithTagUnbinding(Integer id) {
        Motorcycle motorcycleToDelete = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("{service.motorcycle.error.notFoundById}" + id));

        if (motorcycleToDelete.getTags() != null  && !motorcycleToDelete.getTags().isEmpty()) {
            Tag associatedTag = motorcycleToDelete.getTags().get(0);
        }
        repository.deleteById(id);
        try {
            repository.flush();
        } catch (ConstraintViolationException | org.springframework.dao.DataIntegrityViolationException | RollbackException e) {
            logger.error("Falha ao deletar Motorcycle (flush): {}", e.getMessage(), e);
            throw new IllegalStateException("Falha ao deletar a motocicleta: " + e.getMessage(), e);
        }
    }
}