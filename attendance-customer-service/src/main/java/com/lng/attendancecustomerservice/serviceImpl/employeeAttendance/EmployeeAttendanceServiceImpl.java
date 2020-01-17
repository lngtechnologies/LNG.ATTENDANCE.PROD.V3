package com.lng.attendancecustomerservice.serviceImpl.employeeAttendance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.employeeAttendance.EmployeeAttendance;
import com.lng.attendancecustomerservice.entity.employeeAttendance.UnmatchedEmployeeAttendance;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.masters.Shift;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.employeeAttendance.EmployeeAttendanceRepository;
import com.lng.attendancecustomerservice.repositories.employeeAttendance.UnmatchedEmployeeAttendanceRepository;
import com.lng.attendancecustomerservice.repositories.masters.ShiftRepository;
import com.lng.attendancecustomerservice.service.employeeAttendance.EmployeeAttendanceService;
import com.lng.dto.employeeAttendance.CurrentDateDto;
import com.lng.dto.employeeAttendance.EmpSignOutDto;
import com.lng.dto.employeeAttendance.EmpSignOutResponse;
import com.lng.dto.employeeAttendance.EmployeeAttendanceDto;
import com.lng.dto.employeeAttendance.ShiftDetailsDto;
import com.lng.dto.employeeAttendance.ShiftResponseDto;

import status.Status;

@Service
public class EmployeeAttendanceServiceImpl implements EmployeeAttendanceService {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	EmployeeAttendanceRepository employeeAttendanceRepository;

	@Autowired
	ShiftRepository shiftRepository;

	@Autowired
	UnmatchedEmployeeAttendanceRepository unmatchedEmpAttndRepo;

	@Override
	public ShiftResponseDto getShiftDetailsByEmpId(Integer empId) {
		ShiftResponseDto shiftResponseDto = new ShiftResponseDto();
		ShiftDetailsDto shiftDetailsDto = new ShiftDetailsDto();
		String shiftStartTime = null;
		String shiftEndTime = null;

		try {

			Shift shift = shiftRepository.getByEmpId(empId);
			Employee employee = employeeRepository.getByEmpId(empId);

			if(shift != null) {
				shiftStartTime = shift.getShiftStart().substring(5).trim().toUpperCase();
				shiftEndTime = shift.getShiftEnd().substring(5).trim().toUpperCase();

				if(shiftStartTime.equalsIgnoreCase("PM") && shiftEndTime.equalsIgnoreCase("AM")) {
					shiftDetailsDto.setShiftType("D1D2");
				} else {
					shiftDetailsDto.setShiftType("D1D1");
				}
				shiftDetailsDto.setShiftStartTime(shift.getShiftStart());
				shiftDetailsDto.setShiftEndTime(shift.getShiftEnd());
				shiftDetailsDto.setEmpId(employee.getEmpId());
				shiftResponseDto.setDetailsDto(shiftDetailsDto);
				shiftResponseDto.status = new Status(false, 200, "Success");
			} else {
				shiftResponseDto.status = new Status(true, 400, "Shift not found for this employee");
			}

		} catch (Exception e) {
			shiftResponseDto.status = new Status(true, 500, "Opps..! Something went wrong..");
		}

		return shiftResponseDto;
	}

	@Override
	public EmpSignOutResponse getOfficeSignOutDetailsByEmpId(Integer empId) {
		EmpSignOutResponse empSignOutResponse = new EmpSignOutResponse();
		EmpSignOutDto empSignOutDto = new EmpSignOutDto();
		UnmatchedEmployeeAttendance unmatchedEmployeeAttendance = new UnmatchedEmployeeAttendance();
		try {
			Employee employee = employeeRepository.getByEmpId(empId);
			List<Object[]> dateandHrs = employeeAttendanceRepository.getSignOutDetailsByEmpId(empId);
			for(Object[] p: dateandHrs) {

				if(p[0] == null && p[1] == null) {
					empSignOutDto.setFlag("NF");
					empSignOutDto.setEmpId(employee.getEmpId());
					empSignOutResponse.setEmpSignOutDto(empSignOutDto);
					empSignOutResponse.status = new Status(false, 400, "Not Found");

				} else if((p[0] != null && p[1] != null) && Integer.valueOf(p[1].toString()) < 24) {

					String pattern = "dd / MM / yyyy h:mm a";
					SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
					dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
					String date = dateFormat.format((Date)p[0]);

					empSignOutDto.setEmpId(employee.getEmpId());
					empSignOutDto.setAttendanceInDateTime(date);
					empSignOutDto.setFlag("INFND");
					empSignOutResponse.setEmpSignOutDto(empSignOutDto);
					empSignOutResponse.status = new Status(false, 200, "Success");

				} else {
					unmatchedEmployeeAttendance.setEmployee(employee);
					unmatchedEmployeeAttendance.setEmpAttendanceDate(new Date());
					unmatchedEmpAttndRepo.save(unmatchedEmployeeAttendance);
					empSignOutResponse.status = new Status(false, 400, "Un Matched attendance found, Please contact your manager to rectify the same.");
				}
			}
		} catch (Exception e) {
			empSignOutResponse.status = new Status(true, 500, "Opps..! Something went wrong..");
		}
		return empSignOutResponse;
	}

