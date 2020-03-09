package com.lng.attendancecustomerservice.serviceImpl.notification;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Branch;
import com.lng.attendancecustomerservice.entity.masters.Department;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.notification.BranchNotification;
import com.lng.attendancecustomerservice.entity.notification.EmpToken;
import com.lng.attendancecustomerservice.entity.notification.Notification;
import com.lng.attendancecustomerservice.repositories.masters.BranchRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustEmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.DepartmentRepository;
import com.lng.attendancecustomerservice.repositories.notification.BranchNotificationRepository;
import com.lng.attendancecustomerservice.repositories.notification.EmpTokenRepository;
import com.lng.attendancecustomerservice.repositories.notification.NotificationRepository;
import com.lng.attendancecustomerservice.service.notification.PushNotificationService;
import com.lng.attendancecustomerservice.utils.PushNotificationUtil;
import com.lng.dto.notification.BranchDto;
import com.lng.dto.notification.DepartmentDto;
import com.lng.dto.notification.DeptNotificationDto;
import com.lng.dto.notification.NotificationDto;
import com.lng.dto.notification.PushNotificationDto;

import status.Status;

@Service
public class PushNotificationServiceImpl implements PushNotificationService {

	@Autowired
	CustEmployeeRepository custEmployeeRepository;

	@Autowired
	EmpTokenRepository empTokensRepository;

	@Autowired
	BranchRepository branchRepository;

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	DepartmentRepository departmentRepository;

	@Autowired
	BranchNotificationRepository branchNotificationRepository;

	PushNotificationUtil pushNotificationUtil = new PushNotificationUtil();

	@Override
	public Status saveEmpToken(PushNotificationDto pustNotificationDto) {
		Employee employee = custEmployeeRepository.findEmployeeByEmpIdAndEmpInService(pustNotificationDto.getEmpId(), true);
		EmpToken empToken = empTokensRepository.findByEmployee_EmpId(pustNotificationDto.getEmpId());
		Status status = null;
		try {
			if(employee != null) {
				if(empToken == null) {
					EmpToken empToken1 = new EmpToken();
					empToken1.setEmployee(employee);
					empToken1.setToken(pustNotificationDto.getToken());
					empToken1.setCreatedDate(new Date());
					empToken1.setIsActive(true);
					empTokensRepository.save(empToken1);
					status = new Status(false, 200, "Success");
				} else {
					empToken.setToken(pustNotificationDto.getToken());
					empTokensRepository.save(empToken);
					status = new Status(false, 200, "Success");
				}
			} else {
				status = new Status(true, 400, "Employee not found or Employee not in service");
			}
		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}

		return status;
	}

	@Override
	public Status sendPustNotificationToBranch(NotificationDto notificationDto) {

		Status status = null;
		Notification notification = new Notification();
		try {
			for(BranchDto branchDto: notificationDto.getBranchDtoList()) {
				Branch branch = branchRepository.findBranchByBrId(branchDto.getBrId());

				if(branch != null) {
					List<EmpToken> tokenList = empTokensRepository.findByBranchId(branch.getBrId());
					if(!tokenList.isEmpty()) {
						for(EmpToken empToken : tokenList ) {
							pushNotificationUtil.SendPushNotification(empToken.getToken(), notificationDto.getNotificationMessage(), notificationDto.getNotificationHeader());
						}
					} else {
						status = new Status(true, 400, "Employee not found or Employee not in service");
					}
				} else {
					status = new Status(false, 400, "Branch not found");
				}
				notification.setNotificationSentBy(notificationDto.getNotificationSentBy());
				notification.setNotificationSentOn(new Date());
				notification.setNotificationType(notificationDto.getNotificationType());
				notification.setNotificationHeader(notificationDto.getNotificationHeader());
				notification.setNotificationMessage(notificationDto.getNotificationMessage());
				notificationRepository.save(notification);

				BranchNotification brNotification = new BranchNotification();
				brNotification.setRefBrId(branch.getBrId());
				brNotification.setRefNotificationId(notification.getNotificationId());
				branchNotificationRepository.save(brNotification);

				status = new Status(false, 200, "Success");

			}
		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}

		return status;
	}

	@Override
	public Status sendPustNotificationToDepartment(DeptNotificationDto deptNotificationDto) {
		Status status = null;
		Notification notification = new Notification();
		try {
			for(DepartmentDto departmentDto: deptNotificationDto.getDepartmentDtoList()) {
				Department department = departmentRepository.findDepartmentByDeptIdAndDeptIsActive(departmentDto.getDeptId(), true);
				if(department != null) {
					List<EmpToken> tokenList = empTokensRepository.findByDeptId(department.getDeptId());
					if(!tokenList.isEmpty()) {
						for(EmpToken empToken : tokenList ) {
							pushNotificationUtil.SendPushNotification(empToken.getToken(), deptNotificationDto.getNotificationMessage(), deptNotificationDto.getNotificationHeader());
						}
					} else {
						status = new Status(true, 400, "Employee not found or Employee not in service");
					}
				} else {
					status = new Status(false, 400, "Department not found");
				}
			}

			notification.setNotificationSentBy(deptNotificationDto.getNotificationSentBy());
			notification.setNotificationSentOn(new Date());
			notification.setNotificationType(deptNotificationDto.getNotificationType());
			notification.setNotificationHeader(deptNotificationDto.getNotificationHeader());
			notification.setNotificationMessage(deptNotificationDto.getNotificationMessage());

			notificationRepository.save(notification);
			status = new Status(false, 200, "Success");
		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}

}

