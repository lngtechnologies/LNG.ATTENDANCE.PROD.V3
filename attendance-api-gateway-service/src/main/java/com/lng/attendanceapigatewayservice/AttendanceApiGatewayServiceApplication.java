package com.lng.attendanceapigatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/*
 * @SpringBootApplication
 * 
 * @EnableZuulProxy public class AttendanceApiGatewayServiceApplication {
 * 
 * public static void main(String[] args) {
 * SpringApplication.run(AttendanceApiGatewayServiceApplication.class, args); }
 * 
 * }
 */


@SpringBootApplication
@EnableZuulProxy 
public class AttendanceApiGatewayServiceApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AttendanceApiGatewayServiceApplication.class, args); 
	}

	@Override 
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) { 
		return application.sources(AttendanceApiGatewayServiceApplication.class); 
	} 
}


