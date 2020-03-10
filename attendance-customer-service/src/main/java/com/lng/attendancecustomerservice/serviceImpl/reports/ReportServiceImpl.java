package com.lng.attendancecustomerservice.serviceImpl.reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.reports.ReportDto;
import com.lng.attendancecustomerservice.entity.reports.ReportEmployeeSummaryDto;
import com.lng.attendancecustomerservice.entity.reports.ReportMasterDataDto;
import com.lng.attendancecustomerservice.entity.reports.ReportResponseDto;
import com.lng.attendancecustomerservice.entity.reports.ResponseSummaryReport;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.employeeAttendance.ReportRepository;
import com.lng.attendancecustomerservice.repositories.masters.LoginDataRightRepository;
import com.lng.attendancecustomerservice.service.reports.IReport;
import com.lng.dto.reports.EmployeeDetailsDto;
import com.lng.dto.reports.EmployeeDtailsResponse;
import com.lng.dto.reports.ReportParam;

import status.Status;

@Service
public class ReportServiceImpl implements IReport {
	@Autowired ReportRepository reportRepo;

	@Autowired
	LoginDataRightRepository loginDataRightRepository;

	@Autowired
	EmployeeRepository  employeeRepository;

	@Override
	public ReportResponseDto GetAttendanceReport(ReportParam reportParam) {
		ReportResponseDto response = new ReportResponseDto();
		List<ReportDto> rptDataList = new ArrayList<>();
		try {
			if(reportParam.getCustId() != null) {
				List<Object[]> masterData = reportRepo.GetReportMasterData(reportParam.getCustId());
				if(masterData != null) {
					for(Object[] mData: masterData) {
						ReportMasterDataDto mstData = new ReportMasterDataDto();
						mstData.setCustName(mData[0].toString());
						mstData.setDeptName(mData[1].toString());
						mstData.setBrName(mData[2].toString());
						mstData.setEmpType(mData[3].toString());
						response.setMasterData(mstData);
					}
				}
			}

			String whereClause = BuildWhereClause(reportParam);
			if(whereClause != null) {
				List<Object[]> resultReport = null;
				if(reportParam.getReportType() == 1) {
					resultReport = reportRepo.GetPresentReport(whereClause);
					if(resultReport != null) {
						for(Object[] dt: resultReport) {
							ReportDto rptData = new ReportDto();
							rptData.setName(dt[0].toString());
							rptData.setDesignation(dt[1].toString());
							rptData.setBlock(dt[2].toString());
							rptData.setShift(dt[3].toString());
							rptData.setTimeIn(dt[4].toString());
							rptData.setTimeInLocation(dt[5].toString());
							rptData.setTimeOut(dt[6].toString());
							rptData.setTimeOutLocation(dt[7].toString());
							rptData.setApprovedGeoLocation(dt[8].toString());
							rptData.setWorkedHrs(dt[9].toString());
							rptDataList.add(rptData);
						}
						response.setResult(rptDataList);
					}
				} else if(reportParam.getReportType() == 2) {
					String date = null;
					if(reportParam.getFromDate() != null) {
						date = reportParam.getFromDate();
					} else {
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						date = dateFormat.format(new Date());
					}
					resultReport = reportRepo.GetAbsentReport(whereClause, date);
					if(resultReport != null) {
						for(Object[] dt: resultReport) {
							ReportDto rptData = new ReportDto();
							rptData.setName(dt[0].toString());
							rptData.setDesignation(dt[1].toString());
							rptData.setBlock(dt[2].toString());
							rptData.setShift(dt[3].toString());
							rptData.setApprovedGeoLocation(dt[4].toString());
							rptDataList.add(rptData);
						}
						response.setResult(rptDataList);
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return response;
	}

	private String BuildWhereClause(ReportParam reportParam) {
		String whereClause = "";
		if(reportParam.getCustId() != null && reportParam.getCustId() != 0) {
			whereClause += "emp.refCustId = " + reportParam.getCustId();
		}
		if(reportParam.getDeptId() != null && reportParam.getDeptId() != 0) {
			whereClause += " AND empDept.refDeptId = " + reportParam.getDeptId();
		}
		if(reportParam.getBrId() != null && reportParam.getBrId() != 0) {
			whereClause += " AND emp.refBrId = " + reportParam.getBrId();
		}
		if(reportParam.getBlkId() != null && reportParam.getBlkId() != 0) {
			whereClause += " AND empBlk.refBlkId = " + reportParam.getBlkId();
		}
		if(reportParam.getFromDate() != null && reportParam.getToDate() != null && reportParam.getReportType() == 1) {
			whereClause += " AND empAttnd.empAttendanceDate BETWEEN '" + reportParam.getFromDate() +"' AND '" + reportParam.getToDate() +"' ";
		}
		return whereClause;
	}

	@Override
	public ResponseSummaryReport GetEmployeeSummaryReport(ReportParam reportParam) {
		ResponseSummaryReport responseSummaryReport = new ResponseSummaryReport();
		List<ReportEmployeeSummaryDto> rptDataList = new ArrayList<>();

		try {
			if(reportParam.getCustId() != null) {
				List<Object[]> masterData = reportRepo.GetEmployeMasterData(reportParam.getCustId(), reportParam.getEmpId());
				if(masterData != null) {
					for(Object[] mData: masterData) {
						ReportMasterDataDto mstData = new ReportMasterDataDto();
						mstData.setCustName(mData[0].toString());
						mstData.setDeptName(mData[1].toString());
						mstData.setBrName(mData[2].toString());
						mstData.setEmpType(mData[3].toString());
						mstData.setEmpName(mData[4].toString());
						responseSummaryReport.setMasterData(mstData);
					}
				}
			}

			if(reportParam.getEmpId() != null) {
				List<Object[]> summaryReportResult = reportRepo.GetEmployeeSummaryReport(reportParam.getFromDate(), reportParam.getToDate(), reportParam.getEmpId());
				if(summaryReportResult != null) {
					for(Object[] dt: summaryReportResult) {
						ReportEmployeeSummaryDto rptData = new ReportEmployeeSummaryDto();
						rptData.setDate(dt[2].toString());
						rptData.setShift(dt[3].toString());
						rptData.setTimeIn(dt[4].toString());
						rptData.setTimeOut(dt[5].toString());
						rptData.setWorkedHrs(dt[6].toString());
						rptData.setStatus(dt[7].toString());
						rptData.setApprovedGeoLocation(dt[8].toString());
						rptData.setTimeInLocation(dt[9].toString());
						rptData.setTimeOutLocation(dt[10].toString());
						boolean a = (dt[11].toString()).equals("G") || (dt[11].toString()).equals("B");
						if(a == true ) {
							rptData.setInAddress(dt[13].toString());
						}
						boolean b = (dt[11].toString()).equals("T") || (dt[11].toString()).equals("D");
						if(b == true ) {
							String address = reportRepo.GetEmployeeLocation(reportParam.getEmpId());
							rptData.setInAddress(address);
						}
						boolean c = (dt[11].toString()).equals("NA");
						if(c == true) {
							rptData.setInAddress("NA");
						}
						boolean d = (dt[12].toString()).equals("G") || (dt[12].toString()).equals("B");
						if(d == true ) {
							rptData.setOutAddress(dt[14].toString());
						}
						boolean e = (dt[12].toString()).equals("T") || (dt[12].toString()).equals("D");
						if(e == true ) {
							String address = reportRepo.GetEmployeeLocation(reportParam.getEmpId());
							rptData.setOutAddress(address);
						}

						boolean f = (dt[12].toString()).equals("NA");
						if(f == true) {
							rptData.setOutAddress("NA");
						}

						rptDataList.add(rptData);
					}
					responseSummaryReport.setResult(rptDataList);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return responseSummaryReport;
	}

	@Override
	public EmployeeDtailsResponse getEmployeeDetails(Integer empId,Integer custId) {
		EmployeeDtailsResponse employeeDtailsResponse = new EmployeeDtailsResponse();
		List<EmployeeDetailsDto> employeeDetailsDtoList = new ArrayList<>();


		try {
			Employee employee = employeeRepository.getByEmpId(empId);
			if(employee != null) {
				List<Object[]> employeeList  =  loginDataRightRepository.findEmployeeListByEmployee_EmpIdAndCustomer_CustId(empId,custId);
				if(!employeeList.isEmpty()) {
					for(Object[] a : employeeList){
						EmployeeDetailsDto employeeDetailsDto = new EmployeeDetailsDto();
						employeeDetailsDto.setEmpId(Integer.valueOf(a[0].toString()));
						employeeDetailsDto.setEmpName(a[1].toString());
						employeeDetailsDtoList.add(employeeDetailsDto);
						employeeDtailsResponse.setEmployeeDetails(employeeDetailsDtoList);
						employeeDtailsResponse.status = new Status(false, 200, "success");
					}
				} else {
					employeeDtailsResponse.status = new Status(false, 400, "Not found");
				}
			}else {
				List<Object[]> empList = loginDataRightRepository.findEmpListByCustId(custId);
				if(!empList.isEmpty()) {
					for(Object[] b : empList){
						EmployeeDetailsDto employeeDetailsDto = new EmployeeDetailsDto();
						employeeDetailsDto.setEmpId(Integer.valueOf(b[0].toString()));
						employeeDetailsDto.setEmpName(b[1].toString());
						employeeDetailsDtoList.add(employeeDetailsDto);
						employeeDtailsResponse.setEmployeeDetails(employeeDetailsDtoList);
						employeeDtailsResponse.status = new Status(false, 200, "success");
					}  
				} else {
					employeeDtailsResponse.status = new Status(false, 400, "Not found");
				} 
			}
		}catch(Exception  e) {
			employeeDtailsResponse.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return employeeDtailsResponse;
	}

}
