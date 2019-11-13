package com.lng.attendancecustomerservice.serviceImpl.employeeAttendance;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.employeeAttendance.EmployeeAttendance;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.employeeAttendance.EmployeeAttendanceRepository;
import com.lng.attendancecustomerservice.service.employeeAttendance.EmployeeAttendanceService;
import com.lng.dto.employeeAttendance.EmployeeAttendanceDto;

import status.Status;

@Service
public class EmployeeAttendanceServiceImpl implements EmployeeAttendanceService {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	EmployeeAttendanceRepository employeeAttendanceRepository;

	@Override
	public Status save(List<EmployeeAttendanceDto> employeeAttendanceDtos) {
		Status status = null;
		//String msg = "Successfully saved and already marked employee id : ";
		//String empId = "";

		try {

			for(EmployeeAttendanceDto employeeAttendanceDto : employeeAttendanceDtos) {

				EmployeeAttendance employeeAttendance = employeeAttendanceRepository.findByEmployee_EmpIdAndEmpAttendanceModeAndEmpAttendanceDatetimeAndEmpAttendanceLatLong(employeeAttendanceDto.getRefEmpId(), 
						employeeAttendanceDto.getEmpAttendanceMode(), employeeAttendanceDto.getEmpAttendanceDatetime(), employeeAttendanceDto.getEmpAttendanceLatLong());

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
						employeeAttendance1.setEmpAttendanceLatLong(employeeAttendanceDto.getEmpAttendanceLatLong());
					
						employeeAttendanceRepository.save(employeeAttendance1);
						status = new Status(false, 200, "Successfully attendance marked");
						
					} else {
						status = new Status(false, 200, "Successfully attendance marked");
					} 

				}else {
					status = new Status(true, 400, "Employye not found");
				}
			}
			
		} catch (Exception e) {
			status = new Status(true, 500, "Opps..! Something went wrong..");
		}
		return status;
	}

	public EmployeeAttendanceDto convertToEmployeeAttendanceDto(EmployeeAttendance employeeAttendance) {
		EmployeeAttendanceDto  employeeAttendanceDto = modelMapper.map(employeeAttendance, EmployeeAttendanceDto.class);
		return employeeAttendanceDto;
	}


}
