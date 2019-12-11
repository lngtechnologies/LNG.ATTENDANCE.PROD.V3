package com.lng.attendancecustomerservice.serviceImpl.empManualAttendance;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.employeeAttendance.EmployeeAttendance;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.empManualAttendance.EmpManualAttendanceRepository;
import com.lng.attendancecustomerservice.repositories.employeeAttendance.EmployeeAttendanceRepository;
import com.lng.attendancecustomerservice.service.empManualAttendance.EmpManualAttendanceService;
import com.lng.dto.empAttendance.EmpAttendanceParamDto;
import com.lng.dto.empAttendance.EmpAttendanceParamDto2;
import com.lng.dto.empAttendance.EmpAttendanceResponse;
import com.lng.dto.employeeAttendance.EmployeeAttendanceDto;

import status.Status;

@Service
public class EmpManualAttendanceServiceImpl implements EmpManualAttendanceService {

	ModelMapper modelMapper=new ModelMapper();

	@Autowired
	EmpManualAttendanceRepository empAttendanceRepository;
	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	EmployeeAttendanceRepository employeeAttendanceRepository;


	@Override
	public EmpAttendanceResponse getEmpAttendanceByDepartment_deptIdAndEmpAttendanceDatetime(Integer deptId, String empAttendanceDate) {
		EmpAttendanceResponse empAttendanceResponse = new EmpAttendanceResponse();
		List<EmpAttendanceParamDto> empAttendanceDtoList = new ArrayList<>();

		try {
			List<Object[]> empAttendance = empAttendanceRepository
					.findEmpAttendanceByDepartment_deptIdAndEmpAttendanceDatetime(deptId, empAttendanceDate);
			if (empAttendance.isEmpty()) {
<<<<<<< HEAD
				empAttendanceResponse.status = new Status(false, 400, "Records Not Found");
=======
				empAttendanceResponse.status = new Status(true, 400, "Employee Attendance Not Found");
>>>>>>> branch 'develop' of https://github.com/lngtechnologies/LNG.ATTENDANCE.PROD.V3
			} else {
				for (Object[] p : empAttendance) {

					EmpAttendanceParamDto EmpAttendanceDto1 = new EmpAttendanceParamDto();
					EmpAttendanceDto1.setRefEmpId(Integer.valueOf(p[0].toString()));
					EmpAttendanceDto1.setEmpName((p[1].toString()));
					EmpAttendanceDto1.setDeptId(Integer.valueOf(p[2].toString()));
					EmpAttendanceDto1.setShiftName(p[3].toString());
					EmpAttendanceDto1.setShiftStart((p[4].toString()));
					EmpAttendanceDto1.setShiftEnd(p[5].toString());
					EmpAttendanceDto1.setEmpAttendanceId(Integer.valueOf(p[6].toString()));
					
					if(!p[7].toString().equals("NA")) {
						//String pattern = "yyyy / MM / dd h:mm a";
	                    //SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
	                    //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	                    //String date = dateFormat.format((Date)p[7]);
						
						EmpAttendanceDto1.setEmpAttendanceInDatetime(p[7].toString());
					} else {
						EmpAttendanceDto1.setEmpAttendanceInDatetime("NA");
					}
					
					if(!p[8].toString().equals("NA")) {
						//String pattern1 = "yyyy / MM / dd h:mm a";
	                    //SimpleDateFormat dateFormat1 = new SimpleDateFormat(pattern1);
	                    //dateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
	                    //String date1 = dateFormat1.format(p[8]);
						
	                    EmpAttendanceDto1.setEmpAttendanceOutDatetime(p[8].toString());
					} else {
						 EmpAttendanceDto1.setEmpAttendanceOutDatetime("NA");
					}
					
					if(!p[9].toString().equals("NA")) {
					//	String pattern1 = "yyyy / MM / dd";
	                //    SimpleDateFormat dateFormat1 = new SimpleDateFormat(pattern1);
	                //    dateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
	                //    String date1 = dateFormat1.format((Date)p[9]);
						EmpAttendanceDto1.setEmpAttendanceDate(p[9].toString());
					}else {
						EmpAttendanceDto1.setEmpAttendanceDate("NA");
					}
                    
                    
					empAttendanceDtoList.add(EmpAttendanceDto1);
					empAttendanceResponse.status = new Status(false, 200, "success");
				}

			}

		} catch (Exception e) {
			empAttendanceResponse.status = new Status(true, 400, e.getMessage());

		}
		empAttendanceResponse.setData1(empAttendanceDtoList);
		return empAttendanceResponse;
	}

