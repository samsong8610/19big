package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.JobLock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobLockRepository extends JpaRepository<JobLock, String> {
}
