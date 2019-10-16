package com.lng.attendancecompanyservice.controllers.authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Test {
	
	//@Autowired
	//private RestTemplate restTemplate;
	
	@GetMapping(value="/msg")
	public String GetMsg() {
		return "Test is working";
	}
	
	/*
	 * @GetMapping(value="/msg2") public String GetMsgTwo() { return
	 * restTemplate.getForObject(
	 * "http://40.112.180.100:8080/api/customer/test2/msg2", String.class); }
	 */

}
