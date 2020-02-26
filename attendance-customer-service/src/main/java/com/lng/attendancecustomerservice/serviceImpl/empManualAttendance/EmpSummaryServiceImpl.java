/*package com.lng.attendancecustomerservice.serviceImpl.empManualAttendance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.service.empManualAttendance.EmpSummaryService;
import com.lng.dto.employeeAttendance.EmpSummaryDto;
import com.lng.dto.employeeAttendance.EmpSummaryResponse;

import status.Status;
@Service
public class EmpSummaryServiceImpl implements EmpSummaryService {

	@Autowired
	EmployeeRepository  employeeRepository;

	@Override
	public EmpSummaryResponse getEmployeeDetails(EmpSummaryDto empSummaryDto) {
		EmpSummaryResponse empSummaryResponse = new EmpSummaryResponse();
		try {
			if(empSummaryDto.getEmpId() != 0) {
				List<Object[]> employee = employeeRepository.getReportsByLogin_LoginIdAndCustomer_custId(empSummaryDto.getLoginId(),empSummaryDto.getCustId());
				if(!employee.isEmpty()) {
					for(Object[] c: employee) {
						empSummaryDto.setPresent(Integer.valueOf(c[0].toString()));
						empSummaryDto.setAbsent(Integer.valueOf(c[1].toString()));
						empSummaryDto.setTotalLeave(Integer.valueOf(c[2].toString()));
					}
					empSummaryResponse.setEmpSummaryDetails(empSummaryDto);
					empSummaryResponse.status = new Status(false, 200, "success");

				} else {
					empSummaryResponse.status = new Status(false, 400, "Not found");
				}
			}else {
				List<Object[]> employee = employeeRepository.getReportsForAdminByCustomer_custIdAndLogin_loginId(empSummaryDto.getCustId(),empSummaryDto.getLoginId());
				if(!employee.isEmpty()) {
					for(Object[] a : employee){
						empSummaryDto.setPresent(Integer.valueOf(a[0].toString()));
						empSummaryDto.setAbsent(Integer.valueOf(a[1].toString()));
						empSummaryDto.setTotalLeave(Integer.valueOf(a[2].toString()));
					}
					empSummaryResponse.setEmpSummaryDetails(empSummaryDto);
					empSummaryResponse.status = new Status(false, 200, "success");

				} else {
					empSummaryResponse.status = new Status(false, 400, "Not found");
				}
			}
		}catch(Exception e) {
			empSummaryResponse.status = new Status(true, 400, "Opps..! Something went wrong..");

		}
		return empSummaryResponse;
	}

}*/
