package generatik.backend.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import generatik.backend.entities.adspace.AdSpace;

@Repository
public interface AdSpaceRepository extends JpaRepository<AdSpace, Long> {
}
