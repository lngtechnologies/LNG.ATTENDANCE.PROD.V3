package com.lng.attendancecustomerservice.serviceImpl.reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.authentication.Login;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.reports.EmpEarlyLeaversAndLateComersResponse;
import com.lng.attendancecustomerservice.entity.reports.EmpEarlyLeaversDto;
import com.lng.attendancecustomerservice.entity.reports.EmpLateComersDto;
import com.lng.attendancecustomerservice.entity.reports.EmpLeaveReportDto;
import com.lng.attendancecustomerservice.entity.reports.EmpLeaveReportResponse;
import com.lng.attendancecustomerservice.entity.reports.EmpOfficeOutDto;
import com.lng.attendancecustomerservice.entity.reports.EmpOfficeOutResponse;
import com.lng.attendancecustomerservice.entity.reports.EmpReportByReportTypeDto;
import com.lng.attendancecustomerservice.entity.reports.EmpReportByReportTypeResponse;
import com.lng.attendancecustomerservice.entity.reports.EmployeeSummaryParamDto;
import com.lng.attendancecustomerservice.entity.reports.MasterDataDto;
import com.lng.attendancecustomerservice.entity.reports.SummaryDetailsResponse;
import com.lng.attendancecustomerservice.repositories.authentication.ILoginRepository;
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

	@Autowired
	ILoginRepository iLoginRepository;

	@Override
	public EmpTodaySummaryResponse getSummary(int custId, int empId, int loginId) {
		EmpTodaySummaryResponse empTodaySummaryResponse = new EmpTodaySummaryResponse();
		try {
			if(loginId == 0) {
				List<Object[]> summary = custEmployeeRepository.M_getEmployeeTodaysSummary(custId, empId);
				if(summary != null) {
					for(Object[] sm : summary) {
						EmployeeTodaysSummaryDto employeeTodaysSummaryDto = new EmployeeTodaysSummaryDto();
						employeeTodaysSummaryDto.setPresent(Integer.valueOf(sm[0].toString()));
						employeeTodaysSummaryDto.setAbsent(Integer.valueOf(sm[1].toString()));
						employeeTodaysSummaryDto.setApprovedLeave(Integer.valueOf(sm[2].toString()));
						employeeTodaysSummaryDto.setPendingLeaves(Integer.valueOf(sm[3].toString()));
						empTodaySummaryResponse.setEmpSummary(employeeTodaysSummaryDto);
					}
				} else {
					empTodaySummaryResponse.status = new Status(false, 400, "Not found");
				}

				empTodaySummaryResponse.status = new Status(false, 200, "success");
			} else if(empId == 0) {
				Integer iLoginId = 0;
				if(loginId == 1) {
					Login login = iLoginRepository.findByCustomet_CustId(custId);
					iLoginId = login.getLoginId();
				} else {
					iLoginId = loginId;
				}

				List<Object[]> summary = custEmployeeRepository.W_getEmployeeTodaysSummary(custId, iLoginId);
				if(summary != null) {
					for(Object[] sm : summary) {
						EmployeeTodaysSummaryDto employeeTodaysSummaryDto = new EmployeeTodaysSummaryDto();
						employeeTodaysSummaryDto.setPresent(Integer.valueOf(sm[0].toString()));
						employeeTodaysSummaryDto.setAbsent(Integer.valueOf(sm[1].toString()));
						employeeTodaysSummaryDto.setApprovedLeave(Integer.valueOf(sm[2].toString()));
						employeeTodaysSummaryDto.setPendingLeaves(Integer.valueOf(sm[3].toString()));
						empTodaySummaryResponse.setEmpSummary(employeeTodaysSummaryDto);
					}
				} else {
					empTodaySummaryResponse.status = new Status(false, 400, "Not found");
				}
				empTodaySummaryResponse.status = new Status(false, 200, "success");
			}

		} catch (Exception e) {
			empTodaySummaryResponse.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return empTodaySummaryResponse;
	}

	@Override
	public EmpTodaysLeaveSummaryResponse getLeaveSummary(int custId, int empId, int loginId) {
		EmpTodaysLeaveSummaryResponse response = new EmpTodaysLeaveSummaryResponse();
		try {
			if(loginId == 0) {
				List<Object[]> leaveSummary = custEmployeeRepository.M_getEmployeeTodaysLeaveSummary(custId, empId);
				if(leaveSummary != null) {
					for(Object[] ls: leaveSummary) {
						EmpTodaysLeaveSummaryDto dto = new EmpTodaysLeaveSummaryDto();
						dto.setApproved(Integer.valueOf(ls[0].toString()));
						dto.setRejected(Integer.valueOf(ls[1].toString()));
						response.setTodaysLeaveSummary(dto);
					}
				} else {
					response.status = new Status(false, 400, "Not found");
				}

				response.status = new Status(false, 200, "success");
			} else if(empId == 0) {
				Integer iLoginId = 0;
				if(loginId == 1) {
					Login login = iLoginRepository.findByCustomet_CustId(custId);
					iLoginId = login.getLoginId();
				} else {
					iLoginId = loginId;
				}
				List<Object[]> leaveSummary = custEmployeeRepository.W_getEmployeeTodaysLeaveSummary(custId, iLoginId);
				if(leaveSummary != null) {
					for(Object[] ls: leaveSummary) {
						EmpTodaysLeaveSummaryDto dto = new EmpTodaysLeaveSummaryDto();
						dto.setApproved(Integer.valueOf(ls[0].toString()));
						dto.setRejected(Integer.valueOf(ls[1].toString()));
						response.setTodaysLeaveSummary(dto);
					}
				} else {
					response.status = new Status(false, 400, "Not found");
				}
				response.status = new Status(false, 200, "success");
			}
		} catch (Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong..");
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
					response.status = new Status(false, 400, "Late commers not found");
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
					response.status = new Status(false, 400, "Early leavers not found");
				}
			} else if(empId == 0) {
				Integer iLoginId = 0;
				if(loginId == 1) {
					Login login = iLoginRepository.findByCustomet_CustId(custId);
					iLoginId = login.getLoginId();
				} else {
					iLoginId = loginId;
				}
				List<EmpTodaysLateComersDto> lateComersList = new ArrayList<EmpTodaysLateComersDto>();
				List<EmpTodaysEarlyLeaversDto> earlyLeaversList = new ArrayList<EmpTodaysEarlyLeaversDto>(); 
				List<Object[]> lateComers = custEmployeeRepository.getLateComersForAdmin(custId, iLoginId);
				List<Object[]> earlyLeavers = custEmployeeRepository.getEarlyLeaversForAdmin(custId, iLoginId);
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
					response.status = new Status(false, 400, "Late commers not found");
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
					response.status = new Status(false, 400, "Early leavers not found");
				}
			}
			response.status = new Status(false, 200, "success");
		} catch (Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return response;
	}

	@Override
	public EmpReportByReportTypeResponse getReportByReportType(int custId, int brId, int deptId, String reportType) {
		EmpReportByReportTypeResponse response = new EmpReportByReportTypeResponse();
		List<EmpReportByReportTypeDto> reportList = new ArrayList<EmpReportByReportTypeDto>();

		try {
			List<Object[]> master = custEmployeeRepository.GetEmployeMasterData(brId, deptId);
			if(!master.isEmpty()) {
				for(Object[] ms: master) {
					MasterDataDto masterData = new MasterDataDto();
					masterData.setCustName(ms[0].toString());
					masterData.setCustAddress(ms[1].toString());
					masterData.setBrName(ms[2].toString());
					masterData.setBrAddress(ms[3].toString());
					masterData.setDeptName(ms[4].toString());
					String pattern = "yyyy-MM-dd";
					SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
					dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
					Date date = new Date();
					String sysDate = dateFormat.format(date);
					masterData.setDate(sysDate);
					response.setMasterData(masterData);
				}
			}

			List<Object[]> reports = custEmployeeRepository.getReportByReportType(custId, brId, deptId, reportType, new Date(),new Date());
			if(!reports.isEmpty() && reports != null) {
				for(Object[] rt: reports) {
					EmpReportByReportTypeDto dto = new EmpReportByReportTypeDto();
					dto.setEmpName(rt[0].toString());
					dto.setDeptName(rt[1].toString());
					dto.setBranchName(rt[2].toString());
					dto.setBlockName(rt[3].toString());
					dto.setDesignationName(rt[4].toString());
					dto.setShiftName(rt[5].toString());
					reportList.add(dto);
				}
				response.setReportDto(reportList);
				response.status = new Status(false, 200, "Success");
			} else {
				response.status = new Status(false, 400, "Not found");
			}
		} catch (Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return response;
	}

	@Override
	public EmpEarlyLeaversAndLateComersResponse getEarlyLeaversAndLateComers(int brId, int deptId, String reportType, Date fromDate, Date todate) {
		EmpEarlyLeaversAndLateComersResponse response = new EmpEarlyLeaversAndLateComersResponse();
		try {
			if(reportType.trim().equals("earlyLeavers")) {
				List<EmpEarlyLeaversDto> earlyLeavers = new ArrayList<EmpEarlyLeaversDto>();
				List<Object[]> master = custEmployeeRepository.GetEmployeMasterData(brId, deptId);
				if(!master.isEmpty()) {
					for(Object[] ms: master) {
						MasterDataDto masterData = new MasterDataDto();
						masterData.setCustName(ms[0].toString());
						masterData.setCustAddress(ms[1].toString());
						masterData.setBrName(ms[2].toString());
						masterData.setBrAddress(ms[3].toString());
						masterData.setDeptName(ms[4].toString());
						String pattern = "yyyy-MM-dd";
						SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
						dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
						Date date = new Date();
						String sysDate = dateFormat.format(date);
						masterData.setDate(sysDate);
						masterData.setFromDate(fromDate);
						masterData.setToDate(todate);
						response.setMasterData(masterData);
					}
				}
				List<Object[]> earlyLeaversList = custEmployeeRepository.getReportByReportType(0, brId, deptId, reportType, fromDate, todate);
				if(!earlyLeaversList.isEmpty()) {
					for(Object[] el: earlyLeaversList) {
						EmpEarlyLeaversDto dto = new EmpEarlyLeaversDto();
						dto.setEmpName(el[0].toString());
						dto.setDeptName(el[1].toString());
						dto.setBranchName(el[2].toString());
						dto.setBlockName(el[3].toString());
						dto.setDesignationName(el[4].toString());
						dto.setShiftName(el[5].toString());
						dto.setShiftEnd(el[6].toString());
						dto.setOutTime(el[7].toString());
						dto.setDiff(el[8].toString());
						earlyLeavers.add(dto);
					}
					response.setEarlyLeavers(earlyLeavers);
					response.status = new Status(false, 200, "Success");
				} else {
					response.status = new Status(false, 400, "Not found");
				}

			} else if(reportType.trim().equals("lateComers")) {
				List<EmpLateComersDto> lateComers = new ArrayList<EmpLateComersDto>();
				List<Object[]> master = custEmployeeRepository.GetEmployeMasterData(brId, deptId);
				if(!master.isEmpty()) {
					for(Object[] ms: master) {
						MasterDataDto masterData = new MasterDataDto();
						masterData.setCustName(ms[0].toString());
						masterData.setCustAddress(ms[1].toString());
						masterData.setBrName(ms[2].toString());
						masterData.setBrAddress(ms[3].toString());
						masterData.setDeptName(ms[4].toString());
						String pattern = "yyyy-MM-dd";
						SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
						dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
						Date date = new Date();
						String sysDate = dateFormat.format(date);
						masterData.setDate(sysDate);
						masterData.setFromDate(fromDate);
						masterData.setToDate(todate);
						response.setMasterData(masterData);
					}
				}
				List<Object[]> lateComersList = custEmployeeRepository.getReportByReportType(0, brId, deptId, reportType, fromDate, todate);
				if(!lateComersList.isEmpty()) {
					for(Object[] el: lateComersList) {
						EmpLateComersDto dto = new EmpLateComersDto();
						dto.setEmpName(el[0].toString());
						dto.setDeptName(el[1].toString());
						dto.setBranchName(el[2].toString());
						dto.setBlockName(el[3].toString());
						dto.setDesignationName(el[4].toString());
						dto.setShiftName(el[5].toString());
						dto.setShiftStart(el[6].toString());
						dto.setInTime(el[7].toString());
						dto.setDiff(el[8].toString());
						lateComers.add(dto);
					}
					response.setLateComers(lateComers);
					response.status = new Status(false, 200, "Success");
				} else {
					response.status = new Status(false, 400, "Not found");
				}	
			}
		} catch (Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return response;
	}

	@Override
	public EmpLeaveReportResponse getEmpLeaveReport(int brId, int deptId, String reportType, Date fromDate, Date todate) {
		EmpLeaveReportResponse response = new EmpLeaveReportResponse();
		List<EmpLeaveReportDto> leaveReportList = new ArrayList<EmpLeaveReportDto>();
		try {
			List<Object[]> master = custEmployeeRepository.GetEmployeMasterData(brId, deptId);
			if(!master.isEmpty()) {
				for(Object[] ms: master) {
					MasterDataDto masterData = new MasterDataDto();
					masterData.setCustName(ms[0].toString());
					masterData.setCustAddress(ms[1].toString());
					masterData.setBrName(ms[2].toString());
					masterData.setBrAddress(ms[3].toString());
					masterData.setDeptName(ms[4].toString());
					String pattern = "yyyy-MM-dd";
					SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
					dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
					Date date = new Date();
					String sysDate = dateFormat.format(date);
					masterData.setDate(sysDate);
					masterData.setFromDate(fromDate);
					masterData.setToDate(todate);
					response.setMasterData(masterData);
				}
			}

			List<Object[]> leaves = custEmployeeRepository.getReportByReportType(0, brId, deptId, reportType, fromDate, todate);
			if(!leaves.isEmpty()) {
				for(Object[] l: leaves) {
					EmpLeaveReportDto dto = new EmpLeaveReportDto();
					dto.setEmpName(l[0].toString());
					dto.setDeptName(l[1].toString());
					dto.setBranchName(l[2].toString());
					dto.setBlockName(l[3].toString());
					dto.setDesignationName(l[4].toString());
					dto.setShiftName(l[5].toString());
					dto.setLeaveFrom(l[6].toString());
					dto.setLeaveTo(l[7].toString());
					dto.setNoOfDays(l[8].toString());
					dto.setStatus(l[9].toString());
					leaveReportList.add(dto);
				}
				response.setLeaveReport(leaveReportList);
				response.status = new Status(false, 200, "Success");
			} else {
				response.status = new Status(false, 400, "Not found");
			}
		} catch (Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return response;
	}

	@Override
	public EmpOfficeOutResponse getOfficeOutReport(int brId, int deptId, String reportType, Date fromDate, Date todate) {
		EmpOfficeOutResponse response = new EmpOfficeOutResponse();
		List<EmpOfficeOutDto> outList = new ArrayList<EmpOfficeOutDto>();
		try {

			List<Object[]> master = custEmployeeRepository.GetEmployeMasterData(brId, deptId);
			if(!master.isEmpty()) {
				for(Object[] ms: master) {
					MasterDataDto masterData = new MasterDataDto();
					masterData.setCustName(ms[0].toString());
					masterData.setCustAddress(ms[1].toString());
					masterData.setBrName(ms[2].toString());
					masterData.setBrAddress(ms[3].toString());
					masterData.setDeptName(ms[4].toString());
					String pattern = "yyyy-MM-dd";
					SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
					dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
					Date date = new Date();
					String sysDate = dateFormat.format(date);
					masterData.setDate(sysDate);
					masterData.setFromDate(fromDate);
					masterData.setToDate(todate);
					response.setMasterData(masterData);
				}
			}

			List<Object[]> officeOut = custEmployeeRepository.getReportByReportType(0, brId, deptId, reportType, fromDate, todate);
			if(!officeOut.isEmpty()) {
				for(Object[] o: officeOut) {
					EmpOfficeOutDto dto = new EmpOfficeOutDto();
					dto.setEmpName(o[0].toString());
					dto.setDeptName(o[1].toString());
					dto.setBranchName(o[2].toString());
					dto.setBlockName(o[3].toString());
					dto.setDesignationName(o[4].toString());
					dto.setShiftName(o[5].toString());
					dto.setDesignatedLocation(o[6].toString());
					dto.setAttendanceOutLocation(o[7].toString());
					dto.setAttendanceDate(o[8].toString());
					outList.add(dto);
				}
				response.setOfficeOutDetails(outList);
				response.status = new Status(false, 200, "Success");
			} else {
				response.status = new Status(false, 400, "Not found");
			}
		} catch (Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return response;
	}

	@Override
	public SummaryDetailsResponse getEmployeeSumarryDetails(Integer custId, Integer empId,Integer loginId) {
		SummaryDetailsResponse summaryDetailsResponse = new SummaryDetailsResponse();
		List<EmployeeSummaryParamDto> empPresentList = new ArrayList<EmployeeSummaryParamDto>();
		List<EmployeeSummaryParamDto> empAbsentList = new ArrayList<EmployeeSummaryParamDto>();
		List<EmployeeSummaryParamDto> empApprovedList = new ArrayList<EmployeeSummaryParamDto>();
		List<EmployeeSummaryParamDto> empPendingList = new ArrayList<EmployeeSummaryParamDto>();

		try {
			if(loginId == 0) {
			List<Object[]> present = custEmployeeRepository.w_GetEmployeePresentData(custId, empId);
			if(!present.isEmpty()) {
				for(Object[] p: present) {
					EmployeeSummaryParamDto employeeSummaryParamDto = new EmployeeSummaryParamDto();
					employeeSummaryParamDto.setEmpName(p[0].toString());
					employeeSummaryParamDto.setMobileNo(p[1].toString());
					employeeSummaryParamDto.setBranchName(p[2].toString());
					employeeSummaryParamDto.setDeptName(p[3].toString());
					empPresentList.add(employeeSummaryParamDto);
				}
				summaryDetailsResponse.setPresentList(empPresentList);
				summaryDetailsResponse.status = new Status(false, 200, "Success");
			} else {
				summaryDetailsResponse.status = new Status(false, 400, "Not found");
			}
			List<Object[]> absent = custEmployeeRepository.w_GetEmployeeAbsentData(custId, empId);
			if(!absent.isEmpty()) {
				for(Object[] a: absent) {
					EmployeeSummaryParamDto employeeSummaryParamDto1 = new EmployeeSummaryParamDto();
					employeeSummaryParamDto1.setEmpName(a[0].toString());
					employeeSummaryParamDto1.setMobileNo(a[1].toString());
					employeeSummaryParamDto1.setBranchName(a[2].toString());
					employeeSummaryParamDto1.setDeptName(a[3].toString());
					empAbsentList.add(employeeSummaryParamDto1);
				}
				summaryDetailsResponse.setAbsentList(empAbsentList);
				summaryDetailsResponse.status = new Status(false, 200, "Success");
			} else {
				summaryDetailsResponse.status = new Status(false, 400, "Not found");
			}
			List<Object[]> approvedLeave = custEmployeeRepository.w_GetEmployeeApprovedLeaveData(custId, empId);
			if(!approvedLeave.isEmpty()) {
				for(Object[] al: approvedLeave) {
					EmployeeSummaryParamDto employeeSummaryParamDto2 = new EmployeeSummaryParamDto();
					employeeSummaryParamDto2.setEmpName(al[0].toString());
					employeeSummaryParamDto2.setMobileNo(al[1].toString());
					employeeSummaryParamDto2.setBranchName(al[2].toString());
					employeeSummaryParamDto2.setDeptName(al[3].toString());
					empApprovedList.add(employeeSummaryParamDto2);
				}
				summaryDetailsResponse.setApprovedList(empApprovedList);
				summaryDetailsResponse.status = new Status(false, 200, "Success");
			} else {
				summaryDetailsResponse.status = new Status(false, 400, "Not found");
			}
			List<Object[]> pendingLeave = custEmployeeRepository.w_GetEmployeePendingLeaveData(custId, empId);
			if(!pendingLeave.isEmpty()) {
				for(Object[] pl: pendingLeave) {
					EmployeeSummaryParamDto employeeSummaryParamDto3 = new EmployeeSummaryParamDto();
					employeeSummaryParamDto3.setEmpName(pl[0].toString());
					employeeSummaryParamDto3.setMobileNo(pl[1].toString());
					employeeSummaryParamDto3.setBranchName(pl[2].toString());
					employeeSummaryParamDto3.setDeptName(pl[3].toString());
					empPendingList.add(employeeSummaryParamDto3);
				}
				summaryDetailsResponse.setPendingList(empPendingList);
				summaryDetailsResponse.status = new Status(false, 200, "Success");
			} else {
				summaryDetailsResponse.status = new Status(false, 400, "Not found");
			}
			}else if(loginId != 0) {
				List<Object[]> present = custEmployeeRepository.w_GetEmployeePresentDataForAdmin(loginId,custId);
				if(!present.isEmpty()) {
					for(Object[] p: present) {
						EmployeeSummaryParamDto employeeSummaryParamDto = new EmployeeSummaryParamDto();
						employeeSummaryParamDto.setEmpName(p[0].toString());
						employeeSummaryParamDto.setMobileNo(p[1].toString());
						employeeSummaryParamDto.setBranchName(p[2].toString());
						employeeSummaryParamDto.setDeptName(p[3].toString());
						Employee employee = custEmployeeRepository.findEmpReportingToByEmpId(Integer.valueOf(p[4].toString()));
						if(employee != null) {
							employeeSummaryParamDto.setReportingToName(employee.getEmpName());
						}else {
							employeeSummaryParamDto.setReportingToName("NA");
						}
						empPresentList.add(employeeSummaryParamDto);
					}
					summaryDetailsResponse.setPresentList(empPresentList);
					summaryDetailsResponse.status = new Status(false, 200, "Success");
				} else {
					summaryDetailsResponse.status = new Status(false, 400, "Not found");
				}
				List<Object[]> absent = custEmployeeRepository.w_GetEmployeeAbsentDataForAdmin(loginId,custId);
				if(!absent.isEmpty()) {
					for(Object[] a: absent) {
						EmployeeSummaryParamDto employeeSummaryParamDto1 = new EmployeeSummaryParamDto();
						employeeSummaryParamDto1.setEmpName(a[0].toString());
						employeeSummaryParamDto1.setMobileNo(a[1].toString());
						employeeSummaryParamDto1.setBranchName(a[2].toString());
						employeeSummaryParamDto1.setDeptName(a[3].toString());
						Employee employee = custEmployeeRepository.findEmpReportingToByEmpId(Integer.valueOf(a[4].toString()));
						if(employee != null) {
							employeeSummaryParamDto1.setReportingToName(employee.getEmpName());
						}else {
							employeeSummaryParamDto1.setReportingToName("NA");
						}
						empAbsentList.add(employeeSummaryParamDto1);
					}
					summaryDetailsResponse.setAbsentList(empAbsentList);
					summaryDetailsResponse.status = new Status(false, 200, "Success");
				} else {
					summaryDetailsResponse.status = new Status(false, 400, "Not found");
				}
				List<Object[]> approvedLeave = custEmployeeRepository.w_GetEmployeeApprovedDataForAdmin(loginId,custId);
				if(!approvedLeave.isEmpty()) {
					for(Object[] al: approvedLeave) {
						EmployeeSummaryParamDto employeeSummaryParamDto2 = new EmployeeSummaryParamDto();
						employeeSummaryParamDto2.setEmpName(al[0].toString());
						employeeSummaryParamDto2.setMobileNo(al[1].toString());
						employeeSummaryParamDto2.setBranchName(al[2].toString());
						employeeSummaryParamDto2.setDeptName(al[3].toString());
						Employee employee = custEmployeeRepository.findEmpReportingToByEmpId(Integer.valueOf(al[4].toString()));
						if(employee != null) {
							employeeSummaryParamDto2.setReportingToName(employee.getEmpName());
						}else {
							employeeSummaryParamDto2.setReportingToName("NA");
						}
						empApprovedList.add(employeeSummaryParamDto2);
					}
					summaryDetailsResponse.setApprovedList(empApprovedList);
					summaryDetailsResponse.status = new Status(false, 200, "Success");
				} else {
					summaryDetailsResponse.status = new Status(false, 400, "Not found");
				}
				List<Object[]> pendingLeave = custEmployeeRepository.w_GetEmployeePendingDataForAdmin(loginId,custId);
				if(!pendingLeave.isEmpty()) {
					for(Object[] pl: pendingLeave) {
						EmployeeSummaryParamDto employeeSummaryParamDto3 = new EmployeeSummaryParamDto();
						employeeSummaryParamDto3.setEmpName(pl[0].toString());
						employeeSummaryParamDto3.setMobileNo(pl[1].toString());
						employeeSummaryParamDto3.setBranchName(pl[2].toString());
						employeeSummaryParamDto3.setDeptName(pl[3].toString());
						Employee employee = custEmployeeRepository.findEmpReportingToByEmpId(Integer.valueOf(pl[4].toString()));
						if(employee != null) {
							employeeSummaryParamDto3.setReportingToName(employee.getEmpName());
						}else {
							employeeSummaryParamDto3.setReportingToName("NA");
						}
						empPendingList.add(employeeSummaryParamDto3);
					}
					summaryDetailsResponse.setPendingList(empPendingList);
					summaryDetailsResponse.status = new Status(false, 200, "Success");
				} else {
					summaryDetailsResponse.status = new Status(false, 400, "Not found");
				}	
			}
		} catch (Exception e) {
			summaryDetailsResponse.status = new Status(true,500,"Oops..! Something went wrong..");
		}
		return summaryDetailsResponse;
	}
}