	@Override
	public Status saveEmpAttnd(List<EmployeeAttendanceDto> employeeAttendanceDtos) {
		Status status = null;
		BigDecimal bd = new BigDecimal(100.100);
		try {
			for(EmployeeAttendanceDto employeeAttendanceDto : employeeAttendanceDtos) {
				
				List<EmployeeAttendance> employeeAttendance = employeeAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceInDatetime(employeeAttendanceDto.getRefEmpId(), employeeAttendanceDto.getEmpAttendanceInDatetime());
				
				EmployeeAttendance employeeAttendance2 = employeeAttendanceRepository.findByEmpAttendanceId(employeeAttendanceDto.getEmpAttendanceId());
				
				Employee employee = employeeRepository.getByEmpId(employeeAttendanceDto.getRefEmpId());
				if(employee != null) {
					if(employeeAttendance.isEmpty()) {
						
						if(employeeAttendance2 == null) {
							EmployeeAttendance employeeAttendance1 = new EmployeeAttendance();
							employeeAttendance1.setEmpAttendanceDate(employeeAttendanceDto.getEmpAttendanceDate());
							employeeAttendance1.setEmployee(employee);
							employeeAttendance1.setEmpAttendanceInDatetime(employeeAttendanceDto.getEmpAttendanceInDatetime());
							employeeAttendance1.setEmpAttendanceOutDatetime(employeeAttendanceDto.getEmpAttendanceOutDatetime());
							employeeAttendance1.setEmpAttendanceConsiderInDatetime(employeeAttendanceDto.getEmpAttendanceInDatetime());
							employeeAttendance1.setEmpAttendanceConsiderOutDatetime(employeeAttendanceDto.getEmpAttendanceOutDatetime());
							
							if(employeeAttendanceDto.getEmpAttendanceInMode() == null) {
								employeeAttendance1.setEmpAttendanceInMode("D");
							}else {
								employeeAttendance1.setEmpAttendanceInMode(employeeAttendanceDto.getEmpAttendanceInMode());
							}
							
							if(employeeAttendanceDto.getEmpAttendanceOutMode() == null) {
								employeeAttendance1.setEmpAttendanceOutMode("D");
							}else {
								employeeAttendance1.setEmpAttendanceOutMode(employeeAttendanceDto.getEmpAttendanceOutMode());
							}
							
							if(employeeAttendanceDto.getEmpAttendanceInLatLong() == null) {
								employeeAttendance1.setEmpAttendanceInLatLong("00.0000, 00.0000");
							}else {
								employeeAttendance1.setEmpAttendanceInLatLong(employeeAttendanceDto.getEmpAttendanceInLatLong());
							}
							
							if(employeeAttendanceDto.getEmpAttendanceOutLatLong() == null) {
								employeeAttendance1.setEmpAttendanceOutLatLong("00.0000, 00.0000");
							}else {
								employeeAttendance1.setEmpAttendanceOutLatLong(employeeAttendanceDto.getEmpAttendanceOutLatLong());
							}
							
							if(employeeAttendanceDto.getEmpAttendanceInConfidence() == null) {
								employeeAttendance1.setEmpAttendanceInConfidence(bd);
							}else {
								employeeAttendance1.setEmpAttendanceInConfidence(employeeAttendanceDto.getEmpAttendanceInConfidence());
							}
							
							if(employeeAttendanceDto.getEmpAttendanceOutConfidence() == null) {
								employeeAttendance1.setEmpAttendanceOutConfidence(bd);
							}else {
								employeeAttendance1.setEmpAttendanceOutConfidence(employeeAttendanceDto.getEmpAttendanceOutConfidence());
							}
							
							employeeAttendanceRepository.save(employeeAttendance1);
							status = new Status(false, 200, "Successfully attendance marked");
						} else {
							
							employeeAttendance2.setEmpAttendanceDate(employeeAttendanceDto.getEmpAttendanceDate());
							employeeAttendance2.setEmployee(employee);
							employeeAttendance2.setEmpAttendanceInDatetime(employeeAttendance2.getEmpAttendanceInDatetime());
							employeeAttendance2.setEmpAttendanceOutDatetime(employeeAttendanceDto.getEmpAttendanceOutDatetime());
							employeeAttendance2.setEmpAttendanceConsiderInDatetime(employeeAttendance2.getEmpAttendanceConsiderInDatetime());
							employeeAttendance2.setEmpAttendanceConsiderOutDatetime(employeeAttendanceDto.getEmpAttendanceOutDatetime());
							
							if(employeeAttendanceDto.getEmpAttendanceInMode() == null) {
								employeeAttendance2.setEmpAttendanceInMode("D");
							}else {
								employeeAttendance2.setEmpAttendanceInMode(employeeAttendanceDto.getEmpAttendanceInMode());
							}
							
							if(employeeAttendanceDto.getEmpAttendanceOutMode() == null) {
								employeeAttendance2.setEmpAttendanceOutMode("D");
							}else {
								employeeAttendance2.setEmpAttendanceOutMode(employeeAttendanceDto.getEmpAttendanceOutMode());
							}
							
							if(employeeAttendanceDto.getEmpAttendanceInLatLong() == null) {
								employeeAttendance2.setEmpAttendanceInLatLong("00.0000, 00.0000");
							}else {
								employeeAttendance2.setEmpAttendanceInLatLong(employeeAttendanceDto.getEmpAttendanceInLatLong());
							}
							
							if(employeeAttendanceDto.getEmpAttendanceOutLatLong() == null) {
								employeeAttendance2.setEmpAttendanceOutLatLong("00.0000, 00.0000");
							}else {
								employeeAttendance2.setEmpAttendanceOutLatLong(employeeAttendanceDto.getEmpAttendanceOutLatLong());
							}
							
							if(employeeAttendanceDto.getEmpAttendanceInConfidence() == null) {
								employeeAttendance2.setEmpAttendanceInConfidence(bd);
							}else {
								employeeAttendance2.setEmpAttendanceInConfidence(employeeAttendanceDto.getEmpAttendanceInConfidence());
							}
							
							if(employeeAttendanceDto.getEmpAttendanceOutConfidence() == null) {
								employeeAttendance2.setEmpAttendanceOutConfidence(bd);
							}else {
								employeeAttendance2.setEmpAttendanceOutConfidence(employeeAttendanceDto.getEmpAttendanceOutConfidence());
							}
							
							employeeAttendanceRepository.save(employeeAttendance2);
							status = new Status(false, 200, "Successfully attendance marked");
						}

					} else {
						status = new Status(false, 200, "Successfully attendance marked");
					} 

				}else {
					status = new Status(false, 400, "Employye not found");
				}
			}

		} catch (Exception e) {

			status = new Status(true, 500, "Opps..! Something went wrong..");
		}
		return status;
	}