	@Override
	public Status saveSignIn(List<EmployeeAttendanceDto> employeeAttendanceDtos) {
		Status status = null;
		EmployeeAttendance employeeAttendance1 = new EmployeeAttendance();

		try {
			for(EmployeeAttendanceDto employeeAttendanceDto : employeeAttendanceDtos) {
				/*EmployeeAttendance employeeAttendance = employeeAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceInModeAndEmpAttendanceInDatetimeAndEmpAttendanceInLatLong
						(employeeAttendanceDto.getRefEmpId(), employeeAttendanceDto.getEmpAttendanceInMode(), employeeAttendanceDto.getEmpAttendanceInDatetime(), employeeAttendanceDto.getEmpAttendanceInLatLong());*/

				Employee employee = employeeRepository.getByEmpId(employeeAttendanceDto.getRefEmpId());
				employeeAttendance1 = employeeAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceDate(employeeAttendanceDto.getRefEmpId(), employeeAttendanceDto.getEmpAttendanceDate());

				if(employee != null) {
					if(employeeAttendance1 == null) {
						employeeAttendance1 = new EmployeeAttendance();
						employeeAttendance1.setEmpAttendanceDate(employeeAttendanceDto.getEmpAttendanceDate());
						employeeAttendance1.setEmployee(employee);
						employeeAttendance1.setEmpAttendanceInDatetime(employeeAttendanceDto.getEmpAttendanceInDatetime());
						employeeAttendance1.setEmpAttendanceConsiderInDatetime(employeeAttendanceDto.getEmpAttendanceInDatetime());
						employeeAttendance1.setEmpAttendanceInMode(employeeAttendanceDto.getEmpAttendanceInMode());
						employeeAttendance1.setEmpAttendanceInLatLong(employeeAttendanceDto.getEmpAttendanceInLatLong());
						employeeAttendance1.setEmpAttendanceInConfidence(employeeAttendanceDto.getEmpAttendanceInConfidence());

						employeeAttendanceRepository.save(employeeAttendance1);
						status = new Status(false, 200, "Successfully attendance marked");
					}else {
						employeeAttendance1.setEmpAttendanceDate(employeeAttendanceDto.getEmpAttendanceDate());
						employeeAttendance1.setEmployee(employee);
						employeeAttendance1.setEmpAttendanceInDatetime(employeeAttendanceDto.getEmpAttendanceInDatetime());
						employeeAttendance1.setEmpAttendanceConsiderInDatetime(employeeAttendanceDto.getEmpAttendanceInDatetime());
						employeeAttendance1.setEmpAttendanceInMode(employeeAttendanceDto.getEmpAttendanceInMode());
						employeeAttendance1.setEmpAttendanceInLatLong(employeeAttendanceDto.getEmpAttendanceInLatLong());
						employeeAttendance1.setEmpAttendanceInConfidence(employeeAttendanceDto.getEmpAttendanceInConfidence());

						employeeAttendanceRepository.save(employeeAttendance1);
						status = new Status(false, 200, "Successfully attendance marked");
					}
				} else {
					status = new Status(true, 400, "Employee not found");
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
		EmployeeAttendance  emAttendance = new  EmployeeAttendance();
		UnmatchedEmployeeAttendance unmatchedEmployeeAttendance = new UnmatchedEmployeeAttendance();
		try {
			for(EmployeeAttendanceDto employeeAttendanceDto : employeeAttendanceDtos) {
				EmployeeAttendance employeeAttendance = employeeAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceDateAndEmpAttendanceOutModeAndEmpAttendanceOutDatetimeAndEmpAttendanceOutLatLong
						(employeeAttendanceDto.getRefEmpId(), employeeAttendanceDto.getEmpAttendanceDate(),employeeAttendanceDto.getEmpAttendanceOutMode(), employeeAttendanceDto.getEmpAttendanceOutDatetime(), employeeAttendanceDto.getEmpAttendanceOutLatLong());

				Employee employee = employeeRepository.getByEmpId(employeeAttendanceDto.getRefEmpId());
				if(employee != null) {
					emAttendance = employeeAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceDate(employeeAttendanceDto.getRefEmpId(), employeeAttendanceDto.getEmpAttendanceDate());
					if(emAttendance != null) {
						if(employeeAttendance == null) {
							emAttendance.setEmpAttendanceOutDatetime(employeeAttendanceDto.getEmpAttendanceOutDatetime());
							emAttendance.setEmpAttendanceConsiderOutDatetime(employeeAttendanceDto.getEmpAttendanceOutDatetime());
							emAttendance.setEmpAttendanceOutMode(employeeAttendanceDto.getEmpAttendanceOutMode());
							emAttendance.setEmpAttendanceOutLatLong(employeeAttendanceDto.getEmpAttendanceOutLatLong());
							emAttendance.setEmpAttendanceOutConfidence(employeeAttendanceDto.getEmpAttendanceOutConfidence());
							employeeAttendanceRepository.save(emAttendance);
							status = new Status(false, 200, "Successfully attendance marked");
						} else {
							emAttendance.setEmpAttendanceOutDatetime(employeeAttendanceDto.getEmpAttendanceOutDatetime());
							emAttendance.setEmpAttendanceConsiderOutDatetime(employeeAttendanceDto.getEmpAttendanceOutDatetime());
							emAttendance.setEmpAttendanceOutMode(employeeAttendanceDto.getEmpAttendanceOutMode());
							emAttendance.setEmpAttendanceOutLatLong(employeeAttendanceDto.getEmpAttendanceOutLatLong());
							emAttendance.setEmpAttendanceOutConfidence(employeeAttendanceDto.getEmpAttendanceOutConfidence());
							employeeAttendanceRepository.save(emAttendance);
							status = new Status(false, 200, "Successfully attendance marked");
						}
					}else {
						unmatchedEmployeeAttendance.setEmployee(employee);
						unmatchedEmployeeAttendance.setEmpAttendanceDate(new Date());
						unmatchedEmployeeAttendance.setEmpAttendanceOutDatetime(employeeAttendanceDto.getEmpAttendanceOutDatetime());
						unmatchedEmployeeAttendance.setEmpAttendanceConsiderOutDatetime(employeeAttendanceDto.getEmpAttendanceOutDatetime());
						unmatchedEmployeeAttendance.setEmpAttendanceOutMode(employeeAttendanceDto.getEmpAttendanceOutMode());
						unmatchedEmployeeAttendance.setEmpAttendanceOutLatLong(employeeAttendanceDto.getEmpAttendanceOutLatLong());
						unmatchedEmployeeAttendance.setEmpAttendanceOutConfidence(employeeAttendanceDto.getEmpAttendanceOutConfidence());
						unmatchedEmpAttndRepo.save(unmatchedEmployeeAttendance);
						status = new Status(false, 200, "Unmatched attendance found, Please contact your manager to rectify the same.");
					}
				}else {
					status = new Status(true, 400, "Employee not found");
				}
			}
		} catch (Exception e) {

			status = new Status(true, 500, "Opps..! Something went wrong..");
		}
		return status;
	}

	/*@Override
	public Status save(List<EmployeeAttendanceDto> employeeAttendanceDtos) {
		Status status = null;
		//String msg = "Successfully saved and already marked employee id : ";
		//String empId = "";

		try {

			for(EmployeeAttendanceDto employeeAttendanceDto : employeeAttendanceDtos) {

				EmployeeAttendance employeeAttendance = employeeAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceModeAndEmpAttendanceDatetimeAndEmpAttendanceLatitudeAndEmpAttendanceLongitude(employeeAttendanceDto.getRefEmpId(), 
						employeeAttendanceDto.getEmpAttendanceMode(), employeeAttendanceDto.getEmpAttendanceDatetime(), employeeAttendanceDto.getEmpAttendanceLatitude(), employeeAttendanceDto.getEmpAttendanceLongitude());

				Employee employee = employeeRepository.getByEmpId(employeeAttendanceDto.getRefEmpId());
				if(employee != null) {
					if(employeeAttendance == null) {

						EmployeeAttendance employeeAttendance1 = new EmployeeAttendance();
						employeeAttendance1.setEmployee(employee);
						employeeAttendance1.setEmpAttendanceMode(employeeAttendanceDto.getEmpAttendanceMode());
						Date date = employeeAttendanceDto.getEmpAttendanceDatetime();
						employeeAttendance1.setEmpAttendanceDatetime(date);
						employeeAttendance1.setEmpAttendanceConsiderDatetime(employeeAttendanceDto.getEmpAttendanceConsiderDatetime());
						employeeAttendance1.setEmpAttendanceConfidence(employeeAttendanceDto.getEmpAttendanceConfidence());
						employeeAttendance1.setEmpAttendanceLatitude(employeeAttendanceDto.getEmpAttendanceLatitude());
						employeeAttendance1.setEmpAttendanceLongitude(employeeAttendanceDto.getEmpAttendanceLongitude());

						employeeAttendanceRepository.save(employeeAttendance1);
						status = new Status(false, 200, "Successfully attendance marked");

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
	} */


	public EmployeeAttendanceDto convertToEmployeeAttendanceDto(EmployeeAttendance employeeAttendance) {
		EmployeeAttendanceDto  employeeAttendanceDto = modelMapper.map(employeeAttendance, EmployeeAttendanceDto.class);
		return employeeAttendanceDto;
	}

	@Override
	public CurrentDateDto getCurrentDate() {
		CurrentDateDto currentDateDto = new CurrentDateDto();
		try {

			String pattern = "yyyy-MM-dd'T'HH:mm:ss";
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));


			Date date = new Date();
			String sysDate = dateFormat.format(date);
			// dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
			//TimeZone istTimeZone = TimeZone.getTimeZone("Asia/Kolkata");    //Source timezone
			//Calendar today = Calendar.getInstance(istTimeZone);
			//currentDateDto.setCurrentDate(dateFormat.format(today.getTime()));

			currentDateDto.setCurrentDate(sysDate);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return currentDateDto;
	}

}
