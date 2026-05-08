package com.naz.profiler.repository;


import com.naz.profiler.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID>, JpaSpecificationExecutor<Profile> {

    Optional<Profile> findByNameIgnoreCase(String name);

    List<Profile> findByGenderIgnoreCase(String gender);

    List<Profile> findByCountryIdIgnoreCase(String countryId);

    List<Profile> findByAgeGroupIgnoreCase(String ageGroup);
    @Query("""
    SELECT p
    FROM Profile p
    WHERE LOWER(p.name) IN :names
    """)
    List<Profile> findExistingNames(
            @Param("names") Collection<String> names
    );
}
