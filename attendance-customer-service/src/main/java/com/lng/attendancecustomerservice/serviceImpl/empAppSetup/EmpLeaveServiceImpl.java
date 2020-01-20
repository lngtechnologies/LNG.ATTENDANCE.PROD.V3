package com.lng.attendancecustomerservice.serviceImpl.empAppSetup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.CustLeave;
import com.lng.attendancecustomerservice.entity.masters.EmpLeave;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.masters.EmployeeLeave;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustLeaveRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeLeaveRepository;
import com.lng.attendancecustomerservice.service.empAppSetup.EmpLeaveService;
import com.lng.dto.masters.employeeLeave.CustLeaveTrypeListDto;
import com.lng.dto.masters.employeeLeave.CustLeaveTypeDto;
import com.lng.dto.masters.employeeLeave.EmpAppLeaveDto;
import com.lng.dto.masters.employeeLeave.EmpLeaveResponseDto;
import com.lng.dto.masters.employeeLeave.EmployeeLeaveDto;

import status.Status;

@Service
public class EmpLeaveServiceImpl implements EmpLeaveService {

	@Autowired
	EmployeeLeaveRepository employeeLeaveRepository;

	@Autowired
	CustLeaveRepository custLeaveRepository;

	@Autowired
	EmployeeRepository employeeRepository;	

	ModelMapper modelMapper = new ModelMapper();

	@Override
	public CustLeaveTrypeListDto getLeaveListByCustId(Integer custId) {
		CustLeaveTrypeListDto custLeaveTrypeListDto = new CustLeaveTrypeListDto();
		try {
			List<CustLeave> custLeaves = custLeaveRepository.findCustLeaveByCustomer_custIdAndCustLeaveIsActive(custId, true);

			if(!custLeaves.isEmpty()) {
				custLeaveTrypeListDto.setCustId(custId);
				custLeaveTrypeListDto.setCustLeaveTypeDtoList(custLeaves.stream().map(custLeave -> convertToCustLeaveTypeDto(custLeave)).collect(Collectors.toList()));

				custLeaveTrypeListDto.status = new Status(false, 200, "Success");
			}else {
				custLeaveTrypeListDto.status = new Status(false, 400, "Not found");
			}
		} catch (Exception e) {
			custLeaveTrypeListDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}

		return custLeaveTrypeListDto;
	}


