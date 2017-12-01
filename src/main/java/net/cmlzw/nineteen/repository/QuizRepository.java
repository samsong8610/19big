package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Quiz findByPersonIdAndLevelAndCreated(Long id, int level, LocalDate today);

    List<Quiz> findByPersonIdAndCreated(Long uid, LocalDate today);

    List<Quiz> findTop20ByLevelAndCreatedOrderByScoreDesc(Integer level, LocalDate today);
}
