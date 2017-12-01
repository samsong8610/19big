package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.Award;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AwardRepository extends JpaRepository<Award, Long> {
    Award findByPhone(String phone);
}