	@Override
	public Status saveSignOut(List<EmployeeAttendanceDto> employeeAttendanceDtos) {
		Status status = null;
		BigDecimal bd = new BigDecimal(100.100);

		try {
			for(EmployeeAttendanceDto employeeAttendanceDto : employeeAttendanceDtos) {
				EmployeeAttendance employeeAttendance = employeeAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceDateAndEmpAttendanceOutModeAndEmpAttendanceOutDatetimeAndEmpAttendanceOutLatLong
						(employeeAttendanceDto.getRefEmpId(), employeeAttendanceDto.getEmpAttendanceDate(),employeeAttendanceDto.getEmpAttendanceOutMode(), employeeAttendanceDto.getEmpAttendanceOutDatetime(), employeeAttendanceDto.getEmpAttendanceOutLatLong());


				if(employeeAttendance == null) {

					EmployeeAttendance employeeAttendance1 = employeeAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceDate(employeeAttendanceDto.getRefEmpId(), employeeAttendanceDto.getEmpAttendanceDate());

					if(employeeAttendance1 != null) {

						employeeAttendance1.setEmpAttendanceOutDatetime(employeeAttendanceDto.getEmpAttendanceOutDatetime());
						employeeAttendance1.setEmpAttendanceConsiderOutDatetime(employeeAttendanceDto.getEmpAttendanceOutDatetime());

						if(employeeAttendanceDto.getEmpAttendanceOutMode() == null) {
							employeeAttendance1.setEmpAttendanceOutMode("D");
						}else {
							employeeAttendance1.setEmpAttendanceOutMode(employeeAttendanceDto.getEmpAttendanceOutMode());
						}

						if(employeeAttendanceDto.getEmpAttendanceOutLatLong() == null) {
							employeeAttendance1.setEmpAttendanceOutLatLong("00.0000, 00.0000");
						}else {
							employeeAttendance1.setEmpAttendanceOutLatLong(employeeAttendanceDto.getEmpAttendanceOutLatLong());
						}

						if(employeeAttendanceDto.getEmpAttendanceOutConfidence() == null) {
							employeeAttendance1.setEmpAttendanceOutConfidence(bd);
						}else {
							employeeAttendance1.setEmpAttendanceOutConfidence(employeeAttendanceDto.getEmpAttendanceOutConfidence());
						}

						employeeAttendanceRepository.save(employeeAttendance1);
						status = new Status(false, 200, "Successfully attendance marked");
					}else {
						status = new Status(false, 400, "Employye not found");
					}
				} else {
					status = new Status(false, 200, "Successfully attendance marked"); 
				}
			}

		} catch (Exception e) {

			status = new Status(true, 500, "Opps..! Something went wrong..");
		}
		return status;
	}

