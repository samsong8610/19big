package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.QuestionLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface QuestionLevelRepository extends JpaRepository<QuestionLevel, Long> {
    Collection<QuestionLevel> findByActiveAndLevel(boolean active, int level);

    Collection<QuestionLevel> findByActive(boolean active);

    Collection<QuestionLevel> findByLevel(int level);
}
