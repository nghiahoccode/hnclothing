package com.hnclothing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class Hnclothing {

	public static void main(String[] args) {
		SpringApplication.run(Hnclothing.class, args);
	}

}
