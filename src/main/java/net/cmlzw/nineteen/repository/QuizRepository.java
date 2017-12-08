package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Quiz findByUsernameAndLevelAndCreated(String username, int level, Date today);

    List<Quiz> findByUsernameAndCreated(String username, Date today);

    List<Quiz> findTop20ByLevelAndCreatedOrderByScoreDesc(Integer level, Date today);
}