	/*@Override
	public EmpAttendanceResponse saveEmpAttendance(List<EmpAttendanceDto> empAttendanceDtos) {
		EmpAttendanceResponse empAttendanceResponse = new EmpAttendanceResponse();

		//List<EmpAttendanceDto> empAttendanceDto1 = new ArrayList<>();
		/*
	 * String msg = "Successsfully Saved And Already Marked Employee Id:"; String
	 * empId = "";
	 */
	// Date date = null;
	/*try {
			for (EmpAttendanceDto empAttendanceDto : empAttendanceDtos ) {
				int recordCount = empAttendanceRepository.checkEmpManualAttnd(
						empAttendanceDto.getRefEmpId(), empAttendanceDto.getShiftStart(),
						empAttendanceDto.getEmpAttendanceInDatetime());

				if (recordCount <= 0) {
					Employee employee  = employeeRepository.getEmployeeByEmpId(empAttendanceDto.getRefEmpId());
					if(employee != null) {
						EmpManualAttendance empAttendance  = new EmpManualAttendance();
						empAttendance.setEmployee(employee);
						empAttendance.setEmpAttendanceInMode(empAttendanceDto.getEmpAttendanceInMode());
						empAttendance.setEmpAttendanceInDatetime(empAttendanceDto.getEmpAttendanceInDatetime());
						empAttendance.setEmpAttendanceConsiderInDatetime(empAttendanceDto.getEmpAttendanceConsiderInDatetime());
						empAttendance.setEmpAttendanceInConfidence(empAttendanceDto. getEmpAttendanceInConfidence());
						empAttendance.setEmpAttendanceInLatLong(empAttendanceDto.getEmpAttendanceInLatLong());

						empAttendanceRepository.save(empAttendance);
						empAttendanceResponse.status = new Status(false,200, "successfully attendance Marked");
					}
					else{ 
						empAttendanceResponse.status = new Status(true,400, "Employee Not Found");
					}
				}
				else{ 
					empAttendanceResponse.status = new Status(true,400,"successfully attendance Marked");
				}
			}
		}catch(Exception ex){
			empAttendanceResponse.status = new Status(true,500, "Something went wrong"); 
		}

		return empAttendanceResponse;
	}
	public EmpAttendanceDto convertToEmpAttendanceDto(EmpManualAttendance empAttendance) {
		EmpAttendanceDto empAttendanceDto = modelMapper.map(empAttendance,EmpAttendanceDto.class);
		empAttendanceDto.setRefEmpId(empAttendance.getEmployee().getEmpId());
		return empAttendanceDto;
	}*/



