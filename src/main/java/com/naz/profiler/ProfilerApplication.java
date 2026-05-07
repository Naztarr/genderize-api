package com.naz.profiler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableCaching
@EnableAsync
@SpringBootApplication
public class ProfilerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfilerApplication.class, args);
	}

}
