package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Branch;
import com.lng.attendancecustomerservice.entity.masters.Contractor;
import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.entity.masters.Department;
import com.lng.attendancecustomerservice.entity.masters.Designation;
import com.lng.attendancecustomerservice.entity.masters.EmpMonthlyNoOfDays;
import com.lng.attendancecustomerservice.entity.masters.EmpWeeklyOffDay;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.masters.EmployeeBranch;
import com.lng.attendancecustomerservice.entity.masters.EmployeeDepartment;
import com.lng.attendancecustomerservice.entity.masters.EmployeeDesignation;
import com.lng.attendancecustomerservice.entity.masters.EmployeeShift;
import com.lng.attendancecustomerservice.entity.masters.EmployeeType;
import com.lng.attendancecustomerservice.entity.masters.Shift;
import com.lng.attendancecustomerservice.repositories.masters.BranchRepository;
import com.lng.attendancecustomerservice.repositories.masters.ContractorRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustEmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.repositories.masters.DepartmentRepository;
import com.lng.attendancecustomerservice.repositories.masters.DesignationRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmpMonthlyNoOfDaysRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmpWeeklyOffDayRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeBranchRepositories;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeDepartmentRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeDesignationRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeShiftRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeTypeRepository;
import com.lng.attendancecustomerservice.repositories.masters.ShiftRepository;
import com.lng.attendancecustomerservice.service.masters.CustEmployeeService;
import com.lng.attendancecustomerservice.utils.Encoder;
import com.lng.dto.customer.CustomerDtoTwo;
import com.lng.dto.masters.custEmployee.CustEmployeeDto;
import com.lng.dto.masters.custEmployee.CustEmployeeDtoTwo;
import com.lng.dto.masters.custEmployee.CustEmployeeListResponse;
import com.lng.dto.masters.custEmployee.CustEmployeeResponse;
import com.lng.dto.masters.custEmployee.CustEmployeeStatus;

