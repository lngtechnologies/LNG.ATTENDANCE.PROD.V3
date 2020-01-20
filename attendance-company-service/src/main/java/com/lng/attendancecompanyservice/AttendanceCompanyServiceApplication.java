package com.lng.attendancecompanyservice;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/*@SpringBootApplication
public class AttendanceCompanyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttendanceCompanyServiceApplication.class, args);
	}

}*/

@SpringBootApplication
@EnableEurekaClient
@EnableAsync
public class AttendanceCompanyServiceApplication extends SpringBootServletInitializer {

	
	public static void main(String[] args) {
		SpringApplication.run(AttendanceCompanyServiceApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AttendanceCompanyServiceApplication.class);
	}
	
	/**
	 * THIS FOR ASYNCRONOUS PROCESS/METHOD
	 * @return
	 */
	/*@Bean(name = "asyncExecutor")
	public Executor asyncExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(5);
	    executor.setMaxPoolSize(5);
	    executor.setQueueCapacity(500);
	    executor.setThreadNamePrefix("Asynchronous Process-");
	    executor.initialize();
	    return executor;
	}*/

}
