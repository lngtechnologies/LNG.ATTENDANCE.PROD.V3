package com.lng.attendancecompanyservice.controllers.custOnboarding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.custOnboarding.MailBounceService;
import com.lng.dto.customer.MailBounceDto;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/customer/checkBounce")
public class MailBounceController {

	@Autowired
	MailBounceService mailBounceService;
	
	@PostMapping(value = "/mail")
    public ResponseEntity<MailBounceDto> save(@RequestBody MailBounceDto mailBounceDto) {
		MailBounceDto mailBounceDto1 = mailBounceService.save(mailBounceDto);
        if (mailBounceDto1 !=null){
            return new ResponseEntity<MailBounceDto>(mailBounceDto, HttpStatus.CREATED);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
