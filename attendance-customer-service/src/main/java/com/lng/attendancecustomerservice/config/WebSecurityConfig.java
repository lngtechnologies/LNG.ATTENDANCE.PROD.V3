package com.lng.attendancecustomerservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.lng.attendancecustomerservice.security.JwtAuthenticationEntryPoint;
import com.lng.attendancecustomerservice.security.JwtAuthenticationProvider;
import com.lng.attendancecustomerservice.security.JwtAuthenticationTokenFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Autowired
	private JwtAuthenticationProvider jwtAuthenticationProvider;

	@Autowired
	public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) {
		authenticationManagerBuilder.authenticationProvider(jwtAuthenticationProvider);
	}

	@Bean
	public JwtAuthenticationTokenFilter authenticationTokenFilterBean() {
		return new JwtAuthenticationTokenFilter();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
		.csrf().disable()
        .httpBasic().disable()
		.authorizeRequests().antMatchers("/users/**", "/employee/setup/**", "/employee/welcome/screen/**", "/employee/mark/attendance/**",
										"/mobile/app/employee/leave/**", "/employee/movement/**", "/mobile/app/policyandfaq/**",
										"/push/notification/token/save", "/mobile/app/dashboard/**","/mobile/holidayClaendar/**","/mobile/pushNotification/**").permitAll()

		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

		.anyRequest().authenticated().and()
		.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		/*
		 * .and() .authorizeRequests() .anyRequest().authenticated()
		 */;

		httpSecurity.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
		//httpSecurity.headers().cacheControl();
	}
}
