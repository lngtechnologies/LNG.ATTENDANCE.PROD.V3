package com.lng.attendancetabservice.serviceImpl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancetabservice.entity.Branch;
import com.lng.attendancetabservice.entity.Customer;
import com.lng.attendancetabservice.repositories.BranchRepository;
import com.lng.attendancetabservice.repositories.CustomerRepository;
import com.lng.attendancetabservice.repositories.EmployeeRepository;
import com.lng.attendancetabservice.service.CustomerService;
import com.lng.attendancetabservice.utils.MessageUtil;
import com.lng.dto.employeeAppSetup.OtpDto;
import com.lng.dto.employeeAppSetup.OtpResponseDto;
import com.lng.dto.tabService.CustomerDto1;
import com.lng.dto.tabService.CustomerResponse1;

import status.Status;
@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	BranchRepository branchRepository;

	MessageUtil messageUtil = new MessageUtil();

	@Override
	public OtpResponseDto generateOtp(String custCode, String brCode) {

		OtpResponseDto otpResponseDto = new OtpResponseDto();
		try {
			// Generate random otp
			int otp = employeeRepository.generateOtp();
			Customer customer =	customerRepository.findByCustomer_CustCode(custCode);
			if(customer != null) {
				if(!customer.getCustIsActive()) { 
					otpResponseDto.status = new Status(true, 400, "Customer subscription expired, please contact admin");
					return otpResponseDto;
				}
				int custValidity = customerRepository.checkCustValidationByCustId(customer.getCustId());
				if(custValidity == 0) {
					otpResponseDto.status = new Status(true, 400, "Customer subscription expired, please contact admin");
					return otpResponseDto;
				}
				Branch branch =branchRepository.findByBranch_BrCodeAndCustomer_CustId(brCode,customer.getCustId());
				if(branch != null) {
					if(!branch.getBrIsActive()) { 
						otpResponseDto.status = new Status(true, 400, "Branch is not active");
						return otpResponseDto;
					}
					int branchValidity = branchRepository.checkBranchValidity(branch.getBrId());
					if(branchValidity == 0) {
						otpResponseDto.status = new Status(true, 400, "Branch subscription expired, please contact admin");
						return otpResponseDto;
					}
					String mobileNo = customer.getCustMobile();
					String mobileSmS = otp +" is OTP to verify your details";
					String s = messageUtil.sms(mobileNo, mobileSmS);
					if(s != null) {
						otpResponseDto.status = new Status(false,200,"Successfully sent OTP");
						otpResponseDto.otpDto = new OtpDto(otp);				
					}else {
						otpResponseDto.status = new Status(true,400,"There is some problem with the message utility");
					}
				}else {
					otpResponseDto.status = new Status(true,400,"Branch does not exist");
				}
			}else {
				otpResponseDto.status = new Status(true,400,"Customer does not exist");
			}
		} catch(Exception ex) {
			otpResponseDto.status = new Status(true,500,"Oops..! Something went wrong..");
		}
		return otpResponseDto;
	}

	@Override
	public CustomerResponse1 getCustBranchDetails(String custCode, String brCode) {
		CustomerResponse1  customerResponse1  =  new  CustomerResponse1();
		List<CustomerDto1> CustomerDtoList = new ArrayList<>();
		try {
			List<Object[]> customerList =  customerRepository.findCustomerByCustomer_CustCodeAndBranch_BrCode(custCode, brCode);

			if(customerList.isEmpty()) {
				customerResponse1.status = new Status(false,400, " Not found");
			}else {
				for (Object[] p : customerList) {	

					CustomerDto1 customerDto1 = new CustomerDto1();
					customerDto1.setCustId(Integer.valueOf(p[0].toString()));
					customerDto1.setBrId(Integer.valueOf(p[1].toString()));
					customerDto1.setCustLogoFile(byteTobase64((byte[])p[2]));
					customerDto1.setCustName((p[3].toString()));
					customerDto1.setBrName((p[4].toString()));
					customerDto1.setBrCode((p[5].toString()));
					CustomerDtoList.add(customerDto1);
					customerResponse1.status = new Status(false,200, "success");
				}
			}

		}catch (Exception e){
			customerResponse1.status = new Status(true, 500, "Oops..! Something went wrong..");


		}
		customerResponse1.setData1(CustomerDtoList);
		return customerResponse1;
	}

	// convert byte to base64
	public  String byteTobase64(byte[] custLogoFile) {
		String base64 = Base64.getEncoder().encodeToString(custLogoFile);
		return base64;
	}

}
