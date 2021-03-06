package com.lng.attendancetabservice.serviceImpl;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancetabservice.entity.Branch;
import com.lng.attendancetabservice.entity.Customer;
import com.lng.attendancetabservice.entity.Employee;
import com.lng.attendancetabservice.entity.EmployeePic;
import com.lng.attendancetabservice.entity.Shift;
import com.lng.attendancetabservice.repositories.BranchRepository;
import com.lng.attendancetabservice.repositories.CustomerRepository;
import com.lng.attendancetabservice.repositories.EmployeePicRepository;
import com.lng.attendancetabservice.repositories.EmployeeRepository;
import com.lng.attendancetabservice.repositories.ShiftRepository;
import com.lng.attendancetabservice.service.EmployeeService;
import com.lng.attendancetabservice.utils.MessageUtil;
import com.lng.dto.employeeAppSetup.OtpDto;
import com.lng.dto.employeeAppSetup.OtpResponseDto;
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

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BranchRepository branchRepository;

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
			Customer customer = customerRepository.findCustomerByCustId(refCustId);
			if(customer != null) {
				if(!customer.getCustIsActive()) { 
					employeeResponse1.status = new Status(true, 400, "Customer subscription expired, please contact admin");
					return employeeResponse1;
				}
				int custValidity = customerRepository.checkCustValidationByCustId(refCustId);
				if(custValidity == 0) {
					employeeResponse1.status = new Status(true, 400, "Customer subscription expired, please contact admin");
					return employeeResponse1;
				}
				Branch branch = branchRepository.findByBrId(refBrId);
				if(branch != null) {
					if(!branch.getBrIsActive()) { 
						employeeResponse1.status = new Status(true, 400, "Branch is not active");
						return employeeResponse1;
					}
					int branchValidity = branchRepository.checkBranchValidity(refBrId);
					if(branchValidity == 0) {
						employeeResponse1.status = new Status(true, 400, "Branch subscription expired, please contact admin");
						return employeeResponse1;
					}
					Employee   employee  = employeeRepository.checkEmployeeExistsOrNot(refBrId, refCustId,empMobile);
					if(employee != null) {
						Shift shift = shiftRepository.findShiftByEmployee_EmpMobileAndCustomer_custId(empMobile,refCustId);
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
				}else {
					employeeResponse1.status = new Status(true, 400, "Branch does not exist");
				}
			}else {
				employeeResponse1.status = new Status(true, 400, "Customer doesn't exist");
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
				if(!employee.getEmpInService()) { 
					status = new Status(true, 400, "Employee not in service");
					return status;
				}
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
				status = new Status(false,400," Employee doesn't exist");
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
	public OtpResponseDto generateOtp(String empMobile,Integer refCustId) {

		OtpResponseDto otpResponseDto = new OtpResponseDto();
		try {
			// Generate random otp
			int otp = employeeRepository.generateOtp();
			//Check Customer is Exist or Not
			Customer customer = customerRepository.findCustomerByCustId(refCustId);
			if(customer != null) {
				if(!customer.getCustIsActive()) { 
					otpResponseDto.status = new Status(true, 400, "Customer subscription expired, please contact admin");
					return otpResponseDto;
				}
				int custValidity = customerRepository.checkCustValidationByCustId(refCustId);
				if(custValidity == 0) {
					otpResponseDto.status = new Status(true, 400, "Customer subscription expired, please contact admin");
					return otpResponseDto;
				}
				Employee employee = employeeRepository.findEmployeeByEmpMobileAndCustomer_custId(empMobile,refCustId);

				// Check if Employee exist or no
				if(employee == null) {
					otpResponseDto.status = new Status(true,400,"Invalid mobile number");
					return otpResponseDto;
				}
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
			}else {
				otpResponseDto.status = new Status(true,400,"Customer doesn't exist");
			}
		} catch(Exception ex) {
			otpResponseDto.status = new Status(true,500,"Oops..! Something went wrong..");
		}
		return otpResponseDto;
	}


	@Override
	public EmployeeResponse2 getShiftDetailsByEmpIdAndCustId(Integer empId,Integer custId) {
		EmployeeResponse2  employeeResponse2  =  new  EmployeeResponse2();
		String shiftStartTime = null;
		String shiftEndTime = null;
		EmployeeDto3 employeeDto3 = new EmployeeDto3();
		try {
			Customer customer = customerRepository.findCustomerByCustId(custId);
			if(customer != null) {
				if(!customer.getCustIsActive()) { 
					employeeResponse2.status = new Status(true, 400, "Customer subscription expired, please contact admin");
					return employeeResponse2;
				}
				int custValidity = customerRepository.checkCustValidationByCustId(custId);
				if(custValidity == 0) {
					employeeResponse2.status = new Status(true, 400, "Customer subscription expired, please contact admin");
					return employeeResponse2;
				}
				Employee employee = employeeRepository.findByempId(empId);
				if(employee != null) {
					if(!employee.getEmpInService()) { 
						employeeResponse2.status = new Status(true, 400, "Employee not in service");
						return employeeResponse2;
					}
					Shift shift = shiftRepository.findShiftDetailsByEmployee_EmpIdAndCustomer_CustId(empId,custId);
					if(shift != null) {
						String time = employeeRepository.getOutPermissibleTimeByEmployee_EmpIdAndCustomer_CustId(empId,custId);

						shiftStartTime = shift.getShiftStart().substring(5).trim().toUpperCase();
						shiftEndTime = shift.getShiftEnd().substring(5).trim().toUpperCase();
						if(shiftStartTime.equalsIgnoreCase("PM") && shiftEndTime.equalsIgnoreCase("AM")) {
							employeeDto3.setShiftType("D1D2");
						} else {
							employeeDto3.setShiftType("D1D1");
						}
						employeeDto3.setEmpId(employee.getEmpId());
						employeeDto3.setShiftStart(shift.getShiftStart());
						employeeDto3.setShiftEnd(shift.getShiftEnd());
						employeeDto3.setCustId(customer.getCustId());
						employeeDto3.setOutPermissibleTime(time);
						employeeResponse2.setShiftData(employeeDto3);
						employeeResponse2.status = new Status(false,200, "success");
					} else {
						employeeResponse2.status = new Status(true, 400, "Shift not found");
					}
				} else {
					employeeResponse2.status = new Status(true, 400, "Employee doesn't exist");
				}
			} else {
				employeeResponse2.status = new Status(true, 400, "Customer doesn't exist");
			}
		}catch (Exception e){
			employeeResponse2.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return employeeResponse2;
	}

}

