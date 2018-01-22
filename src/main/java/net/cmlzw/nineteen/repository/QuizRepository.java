package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Quiz findByUsernameAndLevelAndCreated(String username, int level, Date today);

    List<Quiz> findByUsernameAndCreated(String username, Date today);

    List<Quiz> findTop20ByLevelAndCreatedOrderByScoreDesc(Integer level, Date today);

    List<Quiz> findTop20ByLevelOrderByScoreDescCreatedDesc(Integer level);

    @Query("select count(distinct q.username) from Quiz q where q.organizationId = ?1")
    int countDistinctUsernameByOrganizationId(Long orgId);

    Quiz findFirstByUsernameAndLevelOrderByScoreDesc(String username, int level);

    Quiz findByUsernameAndLevel(String username, int level);

    List<Quiz> findByUsername(String username);

    List<Quiz> findByLevelAndScore(int level, int score);

    List<Quiz> findByLevelAndScoreGreaterThanEqual(int level, int score);

    List<Quiz> findByPhone(String phone);
}
