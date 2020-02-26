package com.lng.attendancecustomerservice.serviceImpl.reports;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.repositories.masters.CustEmployeeRepository;
import com.lng.attendancecustomerservice.service.reports.EmployeeSummaryService;
import com.lng.dto.reports.EmpTodaySummaryResponse;
import com.lng.dto.reports.EmpTodaysEarlyLeaversDto;
import com.lng.dto.reports.EmpTodaysLateComersDto;
import com.lng.dto.reports.EmpTodaysLeaveSummaryDto;
import com.lng.dto.reports.EmpTodaysLeaveSummaryResponse;
import com.lng.dto.reports.EmployeeTodaysSummaryDto;
import com.lng.dto.reports.TodaysLateComersAndEarlyLeaversResponse;

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

	@Override
	public EmpTodaysLeaveSummaryResponse getLeaveSummary(int custId, int empId, int loginId) {
		EmpTodaysLeaveSummaryResponse response = new EmpTodaysLeaveSummaryResponse();
		try {
			if(loginId == 0) {
				List<Object[]> leaveSummary = custEmployeeRepository.M_getEmployeeTodaysLeaveSummary(custId, empId);
				for(Object[] ls: leaveSummary) {
					EmpTodaysLeaveSummaryDto dto = new EmpTodaysLeaveSummaryDto();
					dto.setApproved(Integer.valueOf(ls[0].toString()));
					dto.setRejected(Integer.valueOf(ls[1].toString()));
					response.setTodaysLeaveSummary(dto);
				}
				response.status = new Status(false, 200, "success");
			} else if(empId == 0) {
				List<Object[]> leaveSummary = custEmployeeRepository.W_getEmployeeTodaysLeaveSummary(custId, loginId);
				for(Object[] ls: leaveSummary) {
					EmpTodaysLeaveSummaryDto dto = new EmpTodaysLeaveSummaryDto();
					dto.setApproved(Integer.valueOf(ls[0].toString()));
					dto.setRejected(Integer.valueOf(ls[1].toString()));
					response.setTodaysLeaveSummary(dto);
				}
				response.status = new Status(false, 200, "success");
			}
		} catch (Exception e) {
			response.status = new Status(false, 200, "Oops..! Something went wrong..");
		}
		return response;
	}

	@Override
	public TodaysLateComersAndEarlyLeaversResponse getLateComersAndEarlyLeavers(int custId, int empId, int loginId) {
		TodaysLateComersAndEarlyLeaversResponse response = new TodaysLateComersAndEarlyLeaversResponse();
		
		try {
			if(loginId == 0) {
				List<EmpTodaysLateComersDto> lateComersList = new ArrayList<EmpTodaysLateComersDto>();
				List<EmpTodaysEarlyLeaversDto> earlyLeaversList = new ArrayList<EmpTodaysEarlyLeaversDto>(); 
				List<Object[]> lateComers = custEmployeeRepository.getLateComers(custId, empId);
				List<Object[]> earlyLeavers = custEmployeeRepository.getEarlyLeavers(custId, empId);
				if(lateComers != null) {
					for(Object[] lc: lateComers) {
						EmpTodaysLateComersDto dto = new EmpTodaysLateComersDto();
						dto.setEmpId(Integer.valueOf(lc[0].toString()));
						dto.setEmpName(lc[1].toString());
						dto.setShiftStart(lc[2].toString());
						if(lc[3].toString() != null) {
							dto.setAttndInDateTime(lc[3].toString());
						} else {
							dto.setAttndInDateTime("NA");
						}
						lateComersList.add(dto);
					}
					response.setLateComers(lateComersList);
				} else {
					response.status = new Status(false, 200, "Late commers not found");
				}
				if(earlyLeavers != null) {
					for(Object[] el: earlyLeavers) {
						EmpTodaysEarlyLeaversDto dto = new EmpTodaysEarlyLeaversDto();
						dto.setEmpId(Integer.valueOf(el[0].toString()));
						dto.setEmpName(el[1].toString());
						dto.setShiftEnd(el[2].toString());
						if(el[3].toString() != null) {
							dto.setAttndOutDateTime(el[3].toString());
						} else {
							dto.setAttndOutDateTime("NA");
						}
						earlyLeaversList.add(dto);
					}
					response.setEarlyLeavers(earlyLeaversList);
				} else {
					response.status = new Status(false, 200, "Early leavers not found");
				}
			} else if(empId == 0) {
				List<EmpTodaysLateComersDto> lateComersList = new ArrayList<EmpTodaysLateComersDto>();
				List<EmpTodaysEarlyLeaversDto> earlyLeaversList = new ArrayList<EmpTodaysEarlyLeaversDto>(); 
				List<Object[]> lateComers = custEmployeeRepository.getLateComersForAdmin(custId, loginId);
				List<Object[]> earlyLeavers = custEmployeeRepository.getEarlyLeaversForAdmin(custId, loginId);
				if(lateComers != null) {
					for(Object[] lc: lateComers) {
						EmpTodaysLateComersDto dto = new EmpTodaysLateComersDto();
						dto.setEmpId(Integer.valueOf(lc[0].toString()));
						dto.setEmpName(lc[1].toString());
						dto.setShiftStart(lc[2].toString());
						if(lc[3].toString() != null) {
							dto.setAttndInDateTime(lc[3].toString());
						} else {
							dto.setAttndInDateTime("NA");
						}
						lateComersList.add(dto);
					}
					response.setLateComers(lateComersList);
				} else {
					response.status = new Status(false, 200, "Late commers not found");
				}
				if(earlyLeavers != null) {
					for(Object[] el: earlyLeavers) {
						EmpTodaysEarlyLeaversDto dto = new EmpTodaysEarlyLeaversDto();
						dto.setEmpId(Integer.valueOf(el[0].toString()));
						dto.setEmpName(el[1].toString());
						dto.setShiftEnd(el[2].toString());
						if(el[3].toString() != null) {
							dto.setAttndOutDateTime(el[3].toString());
						} else {
							dto.setAttndOutDateTime("NA");
						}
						earlyLeaversList.add(dto);
					}
					response.setEarlyLeavers(earlyLeaversList);
				} else {
					response.status = new Status(false, 200, "Early leavers not found");
				}
			}
			response.status = new Status(false, 200, "success");
		} catch (Exception e) {
			response.status = new Status(false, 200, "Oops..! Something went wrong..");
		}
		return response;
	}
}
