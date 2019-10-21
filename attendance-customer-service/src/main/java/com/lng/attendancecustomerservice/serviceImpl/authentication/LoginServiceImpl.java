package com.lng.attendancecustomerservice.serviceImpl.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.authentication.Login;
import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.repositories.authentication.ICustomerRepository;
import com.lng.attendancecustomerservice.repositories.authentication.ILoginRepository;
import com.lng.attendancecustomerservice.security.JwtTokenService;
import com.lng.attendancecustomerservice.service.authentication.ILogin;
import com.lng.attendancecustomerservice.utils.MessageUtil;
import com.lng.dto.authenticate.ChangePasswordDto;
import com.lng.dto.authenticate.ForgotPasswordParamDto;
import com.lng.dto.authenticate.LoginDto;
import com.lng.dto.authenticate.LoginParamDto;
import com.lng.dto.authenticate.LoginResponse;

import status.Status;


/**
 * @author Sachin Kulkarni
 * @Description Login implementation for user 
 * Once authenticated JWT token is generated and sent to client
 * Forget password: password is sent to user mobile no
 * Change password: -do-
 * @created 28-sept-2019
 * 
 */


@Service
public class LoginServiceImpl implements ILogin {
	private ILoginRepository accountRepository;
	private ICustomerRepository custRepository;
	private JwtTokenService jwtTokenService;
	
	MessageUtil messageUtil = new MessageUtil();
	
