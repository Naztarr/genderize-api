package com.naz.profiler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ProfilerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfilerApplication.class, args);
	}

}
