package com.lng.attendancecompanyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/*@SpringBootApplication
public class AttendanceCompanyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttendanceCompanyServiceApplication.class, args);
	}

}*/

@SpringBootApplication
@EnableEurekaClient
public class AttendanceCompanyServiceApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AttendanceCompanyServiceApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AttendanceCompanyServiceApplication.class);
	}

}
