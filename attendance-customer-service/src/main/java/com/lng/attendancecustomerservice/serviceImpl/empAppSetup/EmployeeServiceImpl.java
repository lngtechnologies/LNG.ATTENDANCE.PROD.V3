package com.lng.attendancecustomerservice.serviceImpl.empAppSetup;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.empAppSetup.Employee;
import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.entity.masters.EmpAppSetupStatus;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.service.empAppSetup.EmployeeService;
import com.lng.attendancecustomerservice.utils.MessageUtil;
import com.lng.dto.employee.EmpAppStatusDto;
import com.lng.dto.employee.EmpAppStatusResponseDto;
import com.lng.dto.employee.EmployeeDataDto;
import com.lng.dto.employee.EmployeeSetup2Dto;
import com.lng.dto.employee.OtpDto;
import com.lng.dto.employee.OtpResponseDto;
import com.lng.dto.employee.ResponseDto;

import status.Status;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	EmployeeRepository employeeRepository;

	MessageUtil messageUtil = new MessageUtil();

	@Override
	public ResponseDto getByCustCodeAndEmpMobile(String custCode, String empMobile) {
		ResponseDto response = new ResponseDto();
		Employee employee = new Employee();

		try {
			Customer customer = customerRepository.getByCustCode(custCode);

			// Check customer exist else throw exception	
			if(customer == null || !customer.getCustIsActive()) throw new Exception("Validity Expired or Invalid Data");


			// Check customer validity
			if(customer.getCustIsActive() == true) {

				// Get Employee details by customer id, customer code, employee mobile number
				employee = employeeRepository.getEmployeeDetailsByCustomer_CustCodeAndEmployee_EmpMobile(custCode, empMobile, customer.getCustId());

				// Employee not exist
				if(employee == null) throw new Exception("Employee doesn't exist or Invalid Data");
				if(employee != null)  {
					response.status = new Status(false,200,"success");
					response.employeeDataDto = new EmployeeDataDto(employee.getCustomer().getCustId(), employee.getEmpId(), employee.getEmpName(), employee.getEmpPresistedFaceId(), employee.getEmpAppSetupStatus());					
				}
			} else {
				throw new Exception("Invalid Data");
			}

		} catch(Exception ex) {
			response.status = new Status(true,400,ex.getMessage());
		}
		return response;
	}

	@Override
	public OtpResponseDto generateOtp(Integer custId, Integer empId) {
		OtpResponseDto otpResponseDto = new OtpResponseDto();
		try {
			// Generate random otp
			int otp = employeeRepository.generateOtp();

			Employee employee = employeeRepository.getByEmpIdAndRefCustId(custId, empId);

			// Check if employee exist or no
			if(employee == null) throw new Exception("Employee cannot found");

			// If employee exist Triger otp to employee mobile number
			if(employee != null) {

				String mobileNo = employee.getEmpMobile();
				String mobileSmS = "Welcome to LNG Attendance System, Your OTP is : "+ otp;	
				String s = messageUtil.sms(mobileNo, mobileSmS);
				otpResponseDto.status = new Status(false,200,"Successfully sent OTP");
				otpResponseDto.otpDto = new OtpDto(otp);

			}
		} catch(Exception ex) {
			otpResponseDto.status = new Status(true,400,ex.getMessage());
		}
		return otpResponseDto;
	}

	@Override
	@Transactional(dontRollbackOn = Exception.class)
	public EmpAppStatusResponseDto updateEmpAppStatus(Integer custId, Integer empId) {
		EmpAppStatusResponseDto empAppStatusResponseDto = new EmpAppStatusResponseDto();
		try {
			Employee employee = employeeRepository.getByEmpIdAndRefCustId(custId, empId);

			// Check if employee exist or no
			if(employee == null) throw new Exception("Employee cannot found");

			if(employee != null) {

				// If employee exist then update the employee app status to "ESTP"
				employee.setEmpAppSetupStatus(EmpAppSetupStatus.ESTP.toString());
				try {
					employeeRepository.save(employee);
					empAppStatusResponseDto.empAppStatusDto = new EmpAppStatusDto(employee.getEmpAppSetupStatus());
					empAppStatusResponseDto.status = new Status(false,200,"Successfully Updated Employee App Status Stage 2");
				} catch (Exception ex) {
					empAppStatusResponseDto.status = new Status(true,400,ex.getMessage());
				}
			}	
		}catch (Exception ex) {
			empAppStatusResponseDto.status = new Status(true,400,ex.getMessage());
		}
		return empAppStatusResponseDto;
	}

	//Employee Application Setup Stage 2
	@Override
	public EmpAppStatusResponseDto updateEmpAppStatusStageTwo(EmployeeSetup2Dto employeeSetup2Dto) {
		Employee employee = new Employee();
		EmpAppStatusResponseDto empAppStatusResponseDto = new EmpAppStatusResponseDto();
		try {
			employee = employeeRepository.getEmployeeByEmpId(employeeSetup2Dto.getEmpId());
			if(employee == null) throw new Exception("Employee cannot found");
			if(employee != null) {
				employee.setEmpPicBlobPath(employeeSetup2Dto.getEmpPicBlobPath());
				employee.setEmpPresistedFaceId(employeeSetup2Dto.getEmpPresistedFaceId());
				employee.setEmpAppSetupStatus(EmpAppSetupStatus.FSTP.toString());
				try {
					employeeRepository.save(employee);
					empAppStatusResponseDto.empAppStatusDto = new EmpAppStatusDto(employee.getEmpAppSetupStatus());
					empAppStatusResponseDto.status = new Status(false,200,"Successfully Updated Employee App Status Stage 2");
				} catch (Exception ex) {
					empAppStatusResponseDto.status = new Status(true,400,ex.getMessage());
				}
			}
		}catch (Exception ex) {

			empAppStatusResponseDto.status = new Status(true,400,ex.getMessage());
		}
		return empAppStatusResponseDto;
	}
} 
		

