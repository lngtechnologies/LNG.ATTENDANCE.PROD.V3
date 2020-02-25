package com.lng.attendancecustomerservice.serviceImpl.reports;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.repositories.masters.CustEmployeeRepository;
import com.lng.attendancecustomerservice.service.reports.EmployeeSummaryService;
import com.lng.dto.reports.EmpTodaySummaryResponse;
import com.lng.dto.reports.EmployeeTodaysSummaryDto;

import status.Status;

@Service
public class EmployeeSummaryServiceImpl implements EmployeeSummaryService {

	@Autowired
	CustEmployeeRepository custEmployeeRepository;
	
	@Override
	public EmpTodaySummaryResponse getSummary(int custId, int empId, int loginId) {
		EmpTodaySummaryResponse empTodaySummaryResponse = new EmpTodaySummaryResponse();
		try {
			if(loginId == 0) {
				List<Object[]> summary = custEmployeeRepository.M_getEmployeeTodaysSummary(custId, empId);
				for(Object[] sm : summary) {
					EmployeeTodaysSummaryDto employeeTodaysSummaryDto = new EmployeeTodaysSummaryDto();
					employeeTodaysSummaryDto.setPresent(Integer.valueOf(sm[0].toString()));
					employeeTodaysSummaryDto.setAbsent(Integer.valueOf(sm[1].toString()));
					employeeTodaysSummaryDto.setLeave(Integer.valueOf(sm[2].toString()));
					empTodaySummaryResponse.setEmpSummary(employeeTodaysSummaryDto);
				}
				empTodaySummaryResponse.status = new Status(false, 200, "success");
			} else if(empId == 0) {
				List<Object[]> summary = custEmployeeRepository.W_getEmployeeTodaysSummary(custId, loginId);
				for(Object[] sm : summary) {
					EmployeeTodaysSummaryDto employeeTodaysSummaryDto = new EmployeeTodaysSummaryDto();
					employeeTodaysSummaryDto.setPresent(Integer.valueOf(sm[0].toString()));
					employeeTodaysSummaryDto.setAbsent(Integer.valueOf(sm[1].toString()));
					employeeTodaysSummaryDto.setLeave(Integer.valueOf(sm[2].toString()));
					empTodaySummaryResponse.setEmpSummary(employeeTodaysSummaryDto);
				}
				empTodaySummaryResponse.status = new Status(false, 200, "success");
			}
			
		} catch (Exception e) {
			empTodaySummaryResponse.status = new Status(false, 200, "Oops..! Something went wrong..");
		}
		return empTodaySummaryResponse;
	}
}
