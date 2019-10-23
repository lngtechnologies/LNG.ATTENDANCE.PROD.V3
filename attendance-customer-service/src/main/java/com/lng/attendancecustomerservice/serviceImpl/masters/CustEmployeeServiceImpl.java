package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Branch;
import com.lng.attendancecustomerservice.entity.masters.Department;
import com.lng.attendancecustomerservice.entity.masters.Designation;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.masters.EmployeeBranch;
import com.lng.attendancecustomerservice.entity.masters.EmployeeDepartment;
import com.lng.attendancecustomerservice.entity.masters.EmployeeDesignation;
import com.lng.attendancecustomerservice.entity.masters.EmployeeShift;
import com.lng.attendancecustomerservice.entity.masters.Shift;
import com.lng.attendancecustomerservice.repositories.masters.BranchRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustEmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.repositories.masters.DepartmentRepository;
import com.lng.attendancecustomerservice.repositories.masters.DesignationRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeBranchRepositories;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeDepartmentRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeDesignationRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeShiftRepository;
import com.lng.attendancecustomerservice.repositories.masters.ShiftRepository;
import com.lng.attendancecustomerservice.service.masters.CustEmployeeService;
import com.lng.attendancecustomerservice.utils.Encoder;
import com.lng.dto.employee.EmployeeDto;
import com.lng.dto.masters.custEmployee.CustEmployeeDto;

import status.StatusDto;

@Service
public class CustEmployeeServiceImpl implements CustEmployeeService {

	ModelMapper modelMapper = new ModelMapper();
	
	Encoder encoder = new Encoder();

	@Autowired
	CustEmployeeRepository custEmployeeRepository;

	@Autowired
	BranchRepository branchRepository;

	@Autowired 
	CustomerRepository customerRepository;

	@Autowired
	EmployeeBranchRepositories employeeBranchRepositories;

	@Autowired
	DepartmentRepository departmentRepository;

	@Autowired
	EmployeeDepartmentRepository employeeDepartmentRepository;

	@Autowired
	DesignationRepository designationRepository;

	@Autowired
	EmployeeDesignationRepository employeeDesignationRepository;

	@Autowired
	ShiftRepository shiftRepository;

	@Autowired
	EmployeeShiftRepository employeeShiftRepository;

