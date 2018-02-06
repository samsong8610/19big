package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.QuizArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizArchiveRepository extends JpaRepository<QuizArchive, Long> {
}
