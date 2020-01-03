package com.lng.attendancetabservice.serviceImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancetabservice.entity.Employee;
import com.lng.attendancetabservice.entity.EmployeePic;
import com.lng.attendancetabservice.entity.Shift;
import com.lng.attendancetabservice.repositories.EmployeePicRepository;
import com.lng.attendancetabservice.repositories.EmployeeRepository;
import com.lng.attendancetabservice.repositories.ShiftRepository;
import com.lng.attendancetabservice.service.EmployeeService;
import com.lng.attendancetabservice.utils.MessageUtil;
import com.lng.dto.employee.OtpDto;
import com.lng.dto.employee.OtpResponseDto;
import com.lng.dto.tabService.EmployeeDto1;
import com.lng.dto.tabService.EmployeeDto2;
import com.lng.dto.tabService.EmployeeDto3;
import com.lng.dto.tabService.EmployeeResponse1;
import com.lng.dto.tabService.EmployeeResponse2;

import status.Status;
@Service
public class EmployeeServiceImpl implements EmployeeService {
	@Autowired
	EmployeeRepository employeeRepository;
	@Autowired
	EmployeePicRepository employeePicRepository;

	MessageUtil messageUtil = new MessageUtil();
	@Autowired
	ShiftRepository shiftRepository;



	@Override
	public EmployeeResponse1 verifyMobileNo(Integer refBrId, Integer refCustId,String empMobile) {
		EmployeeResponse1  employeeResponse1  =  new  EmployeeResponse1();
		String shiftStartTime = null;
		String shiftEndTime = null;
		EmployeeDto2 employeeDto2 = new EmployeeDto2();
		try {

			Employee	employee =  employeeRepository.findEmployee(empMobile);
			Employee   employee1  = employeeRepository.checkEmployeeExistsOrNot(refBrId, refCustId,empMobile);
			Shift shift = shiftRepository.findShiftByEmployee_EmpMobile(empMobile);
			if(employee != null && employee1 != null ) {
				if(shift != null) {
					shiftStartTime = shift.getShiftStart().substring(5).trim().toUpperCase();
					shiftEndTime = shift.getShiftEnd().substring(5).trim().toUpperCase();
					if(shiftStartTime.equalsIgnoreCase("PM") && shiftEndTime.equalsIgnoreCase("AM")) {
						employeeDto2.setShiftType("D1D2");
					} else {
						employeeDto2.setShiftType("D1D1");
					}
					employeeDto2.setEmpId(employee.getEmpId());
					employeeDto2.setEmpName(employee.getEmpName());
					if(employee.getEmpPresistedFaceId() == null || employee.getEmpPresistedFaceId().isEmpty()) {
						employeeDto2.setEmpPresistedFaceId("null");
					}else {
						employeeDto2.setEmpPresistedFaceId(employee.getEmpPresistedFaceId());
					}
					employeeResponse1.setData1(employeeDto2);
					employeeResponse1.status = new Status(false,200, "success");
				} else {
					employeeResponse1.status = new Status(true, 400, "Shift not found for this employee");
				}
			} else {
				employeeResponse1.status = new Status(true, 400, "Invalid mobile number");
			}
		}catch (Exception e){
			employeeResponse1.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return employeeResponse1;
	}

	@Override
	public Status updateEmployee(EmployeeDto1 employeeDto1) {
		Status status = null;
		EmployeePic employeePic = new EmployeePic();
		try {
			Employee employee = employeeRepository.findByempId(employeeDto1.getEmpId());
			if(employee != null){
				employee.setEmpPresistedFaceId(employeeDto1.getEmpPresistedFaceId());
				employeeRepository.save(employee);

				employeePic = employeePicRepository.findByEmployee_EmpId(employee.getEmpId());
				if(employeePic == null) {
					EmployeePic employeePic1 = new EmployeePic();
					employeePic1.setEmployee(employee);
					employeePic1.setEmployeePic(base64ToByte(employeeDto1.getEmployeePic()));
					employeePicRepository.save(employeePic1);
					status = new Status(false, 200, "successfully updated");
				} else {
					employeePic.setEmployeePic(base64ToByte(employeeDto1.getEmployeePic()));
					employeePicRepository.save(employeePic);
					status = new Status(false, 200, "successfully updated");
				}
			}else {
				status = new Status(false,400," Employee Not found");
			}
		}catch(Exception e){
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}

	// Convert base64 to byte
	public  byte[] base64ToByte(String base64) {
		byte[] decodedByte = Base64.getDecoder().decode(base64);
		return decodedByte;
	}

	@Override
	public OtpResponseDto generateOtp(String empMobile) {

		OtpResponseDto otpResponseDto = new OtpResponseDto();
		try {
			// Generate random otp
			int otp = employeeRepository.generateOtp();

			Employee employee = employeeRepository.findEmployeeByEmpMobile(empMobile);

			// Check if Employee exist or no
			if(employee == null) throw new Exception("Invalid mobile number");

			// If Employeee exist Triger otp to Employee mobile number
			if(employee != null) {

				String mobileNo = employee.getEmpMobile();
				String mobileSmS = otp +" is OTP to verify your Employee details with "+ employee.getCustomer().getCustName();	
				String s = messageUtil.sms(mobileNo, mobileSmS);
				if(s != null) {
					otpResponseDto.status = new Status(false,200,"Successfully sent OTP");
					otpResponseDto.otpDto = new OtpDto(otp);				
				}else {
					otpResponseDto.status = new Status(true,400,"There is some problem with the message utility");
				}
			}
		} catch(Exception ex) {
			otpResponseDto.status = new Status(true,500,"Oops..! Something went wrong..");
		}
		return otpResponseDto;
	}

	@Override
	public EmployeeResponse2 getShiftDetailsByEmpId(Integer empId) {
		EmployeeResponse2  employeeResponse2  =  new  EmployeeResponse2();
		String shiftStartTime = null;
		String shiftEndTime = null;
		String endTime = null;
		String time = "2:00 PM";
		EmployeeDto3 employeeDto2 = new EmployeeDto3();
		try {
			Shift shift = shiftRepository.findShiftByEmployee_EmpId(empId);
			if(shift != null) {
				shiftStartTime = shift.getShiftStart().substring(5).trim().toUpperCase();
				shiftEndTime = shift.getShiftEnd().substring(5).trim().toUpperCase();
				endTime  = shift.getShiftEnd();
				if(shiftStartTime.equalsIgnoreCase("PM") && shiftEndTime.equalsIgnoreCase("AM")) {
					employeeDto2.setShiftType("D1D2");
				} else {
					employeeDto2.setShiftType("D1D1");
				}
				DateFormat sdf = new SimpleDateFormat("hh:mm aa");
				Date date1 = sdf.parse(endTime);
				Date date2 = sdf.parse(time);
				long difference = date1.getTime() - date2.getTime(); 
				employeeDto2.setOutPermisableTime(difference);
				employeeResponse2.setData(employeeDto2);
				employeeResponse2.status = new Status(false,200, "success");
			} else {
				employeeResponse2.status = new Status(true, 400, "Employee not found");
			}
		}catch (Exception e){
			employeeResponse2.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return employeeResponse2;
	}

}

