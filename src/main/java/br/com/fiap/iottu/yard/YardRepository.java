package br.com.fiap.iottu.yard;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface YardRepository extends JpaRepository<Yard, Integer>, YardRepositoryCustom {
    Optional<Yard> findByZipCodeAndNumber(String zipCode, String number);
    List<Yard> findByUserId(Integer userId);
}