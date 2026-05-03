//package com.naz.profiler.seed;
//
//import com.naz.profiler.dto.SeedProfile;
//import com.naz.profiler.dto.SeedWrapper;
//import com.naz.profiler.entity.Profile;
//import com.naz.profiler.repository.ProfileRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Component;
//import tools.jackson.core.type.TypeReference;
//import tools.jackson.databind.ObjectMapper;
//
//import java.io.InputStream;
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class DataSeeder implements CommandLineRunner {
//    private final ProfileRepository repository;
//    private final ObjectMapper mapper;
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        InputStream input = new ClassPathResource("seed_profiles.json").getInputStream();
//1
//        SeedWrapper wrapper = mapper.readValue(input, SeedWrapper.class);
//
//        List<SeedProfile> profiles = wrapper.getProfiles();
//
//        int inserted = 0;
//
//        for(SeedProfile row : profiles){
//            if(repository.findByNameIgnoreCase(row.getName()).isPresent()){
//                continue;
//            }
//
//            Profile p = new Profile();
//
//            p.setName(row.getName());
//            p.setGender(row.getGender());
//            p.setGenderProbability(row.getGenderProbability());
//            p.setAge(row.getAge());
//            p.setAgeGroup(row.getAgeGroup());
//            p.setCountryId(row.getCountryId());
//            p.setCountryName(row.getCountryName());
//            p.setCountryProbability(row.getCountryProbability());
//
//            repository.save(p);
//            inserted++;
//        }
//
//        log.info("Seed completed. Inserted {}", inserted);
//    }
//}
