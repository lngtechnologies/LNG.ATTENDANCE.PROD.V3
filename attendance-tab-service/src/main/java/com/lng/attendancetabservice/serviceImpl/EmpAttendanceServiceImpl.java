package com.lng.attendancetabservice.serviceImpl;


import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancetabservice.entity.EmpAttendance;
import com.lng.attendancetabservice.entity.Employee;
import com.lng.attendancetabservice.entity.UnmatchedEmployeeAttendance;
import com.lng.attendancetabservice.repositories.EmpAttendanceRepository;
import com.lng.attendancetabservice.repositories.EmployeeRepository;
import com.lng.attendancetabservice.repositories.UnmatchedEmployeeAttendanceRepository;
import com.lng.attendancetabservice.service.EmpAttendanceService;
import com.lng.dto.tabService.EmpAttendanceDto1;

import status.Status;
@Service
public class EmpAttendanceServiceImpl implements EmpAttendanceService {

	ModelMapper modelMapper=new ModelMapper();

	@Autowired
	EmpAttendanceRepository empAttendanceRepository;
	@Autowired
	EmployeeRepository  employeeRepository;

	@Autowired
	UnmatchedEmployeeAttendanceRepository unmatchedEmpAttndRepo;	


	@Override
	public Status saveEmpAttndIn(EmpAttendanceDto1 empAttendanceDto1) {
		Status status = null;
		EmpAttendance empAttendance = new EmpAttendance();
		try {
			empAttendance = empAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceDate(empAttendanceDto1.getRefEmpId(), empAttendanceDto1.getEmpAttendanceDate());
			Employee employee = employeeRepository.findByempId(empAttendanceDto1.getRefEmpId());
			if(employee != null ) {
				if(empAttendance == null ) {
					empAttendance = new EmpAttendance();
					empAttendanceDto1.setFlag("IN");
					empAttendance.setEmpAttendanceDate(empAttendanceDto1.getEmpAttendanceDate());
					empAttendance.setEmployee(employee);
					empAttendance.setEmpAttendanceInDatetime(empAttendanceDto1.getEmpAttendanceInDatetime());
					empAttendance.setEmpAttendanceConsiderInDatetime(empAttendanceDto1.getEmpAttendanceConsiderInDatetime());
					if(empAttendanceDto1.getEmpAttendanceInMode() == null) {
						empAttendance.setEmpAttendanceInMode("T");
					}else {
						empAttendance.setEmpAttendanceInMode(empAttendanceDto1.getEmpAttendanceInMode());
					}

					if(empAttendanceDto1.getEmpAttendanceInLatLong() == null) {
						empAttendance.setEmpAttendanceInLatLong("00.0000, 00.0000");
					}else {
						empAttendance.setEmpAttendanceInLatLong(empAttendanceDto1.getEmpAttendanceInLatLong());
					}
					empAttendance.setEmpAttendanceInConfidence(empAttendanceDto1.getEmpAttendanceInConfidence());

					empAttendanceRepository.save(empAttendance);
					status = new Status(false, 200, "Attendance marked successfully");
				}else {
					empAttendanceDto1.setFlag("IN");
					empAttendance.setEmpAttendanceDate(empAttendanceDto1.getEmpAttendanceDate());
					empAttendance.setEmployee(employee);
					empAttendance.setEmpAttendanceInDatetime(empAttendanceDto1.getEmpAttendanceInDatetime());
					empAttendance.setEmpAttendanceConsiderInDatetime(empAttendanceDto1.getEmpAttendanceConsiderInDatetime());
					if(empAttendanceDto1.getEmpAttendanceInMode() == null) {
						empAttendance.setEmpAttendanceInMode("T");
					}else {
						empAttendance.setEmpAttendanceInMode(empAttendanceDto1.getEmpAttendanceInMode());
					}

					if(empAttendanceDto1.getEmpAttendanceInLatLong() == null) {
						empAttendance.setEmpAttendanceInLatLong("00.0000, 00.0000");
					}else {
						empAttendance.setEmpAttendanceInLatLong(empAttendanceDto1.getEmpAttendanceInLatLong());
					}

					empAttendance.setEmpAttendanceInConfidence(empAttendanceDto1.getEmpAttendanceInConfidence());

					empAttendanceRepository.save(empAttendance);
					status = new Status(false, 200, "Attendance marked successfully");
				}
			}else {
				status = new Status(false, 400, "Employee not found");
			}
		}catch(Exception e) {

			status = new Status(true, 500, "Opps..! Something went wrong..");
		}

		return status;
	}

