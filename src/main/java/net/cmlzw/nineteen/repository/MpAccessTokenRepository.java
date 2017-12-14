package net.cmlzw.nineteen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MpAccessTokenRepository extends JpaRepository<net.cmlzw.nineteen.domain.MpAccessToken, Long> {
}
