package com.lng.attendancecompanyservice.controllers.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.exception.ApplicationExceptionService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/ecxeption")
public class ApplicationExceptionController {

	@Autowired
	ApplicationExceptionService applicationExceptionService;
	
	
}
