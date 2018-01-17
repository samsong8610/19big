package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.OrganizationArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrganizationArchiveRepository extends JpaRepository<OrganizationArchive, Long> {
    @Query("select a.created from OrganizationArchive a group by a.created")
    List<Date> findAllGroupByCreated();

    List<OrganizationArchive> findAllByCreated(Date created);
}
