//package com.naz.profiler.spec;
//
//import com.naz.profiler.dto.ProfileFilterRequest;
//import com.naz.profiler.entity.Profile;
//import jakarta.persistence.criteria.Predicate;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ProfileSpecification {
//
//    public static Specification<Profile> filter(ProfileFilterRequest f) {
//        return (root, query, cb) -> {
//
//            List<Predicate> pd = new ArrayList<>();
//
//            // gender
//            if (f.getGender() != null) {
//                pd.add(cb.equal(root.get("gender"), f.getGender()));
//            }
//
//            // age group
//            if (f.getAgeGroup() != null) {
//                pd.add(cb.equal(root.get("ageGroup"), f.getAgeGroup()));
//            }
//
//            // country
//            if (f.getCountryId() != null) {
//                pd.add(cb.equal(root.get("countryId"), f.getCountryId()));
//            }
//
//            // age
//            if (f.getMinAge() != null) {
//                pd.add(cb.greaterThanOrEqualTo(root.get("age"), f.getMinAge()));
//            }
//
//            if (f.getMaxAge() != null) {
//                pd.add(cb.lessThanOrEqualTo(root.get("age"), f.getMaxAge()));
//            }
//
//            // gender probability
//            if (f.getMinGenderProbability() != null) {
//                pd.add(cb.greaterThanOrEqualTo(
//                        root.get("genderProbability"),
//                        f.getMinGenderProbability()
//                ));
//            }
//
//            // country probability
//            if (f.getMinCountryProbability() != null) {
//                pd.add(cb.greaterThanOrEqualTo(
//                        root.get("countryProbability"),
//                        f.getMinCountryProbability()
//                ));
//            }
//
//            return cb.and(pd.toArray(new Predicate[0]));
//        };
//    }
//}
//




















package com.naz.profiler.spec;

import com.naz.profiler.dto.ProfileFilterRequest;
import com.naz.profiler.entity.Profile;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProfileSpecification {

    public static Specification<Profile> filter(ProfileFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> pd = new ArrayList<>();

            // Use exact match for IDs and Enums if possible
            if (filter.getGender() != null)
                pd.add(cb.equal(cb.lower(root.get("gender")), filter.getGender().toLowerCase().trim()));

            if (filter.getAgeGroup() != null)
                pd.add(cb.equal(cb.lower(root.get("ageGroup")), filter.getAgeGroup().toLowerCase().trim()));

            if (filter.getCountryId() != null && !filter.getCountryId().isBlank())
                // Standardize to Uppercase for IDs
                pd.add(cb.equal(root.get("countryId"), filter.getCountryId().toUpperCase().trim()));

            // Probabilities: Ensure these are strictly Greater Than or Equal
            if (filter.getMinGenderProbability() != null)
                pd.add(cb.greaterThanOrEqualTo(root.get("genderProbability"), filter.getMinGenderProbability()));

            if (filter.getMinCountryProbability() != null)
                pd.add(cb.greaterThanOrEqualTo(root.get("countryProbability"), filter.getMinCountryProbability()));

            // Ages: Ensure strict bounds
            if (filter.getMinAge() != null) pd.add(cb.greaterThanOrEqualTo(root.get("age"), filter.getMinAge()));
            if (filter.getMaxAge() != null) pd.add(cb.lessThanOrEqualTo(root.get("age"), filter.getMaxAge()));

            return pd.isEmpty() ? cb.conjunction() : cb.and(pd.toArray(new Predicate[0]));
        };
    }

}
