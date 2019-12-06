package com.lng.attendancecustomerservice.serviceImpl.reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.reports.ReportDto;
import com.lng.attendancecustomerservice.entity.reports.ReportEmployeeSummaryDto;
import com.lng.attendancecustomerservice.entity.reports.ReportMasterDataDto;
import com.lng.attendancecustomerservice.entity.reports.ReportResponseDto;
import com.lng.attendancecustomerservice.entity.reports.ResponseSummaryReport;
import com.lng.attendancecustomerservice.repositories.employeeAttendance.ReportRepository;
import com.lng.attendancecustomerservice.service.reports.IReport;
import com.lng.dto.reports.ReportParam;

@Service
public class ReportServiceImpl implements IReport {
	@Autowired ReportRepository reportRepo;

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

}
