package br.com.fiap.iottu.antenna;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AntennaRepository extends JpaRepository<Antenna, Integer> {

    List<Antenna> findByYardId(Integer yardId);
    List<Antenna> findByYardUserId(Integer userId);
    Optional<Antenna> findByCode(String code);

    Optional<Antenna> findByCodeAndYardId(String code, Integer yardId);

    List<Antenna> findAllByLatitudeAndLongitudeAndYardId(java.math.BigDecimal latitude, java.math.BigDecimal longitude, Integer yardId);

}