	@Override
	public Status saveEmpAttndOut(EmpAttendanceDto1 empAttendanceDto1) {
		Status status = null;
		UnmatchedEmployeeAttendance unmatchedEmployeeAttendance = new UnmatchedEmployeeAttendance();
		EmpAttendance employeeAttendance1 = new EmpAttendance();
		try {
			EmpAttendance employeeAttendance = empAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceDateAndEmpAttendanceOutModeAndEmpAttendanceOutDatetimeAndEmpAttendanceOutLatLong
					(empAttendanceDto1.getRefEmpId(), empAttendanceDto1.getEmpAttendanceDate(),empAttendanceDto1.getEmpAttendanceOutMode(), empAttendanceDto1.getEmpAttendanceOutDatetime(), empAttendanceDto1.getEmpAttendanceOutLatLong());

			employeeAttendance1 = empAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceDate(empAttendanceDto1.getRefEmpId(),empAttendanceDto1.getEmpAttendanceDate());
			Employee employee = employeeRepository.findByempId(empAttendanceDto1.getRefEmpId());
			if(employee != null ) {
				if(employeeAttendance1 != null ) {
					if(employeeAttendance == null) {
						empAttendanceDto1.setFlag("OUT");
						employeeAttendance1.setEmpAttendanceOutDatetime(empAttendanceDto1.getEmpAttendanceOutDatetime());
						employeeAttendance1.setEmpAttendanceConsiderOutDatetime(empAttendanceDto1.getEmpAttendanceOutDatetime());

						if(empAttendanceDto1.getEmpAttendanceOutMode() == null) {
							employeeAttendance1.setEmpAttendanceOutMode("T");
						}else {
							employeeAttendance1.setEmpAttendanceOutMode(empAttendanceDto1.getEmpAttendanceOutMode());
						}

						if(empAttendanceDto1.getEmpAttendanceOutLatLong() == null) {
							employeeAttendance1.setEmpAttendanceOutLatLong("00.0000, 00.0000");
						}else {
							employeeAttendance1.setEmpAttendanceOutLatLong(empAttendanceDto1.getEmpAttendanceOutLatLong());
						}
						employeeAttendance1.setEmpAttendanceOutConfidence(empAttendanceDto1.getEmpAttendanceOutConfidence());
						empAttendanceRepository.save(employeeAttendance1);
						status = new Status(false, 200, "Attendance marked successfully");
					}else {
						empAttendanceDto1.setFlag("OUT");
						employeeAttendance1.setEmpAttendanceOutDatetime(empAttendanceDto1.getEmpAttendanceOutDatetime());
						employeeAttendance1.setEmpAttendanceConsiderOutDatetime(empAttendanceDto1.getEmpAttendanceOutDatetime());

						if(empAttendanceDto1.getEmpAttendanceOutMode() == null) {
							employeeAttendance1.setEmpAttendanceOutMode("T");
						}else {
							employeeAttendance1.setEmpAttendanceOutMode(empAttendanceDto1.getEmpAttendanceOutMode());
						}

						if(empAttendanceDto1.getEmpAttendanceOutLatLong() == null) {
							employeeAttendance1.setEmpAttendanceOutLatLong("00.0000, 00.0000");
						}else {
							employeeAttendance1.setEmpAttendanceOutLatLong(empAttendanceDto1.getEmpAttendanceOutLatLong());
						}

						employeeAttendance1.setEmpAttendanceOutConfidence(empAttendanceDto1.getEmpAttendanceOutConfidence());


						empAttendanceRepository.save(employeeAttendance1);
						status = new Status(false, 200, "Attendance marked successfully");
					}
				}else {
					unmatchedEmployeeAttendance.setEmployee(employee);
					unmatchedEmployeeAttendance.setEmpAttendanceDate(new Date());
					unmatchedEmployeeAttendance.setEmpAttendanceOutDatetime(empAttendanceDto1.getEmpAttendanceOutDatetime());
					unmatchedEmployeeAttendance.setEmpAttendanceConsiderOutDatetime(empAttendanceDto1.getEmpAttendanceOutDatetime());
					if(empAttendanceDto1.getEmpAttendanceOutMode() == null) {
						unmatchedEmployeeAttendance.setEmpAttendanceOutMode("T");
					}else {
						unmatchedEmployeeAttendance.setEmpAttendanceOutMode(empAttendanceDto1.getEmpAttendanceOutMode());
					}

					if(empAttendanceDto1.getEmpAttendanceOutLatLong() == null) {
						unmatchedEmployeeAttendance.setEmpAttendanceOutLatLong("00.0000, 00.0000");
					}else {
						unmatchedEmployeeAttendance.setEmpAttendanceOutLatLong(empAttendanceDto1.getEmpAttendanceOutLatLong());
					}
					unmatchedEmployeeAttendance.setEmpAttendanceOutConfidence(empAttendanceDto1.getEmpAttendanceOutConfidence());
					unmatchedEmpAttndRepo.save(unmatchedEmployeeAttendance);

					status = new Status(true, 400, "Un Matched attendance found, Please contact your manager to rectify the same.");
				}
			}else {
				status = new Status(false, 400, "Employee not found");
			}
		} catch (Exception e) {
			status = new Status(true, 500, "Opps..! Something went wrong..");
		}
		return status;
	}
}
