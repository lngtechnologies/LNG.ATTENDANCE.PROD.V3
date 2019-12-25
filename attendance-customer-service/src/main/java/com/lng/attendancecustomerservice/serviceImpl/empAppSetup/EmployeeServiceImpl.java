package com.lng.attendancecustomerservice.serviceImpl.empAppSetup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.masters.EmployeePic;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeePicRepository;
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

	@Autowired
	EmployeePicRepository employeePicRepository;

	MessageUtil messageUtil = new MessageUtil();

	@Override
	public ResponseDto getByCustCodeAndEmpMobile(String custCode, String empMobile) {
		ResponseDto response = new ResponseDto();
		Employee employee = new Employee();
		String inDate = "NA";
		String outDate = "NA";
		String attndDate = "NA";
		try {
			Customer customer = customerRepository.getByCustCode(custCode);

			// Check customer exist else throw exception	
			if(customer == null || !customer.getCustIsActive()) throw new Exception("Invalid Data or Validity Expired");

			// Check customer validity
			if(customer.getCustIsActive() == true) {

				// Get Employee details by customer id, customer code, employee mobile number
				employee = employeeRepository.getEmployeeDetailsByCustomer_CustCodeAndEmployee_EmpMobile(custCode, empMobile, customer.getCustId());

				// Employee not exist
				if(employee == null) throw new Exception("Employee doesn't exist or Invalid Data");


				if(employee != null)  {

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
					formatter.setTimeZone(TimeZone.getTimeZone("IST"));
					 Date date = new Date();
					String strDate = formatter.format(date);
				
					
					Date empInAttndDate = employeeRepository.getRecentInDateByAttndDateAndEmpId(strDate, employee.getEmpId());

					Date empOutAttndDate = employeeRepository.getRecentOutDateByAttndDateAndEmpId(strDate, employee.getEmpId());
					
					Date empAttndDate = employeeRepository.getRecentAttndDate(strDate, employee.getEmpId());

					if(empInAttndDate != null || empOutAttndDate != null) {

						String pattern = "yyyy-MM-dd'T'HH:mm:ss";
						SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
						dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
						
						if(empInAttndDate != null) {
							inDate = dateFormat.format(empInAttndDate);
						}
						
						if(empOutAttndDate != null) {
							outDate = dateFormat.format(empOutAttndDate);
						}
						
						if(empAttndDate != null) {
							String pattern1 = "yyyy-MM-dd";
							SimpleDateFormat dateFormat1 = new SimpleDateFormat(pattern1);
							attndDate = dateFormat1.format(empAttndDate);
						}
						
						if(employee.getEmpPresistedFaceId() == null) {

							byte[] custLogo = employee.getCustomer().getCustLogoFile();
							String base64CustLogo = byteTobase64(custLogo);
							String brCode = employee.getBranch().getBrCode().toLowerCase();
							response.status = new Status(false,200,"success");
							response.employeeDataDto = new EmployeeDataDto(employee.getCustomer().getCustId(), employee.getCustomer().getCustName(), employee.getBranch().getBrId(), employee.getBranch().getBrName(), brCode, employee.getEmpId(), employee.getEmpName(), base64CustLogo, false, employee.getEmpPresistedFaceId(), true, attndDate, inDate, outDate);	

						}else {

							byte[] custLogo = employee.getCustomer().getCustLogoFile();
							String base64CustLogo = byteTobase64(custLogo);
							String brCode = employee.getBranch().getBrCode().toLowerCase();
							response.status = new Status(false,200,"success");
							response.employeeDataDto = new EmployeeDataDto(employee.getCustomer().getCustId(), employee.getCustomer().getCustName(), employee.getBranch().getBrId(), employee.getBranch().getBrName(), brCode, employee.getEmpId(), employee.getEmpName(), base64CustLogo, true, employee.getEmpPresistedFaceId(), true, attndDate, inDate, outDate);	
						}
					}else {
						if(employee.getEmpPresistedFaceId() == null) {

							byte[] custLogo = employee.getCustomer().getCustLogoFile();
							String base64CustLogo = byteTobase64(custLogo);
							String brCode = employee.getBranch().getBrCode().toLowerCase();
							response.status = new Status(false,200,"success");
							response.employeeDataDto = new EmployeeDataDto(employee.getCustomer().getCustId(), employee.getCustomer().getCustName(), employee.getBranch().getBrId(), employee.getBranch().getBrName(), brCode, employee.getEmpId(), employee.getEmpName(), base64CustLogo, false, employee.getEmpPresistedFaceId(), false, attndDate, inDate,outDate);	

						}else {

							byte[] custLogo = employee.getCustomer().getCustLogoFile();
							String base64CustLogo = byteTobase64(custLogo);
							String brCode = employee.getBranch().getBrCode().toLowerCase();
							response.status = new Status(false,200,"success");
							response.employeeDataDto = new EmployeeDataDto(employee.getCustomer().getCustId(), employee.getCustomer().getCustName(), employee.getBranch().getBrId(), employee.getBranch().getBrName(), brCode, employee.getEmpId(), employee.getEmpName(), base64CustLogo, true, employee.getEmpPresistedFaceId(), false, attndDate, inDate,outDate);	
						}
					}
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
			otpResponseDto.status = new Status(true,400,ex.getMessage());
		}
		return otpResponseDto;
	}

	@Override
	public StatusDto updateEmpAppStatus(EmployeeDto employeeDto) {
		StatusDto statusDto = new StatusDto();
		EmployeePic employeePic = new EmployeePic();
		Employee employee = employeeRepository.getByEmpId(employeeDto.getEmpId());

		try {
			if(employee != null) {
				//employee.setEmpPicBlobPath(employeeDto.getEmpPicBlobPath());
				employee.setEmpPresistedFaceId(employeeDto.getEmpPresistedFaceId());
				employee.setEmpDeviceName(employeeDto.getEmpDeviceName());
				employee.setEmpModelNumber(employeeDto.getEmpModelNumber());
				employee.setEmpAndriodVersion(employeeDto.getEmpAndriodVersion());
				employee.setEmpAppSetupStatus(true);
				employeeRepository.save(employee);
				try {
					employeePic = employeePicRepository.findByEmployee_EmpId(employee.getEmpId());

					if(employeePic == null) {
						EmployeePic employeePic1 = new EmployeePic();
						employeePic1.setEmployee(employee);
						employeePic1.setEmployeePic(base64ToByte(employeeDto.getEmployeePic()));
						employeePicRepository.save(employeePic1);

					} else {
						employeePic.setEmployeePic(base64ToByte(employeeDto.getEmployeePic()));
						employeePicRepository.save(employeePic);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}


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

	// Convert base64 to byte
	public  byte[] base64ToByte(String base64) {
		byte[] decodedByte = Base64.getDecoder().decode(base64);
		return decodedByte;
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

	public static Date getDateWithoutTime(Date empAttndDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		System.out.println(formatter.format(empAttndDate));
		return formatter.parse(formatter.format(empAttndDate));

	}

} 


