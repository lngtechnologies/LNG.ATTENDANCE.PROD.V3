package com.lng.attendancetabservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


/*@SpringBootApplication
public class AttendanceTabServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttendanceTabServiceApplication.class, args);
	}

}*/

@SpringBootApplication
@EnableEurekaClient
public class AttendanceTabServiceApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AttendanceTabServiceApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AttendanceTabServiceApplication.class);
	}

}