	@Override
	public EmpAttendanceResponse searchEmployeeByNameAndRefCustIdAndEmpAttendanceDatetime(String emp, Integer refCustId, Date empAttendanceDatetime) {
		EmpAttendanceResponse empAttendanceResponse = new EmpAttendanceResponse();
		List<EmpAttendanceParamDto2> empAttendanceDtoList = new ArrayList<>();

		try {
			if(emp.length() >= 3) {
				List<Object[]> empAttendance = empAttendanceRepository.SearchEmployeeByNameAndDate(emp, refCustId, empAttendanceDatetime);
				if (empAttendance.isEmpty()) {
<<<<<<< HEAD
					empAttendanceResponse.status = new Status(false, 400, "Records Not Found");
=======
					empAttendanceResponse.status = new Status(true, 400, "Employee Attendance Not Found");
>>>>>>> branch 'develop' of https://github.com/lngtechnologies/LNG.ATTENDANCE.PROD.V3
				} else {
					for (Object[] p : empAttendance) {

						EmpAttendanceParamDto2 EmpAttendanceDto1 = new EmpAttendanceParamDto2();
						EmpAttendanceDto1.setRefEmpId(Integer.valueOf(p[0].toString()));
						EmpAttendanceDto1.setEmpName((p[1].toString()));
						EmpAttendanceDto1.setEmpAttendanceDate((Date)p[2]);
						EmpAttendanceDto1.setShiftStart((p[3].toString()));
						EmpAttendanceDto1.setShiftEnd(p[4].toString());
						EmpAttendanceDto1.setEmpAttendanceInDatetime((Date)p[5]);
						EmpAttendanceDto1.setEmpAttendanceOutDatetime((Date)p[6]);
						empAttendanceDtoList.add(EmpAttendanceDto1);
						empAttendanceResponse.status = new Status(false, 200, "success");
					}

				}
<<<<<<< HEAD
			} else {
				empAttendanceResponse.status = new Status(true, 4000, "Please enter more than 3 character");
=======
			}
			else {
				empAttendanceResponse.status = new Status(true, 4000, "Data too less ");
>>>>>>> branch 'develop' of https://github.com/lngtechnologies/LNG.ATTENDANCE.PROD.V3

			}

		} catch (Exception e) {
			empAttendanceResponse.status = new Status(true, 400, e.getMessage());

		}
		empAttendanceResponse.setData2(empAttendanceDtoList);
		return empAttendanceResponse;
	}

	@Override
	public Status updateEmpOverRideAttendance(EmployeeAttendanceDto employeeAttendanceDto) {
		Status status = null;
		try {
			List<EmployeeAttendance> employeeAttendance1 = employeeAttendanceRepository.findAllByEmployee_EmpIdAndEmpAttendanceDate(employeeAttendanceDto.getRefEmpId(), employeeAttendanceDto.getEmpAttendanceDate());
			if(!employeeAttendance1.isEmpty()) {
				for(EmployeeAttendance employeeAttendance: employeeAttendance1) {
					
					employeeAttendance.setEmpAttendanceConsiderInDatetime(employeeAttendanceDto.getEmpAttendanceInDatetime());
					employeeAttendance.setEmpAttendanceConsiderOutDatetime(employeeAttendanceDto.getEmpAttendanceOutDatetime());
					
					employeeAttendanceRepository.save(employeeAttendance);
					status = new Status(false, 200, "Successfully attendance marked");
				}

			}else {
				status = new Status(false, 400, "Employye not found");
			}

		} catch (Exception e) {
			status = new Status(true, 500, "Opps..! Something went wrong..");
		}
		return status;
	}

	/*@Override
	public EmpAttendanceResponse updateEmpOverRideAttendance(EmpAttendanceParamDto2 empAttendanceParamDto2)  {
		EmpAttendanceResponse empAttendanceResponse = new EmpAttendanceResponse();

		try {
			int a = empAttendanceRepository.checkEmpOverRideManualAttnd(empAttendanceParamDto2.getRefEmpId(),empAttendanceParamDto2.getEmpAttendanceConsiderDatetime());
			if(a != 0) {
				EmpManualAttendance empManualAttendance1  = empAttendanceRepository.findEmpManualAttendanceByRefEmpIdAndRefCustIdAndEmpAttendanceConsiderDatetime(empAttendanceParamDto2.getRefEmpId(), empAttendanceParamDto2.getRefCustId(), empAttendanceParamDto2.getEmpAttendanceConsiderDatetime());
				if(empManualAttendance1 != null) {
					empManualAttendance1.setEmpAttendanceConsiderInDatetime(empAttendanceParamDto2.getEmpAttendanceConsiderInDatetime());
					empAttendanceRepository.save(empManualAttendance1);

					empAttendanceResponse.status = new Status(false, 200, "Attendance Remarked");
				}
				else {
					empAttendanceResponse.status = new Status(true, 4000, "Not Found ");
				}
			}
			else {
				empAttendanceResponse.status = new Status(true, 4000, "Not Found ");
			}
		}catch(Exception e){
			empAttendanceResponse.status = new Status(true, 400, e.getMessage());
		}
		return empAttendanceResponse;
	}*/
} 
