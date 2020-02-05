package com.lng.attendancetabservice.serviceImpl;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancetabservice.entity.EmpAttendance;
import com.lng.attendancetabservice.entity.Employee;
import com.lng.attendancetabservice.repositories.EmpAttendanceRepository;
import com.lng.attendancetabservice.repositories.EmployeeRepository;
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
	//private final Lock displayQueueLock = new ReentrantLock();

	@Override
	public Status saveEmpAttndIn(EmpAttendanceDto1 empAttendanceDto1) {
		Status status = null;
		EmpAttendance empAttendance = new EmpAttendance();
		try {
			Employee employee = employeeRepository.findByempId(empAttendanceDto1.getRefEmpId());
			if(employee != null ) {
				empAttendance = empAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceDate(empAttendanceDto1.getRefEmpId(), empAttendanceDto1.getEmpAttendanceDate());
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
		EmpAttendance employeeAttendance1 = new EmpAttendance();
		try {
			Employee employee = employeeRepository.findByempId(empAttendanceDto1.getRefEmpId());
			if(employee != null ) {
				employeeAttendance1 = empAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceDate(empAttendanceDto1.getRefEmpId(),empAttendanceDto1.getEmpAttendanceDate());
				if(employeeAttendance1 != null ) {
					EmpAttendance employeeAttendance = empAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceDateAndEmpAttendanceOutModeAndEmpAttendanceOutDatetimeAndEmpAttendanceOutLatLong
							(empAttendanceDto1.getRefEmpId(), empAttendanceDto1.getEmpAttendanceDate(),empAttendanceDto1.getEmpAttendanceOutMode(), empAttendanceDto1.getEmpAttendanceOutDatetime(), empAttendanceDto1.getEmpAttendanceOutLatLong());
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
					employeeAttendance1 = new EmpAttendance();
					empAttendanceDto1.setFlag("OUT");
					employeeAttendance1.setEmpAttendanceDate(empAttendanceDto1.getEmpAttendanceDate());
					employeeAttendance1.setEmployee(employee);
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
				status = new Status(false, 400, "Employee not found");
			}
		} catch (Exception e) {
			status = new Status(true, 500, "Opps..! Something went wrong..");
		}
		return status;
	}

	/*@Override
	public Status saveEmployeeAttnd(EmpAttendanceDto1 empAttendanceDto1) {
		final Lock displayLock = this.displayQueueLock;
		Status status = null;
		EmpAttendance empAttendance = new EmpAttendance();
		try {
			displayLock.lock();
			Employee employee = employeeRepository.findByempId(empAttendanceDto1.getRefEmpId());
			if(employee != null) {
				EmpAttendance empAttendance1 = empAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceDate(empAttendanceDto1.getRefEmpId(),empAttendanceDto1.getEmpAttendanceDate());
				if(empAttendance1 == null) {
					empAttendance = new EmpAttendance();
					empAttendance.setEmpAttendanceDate(empAttendanceDto1.getEmpAttendanceDate());
					empAttendance.setEmployee(employee);

					if(empAttendanceDto1.getEmpAttendanceInDatetime() != null) {
						empAttendance.setEmpAttendanceInDatetime(empAttendanceDto1.getEmpAttendanceInDatetime());
						empAttendance.setEmpAttendanceConsiderInDatetime(empAttendanceDto1.getEmpAttendanceInDatetime());
					}

					if(empAttendanceDto1.getEmpAttendanceInMode() == null) {
						empAttendance.setEmpAttendanceInMode("T");
					}else {
						empAttendance.setEmpAttendanceInMode(empAttendanceDto1.getEmpAttendanceInMode());
					}
					if(empAttendanceDto1.getEmpAttendanceInLatLong() != null) {
						empAttendance.setEmpAttendanceInLatLong(empAttendanceDto1.getEmpAttendanceInLatLong());
					}
					if(empAttendanceDto1.getEmpAttendanceInConfidence() != null) {

						empAttendance.setEmpAttendanceInConfidence(empAttendanceDto1.getEmpAttendanceInConfidence());
					}
					if(empAttendanceDto1.getEmpAttendanceOutDatetime() != null) {
						empAttendance.setEmpAttendanceOutDatetime(empAttendanceDto1.getEmpAttendanceOutDatetime());
						empAttendance.setEmpAttendanceConsiderOutDatetime(empAttendanceDto1.getEmpAttendanceOutDatetime());
					}

					if(empAttendanceDto1.getEmpAttendanceOutMode() == null) {
						empAttendance.setEmpAttendanceOutMode("T");
					}else {
						empAttendance.setEmpAttendanceOutMode(empAttendanceDto1.getEmpAttendanceOutMode());
					}
					if(empAttendanceDto1.getEmpAttendanceOutLatLong() != null) {
						empAttendance.setEmpAttendanceOutLatLong(empAttendanceDto1.getEmpAttendanceOutLatLong());
					}
					if(empAttendanceDto1.getEmpAttendanceOutConfidence() != null) {

						empAttendance.setEmpAttendanceOutConfidence(empAttendanceDto1.getEmpAttendanceOutConfidence());
					}

					empAttendanceRepository.save(empAttendance);
					status = new Status(false, 200, "Attendance marked successfully");

				} else {
					empAttendance1.setEmpAttendanceDate(empAttendanceDto1.getEmpAttendanceDate());
					empAttendance1.setEmployee(employee);

					if(empAttendanceDto1.getEmpAttendanceInDatetime() != null) {
						empAttendance1.setEmpAttendanceInDatetime(empAttendanceDto1.getEmpAttendanceInDatetime());
						empAttendance1.setEmpAttendanceConsiderInDatetime(empAttendanceDto1.getEmpAttendanceInDatetime());
					}

					if(empAttendanceDto1.getEmpAttendanceInMode() == null) {
						empAttendance1.setEmpAttendanceInMode("T");
					}else {
						empAttendance1.setEmpAttendanceInMode(empAttendanceDto1.getEmpAttendanceInMode());
					}
					if(empAttendanceDto1.getEmpAttendanceInLatLong() != null) {
						empAttendance1.setEmpAttendanceInLatLong(empAttendanceDto1.getEmpAttendanceInLatLong());
					}
					if(empAttendanceDto1.getEmpAttendanceInConfidence() != null) {

						empAttendance1.setEmpAttendanceInConfidence(empAttendanceDto1.getEmpAttendanceInConfidence());
					}
					if(empAttendanceDto1.getEmpAttendanceOutDatetime() != null) {
						empAttendance1.setEmpAttendanceOutDatetime(empAttendanceDto1.getEmpAttendanceOutDatetime());
						empAttendance1.setEmpAttendanceConsiderOutDatetime(empAttendanceDto1.getEmpAttendanceOutDatetime());
					}

					if(empAttendanceDto1.getEmpAttendanceOutMode() == null) {
						empAttendance1.setEmpAttendanceOutMode("T");
					}else {
						empAttendance1.setEmpAttendanceOutMode(empAttendanceDto1.getEmpAttendanceOutMode());
					}
					if(empAttendanceDto1.getEmpAttendanceOutLatLong() != null) {
						empAttendance1.setEmpAttendanceOutLatLong(empAttendanceDto1.getEmpAttendanceOutLatLong());
					}
					if(empAttendanceDto1.getEmpAttendanceOutConfidence() != null) {

						empAttendance1.setEmpAttendanceOutConfidence(empAttendanceDto1.getEmpAttendanceOutConfidence());
					}
					empAttendanceRepository.save(empAttendance1);
					status = new Status(false, 200, "Attendance marked successfully");
				}
			}else {
				status = new Status(false, 400, "Employee not found");

			}

		} catch (Exception e) {

			status = new Status(true, 500,"Opps..! Something went wrong..");

		}
		finally {
			displayLock.unlock();
		}
		return status;
	}*/
}
