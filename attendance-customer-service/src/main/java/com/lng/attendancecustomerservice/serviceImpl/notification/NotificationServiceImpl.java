package com.lng.attendancecustomerservice.serviceImpl.notification;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Branch;
import com.lng.attendancecustomerservice.entity.masters.Department;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.notification.Notification;
import com.lng.attendancecustomerservice.repositories.masters.BranchRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustEmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.DepartmentRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeDepartmentRepository;
import com.lng.attendancecustomerservice.repositories.notification.NotificationRepository;
import com.lng.attendancecustomerservice.service.notification.NotificationService;
import com.lng.attendancecustomerservice.utils.MessageUtil;
import com.lng.dto.notification.BranchDto;
import com.lng.dto.notification.BranchSmsResponseDto;
import com.lng.dto.notification.DepartmentDto;
import com.lng.dto.notification.DeptNotificationDto;
import com.lng.dto.notification.DeptSmsResponseDto;
import com.lng.dto.notification.NotificationDto;

import status.Status;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	BranchRepository branchRepository;

	@Autowired
	CustEmployeeRepository custEmployeeRepository;

	@Autowired
	DepartmentRepository departmentRepository;

	@Autowired
	EmployeeDepartmentRepository employeeDepartmentRepository;

	ModelMapper modelMapper = new ModelMapper();

	MessageUtil messageUtil = new MessageUtil();

	@Override
	public BranchSmsResponseDto getBranchListByCustId(Integer custId) {
		BranchSmsResponseDto responseDto = new BranchSmsResponseDto();
		try {
			List<Branch> branchList = branchRepository.getBranchByCustomer_custIdAndBrIsActive(custId, true);

			if(!branchList.isEmpty()) {
				responseDto.setBranchDtoList(branchList.stream().map(branch -> convertToBranchDto(branch)).collect(Collectors.toList()));
				responseDto.status = new Status(false, 200, "Success");
			} else {
				responseDto.status = new Status(false, 400, "Not found");
			}

		} catch (Exception e) {
			responseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return responseDto;
	}

	@Override
	public Status sendNotificationToBranchBySms(NotificationDto notificationDto) {
		Status status = null;
		Notification notification = new Notification();
		try {

			for(BranchDto branchDto: notificationDto.getBranchDtoList()) {
				Branch branch = branchRepository.findBranchByBrId(branchDto.getBrId());
				if(branch != null) {

					List<Employee> employeeList =  custEmployeeRepository.findByBranch_BrId(branch.getBrId());
					if(!employeeList.isEmpty()) {
						for(Employee employee: employeeList) {
							String mobileNo = employee.getEmpMobile();
							String mobileSmS = notificationDto.getNotificationMessage();	
							String s = messageUtil.sms(mobileNo, mobileSmS);
						}
					} else {
						status = new Status(false, 400, "Employee not found");
					}

				} else {
					status = new Status(false, 400, "Branch not found");
				}
			}

			notification.setNotificationSentBy(notificationDto.getNotificationSentBy());
			notification.setNotificationSentOn(new Date());
			notification.setNotificationType(notificationDto.getNotificationType());
			notification.setNotificationHeader(notificationDto.getNotificationHeader());
			notification.setNotificationMessage(notificationDto.getNotificationMessage());

			notificationRepository.save(notification);
			status = new Status(false, 200, "Success");

		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}

		return status;
	}	

	@Override
	public DeptSmsResponseDto getDepartmentListByCustId(Integer custId) {
		DeptSmsResponseDto deptSmsResponseDto = new DeptSmsResponseDto();

		try {
			List<Department> departmentList = departmentRepository.findAllByCustomer_CustIdAndDeptIsActive(custId, true);

			if(!departmentList.isEmpty()) {
				deptSmsResponseDto.setDepartmentDtoList(departmentList.stream().map(department -> convertToDepartmentDto(department)).collect(Collectors.toList()));
				deptSmsResponseDto.status = new Status(false, 200, "Success");
			} else {
				deptSmsResponseDto.status = new Status(false, 400, "Not found");
			}

		} catch (Exception e) {
			deptSmsResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return deptSmsResponseDto;
	}


	@Override
	public Status sendNotificationToDeptBySms(DeptNotificationDto deptNotificationDto) {
		Status status = null;
		Notification notification = new Notification();
		
		try {
			for(DepartmentDto departmentDto: deptNotificationDto.getDepartmentDtoList()) {
				Department department = departmentRepository.findDepartmentByDeptIdAndDeptIsActive(departmentDto.getDeptId(), true);
				if(department != null) {

					List<Employee> employeeList = custEmployeeRepository.findByDeptId(department.getDeptId());
					if(!employeeList.isEmpty()) {
						for(Employee employee: employeeList) {

							String mobileNo = employee.getEmpMobile();
							String mobileSmS = deptNotificationDto.getNotificationMessage();	
							String s = messageUtil.sms(mobileNo, mobileSmS);
						}
					} else {
						status = new Status(false, 400, "Employee not found");
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


	public BranchDto convertToBranchDto(Branch branch) {
		BranchDto branchDto = modelMapper.map(branch,BranchDto.class);
		branchDto.setCustId(branch.getCustomer().getCustId());
		return branchDto;
	}

	public DepartmentDto convertToDepartmentDto(Department department) {
		DepartmentDto departmentDto = modelMapper.map(department,DepartmentDto.class);
		departmentDto.setCustId(department.getCustomer().getCustId());
		return departmentDto;
	}

}