	@Bean
	public BCryptPasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}

	public LoginServiceImpl(ILoginRepository accountRepository, JwtTokenService jwtTokenService, ICustomerRepository custRepository) {
		this.accountRepository = accountRepository;
		this.jwtTokenService = jwtTokenService;
		this.custRepository = custRepository;
	}

	// Authenticated user

	@Override
	public LoginResponse AuthenticateUser(LoginParamDto loginDto) {
		// Object to hold response
		LoginResponse response = new LoginResponse();

		try {
			//String hashedPassword = BCrypt.hashpw(loginDto.getLoginPassword(), BCrypt.gensalt(4));

			// Check login name not null
			if(isNullOrEmpty(loginDto.getLoginName())) throw new Exception("Please enter user name");

			// Check password not null
			if(isNullOrEmpty(loginDto.getLoginPassword())) throw new Exception("Please enter password");

			// Get user by login name
			Login user = accountRepository.findByLoginName(loginDto.getLoginName());
			
			// Check user exist else throw exception
			if(user == null) throw new Exception(loginDto.getLoginName() + " not found");

			// Check customer validity
			if(user != null && user.getRefCustId() != 0) {

				// Get customer details by customer id
				Customer cust = custRepository.findByCustId(user.getRefCustId());

				// Customer not exist
				if(cust == null) throw new Exception("Customer doesn't exist");

				//
				if(!cust.getCustIsActive()) throw new Exception("Subscription expired, please contact admin");
			}

			// Check user is active
			if(user.getIsActive() != 1) throw new Exception("Please contact admin "+loginDto.getLoginName() + "is not active");

			// Validate password else throw invalid details
			//if(matches(loginDto.getLoginPassword(), user.loginPassword)) {
			if(getEncoder().matches(loginDto.getLoginPassword(), user.loginPassword)) {

				// Generate token and send user details to client 
				response.data = new LoginDto(user.getLoginId(),user.getRefCustId(), user.getLoginName(), jwtTokenService.generateToken(user.getLoginName()).toString());
				response.status = new Status(false,200,"success");

			} else {

				// Throw invalid details
				throw new Exception("Invalid credentials");

			}

		} catch(Exception ex) {

			response.status = new Status(true, 3000, ex.getMessage());

		}

		return response;
	}

	// Forgot password
	@Override
	public Status UserForgotPassword(ForgotPasswordParamDto loginDto) {
		Status response = null;

		try {
			// Check login name not null
			if(isNullOrEmpty(loginDto.getUserName())) throw new Exception("Please enter user name");

			// Get user by login name
			Login user = accountRepository.findByLoginName(loginDto.getUserName());

			// Check user exist else throw exception
			if(user == null) throw new Exception(loginDto.getUserName() + " not found");
			
			// Check user is active
			if(user.getIsActive() != 1) throw new Exception("Please contact admin "+loginDto.getUserName() + "is not active");
			
			// Check mobile exist
			if(user.getLoginMobile() == null) throw new Exception("Mobile no doesn't exists. Unable to reset password.");
			
			// Check customer validity
			if(user != null && user.getRefCustId() != 0) {

				// Get customer details by customer id
				Customer cust = custRepository.findByCustId(user.getRefCustId());

				// Customer not exist
				if(cust == null) throw new Exception("Customer doesn't exist");

				//
				if(!cust.getCustIsActive()) throw new Exception("Subscription expired, please contact admin");
			}

			// Generate new password
			String nPassword = accountRepository.generatePassword();
			
			// set new password
			String hashedPassword = getEncoder().encode(nPassword);
			user.setLoginPassword(hashedPassword);
			accountRepository.save(user);
			
			// Send new password to registered mobile
			String mobileNo = user.getLoginMobile();
			String mobileSmS = "Dear " + loginDto.getUserName() +" \n, Your new login password is " + nPassword;	
			String s = messageUtil.sms(mobileNo, mobileSmS);
			
			//Set msg
			response = new Status(false, 200, "Your new password has been sent to your registered Mobile no.");

		} catch(Exception ex) {
			response = new Status(true,5000,ex.getMessage());
		}

		return response;
	}

	// Change password
	public Status UserChangePassword(ChangePasswordDto changePasswordDto) {
		Status response = null;

		try {
			// Check login name not null
			if(isNullOrEmpty(changePasswordDto.getUserName())) throw new Exception("Please enter user name");

			// Check password not null
			if(isNullOrEmpty(changePasswordDto.getOldPassword())) throw new Exception("Please enter password");

			// Get user by login name
			Login user = accountRepository.findByLoginName(changePasswordDto.getUserName());
			
			// Check user exist else throw exception
			if(user == null) throw new Exception(changePasswordDto.getUserName() + " not found");
			
			// Check user is active
			if(user.getIsActive() != 1) throw new Exception("Please contact admin "+changePasswordDto.getUserName() + "is not active");

			// Check customer validity
			if(user != null && user.getRefCustId() != 0) {

				// Get customer details by customer id
				Customer cust = custRepository.findByCustId(user.getRefCustId());

				// Customer not exist
				if(cust == null) throw new Exception("Customer doesn't exist");

				//
				if(!cust.getCustIsActive()) throw new Exception("Subscription expired, please contact admin");
			}

			// Validate password else throw invalid details
			//if(matches(changePasswordDto.getOldPassword(), user.loginPassword)) {
			if(getEncoder().matches(changePasswordDto.getOldPassword(), user.loginPassword)) {

				// set new password
				String hashedPassword = getEncoder().encode(changePasswordDto.getNewPassword());
				//String hashedPassword = BCrypt.hashpw(changePasswordDto.getNewPassword(), BCrypt.gensalt(4));
				user.setLoginPassword(hashedPassword);
				accountRepository.save(user);
				response = new Status(false,200,"Password Changed Successfully! Re-login.");

			} else {

				// Throw invalid details
				throw new Exception("Invalid credentials");

			}


		} catch(Exception ex) {
			// ex.getMessage() log in log file and show user readable msg
			//response = new Status(true,5000,"Something went wrong, please try again");
			response = new Status(true,5000,ex.getMessage());
		}

		return response;
	}

	// Function to validate or match raw passowrd with encoded password
	/*
	 * public boolean matches(CharSequence rawPassword, String encodedPassword) {
	 * 
	 * return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
	 * 
	 * }
	 */

	// Function to check string is null or empty
	public boolean isNullOrEmpty(String str) {

		if(str != null && !str.isEmpty())
			return false;
		return true;

	}

}
