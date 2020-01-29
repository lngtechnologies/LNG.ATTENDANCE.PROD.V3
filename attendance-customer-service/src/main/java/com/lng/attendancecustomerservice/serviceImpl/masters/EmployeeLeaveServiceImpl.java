package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Branch;
import com.lng.attendancecustomerservice.entity.masters.CustLeave;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.masters.EmployeeLeave;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.BranchRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustLeaveRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeLeaveRepository;
import com.lng.attendancecustomerservice.service.masters.EmployeeLeaveService;
import com.lng.dto.masters.employeeLeave.BranchDto;
import com.lng.dto.masters.employeeLeave.BranchListDto;
import com.lng.dto.masters.employeeLeave.CustLeaveTrypeListDto;
import com.lng.dto.masters.employeeLeave.CustLeaveTypeDto;
import com.lng.dto.masters.employeeLeave.EmployeeDtatListDto;
import com.lng.dto.masters.employeeLeave.EmployeeDto;
import com.lng.dto.masters.employeeLeave.EmployeeLeaveDto;

import status.Status;

@Service
public class EmployeeLeaveServiceImpl implements EmployeeLeaveService {

	@Autowired
	BranchRepository branchRepository;

	@Autowired
	EmployeeLeaveRepository employeeLeaveRepository;

	@Autowired
	CustLeaveRepository custLeaveRepository;

	@Autowired
	EmployeeRepository employeeRepository;

	ModelMapper modelMapper = new ModelMapper();
	
	private final Lock displayQueueLock = new ReentrantLock();

	@Override
	public BranchListDto getBranchListByCustId(Integer custId) {
		BranchListDto branchListDto = new BranchListDto();

		try {
			List<Branch> branchList = branchRepository.getBranchByCustomer_custIdAndBrIsActive(custId,true);


			if(!branchList.isEmpty()) {
				branchListDto.setCustId(custId);
				branchListDto.setBranchList(branchList.stream().map(branch -> convertToBranchDto(branch)).collect(Collectors.toList()));
				branchListDto.status = new Status(false, 200, "Success");
			}else {
				branchListDto.status = new Status(false, 400, "Not found");
			}

		} catch (Exception e) {
			branchListDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}

		return branchListDto;
	}
	@Override
	public BranchListDto getBranchListByCustIdAndLoginId(Integer custId,Integer loginId) {
		BranchListDto branchListDto = new BranchListDto();

		try {
			List<Branch> branchList = branchRepository.getBranchByCustomer_custIdAndUser_loginId(custId,loginId);


			if(!branchList.isEmpty()) {
				branchListDto.setCustId(custId);
				branchListDto.setLoginId(loginId);
				branchListDto.setBranchList(branchList.stream().map(branch -> convertToBranchDto(branch)).collect(Collectors.toList()));
				branchListDto.status = new Status(false, 200, "Success");
			}else {
				branchListDto.status = new Status(false, 400, "Not found");
			}

		} catch (Exception e) {
			branchListDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}

		return branchListDto;
	}
	@Override
	public BranchListDto getBranchDetailsByCustIdAndLoginId(Integer custId, Integer loginId) {
		BranchListDto branchListDto = new BranchListDto();

		try {
			List<Branch> branchList = branchRepository.getBranchDetailsByCustomer_custIdAndUser_loginId(custId,loginId);


			if(!branchList.isEmpty()) {
				branchListDto.setCustId(custId);
				branchListDto.setLoginId(loginId);
				branchListDto.setBranchList(branchList.stream().map(branch -> convertToBranchDto(branch)).collect(Collectors.toList()));
				branchListDto.status = new Status(false, 200, "Success");
			}else {
				branchListDto.status = new Status(false, 400, "Not found");
			}

		} catch (Exception e) {
			branchListDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}

		return branchListDto;
	}

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
	public EmployeeDtatListDto getEmpDataByBrID(Integer brId) {
		EmployeeDtatListDto employeeDtatListDto = new EmployeeDtatListDto();
		List<EmployeeDto> employeeDtoList = new ArrayList<>();
		try {
			List<Object[]> employeeDataList = employeeLeaveRepository.findEmpDataByBrId(brId);

			if(!employeeDataList.isEmpty()) {
				for(Object[] p : employeeDataList) {
					EmployeeDto employeeDto = new EmployeeDto();

					employeeDto.setEmpId(Integer.valueOf(p[0].toString()));
					employeeDto.setEmpName(p[1].toString());
					employeeDto.setDeptId(Integer.valueOf(p[2].toString()));
					employeeDto.setDeptName(p[3].toString());
					employeeDtoList.add(employeeDto);

					employeeDtatListDto.setEmployeeDtoList(employeeDtoList);
					employeeDtatListDto.setBrId(brId);
					employeeDtatListDto.status = new Status(false, 200, "Success");
				}
			}else {
				employeeDtatListDto.status = new Status(false, 400, "Not found");
			}

		} catch (Exception e) {
			employeeDtatListDto.status = new Status(false, 500, "Oops..! Something went wrong..");
		}

		return employeeDtatListDto;
	}	


