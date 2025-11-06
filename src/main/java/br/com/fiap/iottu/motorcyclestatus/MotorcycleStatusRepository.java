package br.com.fiap.iottu.motorcyclestatus;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MotorcycleStatusRepository extends JpaRepository<MotorcycleStatus, Integer> {
}
