package com.lng.attendancecustomerservice.service.authentication;

import com.lng.dto.authenticate.ChangePasswordDto;
import com.lng.dto.authenticate.ForgotPasswordParamDto;
import com.lng.dto.authenticate.LoginParamDto;
import com.lng.dto.authenticate.LoginResponse;

import status.Status;


public interface ILogin {
	LoginResponse AuthenticateUser(LoginParamDto loginDto);
	Status UserForgotPassword(ForgotPasswordParamDto loginDto);
	Status UserChangePassword(ChangePasswordDto changePasswordDto);
}
