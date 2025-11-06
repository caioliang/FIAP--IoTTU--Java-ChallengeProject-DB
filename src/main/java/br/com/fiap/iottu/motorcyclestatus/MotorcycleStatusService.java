package br.com.fiap.iottu.motorcyclestatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MotorcycleStatusService {

    @Autowired
    private MotorcycleStatusRepository repository;

    public List<MotorcycleStatus> findAll() {
        return repository.findAll();
    }

}