	@Override
	public StatusDto save(CustEmployeeDto custEmployeeDto) {
		StatusDto statusDto = new StatusDto();
		try {
			
			Employee employee = saveCustEmployeeData(custEmployeeDto);
			if(employee != null) {
				EmployeeBranch employeeBranch = new EmployeeBranch();
				try {
					
					Branch branch = branchRepository.findBranchByBrId(custEmployeeDto.getBrId());		
					employeeBranch.setEmployee(employee);
					employeeBranch.setBranch(branch);
					employeeBranch.setBranchFromDate(new Date());
					
				}catch (Exception e) {
					e.printStackTrace();
				}

				if(employeeBranch != null) {
					saveEmpBranch(employeeBranch);
				}

				EmployeeDepartment employeeDepartment = new EmployeeDepartment();
				try {
					Department department = departmentRepository.findDepartmentByDeptId(custEmployeeDto.getDeptartmentId());
					employeeDepartment.setEmployee(employee);
					employeeDepartment.setDepartment(department);
					employeeDepartment.setEmpFromDate(new Date());
				}catch (Exception e) {	
					e.printStackTrace();
				}

				if(employeeDepartment != null) {
					saveEmpDept(employeeDepartment);
				}

				EmployeeDesignation employeeDesignation = new EmployeeDesignation();
				try {
					Designation designation = designationRepository.findDesignationByDesignationId(custEmployeeDto.getDesignationId());
					employeeDesignation.setEmployee(employee);
					employeeDesignation.setDesignation(designation);
					employeeDesignation.setEmpFromDate(new Date());
				}catch (Exception e) {	
					e.printStackTrace();
				}

				if(employeeDesignation != null) {
					saveEmpDesgn(employeeDesignation);
				}

				EmployeeShift employeeShift = new EmployeeShift();
				try {
					Shift shift = shiftRepository.findShiftByShiftId(custEmployeeDto.getShiftId());
					employeeShift.setEmployee(employee);
					employeeShift.setShift(shift);
					employeeShift.setShiftFromDate(new Date());
				}catch (Exception e) {	
					e.printStackTrace();
				}

				if(employeeShift != null) {
					saveEmpShift(employeeShift);
				}
				
				statusDto.setCode(200);
				statusDto.setError(false);
				statusDto.setMessage("Successfully Employee Created");
			} else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Cannot create");
			}
			
		}catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage(e.getMessage());
		}
		
		return statusDto;
	}


	public Employee saveCustEmployeeData(CustEmployeeDto custEmployeeDto) {

		Employee employee = modelMapper.map(custEmployeeDto, Employee.class);
		try {
			Branch branch = branchRepository.findBranchByBrId(custEmployeeDto.getBrId());
			employee.setEmpInService(true);
			employee.setEmpPassword(encoder.getEncoder().encode(branch.getBrCode()));
			employee = custEmployeeRepository.save(employee);
		}catch (Exception e) {
			e.printStackTrace();
		}
				
		return employee;
	}

	//set EmpId and Branch Id to empBranch
	/*public EmployeeBranch setEmpBranch(Employee employee) {
		CustEmployeeDto custEmployeeDto = new CustEmployeeDto();
		EmployeeBranch employeeBranch = new EmployeeBranch();
		try {
			Branch branch = branchRepository.findBranchByBrId(custEmployeeDto.getBrId());		
			employeeBranch.setEmployee(employee);
			employeeBranch.setBranch(branch);
			employeeBranch.setBranchFromDate(new Date());
		}catch (Exception e) {	
			e.printStackTrace();
		}				
		return employeeBranch;
	}*/

	//Save to empBranch table
	public EmployeeBranch saveEmpBranch(EmployeeBranch employeeBranch) {
		try {
			employeeBranchRepositories.save(employeeBranch);
		}catch (Exception e) {	
			e.printStackTrace();
		}	

		return employeeBranch;		
	}

	//set EmpId and DeptId to empDept
	/*public EmployeeDepartment setEmpDept(Employee employee) {
		CustEmployeeDto custEmployeeDto = new CustEmployeeDto();
		EmployeeDepartment employeeDepartment = new EmployeeDepartment();
		try {
			Department department = departmentRepository.findDepartmentByDeptId(custEmployeeDto.getDeptartmentId());
			employeeDepartment.setEmployee(employee);
			employeeDepartment.setDepartment(department);
			employeeDepartment.setEmpFromDate(new Date());
		}catch (Exception e) {	
			e.printStackTrace();
		}	

		return employeeDepartment;		
	}*/

	//Save to empDept table
	public EmployeeDepartment saveEmpDept(EmployeeDepartment employeeDepartment) {
		try {
			employeeDepartmentRepository.save(employeeDepartment);
		}catch (Exception e) {	
			e.printStackTrace();
		}

		return employeeDepartment;
	}

	//set EmpId and DesignationId to empDesg
	/*public EmployeeDesignation setEmpDesgn(Employee employee) {
		CustEmployeeDto custEmployeeDto = new CustEmployeeDto();
		EmployeeDesignation employeeDesignation = new EmployeeDesignation();
		try {
			Designation designation = designationRepository.findDesignationByDesignationId(custEmployeeDto.getDesignationId());
			employeeDesignation.setEmployee(employee);
			employeeDesignation.setDesignation(designation);
			employeeDesignation.setEmpFromDate(new Date());
		}catch (Exception e) {	
			e.printStackTrace();
		}

		return employeeDesignation;
	}*/

	// Save to employee designation table
	public EmployeeDesignation saveEmpDesgn(EmployeeDesignation employeeDesignation) {
		try {
			employeeDesignationRepository.save(employeeDesignation);
		}catch (Exception e) {	
			e.printStackTrace();
		}

		return employeeDesignation;
	}

	//set EmpId and DesignationId to empShift
	/*public EmployeeShift setEmpShift(Employee employee) {
		CustEmployeeDto custEmployeeDto = new CustEmployeeDto();
		EmployeeShift employeeShift = new EmployeeShift();
		try {
			Shift shift = shiftRepository.findShiftByShiftId(custEmployeeDto.getShiftId());
			employeeShift.setEmployee(employee);
			employeeShift.setShift(shift);
			employeeShift.setShiftFromDate(new Date());
		}catch (Exception e) {	
			e.printStackTrace();
		}

		return employeeShift;
	}*/

	// Save to employee designation table
	public EmployeeShift saveEmpShift(EmployeeShift employeeShift) {
		try {
			employeeShiftRepository.save(employeeShift);
		}catch (Exception e) {	
			e.printStackTrace();
		}

		return employeeShift;
	}


	public EmployeeDto convertToCustEmployeeDto(Employee employee) {
		EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);
		return employeeDto;
	}
}
