package br.com.fiap.iottu.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    @Query("SELECT t FROM Tag t WHERE t.motorcycles IS EMPTY")
    List<Tag> findAvailableTags();

    @Query("SELECT DISTINCT t FROM Tag t JOIN t.motorcycles m WHERE m.yard.user.id = :userId")
    List<Tag> findByUserId(@Param("userId") Integer userId);

    Optional<Tag> findByRfidCode(String rfidCode);
}