import status.Status;

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
	DepartmentRepository departmentRepository;

	@Autowired
	EmployeeBranchRepositories employeeBranchRepositories;

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

	@Autowired
	EmpWeeklyOffDayRepository empWeeklyOffDayRepository;

	@Autowired
	EmpMonthlyNoOfDaysRepository empMonthlyNoOfDaysRepository;

	@Autowired
	EmployeeTypeRepository employeeTypeRepository;

	@Autowired
	ContractorRepository contractorRepository;

	@Override
	@Transactional(rollbackOn={Exception.class})
	public CustEmployeeStatus save(CustEmployeeDto custEmployeeDto) {
		CustEmployeeStatus custEmployeeStatus = new CustEmployeeStatus();
		Employee employee = new Employee();

		try {
			List<Employee> employee1 = custEmployeeRepository.findAllEmployeeByEmpMobile(custEmployeeDto.getEmpMobile());

			if(employee1.isEmpty()) {

				employee = saveCustEmployeeData(custEmployeeDto);

				if(employee != null) {

					//set EmpId and Branch Id to empBranch
					EmployeeBranch employeeBranch = new EmployeeBranch();
					try {

						Branch branch = branchRepository.findBranchByBrId(custEmployeeDto.getBrId());
						if(branch == null) throw new Exception("Cannot find branch id for Employee Branch");
						employeeBranch.setEmployee(employee);
						employeeBranch.setBranch(branch);
						employeeBranch.setBranchFromDate(custEmployeeDto.getEmployeeBranchFromDate());
						Date empBrToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeBranchFromDate());
						employeeBranch.setBranchToDate(empBrToDate);

					}catch (Exception e) {
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}

					if(employeeBranch != null) {
						saveEmpBranch(employeeBranch);
					}

					//set EmpId and DeptId to empDept
					EmployeeDepartment employeeDepartment = new EmployeeDepartment();
					try {
						Department department = departmentRepository.findDepartmentByDeptId(custEmployeeDto.getDepartmentId());
						if(department == null) throw new Exception("Cannot find department id for Employee Department");
						employeeDepartment.setEmployee(employee);
						employeeDepartment.setDepartment(department);
						employeeDepartment.setEmpFromDate(custEmployeeDto.getEmployeeDepartmentFromDate());
						Date empDeptToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeDepartmentFromDate());
						employeeDepartment.setEmpToDate(empDeptToDate);
					}catch (Exception e) {	
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}

					if(employeeDepartment != null) {
						saveEmpDept(employeeDepartment);
					}

					//set EmpId and DesignationId to empDesg
					EmployeeDesignation employeeDesignation = new EmployeeDesignation();
					try {
						Designation designation = designationRepository.findDesignationByDesignationId(custEmployeeDto.getDesignationId());
						if(designation == null) throw new Exception("Cannot find designation id for Employee Designation");
						employeeDesignation.setEmployee(employee);
						employeeDesignation.setDesignation(designation);
						employeeDesignation.setEmpFromDate(custEmployeeDto.getEmployeeDesignationFromDate());
						Date empDesgToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeDesignationFromDate());
						employeeDesignation.setEmpToDate(empDesgToDate);
					}catch (Exception e) {	
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}

					if(employeeDesignation != null) {
						saveEmpDesgn(employeeDesignation);
					}

					//set EmpId and ShiftId to empShift
					EmployeeShift employeeShift = new EmployeeShift();
					try {
						Shift shift = shiftRepository.findShiftByShiftId(custEmployeeDto.getShiftId());
						if(shift == null) throw new Exception("Cannot find shift id for Employee Shift");
						employeeShift.setEmployee(employee);
						employeeShift.setShift(shift);
						employeeShift.setShiftFromDate(custEmployeeDto.getEmployeeShiftFromDate());
						Date empShiftToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeShiftFromDate());
						employeeShift.setShiftToDate(empShiftToDate);
					}catch (Exception e) {	
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}

					if(employeeShift != null) {
						saveEmpShift(employeeShift);
					}

					//set EmpId and WeekoffDay to empShift
					EmpWeeklyOffDay empWeeklyOffDay = new EmpWeeklyOffDay();
					try {
						empWeeklyOffDay.setEmployee(employee);
						empWeeklyOffDay.setYearMonth(new Date());
						empWeeklyOffDay.setDayOfWeek(custEmployeeDto.getDayOfWeek());
						empWeeklyOffDay.setFromDate(custEmployeeDto.getEmpWeeklyOffDayFromDate());
						Date empweeklyOffToDate = subtractDaysFromDate(custEmployeeDto.getEmpWeeklyOffDayFromDate());
						empWeeklyOffDay.setToDate(empweeklyOffToDate);
					}catch (Exception e) {
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}
					if(empWeeklyOffDay != null) {
						saveEmpWeekOffDay(empWeeklyOffDay);
					}

					//set EmpId and MonthlyNoOfDays to empShift
					EmpMonthlyNoOfDays empMonthlyNoOfDays = new EmpMonthlyNoOfDays();
					try {
						empMonthlyNoOfDays.setEmployee(employee);
						empMonthlyNoOfDays.setYearMonth(new Date());
						Integer NoOfDays = empMonthlyNoOfDaysRepository.findNoOfDaysByYearMonth(empMonthlyNoOfDays.getYearMonth());
						empMonthlyNoOfDays.setNoOfDays(NoOfDays);
					}catch (Exception e) {
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}

					if(empMonthlyNoOfDays != null) {
						saveEmpMonthlyNoOfDays(empMonthlyNoOfDays);
					}

					custEmployeeStatus.status = new Status(false, 200, "Successfully Saved");
				} else {
					custEmployeeStatus.status = new Status(true, 400, "Cannot Save");
				}
			}else {
				custEmployeeStatus.status = new Status(true, 400, "Employee Mobile Number Already Exists");
			}

		}catch (Exception e) {
			custEmployeeStatus.status = new Status(true, 400, "Oops...! Something Went Wrong");
		}

		return custEmployeeStatus;
	}

	public Employee saveCustEmployeeData(CustEmployeeDto custEmployeeDto) {		

		Employee employee = modelMapper.map(custEmployeeDto, Employee.class);

		try {
			Branch branch = branchRepository.findBranchByBrId(custEmployeeDto.getBrId());
			Customer customer = customerRepository.findCustomerByCustId(custEmployeeDto.getCustId());

			if(custEmployeeDto.getContractorId() == null) {
				employee.setRefContractorId(0);
			}else {
				employee.setRefContractorId(custEmployeeDto.getContractorId());
			}
			employee.setBranch(branch);
			employee.setCustomer(customer);
			employee.setEmpInService(true);
			employee.setEmpPassword(encoder.getEncoder().encode(branch.getBrCode()));
			employee.setEmpAppSetupStatus(false);
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

	//set EmpId and ShiftId to empShift
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

	public EmpWeeklyOffDay saveEmpWeekOffDay(EmpWeeklyOffDay empWeeklyOffDay) {
		try {
			empWeeklyOffDayRepository.save(empWeeklyOffDay);
		}catch (Exception e) {	
			e.printStackTrace();
		}

		return empWeeklyOffDay;
	}

	public EmpMonthlyNoOfDays saveEmpMonthlyNoOfDays(EmpMonthlyNoOfDays empMonthlyNoOfDays) {
		try {
			empMonthlyNoOfDaysRepository.save(empMonthlyNoOfDays);
		}catch (Exception e) {	
			e.printStackTrace();
		}
		return empMonthlyNoOfDays;
	}


	@Override
	public CustEmployeeListResponse findEmployeeByEmpId(Integer empId) {

		CustEmployeeListResponse custEmployeeListResponse = new CustEmployeeListResponse();
		List<CustEmployeeDtoTwo> custEmployeeDtoTwoList = new ArrayList<>();

		try {
			List<Object[]> employeeList = custEmployeeRepository.findAllEmployeeByEmpIdAndEmpInService(empId);
			Employee employee = custEmployeeRepository.findEmpReportingToByEmpId(empId);

			if(employeeList.isEmpty()) {
				custEmployeeListResponse.status = new Status(true, 4000, "Not Found");

			} else {
				for(Object[] p : employeeList) {

					CustEmployeeDtoTwo custEmployeeDto = new CustEmployeeDtoTwo();
					custEmployeeDto.setEmpId(Integer.valueOf(p[0].toString()));
					custEmployeeDto.setBrId(Integer.valueOf(p[1].toString()));
					custEmployeeDto.setCustId(Integer.valueOf(p[2].toString()));
					custEmployeeDto.setContractorId(Integer.valueOf(p[3].toString()));
					custEmployeeDto.setShiftId(Integer.valueOf(p[4].toString()));
					custEmployeeDto.setEmpTypeId(Integer.valueOf(p[5].toString()));
					custEmployeeDto.setEmpName(p[6].toString());
					custEmployeeDto.setEmpMobile(p[7].toString());
					custEmployeeDto.setEmpGender(p[8].toString());
					custEmployeeDto.setEmpInService(Boolean.valueOf(p[9].toString()));
					custEmployeeDto.setEmpIsSupervisor_Manager(Boolean.valueOf(p[10].toString()));
					custEmployeeDto.setEmpReportingToId(Integer.valueOf(p[11].toString()));
					custEmployeeDto.setEmpJoiningDate((Date)p[12]);
					custEmployeeDto.setBrName(p[13].toString());
					custEmployeeDto.setShiftName(p[14].toString());
					custEmployeeDto.setDeptName(p[15].toString());
					custEmployeeDto.setDesignationName(p[16].toString());
					custEmployeeDto.setContractorName(p[17].toString());
					custEmployeeDto.setEmpType(p[18].toString());
					custEmployeeDto.setEmpReportingTo(employee.getEmpName());

					custEmployeeDtoTwoList.add(custEmployeeDto);
					custEmployeeListResponse.setEmployyeList(custEmployeeDtoTwoList);
					custEmployeeListResponse.status = new Status(false, 2000, "Success");
				}
			}
		} catch (Exception e) {
			custEmployeeListResponse.status = new Status(true, 5000, "Opps...! Something Went Wrong");
		}
		return custEmployeeListResponse;
	}

	@Override
	public CustEmployeeStatus updateEmployee(CustEmployeeDto custEmployeeDto) {
		CustEmployeeStatus custEmployeeStatus = new CustEmployeeStatus();

		try {
			Employee employee = custEmployeeRepository.findEmployeeByEmpIdAndEmpInService(custEmployeeDto.getEmpId(), true);
			Customer customer =  customerRepository.findCustomerByCustId(custEmployeeDto.getCustId());
			EmployeeType employeeType = employeeTypeRepository.findEmployeeTypeByEmpTypeId(custEmployeeDto.getEmpTypeId());
			Shift shift = shiftRepository.findShiftByShiftId(custEmployeeDto.getShiftId());
			Branch branch = branchRepository.findBranchByBrId(custEmployeeDto.getBrId());
			// Contractor contractor = contractorRepository.findContractorByContractorId(custEmployeeDto.getContractorId());
			if(employee != null) {
				employee.setBranch(branch);
				employee.setCustomer(customer);
				employee.setEmployeeType(employeeType);
				employee.setShift(shift);
				if(custEmployeeDto.getContractorId() != null) {
					employee.setRefContractorId(custEmployeeDto.getContractorId());
				}else {
					employee.setRefContractorId(0);
				}

				employee.setEmpReportingTo(custEmployeeDto.getEmpReportingToId());
				employee.setEmpName(custEmployeeDto.getEmpName());
				employee.setEmpMobile(custEmployeeDto.getEmpMobile());
				employee.setEmpGender(custEmployeeDto.getEmpGender());
				employee.setEmpJoiningDate(custEmployeeDto.getEmpJoiningDate());
				employee.setEmpIsSupervisor_Manager(custEmployeeDto.getEmpIsSupervisor_Manager());
				custEmployeeRepository.save(employee);

				if(employee != null) {

					EmployeeDepartment employeeDepartment = employeeDepartmentRepository.findByEmployee_EmpId(custEmployeeDto.getEmpId());
					try {
						Department department = departmentRepository.findDepartmentByDeptId(custEmployeeDto.getDepartmentId());
						if(department == null) throw new Exception("Cannot find department id for Employee Department");
						employeeDepartment.setEmployee(employee);
						employeeDepartment.setDepartment(department);
						employeeDepartment.setEmpFromDate(custEmployeeDto.getEmployeeDepartmentFromDate());
						Date empDeptToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeDepartmentFromDate());
						employeeDepartment.setEmpToDate(empDeptToDate);

						employeeDepartmentRepository.save(employeeDepartment);
					}catch (Exception e) {	
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}

					//set EmpId and DesignationId to empDesg
					EmployeeDesignation employeeDesignation = employeeDesignationRepository.findByEmployee_EmpId(custEmployeeDto.getEmpId());
					try {
						Designation designation = designationRepository.findDesignationByDesignationId(custEmployeeDto.getDesignationId());
						if(designation == null) throw new Exception("Cannot find designation id for Employee Designation");
						employeeDesignation.setEmployee(employee);
						employeeDesignation.setDesignation(designation);
						employeeDesignation.setEmpFromDate(custEmployeeDto.getEmployeeDesignationFromDate());
						Date empDesgToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeDesignationFromDate());
						employeeDesignation.setEmpToDate(empDesgToDate);

						employeeDesignationRepository.save(employeeDesignation);

					}catch (Exception e) {	
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}

					//set EmpId and ShiftId to empShift
					EmployeeShift employeeShift = employeeShiftRepository.findByEmployee_EmpId(custEmployeeDto.getEmpId());
					try {
						Shift shift1 = shiftRepository.findShiftByShiftId(custEmployeeDto.getShiftId());
						if(shift1 == null) throw new Exception("Cannot find shift id for Employee Shift");
						employeeShift.setEmployee(employee);
						employeeShift.setShift(shift1);
						employeeShift.setShiftFromDate(custEmployeeDto.getEmployeeShiftFromDate());
						Date empShiftToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeShiftFromDate());
						employeeShift.setShiftToDate(empShiftToDate);

						employeeShiftRepository.save(employeeShift);

					}catch (Exception e) {	
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}


					//set EmpId and WeekoffDay to empShift
					EmpWeeklyOffDay empWeeklyOffDay = empWeeklyOffDayRepository.findEEmpWeeklyOffDayByEmployee_EmpId(custEmployeeDto.getEmpId());
					try {
						empWeeklyOffDay.setEmployee(employee);
						empWeeklyOffDay.setYearMonth(new Date());
						empWeeklyOffDay.setDayOfWeek(custEmployeeDto.getDayOfWeek());
						empWeeklyOffDay.setFromDate(custEmployeeDto.getEmpWeeklyOffDayFromDate());
						Date empweeklyOffToDate = subtractDaysFromDate(custEmployeeDto.getEmpWeeklyOffDayFromDate());
						empWeeklyOffDay.setToDate(empweeklyOffToDate);

						empWeeklyOffDayRepository.save(empWeeklyOffDay);

					}catch (Exception e) {
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}


					//set EmpId and MonthlyNoOfDays to empShift
					EmpMonthlyNoOfDays empMonthlyNoOfDays = empMonthlyNoOfDaysRepository.findByEmployee_EmpId(custEmployeeDto.getEmpId());
					try {
						empMonthlyNoOfDays.setEmployee(employee);
						empMonthlyNoOfDays.setYearMonth(new Date());
						Integer NoOfDays = empMonthlyNoOfDaysRepository.findNoOfDaysByYearMonth(empMonthlyNoOfDays.getYearMonth());
						empMonthlyNoOfDays.setNoOfDays(NoOfDays);
						
						empMonthlyNoOfDaysRepository.save(empMonthlyNoOfDays);
						
					}catch (Exception e) {
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}

				}

				custEmployeeStatus.status = new Status(false, 200, "Successfully Updated");
			} else {
				custEmployeeStatus.status = new Status(true, 400, "Employee Not Found or Employee Not In Service");
			}
		} catch (Exception e) {
			custEmployeeStatus.status = new Status(true, 500, "Opps...! Something Went Wrong");
		}
		return custEmployeeStatus;
	}

	@Override
	public CustEmployeeListResponse findAll() {
		CustEmployeeListResponse custEmployeeListResponse = new CustEmployeeListResponse();
		List<CustEmployeeDtoTwo> custEmployeeDtoTwoList = new ArrayList<>();

		try {

			List<Object[]> employeeList = custEmployeeRepository.findAllEmployeeByEmpInService();

			if(employeeList.isEmpty()) {
				custEmployeeListResponse.status = new Status(true, 4000, "Not Found");
			}else {
				for(Object[] p : employeeList) {

					CustEmployeeDtoTwo custEmployeeDto = new CustEmployeeDtoTwo();
					custEmployeeDto.setEmpId(Integer.valueOf(p[0].toString()));
					custEmployeeDto.setBrId(Integer.valueOf(p[1].toString()));
					custEmployeeDto.setCustId(Integer.valueOf(p[2].toString()));
					custEmployeeDto.setContractorId(Integer.valueOf(p[3].toString()));
					custEmployeeDto.setShiftId(Integer.valueOf(p[4].toString()));
					custEmployeeDto.setEmpTypeId(Integer.valueOf(p[5].toString()));
					custEmployeeDto.setEmpName(p[6].toString());
					custEmployeeDto.setEmpMobile(p[7].toString());
					custEmployeeDto.setEmpGender(p[8].toString());
					custEmployeeDto.setEmpInService(Boolean.valueOf(p[9].toString()));
					custEmployeeDto.setEmpIsSupervisor_Manager(Boolean.valueOf(p[10].toString()));
					custEmployeeDto.setEmpReportingToId(Integer.valueOf(p[11].toString()));
					custEmployeeDto.setEmpJoiningDate((Date)p[12]);
					custEmployeeDto.setBrName(p[13].toString());
					custEmployeeDto.setShiftName(p[14].toString());
					custEmployeeDto.setDeptName(p[15].toString());
					custEmployeeDto.setDesignationName(p[16].toString());
					custEmployeeDto.setContractorName(p[17].toString());
					custEmployeeDto.setEmpType(p[18].toString());

					custEmployeeDtoTwoList.add(custEmployeeDto);
					custEmployeeListResponse.setEmployyeList(custEmployeeDtoTwoList);
					custEmployeeListResponse.status = new Status(false, 2000, "Success");
				}

			}
		} catch (Exception e) {
			custEmployeeListResponse.status = new Status(true, 5000, "Opps...! Something Went Wrong");
		}
		return custEmployeeListResponse;
	}

	@Override
	public CustEmployeeStatus deleteEmployeeByEmpIdId(Integer empId) {

		CustEmployeeStatus custEmployeeStatus = new CustEmployeeStatus();

		Employee employee = custEmployeeRepository.findEmployeeByEmpIdAndEmpInService(empId, true);
		try {
			if(employee != null) {
				employee.setEmpInService(false);
				custEmployeeRepository.save(employee);
				custEmployeeStatus.status = new Status(false, 200, "Successfully Deleted");
			} else {
				custEmployeeStatus.status = new Status(true, 400, "Employee Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace(); 
			custEmployeeStatus.status = new Status(true, 5000, "Opps...! Something Went Wrong");
		}
		return custEmployeeStatus;
	}

	@Override
	public CustEmployeeListResponse searchEmployeeByEmpName(String empName) {

		CustEmployeeListResponse custEmployeeListResponse = new CustEmployeeListResponse();

		try {

			if(empName.length() >= 3 && empName.length() <= 10) {

				List<Employee> employeeList = custEmployeeRepository.searchAllEmployeeByEmpName(empName);;

				custEmployeeListResponse.setEmployyeList(employeeList.stream().map(employee -> convertToCustEmployeeDtoTwo(employee)).collect(Collectors.toList()));

				if(custEmployeeListResponse != null && custEmployeeListResponse.getEmployyeList() != null) {
					custEmployeeListResponse.status = new Status(false, 2000, "Success");
				}else {
					custEmployeeListResponse.status = new Status(true, 4000, "Not Found");
				}
			} else {
				custEmployeeListResponse.status = new Status(true, 4000, "Data too long or too less");
			}
		} catch (Exception e) {
			custEmployeeListResponse.status = new Status(true, 5000, "Opps...! Something Went Wrong");
		}
		return custEmployeeListResponse;
	}



	public CustEmployeeDto convertToCustEmployeeDto(Employee employee) {
		CustEmployeeDto custEmployeeDto = modelMapper.map(employee, CustEmployeeDto.class);
		custEmployeeDto.setBrId(employee.getBranch().getBrId());
		custEmployeeDto.setCustId(employee.getCustomer().getCustId());
		//custEmployeeDto.setContractorId(employee.getContractor().getContractorId());		
		return custEmployeeDto;
	}

	public CustEmployeeDtoTwo convertToCustEmployeeDtoTwo(Employee employee) {

		CustEmployeeDtoTwo custEmployeeDtoTwo = modelMapper.map(employee, CustEmployeeDtoTwo.class);
		Contractor contractor = contractorRepository.findContractorByContractorId(custEmployeeDtoTwo.getContractorId());
		custEmployeeDtoTwo.setBrId(employee.getBranch().getBrId());
		custEmployeeDtoTwo.setBrName(employee.getBranch().getBrName());
		custEmployeeDtoTwo.setContractorName(contractor.getContractorName());
		custEmployeeDtoTwo.setShiftName(employee.getShift().getShiftName());
		custEmployeeDtoTwo.setEmpType(employee.getEmployeeType().getEmpType());
		return custEmployeeDtoTwo;
	}

	public Date subtractDaysFromDate(Date date) {
		Date toDate = custEmployeeRepository.subtractDaysFromDate(date);
		return toDate;
	}
}