	@Override
	public Status saveEmpLeave(EmployeeLeaveDto employeeLeaveDto) {
		Status status = null;
		final Lock displayLock = this.displayQueueLock;
		
		try {
			displayLock.lock();
			Employee employee = employeeRepository.getByEmpId(employeeLeaveDto.getEmpId());
			if(employeeLeaveDto.getEmpLeaveFrom().compareTo(employee.getEmpJoiningDate()) < 0) {
				status = new Status(true, 400, "Employee can't apply leave before joining date");
			} else if(employee != null) {
				CustLeave custLeave = custLeaveRepository.findCustLeaveByCustLeaveIdAndCustLeaveIsActive(employeeLeaveDto.getCustLeaveId(), true);
				if(custLeave != null) {
					int empLeave = employeeLeaveRepository.getEmpLeaveAlreadyApplied(employeeLeaveDto.getEmpLeaveFrom(), employeeLeaveDto.getEmpLeaveTo(), employeeLeaveDto.getEmpId());
					if(empLeave == 0) {
						EmployeeLeave employeeLeave = modelMapper.map(employeeLeaveDto, EmployeeLeave.class);
						Integer countNoOfDays = employeeLeaveRepository.getNoOfDaysCount(employeeLeaveDto.getEmpLeaveFrom(), employeeLeaveDto.getEmpLeaveTo());
						employeeLeave.setEmployee(employee);
						employeeLeave.setCustLeave(custLeave);
						employeeLeave.setEmpLeaveDaysCount(countNoOfDays);
						employeeLeave.setEmpLeaveAppliedDatetime(new Date());
						employeeLeave.setEmpLeaveStatus("");
						employeeLeaveRepository.save(employeeLeave);
						status = new Status(false, 200, "Leave Applied for "+employee.getEmpName());
						
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
		finally {
			displayLock.unlock();
		}
		return status;
	}

	public BranchDto convertToBranchDto(Branch branch) {
		BranchDto  branchDto = modelMapper.map(branch, BranchDto.class);
		branchDto.setBrId(branch.getBrId());
		branchDto.setBrName(branch.getBrName());
		return branchDto;
	}

	public CustLeaveTypeDto convertToCustLeaveTypeDto(CustLeave custLeave) {
		CustLeaveTypeDto  custLeaveTypeDto = modelMapper.map(custLeave, CustLeaveTypeDto.class);
		custLeaveTypeDto.setCustLeaveId(custLeave.getCustLeaveId());
		custLeaveTypeDto.setCustLeaveName(custLeave.getCustLeaveName());
		return custLeaveTypeDto;
	}
	@Override
	public BranchListDto getAllBranchListByCustId(Integer custId) {
		BranchListDto branchListDto = new BranchListDto();

		try {
			List<Branch> branchList = branchRepository.getAllBranchListByCustId(custId);


			if(!branchList.isEmpty()) {
				branchListDto.setCustId(custId);
				branchListDto.setBranchList(branchList.stream().map(branch -> convertToBranchDto(branch)).collect(Collectors.toList()));
				branchListDto.status = new Status(false, 200, "Success");
			}else {
				branchListDto.status = new Status(false, 400, "Not found");
			}

		} catch (Exception e) {
			branchListDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}

		return branchListDto;
	}
}