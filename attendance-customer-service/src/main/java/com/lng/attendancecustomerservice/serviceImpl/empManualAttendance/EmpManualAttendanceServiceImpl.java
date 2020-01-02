package com.lng.attendancecustomerservice.serviceImpl.empManualAttendance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.employeeAttendance.EmployeeAttendance;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.empManualAttendance.EmpManualAttendanceRepository;
import com.lng.attendancecustomerservice.repositories.employeeAttendance.EmployeeAttendanceRepository;
import com.lng.attendancecustomerservice.repositories.employeeAttendance.UnmatchedEmployeeAttendanceRepository;
import com.lng.attendancecustomerservice.service.empManualAttendance.EmpManualAttendanceService;
import com.lng.dto.empAttendance.EmpAttendResponseDto;
import com.lng.dto.empAttendance.EmpAttendaceOutDto;
import com.lng.dto.empAttendance.EmpAttendanceInDto;
import com.lng.dto.empAttendance.EmpAttendanceParamDto;
import com.lng.dto.empAttendance.EmpAttendanceParamDto2;
import com.lng.dto.empAttendance.EmpAttendanceResponse;
import com.lng.dto.empAttendance.EmpManualAttendance;
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


	@Autowired
	UnmatchedEmployeeAttendanceRepository unmatchedEmpAttndRepo;


	@Override
	public EmpAttendanceResponse getEmpAttendanceByDepartment_deptIdAndEmpAttendanceDatetime(Integer deptId, String empAttendanceDate) {
		EmpAttendanceResponse empAttendanceResponse = new EmpAttendanceResponse();
		List<EmpAttendanceParamDto> empAttendanceDtoList = new ArrayList<>();

		try {
			List<Object[]> empAttendance = empAttendanceRepository
					.findEmpAttendanceByDepartment_deptIdAndEmpAttendanceDatetime(deptId, empAttendanceDate);
			if (empAttendance.isEmpty()) {

				empAttendanceResponse.status = new Status(false, 400, "Records Not Found");

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
			empAttendanceResponse.status = new Status(true, 400, "Opps..! Something went wrong..");

		}
		empAttendanceResponse.setData1(empAttendanceDtoList);
		return empAttendanceResponse;
	}

	@Override
	public Status saveEmpAttnd(List<EmployeeAttendanceDto> employeeAttendanceDtos) {
		Status status = null;
		BigDecimal bd = new BigDecimal(100.100);
		EmployeeAttendance employeeAttendance1 = new EmployeeAttendance();
		try {
			for(EmployeeAttendanceDto employeeAttendanceDto : employeeAttendanceDtos) {
				employeeAttendance1 = employeeAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceDate(employeeAttendanceDto.getRefEmpId(), employeeAttendanceDto.getEmpAttendanceDate());
				EmployeeAttendance employeeAttendance2 = employeeAttendanceRepository.findByEmpAttendanceId(employeeAttendanceDto.getEmpAttendanceId());
				Employee employee = employeeRepository.getByEmpId(employeeAttendanceDto.getRefEmpId());
				if(employee != null) {
					if(employeeAttendance2 == null) {
						if(employeeAttendance1 == null) {
							employeeAttendance1 = new EmployeeAttendance();
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
							status = new Status(false, 200, "Attendance IN marked successfully");
						} else {
							//employeeAttendance1 = new EmployeeAttendance();
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
							status = new Status(false, 200, "Attendance IN marked successfully");

						}
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
						status = new Status(false, 200, "Attendance OUT marked successfully");
					}
				}else {
					status = new Status(false, 400, "Employee not found");
				}
			}

		} catch (Exception e) {

			status = new Status(true, 500,"Opps..! Something went wrong..");
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
						status = new Status(false, 200, "Attendance marked successfully");
					}else {
						status = new Status(false, 400, "Employee not found");
					}
				} else {
					status = new Status(false, 200, "Attendance marked successfully"); 
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

					empAttendanceResponse.status = new Status(false, 400, "Records not found");

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
						EmpAttendanceDto1.setEmpAttendanceConsiderInDatetime((Date)p[7]);
						EmpAttendanceDto1.setEmpAttendanceConsiderOutDatetime((Date)p[8]);
						empAttendanceDtoList.add(EmpAttendanceDto1);
						empAttendanceResponse.status = new Status(false, 200, "success");
					}
				}
			} else {
				empAttendanceResponse.status = new Status(true, 400, "Please enter more than 3 character");
			} 
		} catch (Exception e) {
			empAttendanceResponse.status = new Status(true, 500, "Opps..! Something went wrong..");
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

					employeeAttendance.setEmpAttendanceConsiderInDatetime(employeeAttendanceDto.getEmpAttendanceConsiderInDatetime());
					employeeAttendance.setEmpAttendanceConsiderOutDatetime(employeeAttendanceDto.getEmpAttendanceConsiderOutDatetime());

					employeeAttendanceRepository.save(employeeAttendance);
					status = new Status(false, 200, "Attendance marked successfully");
				}

			}else {
				status = new Status(false, 400, "Employee not found");
			}

		} catch (Exception e) {
			status = new Status(true, 500, "Opps..! Something went wrong..");
		}
		return status;
	}

	@Override
	public EmpAttendResponseDto getEmpAttendanceBydeptIdAndEmpAttendanceDate(Integer deptId, String empAttendanceDate) {
		EmpAttendResponseDto empAttendResponseDto = new EmpAttendResponseDto();
		EmpManualAttendance empManualAttendanceDto = new EmpManualAttendance();
		List<EmpAttendanceInDto> inList = new ArrayList<>();
		List<EmpAttendaceOutDto> outList = new ArrayList<>();
		
		try {
			List<Object[]> empInAttendance = empAttendanceRepository.findEmpInAttendanceByDeptAndDate(deptId, empAttendanceDate);
			if(!empInAttendance.isEmpty()) {
				for(Object[] p : empInAttendance) {
					EmpAttendanceInDto empAttendanceInDto = new EmpAttendanceInDto();
					empAttendanceInDto.setRefEmpId(Integer.valueOf(p[0].toString()));
					empAttendanceInDto.setEmpName(p[1].toString());
					empAttendanceInDto.setDeptId(Integer.valueOf(p[2].toString()));
					empAttendanceInDto.setShiftName(p[3].toString());
					empAttendanceInDto.setShiftStart(p[4].toString());
					empAttendanceInDto.setShiftEnd(p[5].toString());
					empAttendanceInDto.setEmpAttendanceId(Integer.valueOf(p[6].toString()));
					empAttendanceInDto.setEmpAttendanceInDatetime(p[7].toString());
					empAttendanceInDto.setEmpAttendanceOutDatetime(p[8].toString());
					empAttendanceInDto.setEmpAttendanceDate(p[9].toString());
					inList.add(empAttendanceInDto);
					empManualAttendanceDto.setAttendanceInDetails(inList);		
				}
			}
			
			List<Object[]> empoutAttendance = empAttendanceRepository.findEmpOutAttendanceByDeptAndDate(deptId, empAttendanceDate);
			if(!empoutAttendance.isEmpty()) {
				for(Object[] p : empoutAttendance) {
					EmpAttendaceOutDto empAttendanceOutDto = new EmpAttendaceOutDto();
					empAttendanceOutDto.setRefEmpId(Integer.valueOf(p[0].toString()));
					empAttendanceOutDto.setEmpName(p[1].toString());
					empAttendanceOutDto.setDeptId(Integer.valueOf(p[2].toString()));
					empAttendanceOutDto.setShiftName(p[3].toString());
					empAttendanceOutDto.setShiftStart(p[4].toString());
					empAttendanceOutDto.setShiftEnd(p[5].toString());
					empAttendanceOutDto.setEmpAttendanceId(Integer.valueOf(p[6].toString()));
					empAttendanceOutDto.setEmpAttendanceInDatetime(p[7].toString());
					empAttendanceOutDto.setEmpAttendanceOutDatetime(p[8].toString());
					empAttendanceOutDto.setEmpAttendanceDate(p[9].toString());
					outList.add(empAttendanceOutDto);
					empManualAttendanceDto.setAttendanceOutDetails(outList);
				}
			}
			empAttendResponseDto.setResponse(empManualAttendanceDto);
			empAttendResponseDto.status = new Status(false, 200, "Success");
		} catch (Exception e) {
			empAttendResponseDto.status = new Status(true, 500, "Oops..! Something went wrong");
		}
		return empAttendResponseDto;
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
