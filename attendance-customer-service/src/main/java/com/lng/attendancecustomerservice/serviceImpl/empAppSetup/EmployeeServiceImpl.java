package com.lng.attendancecustomerservice.serviceImpl.empAppSetup;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.service.empAppSetup.EmployeeService;
import com.lng.attendancecustomerservice.utils.MessageUtil;
import com.lng.dto.employee.EmployeeDataDto;
import com.lng.dto.employee.EmployeeDto;
import com.lng.dto.employee.OtpDto;
import com.lng.dto.employee.OtpResponseDto;
import com.lng.dto.employee.ResponseDto;

import status.Status;
import status.StatusDto;

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
					byte[] custLogo = employee.getCustomer().getCustLogoFile();
					String base64CustLogo = byteTobase64(custLogo);
					response.status = new Status(false,200,"success");
					response.employeeDataDto = new EmployeeDataDto(employee.getCustomer().getCustId(), employee.getCustomer().getCustName(), employee.getBranch().getBrCode(), employee.getEmpId(), employee.getEmpName(), employee.getEmpPresistedFaceId(), employee.getEmpAppSetupStatus(), base64CustLogo);					
				}
			} else {
				throw new Exception("Invalid Data");
			}

		} catch(Exception ex) {
			response.status = new Status(true,400,ex.getMessage());
		}
		return response;
	}

	// convert byte to base64
	public  String byteTobase64(byte[] custLogoFile) {
		String base64 = Base64.getEncoder().encodeToString(custLogoFile);
		return base64;
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
	public StatusDto updateEmpAppStatus(EmployeeDto employeeDto) {
		StatusDto statusDto = new StatusDto();
		Employee employee = employeeRepository.getByEmpId(employeeDto.getEmpId());

		try {
			if(employee != null) {
				employee.setEmpPicBlobPath(employeeDto.getEmpPicBlobPath());
				employee.setEmpPresistedFaceId(employeeDto.getEmpPresistedFaceId());
				employee.setEmpDeviceName(employeeDto.getEmpDeviceName());
				employee.setEmpModelNumber(employeeDto.getEmpModelNumber());
				employee.setEmpAndriodVersion(employeeDto.getEmpAndriodVersion());
				employee.setEmpAppSetupStatus(true);
				employeeRepository.save(employee);
				//empAppStatusResponseDto.empAppStatusDto = new EmpAppStatusDto(employee.getEmpAppSetupStatus());
				statusDto.setCode(200);
				statusDto.setError(false);
				statusDto.setMessage("Succesfully Updated");
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Employee not found");
			}


		} catch (Exception ex) {
			statusDto.setCode(200);
			statusDto.setError(false);
			statusDto.setMessage("Succesfully Updated");
		}
		return statusDto;
	}


	//Employee Application Setup Stage 2
	/*@Override
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
	}*/
} 


