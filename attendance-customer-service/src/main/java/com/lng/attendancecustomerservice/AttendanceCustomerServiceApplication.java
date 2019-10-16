package com.lng.attendancecustomerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/*@SpringBootApplication
public class AttendanceCustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttendanceCustomerServiceApplication.class, args);
	}

}*/

@SpringBootApplication
@EnableEurekaClient
public class AttendanceCustomerServiceApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AttendanceCustomerServiceApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AttendanceCustomerServiceApplication.class);
	}

}