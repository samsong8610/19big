package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
