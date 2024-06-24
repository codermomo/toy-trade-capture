package com.github.codermomo.ViewService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "com.github.codermomo.ViewService.daos")
@EntityScan("com.github.codermomo.CommonLibrary")
public class ViewServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ViewServiceApplication.class, args);
	}

}
