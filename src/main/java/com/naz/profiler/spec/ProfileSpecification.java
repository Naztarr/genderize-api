package com.naz.profiler.spec;

import com.naz.profiler.dto.ProfileFilterRequest;
import com.naz.profiler.entity.Profile;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProfileSpecification {
    public static Specification<Profile> filter(ProfileFilterRequest filter){
        return((root, query, cb) -> {
            List<Predicate> pd = new ArrayList<>();
            if(filter.getGender() != null) pd.add(cb.equal(cb.lower(root.get("gender")),
                    filter.getGender().toLowerCase()));
            if(filter.getAgeGroup() != null) pd.add(cb.equal(cb.lower(root.get("ageGroup")),
                    filter.getAgeGroup().toLowerCase()));
            if(filter.getCountryId() != null) pd.add(cb.equal(cb.lower(root.get("countryId")),
                    filter.getCountryId().toLowerCase()));
            if(filter.getMinAge() != null) pd.add(cb.ge(root.get("age"), filter.getMinAge()));
            if(filter.getMaxAge() != null) pd.add(cb.le(root.get("age"), filter.getMaxAge()));
            if(filter.getMinGenderProbability() != null) pd.add(cb.ge(root.get("genderProbability"),
                    filter.getMinGenderProbability()));
            if(filter.getMinCountryProbability() != null) pd.add(cb.ge(root.get("countryProbability"),
                    filter.getMinCountryProbability()));
            return cb.and(pd.toArray(new Predicate[0]));

        });
    }
}
