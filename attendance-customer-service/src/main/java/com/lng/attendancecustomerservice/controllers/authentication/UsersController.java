package com.lng.attendancecustomerservice.controllers.authentication;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.authentication.ILogin;
import com.lng.dto.authenticate.ChangePasswordDto;
import com.lng.dto.authenticate.ForgotPasswordParamDto;
import com.lng.dto.authenticate.LoginParamDto;
import com.lng.dto.authenticate.LoginResponse;

import status.Status;


/**
 * @author Admin
 *
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value="/users")
public class UsersController {

	@Autowired
	ILogin itILogin;

	@PostMapping(value="/login")
	public ResponseEntity<LoginResponse> UserAuthenticate(@RequestBody LoginParamDto loginDto) {
		return new ResponseEntity<>(itILogin.AuthenticateUser(loginDto), HttpStatus.OK);
	}

	@PostMapping(value="/forgot/password")
	public ResponseEntity<Status> ForgotPassword(@RequestBody ForgotPasswordParamDto loginDto) {
		return new ResponseEntity<>(itILogin.UserForgotPassword(loginDto), HttpStatus.OK);
	}

	@PostMapping(value="/change/password")
	public ResponseEntity<Status> ChangePassword(@RequestBody ChangePasswordDto changePasswordDto) {
		return new ResponseEntity<>(itILogin.UserChangePassword(changePasswordDto), HttpStatus.OK);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity handleEntityNotFoundException(EntityNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@GetMapping(value="/test")
	public String GetMsg() {
		return "Working";
	}

	@GetMapping(value="/hello")
	public String Hello() {
		return "Hello";
	}
}
