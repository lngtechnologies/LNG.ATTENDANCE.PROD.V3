package com.lng.attendancecustomerservice.serviceImpl.empManualAttendance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.empManualAttendance.EmpManualAttendance;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.empManualAttendance.EmpManualAttendanceRepository;
import com.lng.attendancecustomerservice.service.empManualAttendance.EmpManualAttendanceService;
import com.lng.dto.empAttendance.EmpAttendanceDto;
import com.lng.dto.empAttendance.EmpAttendanceParamDto;
import com.lng.dto.empAttendance.EmpAttendanceResponse;

import status.Status;

@Service
public class EmpManualAttendanceServiceImpl implements EmpManualAttendanceService {

	ModelMapper modelMapper=new ModelMapper();

	@Autowired
	EmpManualAttendanceRepository empAttendanceRepository;
	@Autowired
	EmployeeRepository employeeRepository;


	@Override
	public EmpAttendanceResponse getEmpAttendanceByDepartment_deptIdAndEmpAttendanceDatetime(Integer deptId,
			Date empAttendanceDatetime) {
		EmpAttendanceResponse empAttendanceResponse = new EmpAttendanceResponse();
		List<EmpAttendanceParamDto> empAttendanceDtoList = new ArrayList<>();

		try {
			List<Object[]> empAttendance = empAttendanceRepository
					.findEmpAttendanceByDepartment_deptIdAndEmpAttendanceDatetime(deptId, empAttendanceDatetime);
			if (empAttendance.isEmpty()) {
				empAttendanceResponse.status = new Status(true, 400, "Emp Attendance Not Found");
			} else {
				for (Object[] p : empAttendance) {

					EmpAttendanceParamDto EmpAttendanceDto1 = new EmpAttendanceParamDto();
					EmpAttendanceDto1.setRefEmpId(Integer.valueOf(p[0].toString()));
					EmpAttendanceDto1.setEmpName((p[1].toString()));
					EmpAttendanceDto1.setShiftStart((p[2].toString()));
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
	public EmpAttendanceResponse saveEmpAttendance(List<EmpAttendanceDto> empAttendanceDtos) {
		EmpAttendanceResponse empAttendanceResponse = new EmpAttendanceResponse();
		
		//List<EmpAttendanceDto> empAttendanceDto1 = new ArrayList<>();
		/*
		 * String msg = "Successsfully Saved And Already Marked Employee Id:"; String
		 * empId = "";
		 */
		// Date date = null;
		try {
			for (EmpAttendanceDto empAttendanceDto : empAttendanceDtos ) {
				int recordCount = empAttendanceRepository.checkEmpManualAttnd(
						empAttendanceDto.getRefEmpId(), empAttendanceDto.getShiftStart(),
						empAttendanceDto.getEmpAttendanceDatetime());

				if (recordCount <= 0) {
					Employee employee  = employeeRepository.getEmployeeByEmpId(empAttendanceDto.getRefEmpId());
					if(employee != null) {
						EmpManualAttendance empAttendance  = new EmpManualAttendance();
						empAttendance.setEmployee(employee);
						empAttendance.setEmpAttendanceMode(empAttendanceDto.getEmpAttendanceMode());
						empAttendance.setEmpAttendanceDatetime(empAttendanceDto.getEmpAttendanceDatetime());
						empAttendance.setEmpAttendanceConsiderDatetime(empAttendanceDto.getEmpAttendanceConsiderDatetime());
						empAttendance.setEmpAttendanceConfidence(empAttendanceDto. getEmpAttendanceConfidence());
						empAttendance.setEmpAttendanceLatLong(empAttendanceDto.getEmpAttendanceLatLong());
						empAttendance.setEmpAttendanceWithinBeacon(true);
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
		}


}