	@Override
	public Status saveEmpLeave(EmployeeLeaveDto employeeLeaveDto) {
		Status status = null;
		try {	
			Integer countNoOfDays = employeeLeaveRepository.getNoOfDaysCount(employeeLeaveDto.getEmpLeaveFrom(), employeeLeaveDto.getEmpLeaveTo());
			Employee employee = employeeRepository.getByEmpId(employeeLeaveDto.getEmpId());
			CustLeave custLeave = custLeaveRepository.findCustLeaveByCustLeaveIdAndCustLeaveIsActive(employeeLeaveDto.getCustLeaveId(), true);
			int empLeave = employeeLeaveRepository.getEmpLeaveAlreadyApplied(employeeLeaveDto.getEmpLeaveFrom(), employeeLeaveDto.getEmpLeaveTo(), employeeLeaveDto.getEmpId());

			if(employee != null) {
				if(custLeave != null) {
					if(empLeave == 0) {
						EmployeeLeave employeeLeave = modelMapper.map(employeeLeaveDto, EmployeeLeave.class);

						employeeLeave.setEmployee(employee);
						employeeLeave.setCustLeave(custLeave);
						employeeLeave.setEmpLeaveDaysCount(countNoOfDays);
						employeeLeave.setEmpLeaveAppliedDatetime(new Date());
						employeeLeave.setEmpLeaveStatus("");
						employeeLeaveRepository.save(employeeLeave);
						status = new Status(false, 200, "Leave applied successfully");
					} else {
						status = new Status(true, 400, "Leave already applied for this date");
					}

				}else {
					status = new Status(true, 400, "Cust Leave not found");
				}
			} else {
				status = new Status(true, 400, "Employee not found");
			}

		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}

	/*@Override
	public EmpLeaveResponseDto getEmpLeaveByEmpId(Integer empId) {
		EmpLeaveResponseDto empLeaveResponseDto = new EmpLeaveResponseDto();
		try {
			Employee employee = employeeRepository.getByEmpId(empId);
			if(employee != null) {
				EmployeeLeave employeeLeave = employeeLeaveRepository.findByEmployee_EmpId(empId);
				if(employeeLeave != null) {

					EmployeeLeaveDto employeeLeaveDto = convertToEmployeeLeaveDto(employeeLeave);
					empLeaveResponseDto.setData(employeeLeaveDto);
					empLeaveResponseDto.status = new Status(false, 200, "Success");

				}else {
					empLeaveResponseDto.status = new Status(false, 400, "Employee not applied for leave");
				}
			} else {
				empLeaveResponseDto.status = new Status(false, 400, "Employee not found");
			}
		} catch (Exception e) {
			empLeaveResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return empLeaveResponseDto;
	}*/

	public EmpLeaveResponseDto getEmpLeaveByEmpId(Integer empId) {
		EmpLeaveResponseDto empLeaveResponseDto = new EmpLeaveResponseDto();
		try {
			Employee employee = employeeRepository.getByEmpId(empId);
			if(employee != null) {
				List<Object[]> employeeLeave = employeeLeaveRepository.findByEmployee_EmpId(empId);
				if(!employeeLeave.isEmpty()) {
					for(Object[] p: employeeLeave) {
						EmpAppLeaveDto empAppLeaveDto = new EmpAppLeaveDto();
						empAppLeaveDto.setEmpLeaveId(Integer.valueOf(p[0].toString()));
						empAppLeaveDto.setEmpId(Integer.valueOf(p[1].toString()));
						empAppLeaveDto.setCustLeaveId(Integer.valueOf(p[2].toString()));

						String pattern = "dd - MM - yyyy h:mm a";
						SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
						dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
						String date = dateFormat.format((Date)p[3]);
						empAppLeaveDto.setEmpLeaveAppliedDatetime(date);

						String pattern1 = "dd - MMM - yyyy";
						SimpleDateFormat dateFormat1 = new SimpleDateFormat(pattern1);
						dateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
						String date1 = dateFormat1.format((Date)p[4]);
						empAppLeaveDto.setEmpLeaveFrom(date1);

						String pattern2 = "dd - MMM - yyyy";
						SimpleDateFormat dateFormat2 = new SimpleDateFormat(pattern2);
						dateFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));
						String date2 = dateFormat2.format((Date)p[5]);
						empAppLeaveDto.setEmpLeaveTo(date2);

						empAppLeaveDto.setEmpLeaveDaysCount(Integer.valueOf(p[6].toString()));
						empAppLeaveDto.setEmpLeaveRemarks(p[7].toString());
						empAppLeaveDto.setEmpLeaveStatus(p[8].toString());
						empAppLeaveDto.setEmpLeaveRejectionRemarks(p[9].toString());
						empAppLeaveDto.setEmpLeaveAppRejBy(Integer.valueOf(p[10].toString()));

						/*if(p[11].toString() != null) {
							String pattern3 = "dd - MM - yyyy h:mm a"; 
							SimpleDateFormat dateFormat3 = new SimpleDateFormat(pattern3);
							dateFormat3.setTimeZone(TimeZone.getTimeZone("UTC")); 
							String date3 = dateFormat.format((Date)p[11]);
							empAppLeaveDto.setEmpLeaveStatusUpdatedDatetime(date3);
						} else {
							empAppLeaveDto.setEmpLeaveStatusUpdatedDatetime(p[11].toString());
						}*/

						empAppLeaveDto.setEmpLeaveStatusUpdatedDatetime(p[11].toString());

						empAppLeaveDto.setEmpLeaveRequestForCancellation(Integer.valueOf(p[12].toString()));

						empLeaveResponseDto.setData(empAppLeaveDto);
						empLeaveResponseDto.status = new Status(false, 200, "Success");
					}

				}else {
					empLeaveResponseDto.status = new Status(false, 400, "Employee not applied for leave");
				}
			} else {
				empLeaveResponseDto.status = new Status(false, 400, "Employee not found");
			}
		} catch (Exception e) {
			empLeaveResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return empLeaveResponseDto;
	}

	public CustLeaveTypeDto convertToCustLeaveTypeDto(CustLeave custLeave) {
		CustLeaveTypeDto  custLeaveTypeDto = modelMapper.map(custLeave, CustLeaveTypeDto.class);
		custLeaveTypeDto.setCustLeaveId(custLeave.getCustLeaveId());
		custLeaveTypeDto.setCustLeaveName(custLeave.getCustLeaveName());
		return custLeaveTypeDto;
	}

	public EmployeeLeaveDto convertToEmployeeLeaveDto(EmployeeLeave employeeLeave) {
		EmployeeLeaveDto employeeLeaveDto = modelMapper.map(employeeLeave, EmployeeLeaveDto.class);
		employeeLeaveDto.setEmpId(employeeLeave.getEmployee().getEmpId());
		employeeLeaveDto.setCustLeaveId(employeeLeave.getCustLeave().getCustLeaveId());
		return employeeLeaveDto;
	}

}
