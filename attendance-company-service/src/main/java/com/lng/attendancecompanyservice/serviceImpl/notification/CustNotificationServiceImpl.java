package com.lng.attendancecompanyservice.serviceImpl.notification;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;
import com.lng.attendancecompanyservice.entity.notification.Notification;
import com.lng.attendancecompanyservice.repositories.custOnboarding.CustomerRepository;
import com.lng.attendancecompanyservice.repositories.notification.NotificationRepository;
import com.lng.attendancecompanyservice.service.notification.CustNotificationService;
import com.lng.attendancecompanyservice.utils.MessageUtil;
import com.lng.dto.notification.company.CustNotificationDto;
import com.lng.dto.notification.company.CustSMSResponseDto;
import com.lng.dto.notification.company.SMSNotificationDto;

import status.Status;

@Service
public class CustNotificationServiceImpl implements CustNotificationService {

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	NotificationRepository notificationRepository;
 	
	ModelMapper modelMapper = new ModelMapper();
	
	MessageUtil messageUtil = new MessageUtil();
	
	@Override
	public CustSMSResponseDto getAllActiveCustomers() {
		CustSMSResponseDto custSMSResponseDto = new CustSMSResponseDto();
		try {
			
			List<Customer> custList = customerRepository.findAllCustomerByCustIsActive(true);
			if(!custList.isEmpty()) {
				custSMSResponseDto.setCustDto(custList.stream().map(customer -> convertToCustNotificationDto(customer)).collect(Collectors.toList()));
				custSMSResponseDto.status = new Status(false, 200, "Success");
			} else {
				custSMSResponseDto.status = new Status(false, 400, "Not found");
			}			
		} catch (Exception e) {
			custSMSResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return custSMSResponseDto;
	}
	
	
	@Override
	public CustSMSResponseDto getAllInActiveCustomers() {
		CustSMSResponseDto custSMSResponseDto = new CustSMSResponseDto();
		try {
			
			List<Customer> custList = customerRepository.findAllCustomerByCustIsActive(false);
			if(!custList.isEmpty()) {
				custSMSResponseDto.setCustDto(custList.stream().map(customer -> convertToCustNotificationDto(customer)).collect(Collectors.toList()));
				custSMSResponseDto.status = new Status(false, 200, "Success");
			} else {
				custSMSResponseDto.status = new Status(false, 400, "Not found");
			}			
		} catch (Exception e) {
			custSMSResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return custSMSResponseDto;
	}

	@Override
	public Status sendSMSNotificationToAllCustomers(SMSNotificationDto smsNotificationDto) {
		Status status = null;
		Notification notification = new Notification();
		try {
			for(CustNotificationDto custDto: smsNotificationDto.getCustDto()) {
				Customer customer = customerRepository.findCustomerByCustId(custDto.getCustId());
				if(customer != null) {
					String mobileNo = customer.getCustMobile();
					String mobileSmS = smsNotificationDto.getNotificationMessage();	
					String s = messageUtil.sms(mobileNo, mobileSmS);
				} else {
					status = new Status(false, 400, "Customer not found");
				}
				
				notification.setNotificationSentBy(smsNotificationDto.getNotificationSentBy());
				notification.setNotificationSentOn(new Date());
				notification.setNotificationType(smsNotificationDto.getNotificationType());
				notification.setNotificationHeader(smsNotificationDto.getNotificationHeader());
				notification.setNotificationMessage(smsNotificationDto.getNotificationMessage());

				notificationRepository.save(notification);
				status = new Status(false, 200, "Success");
			}
		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}
	
	public CustNotificationDto convertToCustNotificationDto(Customer customer) {
		CustNotificationDto custNotificationDto = modelMapper.map(customer,CustNotificationDto.class);
		custNotificationDto.setCustId(customer.getCustId());
		custNotificationDto.setCustCode(customer.getCustCode());
		custNotificationDto.setCustMobile(customer.getCustMobile());
		custNotificationDto.setCustEmail(customer.getCustEmail());
		custNotificationDto.setCustName(customer.getCustName());
		return custNotificationDto;
	}

	

}
