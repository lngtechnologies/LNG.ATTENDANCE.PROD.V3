package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.authentication.Login;
import com.lng.attendancecustomerservice.entity.masters.EmployeeLeave;
import com.lng.attendancecustomerservice.repositories.authentication.ILoginRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeLeaveRepository;
import com.lng.attendancecustomerservice.service.masters.EmpLeaveApproveOrCancelService;
import com.lng.dto.masters.empLeaveApproveOrCancel.EmpLeaveDto;
import com.lng.dto.masters.empLeaveApproveOrCancel.EmpLeaveResponseDto;
import com.lng.dto.masters.employeeLeave.EmployeeLeaveDto;

import status.Status;

@Service
public class EmpLeaveApproveOrCancelServiceImpl implements EmpLeaveApproveOrCancelService {

	@Autowired
	EmployeeLeaveRepository employeeLeaveRepository;
	
	@Autowired
	ILoginRepository iLoginRepository;

	ModelMapper ModelMapper = new ModelMapper();

	@Override
	public EmpLeaveResponseDto getByLoginIdAndCustID(Integer loginId, Integer custId) {

		EmpLeaveResponseDto empLeaveResponseDto = new EmpLeaveResponseDto();
		List<EmpLeaveDto> empLeaveDtoList = new ArrayList<>();
		try {
			
			List<Object[]> employeeList = employeeLeaveRepository.getEmpLeaveByLoginIdAndCustId(loginId, custId);
			if(!employeeList.isEmpty()) {
				for(Object[] p: employeeList) {				
					EmpLeaveDto empLeaveDto = new EmpLeaveDto();
					
					empLeaveDto.setEmpLeaveId(Integer.valueOf(p[0].toString()));
					empLeaveDto.setLoginId(Integer.valueOf(p[1].toString()));
					empLeaveDto.setCustId(Integer.valueOf(p[2].toString()));
					empLeaveDto.setEmpId(Integer.valueOf(p[3].toString()));
					empLeaveDto.setEmpName(p[4].toString());
					empLeaveDto.setDeptId(Integer.valueOf(p[5].toString()));
					empLeaveDto.setDeptName(p[6].toString());
					empLeaveDto.setEmpLeaveFrom((Date)p[7]);
					empLeaveDto.setEmpLeaveTo((Date)p[8]);
					empLeaveDto.setEmpLeaveDaysCount(Integer.valueOf(p[9].toString()));
					empLeaveDto.setEmpLeaveStatus(p[10].toString());
					empLeaveDtoList.add(empLeaveDto);
					
					empLeaveResponseDto.setEmpLeaveDtoList(empLeaveDtoList);
					empLeaveResponseDto.status = new Status(false, 200, "Success");
				}
			}else {
				empLeaveResponseDto.status = new Status(false, 400, "No records found");
			}
		
		} catch (Exception e) {
			empLeaveResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return empLeaveResponseDto;
	}

	@Override
	public Status empApproveLeave(EmployeeLeaveDto employeeLeaveDto) {
		Status status = null;
		try {
			
			EmployeeLeave employeeLeave = employeeLeaveRepository.findByEmpLeaveId(employeeLeaveDto.getEmpLeaveId());
			if(employeeLeave != null) {
				employeeLeave.setEmpLeaveStatus("App");
				employeeLeaveRepository.save(employeeLeave);
				status = new Status(false, 200, "Leave Approved for "+ employeeLeave.getEmployee().getEmpName());
			} else {
				status = new Status(false, 400, "Not found");
			}
			
		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}

	@Override
	public Status empRejectLeave(EmployeeLeaveDto employeeLeaveDto) {
		Status status = null;
		try {
			
			EmployeeLeave employeeLeave = employeeLeaveRepository.findByEmpLeaveId(employeeLeaveDto.getEmpLeaveId());
			if(employeeLeave != null) {
				employeeLeave.setEmpLeaveStatus("Rej");
				employeeLeave.setEmpLeaveRejectionRemarks(employeeLeaveDto.getEmpLeaveRejectionRemarks());
				employeeLeaveRepository.save(employeeLeave);
				status = new Status(false, 200, "Leave Rejected for "+ employeeLeave.getEmployee().getEmpName());
			} else {
				status = new Status(false, 400, "Not found");
			}
			
		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}

	@Override
	public EmpLeaveResponseDto getEmpLeaveAppByLoginIdAndCustID(Integer loginId, Integer custId) {
		EmpLeaveResponseDto empLeaveResponseDto = new EmpLeaveResponseDto();
		List<EmpLeaveDto> empLeaveDtoList = new ArrayList<>();
		try {
			
			List<Object[]> employeeList = employeeLeaveRepository.getEmpLeaveAppByLoginIdAndCustId(loginId, custId);
			if(!employeeList.isEmpty()) {
				for(Object[] p: employeeList) {				
					EmpLeaveDto empLeaveDto = new EmpLeaveDto();
					
					empLeaveDto.setEmpLeaveId(Integer.valueOf(p[0].toString()));
					empLeaveDto.setLoginId(Integer.valueOf(p[1].toString()));
					empLeaveDto.setCustId(Integer.valueOf(p[2].toString()));
					empLeaveDto.setEmpId(Integer.valueOf(p[3].toString()));
					empLeaveDto.setEmpName(p[4].toString());
					empLeaveDto.setDeptId(Integer.valueOf(p[5].toString()));
					empLeaveDto.setDeptName(p[6].toString());
					empLeaveDto.setEmpLeaveFrom((Date)p[7]);
					empLeaveDto.setEmpLeaveTo((Date)p[8]);
					empLeaveDto.setEmpLeaveDaysCount(Integer.valueOf(p[9].toString()));
					empLeaveDto.setEmpLeaveStatus(p[10].toString());
					empLeaveDtoList.add(empLeaveDto);
					
					empLeaveResponseDto.setEmpLeaveDtoList(empLeaveDtoList);
					empLeaveResponseDto.status = new Status(false, 200, "Success");
				}
			}else {
				empLeaveResponseDto.status = new Status(false, 400, "No records found");
			}
		
		} catch (Exception e) {
			empLeaveResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return empLeaveResponseDto;
	}

	@Override
	public Status empApproveCancelLeave(EmployeeLeaveDto employeeLeaveDto) {
		Status status = null;
		
		try {
			
			EmployeeLeave employeeLeave = employeeLeaveRepository.findByEmpLeaveId(employeeLeaveDto.getEmpLeaveId());
			if(employeeLeave != null) {
				employeeLeave.setEmpLeaveStatus("AppCan");
				employeeLeave.setEmpLeaveRejectionRemarks(employeeLeaveDto.getEmpLeaveRejectionRemarks());
				employeeLeaveRepository.save(employeeLeave);
				status = new Status(false, 200, "Leave Cancelled for "+ employeeLeave.getEmployee().getEmpName());
			} else {
				status = new Status(false, 400, "Not found");
			}
			
		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}

	@Override
	public EmpLeaveResponseDto getByLoginIdAndCustIDAndEmpId(Integer loginId, Integer custId) {
		EmpLeaveResponseDto empLeaveResponseDto = new EmpLeaveResponseDto();
		List<EmpLeaveDto> empLeaveDtoList = new ArrayList<>();
		try {
			Login login = iLoginRepository.findByLoginId(loginId);
			if(login != null) {
				
				if(login.getRefEmpId() != 0) {
					
					List<Object[]> employeeList = employeeLeaveRepository.getEmpLeaveByLoginIdAndCustIdAndEmpId(loginId, custId, login.getRefEmpId());
					if(!employeeList.isEmpty()) {
						
						for(Object[] p: employeeList) {				
							EmpLeaveDto empLeaveDto = new EmpLeaveDto();
							
							empLeaveDto.setEmpLeaveId(Integer.valueOf(p[0].toString()));
							empLeaveDto.setLoginId(Integer.valueOf(p[1].toString()));
							empLeaveDto.setCustId(Integer.valueOf(p[2].toString()));
							empLeaveDto.setEmpId(Integer.valueOf(p[3].toString()));
							empLeaveDto.setEmpName(p[4].toString());
							empLeaveDto.setDeptId(Integer.valueOf(p[5].toString()));
							empLeaveDto.setDeptName(p[6].toString());
							empLeaveDto.setEmpLeaveFrom((Date)p[7]);
							empLeaveDto.setEmpLeaveTo((Date)p[8]);
							empLeaveDto.setEmpLeaveDaysCount(Integer.valueOf(p[9].toString()));
							empLeaveDto.setEmpLeaveStatus(p[10].toString());
							empLeaveDtoList.add(empLeaveDto);
							
							empLeaveResponseDto.setEmpLeaveDtoList(empLeaveDtoList);
							empLeaveResponseDto.status = new Status(false, 200, "Success");
						}
					}else {
						empLeaveResponseDto.status = new Status(false, 400, "No records found");
					}
				} else {
					empLeaveResponseDto.status = new Status(false, 400, "Not authorized user to approve leave");
				}
			}
			
		} catch (Exception e) {
			empLeaveResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return empLeaveResponseDto;
	}
}
