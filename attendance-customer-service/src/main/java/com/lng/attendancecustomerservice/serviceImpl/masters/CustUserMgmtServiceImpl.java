package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.authentication.Login;
import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.repositories.authentication.ILoginRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.service.masters.CustUserMgmtService;
import com.lng.attendancecustomerservice.utils.Encoder;
import com.lng.attendancecustomerservice.utils.MessageUtil;
import com.lng.dto.masters.custUserMgmt.CustUserMgmtDto;

import status.Status;

@Service
public class CustUserMgmtServiceImpl implements CustUserMgmtService {

	@Autowired
	ILoginRepository iLoginRepository;

	@Autowired
	CustomerRepository customerRepository;
	
	MessageUtil messageUtil = new MessageUtil();
	
	Encoder encoder = new Encoder();

	@Override
	public Status save(CustUserMgmtDto custUserMgmtDto) {
		Status status = null;
		
		Customer customer = customerRepository.findCustomerByCustId(custUserMgmtDto.getCustomerId());
		
		try {
			if(customer != null) {
			
				String userName = custUserMgmtDto.getUserName();
				String custCode = customer.getCustCode();

				String loginUserName = userName+"@"+custCode;
				
				Login login1 = iLoginRepository.findByLoginNameAndRefCustId(loginUserName, custUserMgmtDto.getCustomerId());
				Login login2 = iLoginRepository.findByLoginMobileAndRefCustId(custUserMgmtDto.getuMobileNumber(), custUserMgmtDto.getCustomerId());
				
				if(login1 == null) {
					if(login2 == null) {

						String newPassword = iLoginRepository.generatePassword();
						Login login = new Login();
						login.setLoginName(loginUserName);
						login.setLoginMobile(custUserMgmtDto.getuMobileNumber());
						login.setLoginPassword(encoder.getEncoder().encode(newPassword));
						login.setLoginCreatedDate(new Date());
						login.setLoginIsActive(true);
						login.setRefCustId(customer.getCustId());
						iLoginRepository.save(login);
						
						String mobileNo = login.getLoginMobile();
						String mobileSmS = "User Id has been successfully created to access the Attendance System Web application."
											+ "The login details are User Id: "+loginUserName+" and Password is : "+ newPassword;	
						String s = messageUtil.sms(mobileNo, mobileSmS);
						
						status = new Status(false, 200, "User Added successfully");
						
					}else {
						status = new Status(true, 400, "Mobile number already exist");
					}
				}else {
					status = new Status(true, 400, "User name already exist");
				}
			} else {
				status = new Status(true, 400, "Customer is not exist");
			}

		} catch (Exception e) {

		}
		return status;
	}

}
