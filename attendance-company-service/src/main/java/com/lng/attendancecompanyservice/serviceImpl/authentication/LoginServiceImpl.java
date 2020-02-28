package com.lng.attendancecompanyservice.serviceImpl.authentication;

import java.util.Base64;
import java.util.List;

import javax.swing.JTextPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;
import com.lng.attendancecompanyservice.entity.masters.Branch;
import com.lng.attendancecompanyservice.entity.masters.Employee;
import com.lng.attendancecompanyservice.entity.masters.Login;
import com.lng.attendancecompanyservice.entity.masters.LoginDataRight;
import com.lng.attendancecompanyservice.repositories.authentication.ICustomerRepository;
import com.lng.attendancecompanyservice.repositories.authentication.ILoginRepository;
import com.lng.attendancecompanyservice.repositories.masters.BranchRepository;
import com.lng.attendancecompanyservice.repositories.masters.CustEmployeeRepository;
import com.lng.attendancecompanyservice.repositories.masters.LoginDataRightRepository;
import com.lng.attendancecompanyservice.security.JwtTokenService;
import com.lng.attendancecompanyservice.service.authentication.ILogin;
import com.lng.attendancecompanyservice.utils.Encoder;
import com.lng.attendancecompanyservice.utils.MessageUtil;
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

	Encoder encoder = new Encoder();

	/*
	 * @Bean public BCryptPasswordEncoder getEncoder() { return new
	 * BCryptPasswordEncoder(); }
	 */


	public LoginServiceImpl(ILoginRepository accountRepository, JwtTokenService jwtTokenService, ICustomerRepository custRepository) {
		this.accountRepository = accountRepository;
		this.jwtTokenService = jwtTokenService;
		this.custRepository = custRepository;
	}

	@Autowired
	CustEmployeeRepository custEmployeeRepository;

	@Autowired
	BranchRepository branchRepository;

	@Autowired
	LoginDataRightRepository loginDataRightRepository;

	// Authenticated user

	@Override
	public LoginResponse AuthenticateUser(LoginParamDto loginDto) {
		// Object to hold response
		LoginResponse response = new LoginResponse();

		String logo = null;

		try {
			//String hashedPassword = BCrypt.hashpw(loginDto.getLoginPassword(), BCrypt.gensalt(4));

			// Check login name not null
			if(isNullOrEmpty(loginDto.getLoginName())) throw new Exception("Please enter user name");

			// Check password not null
			if(isNullOrEmpty(loginDto.getLoginPassword())) throw new Exception("Please enter password");

			// Get user by login name
			Login user = accountRepository.findByLoginNameAndLoginIsActive(loginDto.getLoginName(),true);

			// Check user exist else throw exception
			if(user == null) throw new Exception(loginDto.getLoginName() + " not found");

			// Check customer validity
			if(user != null && user.getRefCustId() != 0) {

				// Get customer details by customer id
				Customer cust = custRepository.findByCustId(user.getRefCustId());

				// Customer not exist
				if(cust == null) throw new Exception("Customer doesn't exist");

				//
				if(!cust.getCustIsActive()) throw new Exception("Customer is not active, contact admin");

				// Check customer validity
				int custValidity = custRepository.checkCustValidationByCustId(cust.getCustId());
				if(custValidity == 0) throw new Exception("Subscription expired, contact admin");

				// Check Branch validity
				int branchValidity = branchRepository.checkBranchValidity(user.getLoginId());
				if(branchValidity == 0) throw new Exception("Subscription expired, contact admin");


				// convert byte to base64
				if(cust != null)
					logo = Base64.getEncoder().encodeToString(cust.getCustLogoFile());

			}

			// Check user is active
			if(user.getLoginIsActive() == false) throw new Exception("Please contact admin "+loginDto.getLoginName() + " is not active");

			// Validate password else throw invalid details
			//if(matches(loginDto.getLoginPassword(), user.loginPassword)) {
			if(encoder.getEncoder().matches(loginDto.getLoginPassword(), user.getLoginPassword())) {


				// Generate token and send user details to client 
				response.data = new LoginDto(user.getLoginId(), user.getRefCustId(), user.getLoginName(), jwtTokenService.generateToken(user.getLoginName()).toString(), logo, user.getRefEmpId());
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
			Login user = accountRepository.findByLoginNameAndLoginIsActive(loginDto.getUserName(), true);

			// Check user exist else throw exception
			if(user == null) throw new Exception(loginDto.getUserName() + " not found");

			// Check user is active
			if(user.getLoginIsActive()== false) throw new Exception("Please contact admin "+loginDto.getUserName() + "is not active");

			// Check customer validity
			int custValidity = custRepository.checkCustValidationByCustId(user.getRefCustId());
			if(custValidity == 0) throw new Exception("Subscription expired, contact admin");

			// Check Branch validity
			int branchValidity = branchRepository.checkBranchValidity(user.getLoginId());
			if(branchValidity == 0) throw new Exception("Subscription expired, contact admin");

			// Check customer validity
			if(user != null && user.getRefCustId() != 0) {

				// Get customer details by customer id
				Customer cust = custRepository.findByCustId(user.getRefCustId());

				// Customer not exist
				if(cust == null) throw new Exception("Customer doesn't exist");

				//
				if(!cust.getCustIsActive()) throw new Exception("Customer is not active, contact admin");
			}

			// Generate new password
			String nPassword = accountRepository.generatePassword();

			// set new password
			String hashedPassword = encoder.getEncoder().encode(nPassword);
			user.setLoginPassword(hashedPassword);
			accountRepository.save(user);

			// Send new password to registered mobile
			String mobileNo = null;
			if(user.getLoginMobile() != null) {
				mobileNo = user.getLoginMobile();
			} else {
				// Get emp mobile no
				Employee employee = custEmployeeRepository.findEmployeeByEmpIdAndEmpInService(user.getRefEmpId(), true);
				mobileNo = employee.getEmpMobile();
			}
			// Check mobile exist
			if(mobileNo == null) throw new Exception("Mobile no doesn't exists. Unable to reset password.");

			String mobileSmS = "Password to access the Smart Attendance System has been reset to: " + nPassword +" for "+ user.getLoginName();	
			// String s = messageUtil.sms(mobileNo, mobileSmS);
			messageUtil.sms(mobileNo, mobileSmS);

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
			Login user = accountRepository.findByLoginNameAndLoginIsActive(changePasswordDto.getUserName(), true);

			// Check user exist else throw exception
			if(user == null) throw new Exception(changePasswordDto.getUserName() + " not found");


			// Check user is active
			if(user.getLoginIsActive() == false) throw new Exception("Please contact admin "+changePasswordDto.getUserName() + "is not active");

			// Check customer validity
			if(user != null && user.getRefCustId() != 0) {

				// Check customer validity
				int custValidity = custRepository.checkCustValidationByCustId(user.getRefCustId());
				if(custValidity == 0) throw new Exception("Subscription expired, contact admin");

				// Check Branch validity
				int branchValidity = branchRepository.checkBranchValidity(user.getLoginId());
				if(branchValidity == 0) throw new Exception("Subscription expired, contact admin");

				// Get customer details by customer id
				Customer cust = custRepository.findByCustId(user.getRefCustId());

				// Customer not exist
				if(cust == null) throw new Exception("Customer doesn't exist");

				//
				if(!cust.getCustIsActive()) throw new Exception("Customer is not active, contact admin");
			}

			// Validate password else throw invalid details
			//if(matches(changePasswordDto.getOldPassword(), user.loginPassword)) {
			if(encoder.getEncoder().matches(changePasswordDto.getOldPassword(), user.getLoginPassword())) {

				// set new password
				String hashedPassword = encoder.getEncoder().encode(changePasswordDto.getNewPassword());
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
