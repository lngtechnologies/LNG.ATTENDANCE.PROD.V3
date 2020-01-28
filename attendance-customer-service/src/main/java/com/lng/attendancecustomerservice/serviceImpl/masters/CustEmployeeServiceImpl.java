package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Block;
import com.lng.attendancecustomerservice.entity.masters.Branch;
import com.lng.attendancecustomerservice.entity.masters.Contractor;
import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.entity.masters.Department;
import com.lng.attendancecustomerservice.entity.masters.Designation;
import com.lng.attendancecustomerservice.entity.masters.EmpMonthlyNoOfDays;
import com.lng.attendancecustomerservice.entity.masters.EmpWeeklyOffDay;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.masters.EmployeeBlock;
import com.lng.attendancecustomerservice.entity.masters.EmployeeBranch;
import com.lng.attendancecustomerservice.entity.masters.EmployeeDepartment;
import com.lng.attendancecustomerservice.entity.masters.EmployeeDesignation;
import com.lng.attendancecustomerservice.entity.masters.EmployeeReportingTo;
import com.lng.attendancecustomerservice.entity.masters.EmployeeShift;
import com.lng.attendancecustomerservice.entity.masters.EmployeeType;
import com.lng.attendancecustomerservice.entity.masters.Shift;
import com.lng.attendancecustomerservice.repositories.masters.BlockRepository;
import com.lng.attendancecustomerservice.repositories.masters.BranchRepository;
import com.lng.attendancecustomerservice.repositories.masters.ContractorRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustEmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.repositories.masters.DepartmentRepository;
import com.lng.attendancecustomerservice.repositories.masters.DesignationRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmpMonthlyNoOfDaysRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmpWeeklyOffDayRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeBlockRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeBranchRepositories;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeDepartmentRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeDesignationRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeReportingToRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeShiftRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeTypeRepository;
import com.lng.attendancecustomerservice.repositories.masters.ShiftRepository;
import com.lng.attendancecustomerservice.service.masters.CustEmployeeService;
import com.lng.attendancecustomerservice.utils.Encoder;
import com.lng.dto.employeeAppSetup.EmployeeDto2;
import com.lng.dto.masters.custEmployee.CustEmployeeDto;
import com.lng.dto.masters.custEmployee.CustEmployeeDtoTwo;
import com.lng.dto.masters.custEmployee.CustEmployeeListResponse;
import com.lng.dto.masters.custEmployee.CustEmployeeStatus;
import com.lng.dto.masters.custEmployee.EmpBlockMapDto;

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

	@Autowired
	BlockRepository blockRepository;

	@Autowired
	EmployeeBlockRepository employeeBlockRepository;

	@Autowired
	EmployeeReportingToRepository employeeReportingToRepository;

	private final Lock displayQueueLock = new ReentrantLock();

	@Override
	@Transactional(rollbackOn={Exception.class})
	public CustEmployeeStatus save(CustEmployeeDto custEmployeeDto) {
		CustEmployeeStatus custEmployeeStatus = new CustEmployeeStatus();
		Employee employee = new Employee();
		final Lock displayLock = this.displayQueueLock; 

		try {
			displayLock.lock();
			List<Employee> employee1 = custEmployeeRepository.findAllEmployeeByEmpMobileAndCustomer_CustId(custEmployeeDto.getEmpMobile(), custEmployeeDto.getCustId());

			if(employee1.isEmpty()) {

				employee = saveCustEmployeeData(custEmployeeDto);

				if(employee != null) {

					// Set empid and BlockId to empBlock
					try {
						for(EmpBlockMapDto CustEmployeeDto : custEmployeeDto.getEmpBlockMapDtoList()){
							EmployeeBlock employeeBlock = new EmployeeBlock();
							Block block = blockRepository.findByBlkId(CustEmployeeDto.getBlkId());
							if(block == null) throw new Exception("Cannot find block for Employee");
							employeeBlock.setBlock(block);
							employeeBlock.setEmployee(employee);
							employeeBlockRepository.save(employeeBlock);

						}
					} catch (Exception e) {
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}

					//set empId to employeeReportingTo
					EmployeeReportingTo employeeReportingTo = new EmployeeReportingTo();
					//String date= "0000-00-00";
					//SimpleDateFormat sf = new SimpleDateFormat("yyyy-mm-dd");
					//Date mydate = new Date(date);
					try {
						employeeReportingTo.setEmployee(employee);
						if(custEmployeeDto.getEmpReportingToId() != null) {
							employeeReportingTo.setRefEmpReportingToId(custEmployeeDto.getEmpReportingToId());
						}else {
							employeeReportingTo.setRefEmpReportingToId(0);
						}
						if(custEmployeeDto.getEmpReportingToFromDate() != null) {
							employeeReportingTo.setEmpFromDate(custEmployeeDto.getEmpReportingToFromDate());
						}else {
							employeeReportingTo.setEmpFromDate(null);
						}

						employeeReportingToRepository.save(employeeReportingTo);

					} catch (Exception e) {
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}

					//set EmpId and Branch Id to empBranch
					EmployeeBranch employeeBranch = new EmployeeBranch();
					try {

						Branch branch = branchRepository.findBranchByBrId(custEmployeeDto.getBrId());
						if(branch == null) throw new Exception("Cannot find branch for Employee");
						employeeBranch.setEmployee(employee);
						employeeBranch.setBranch(branch);
						employeeBranch.setBranchFromDate(custEmployeeDto.getEmployeeBranchFromDate());
						//Date empBrToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeBranchFromDate());
						//employeeBranch.setBranchToDate(empBrToDate);

					}catch (Exception e) {
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}

					if(employeeBranch != null) {
						saveEmpBranch(employeeBranch);
					}

					//set EmpId and DeptId to empDept
					EmployeeDepartment employeeDepartment = new EmployeeDepartment();
					try {
						Department department = departmentRepository.findDepartmentByDeptIdAndDeptIsActive(custEmployeeDto.getDepartmentId(), true);
						if(department == null) throw new Exception("Cannot find department for Employee");
						employeeDepartment.setEmployee(employee);
						employeeDepartment.setDepartment(department);
						employeeDepartment.setEmpFromDate(custEmployeeDto.getEmployeeDepartmentFromDate());
						//Date empDeptToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeDepartmentFromDate());
						//employeeDepartment.setEmpToDate(empDeptToDate);
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
						if(designation == null) throw new Exception("Cannot find designation for Employee");
						employeeDesignation.setEmployee(employee);
						employeeDesignation.setDesignation(designation);
						employeeDesignation.setEmpFromDate(custEmployeeDto.getEmployeeDesignationFromDate());
						//Date empDesgToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeDesignationFromDate());
						//employeeDesignation.setEmpToDate(empDesgToDate);
					}catch (Exception e) {	
						custEmployeeStatus.status = new Status(true, 400, e.getMessage());
					}

					if(employeeDesignation != null) {
						saveEmpDesgn(employeeDesignation);
					}

					//set EmpId and ShiftId to empShift
					EmployeeShift employeeShift = new EmployeeShift();
					try {
						Shift shift = shiftRepository.findShiftByShiftIdAndShiftIsActive(custEmployeeDto.getShiftId(), true);
						if(shift == null) throw new Exception("Cannot find shift for Employee");
						employeeShift.setEmployee(employee);
						employeeShift.setShift(shift);
						employeeShift.setShiftFromDate(custEmployeeDto.getEmployeeShiftFromDate());
						//Date empShiftToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeShiftFromDate());
						//employeeShift.setShiftToDate(empShiftToDate);
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
						//Date empweeklyOffToDate = subtractDaysFromDate(custEmployeeDto.getEmpWeeklyOffDayFromDate());
						//empWeeklyOffDay.setToDate(empweeklyOffToDate);
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

					custEmployeeStatus.status = new Status(false, 200, "created");

				} else {
					custEmployeeStatus.status = new Status(true, 400, "Cannot Save");

				}
			}else {
				custEmployeeStatus.status = new Status(true, 400, "Employee mobile number already exists");

			}

		}catch (Exception e) {
			custEmployeeStatus.status = new Status(true, 400, "Oops...! Something went wrong");

		}
		finally {
			displayLock.unlock();
		}
		return custEmployeeStatus;
	}

	public Employee saveCustEmployeeData(CustEmployeeDto custEmployeeDto) {		

		Employee employee = modelMapper.map(custEmployeeDto, Employee.class);

		try {
			Branch branch = branchRepository.findBranchByBrId(custEmployeeDto.getBrId());
			Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(custEmployeeDto.getCustId(), true);

			if(custEmployeeDto.getContractorId() == null) {
				employee.setRefContractorId(0);
			}else {
				employee.setRefContractorId(custEmployeeDto.getContractorId());
			}

			if(custEmployeeDto.getEmpIsSupervisor_Manager() == null) {
				employee.setEmpIsSupervisor_Manager(false);
			}else {
				employee.setEmpIsSupervisor_Manager(custEmployeeDto.getEmpIsSupervisor_Manager());
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

	public EmployeeBlock saveEmpBlock(EmployeeBlock employeeBlock){

		try {
			employeeBlockRepository.save(employeeBlock);

		} catch (Exception e) {

		}
		return employeeBlock;
	}

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
		Integer deptId = null;

		try {
			List<Object[]> employeeList = custEmployeeRepository.findAllEmployeeByEmpIdAndEmpInService(empId);
			Employee employee = custEmployeeRepository.findEmpReportingToByEmpId(empId);
			
			//EmpWeeklyOffDay empWeeklyOffDay = empWeeklyOffDayRepository.findEEmpWeeklyOffDayByEmployee_EmpId(employee.getEmpId());
			if(employeeList.isEmpty()) {
				custEmployeeListResponse.status = new Status(true, 4000, "Employee not found");

			} else {
				for(Object[] p : employeeList) {

					CustEmployeeDtoTwo custEmployeeDto = new CustEmployeeDtoTwo();
					custEmployeeDto.setEmpId(Integer.valueOf(p[0].toString()));
					
					Branch branch = branchRepository.findBranchByBrIdAndBrIsActive(Integer.valueOf(p[1].toString()), true);
					if(branch == null) {
						custEmployeeDto.setBrId(null);
					} else {
						custEmployeeDto.setBrId(Integer.valueOf(p[1].toString()));
					}
				
					custEmployeeDto.setCustId(Integer.valueOf(p[2].toString()));
					custEmployeeDto.setContractorId(Integer.valueOf(p[3].toString()));
					
					Shift shift = shiftRepository.findShiftByShiftIdAndShiftIsActive(Integer.valueOf(p[4].toString()), true);
					if(shift == null) {
						custEmployeeDto.setShiftId(null);
					} else {
						custEmployeeDto.setShiftId(Integer.valueOf(p[4].toString()));
					}
					
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
					
					Department department = departmentRepository.findDepartmentByDeptIdAndDeptIsActive(Integer.valueOf(p[19].toString()), true);
					if(department == null) {
						custEmployeeDto.setDepartmentId(deptId);
					} else {
						custEmployeeDto.setDepartmentId(Integer.valueOf(p[19].toString()));
					}
					
					Designation designation = designationRepository.findDesignationByDesignationIdAndDesigIsActive(Integer.valueOf(p[20].toString()), true);
					if(designation == null) {
						custEmployeeDto.setDesignationId(null);
					} else {
						custEmployeeDto.setDesignationId(Integer.valueOf(p[20].toString()));
					}
					
					if(employee != null) {
						custEmployeeDto.setEmpReportingTo(employee.getEmpName());
					}else {
						custEmployeeDto.setEmpReportingTo("NA");
					}
					custEmployeeDto.setEmployeeBranchFromDate((Date)p[21]);
					custEmployeeDto.setEmployeeShiftFromDate((Date)p[22]);
					custEmployeeDto.setEmployeeDepartmentFromDate((Date)p[23]);
					custEmployeeDto.setEmployeeDesignationFromDate((Date)p[24]);
					custEmployeeDto.setEmpWeeklyOffDayFromDate((Date)p[25]);
					custEmployeeDto.setDayOfWeek(p[26].toString());
					custEmployeeDto.setEmpReportingToFromDate((Date)p[27]);

					if(custEmployeeDto.getEmpReportingToId() != 0) {
						List<Object[]> reportingToList = custEmployeeRepository.getReportingToDepartment(custEmployeeDto.getEmpReportingToId());
						for(Object[] r: reportingToList) {
							custEmployeeDto.setReportingToDeptId(Integer.valueOf(r[0].toString()));
							custEmployeeDto.setReportingToDeptName(r[1].toString());
						}
					}

					List<EmployeeBlock> empBlockMapDtoList = employeeBlockRepository.findByEmployee_EmpId(custEmployeeDto.getEmpId());

					if(!empBlockMapDtoList.isEmpty()) {

						custEmployeeDto.setEmpBlockMapDtoList(empBlockMapDtoList.stream().map(employeeBlock -> convertToEmpBlockMapDto(employeeBlock)).collect(Collectors.toList()));
						custEmployeeDtoTwoList.add(custEmployeeDto);
						custEmployeeListResponse.setEmployyeList(custEmployeeDtoTwoList);
						custEmployeeListResponse.status = new Status(false, 2000, "Success");
					}else {
						custEmployeeDtoTwoList.add(custEmployeeDto);
						custEmployeeListResponse.setEmployyeList(custEmployeeDtoTwoList);
						custEmployeeListResponse.status = new Status(false, 2000, "Success and no blocks mapped to this employee");
					}

				}
			}

		} catch (Exception e) {
			custEmployeeListResponse.status = new Status(true, 5000, "Opps...! Something went wrong");
		}
		return custEmployeeListResponse;
	}

	@Override
	public CustEmployeeStatus updateEmployee(CustEmployeeDto custEmployeeDto) {
		CustEmployeeStatus custEmployeeStatus = new CustEmployeeStatus();
		final Lock displayLock = this.displayQueueLock;
		try {
			displayLock.lock();
			Employee employee = custEmployeeRepository.findEmployeeByEmpIdAndEmpInService(custEmployeeDto.getEmpId(), true);
			Customer customer =  customerRepository.findCustomerByCustIdAndCustIsActive(custEmployeeDto.getCustId(), true);
			EmployeeType employeeType = employeeTypeRepository.findEmployeeTypeByEmpTypeId(custEmployeeDto.getEmpTypeId());
			Shift shift = shiftRepository.findShiftByShiftIdAndShiftIsActive(custEmployeeDto.getShiftId(), true);
			Branch branch = branchRepository.findBranchByBrId(custEmployeeDto.getBrId());
			Employee employee1 = custEmployeeRepository.findByEmpMobileAndCustomer_CustId(custEmployeeDto.getEmpMobile(), custEmployeeDto.getCustId());

			// Contractor contractor = contractorRepository.findContractorByContractorId(custEmployeeDto.getContractorId());
			if(employee1 == null || (employee.getEmpId() == custEmployeeDto.getEmpId() && employee.getEmpMobile().equals(custEmployeeDto.getEmpMobile())))
			{
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
					employee.setEmpName(custEmployeeDto.getEmpName());
					employee.setEmpMobile(custEmployeeDto.getEmpMobile());
					employee.setEmpGender(custEmployeeDto.getEmpGender());
					employee.setEmpJoiningDate(custEmployeeDto.getEmpJoiningDate());
					employee.setEmpIsSupervisor_Manager(custEmployeeDto.getEmpIsSupervisor_Manager());
					custEmployeeRepository.save(employee);

					if(employee != null) {

						try {
							List<EmployeeBlock> alreadyMappedBlocks = employeeBlockRepository.findByEmployee_EmpId(custEmployeeDto.getEmpId());

							List<EmpBlockMapDto> nonNullEmpBlkIds = custEmployeeDto.getEmpBlockMapDtoList().stream().filter(e -> e.getEmpBlkId() != null).collect(Collectors.toList());

							List<EmployeeBlock> removed = alreadyMappedBlocks.stream().filter(o1 -> nonNullEmpBlkIds.stream().noneMatch(o2 -> o2.getEmpBlkId().equals(o1.getEmpBlkId())))
									.collect(Collectors.toList());

							for(EmployeeBlock CustBlockMapDto : removed) {
								EmployeeBlock employeeBlock = employeeBlockRepository.findByEmpBlkId(CustBlockMapDto.getEmpBlkId());
								employeeBlockRepository.delete(employeeBlock);
							}

							for(EmpBlockMapDto UpdateEmpBlock : custEmployeeDto.getEmpBlockMapDtoList()){
								if(UpdateEmpBlock.getEmpBlkId() == null) {
									EmployeeBlock employeeBlock = new EmployeeBlock();
									//EmployeeBlock employeeBlock = modelMapper.map(UpdateEmpBlock, EmployeeBlock.class);
									Block block = blockRepository.findByBlkId(UpdateEmpBlock.getBlkId());
									employeeBlock.setBlock(block);
									employeeBlock.setEmployee(employee);
									employeeBlockRepository.save(employeeBlock);
								}else if(UpdateEmpBlock.getEmpBlkId() != null) {
									Block block = blockRepository.findByBlkId(UpdateEmpBlock.getBlkId());
									EmployeeBlock employeeBlock1 = employeeBlockRepository.findByEmpBlkId(UpdateEmpBlock.getEmpBlkId());
									if(employeeBlock1 == null) {
										EmployeeBlock employeeBlock = new EmployeeBlock();
										employeeBlock.setBlock(block);
										employeeBlock.setEmployee(employee);
										employeeBlockRepository.save(employeeBlock);
									}
									else {
										employeeBlock1.setBlock(block);
										employeeBlock1.setEmployee(employee);
										employeeBlockRepository.save(employeeBlock1);
									}

								}

							}
						} catch (Exception e) {
							custEmployeeStatus.status = new Status(true, 400, e.getMessage());
						}

						// Set emp Reporting To details and save to emp Reporting To table
						/*try {
							EmployeeReportingTo employeeReportingTo2 = employeeReportingToRepository.findByEmployee_EmpIdAndRefEmpReportingToIdAndEmpFromDate(custEmployeeDto.getEmpId(), custEmployeeDto.getEmpReportingToId(), custEmployeeDto.getEmpReportingToFromDate());
							if(employeeReportingTo2 == null) {

								EmployeeReportingTo employeeReportingTo1 = employeeReportingToRepository.findByEmpIdAndReportingToDateNull(custEmployeeDto.getEmpId());
								if(employeeReportingTo1 != null) {
									Date empRetortingToDate = subtractDaysFromDate(custEmployeeDto.getEmpReportingToFromDate());
									employeeReportingTo1.setEmpToDate(empRetortingToDate);
									employeeReportingToRepository.save(employeeReportingTo1);
								}

								EmployeeReportingTo employeeReportingTo = new EmployeeReportingTo();
								employeeReportingTo.setEmployee(employee1);
								employeeReportingTo.setRefEmpReportingToId(custEmployeeDto.getEmpReportingToId());
								employeeReportingTo.setEmpFromDate(custEmployeeDto.getEmpReportingToFromDate());
								employeeReportingToRepository.save(employeeReportingTo);
							} else {
								employeeReportingTo2.setEmployee(employee1);
								employeeReportingTo2.setRefEmpReportingToId(custEmployeeDto.getEmpReportingToId());
								employeeReportingTo2.setEmpFromDate(custEmployeeDto.getEmpReportingToFromDate());
								employeeReportingTo2.setEmpToDate(null);
								employeeReportingToRepository.save(employeeReportingTo2);
							}
						} catch (Exception e) {
							custEmployeeStatus.status = new Status(true, 400, e.getMessage());
						}*/
						try {
							EmployeeReportingTo employeeReportingTo2 = employeeReportingToRepository.findByEmployee_EmpIdAndEmpFromDate(custEmployeeDto.getEmpId(), custEmployeeDto.getEmpReportingToFromDate());
							if(employeeReportingTo2 == null) {

								EmployeeReportingTo employeeReportingTo1 = employeeReportingToRepository.findByEmpIdAndReportingToDateNull(custEmployeeDto.getEmpId());
								if(employeeReportingTo1 != null) {
									Date empRetortingToDate = subtractDaysFromDate(custEmployeeDto.getEmpReportingToFromDate());
									employeeReportingTo1.setEmpToDate(empRetortingToDate);
									employeeReportingToRepository.save(employeeReportingTo1);
								}

								EmployeeReportingTo employeeReportingTo = new EmployeeReportingTo();
								employeeReportingTo.setEmployee(employee1);
								employeeReportingTo.setRefEmpReportingToId(custEmployeeDto.getEmpReportingToId());
								employeeReportingTo.setEmpFromDate(custEmployeeDto.getEmpReportingToFromDate());
								employeeReportingToRepository.save(employeeReportingTo);
							} else {
								employeeReportingTo2.setEmployee(employee1);
								employeeReportingTo2.setRefEmpReportingToId(custEmployeeDto.getEmpReportingToId());
								employeeReportingTo2.setEmpFromDate(custEmployeeDto.getEmpReportingToFromDate());
								employeeReportingTo2.setEmpToDate(null);
								employeeReportingToRepository.save(employeeReportingTo2);
							}
						} catch (Exception e) {
							custEmployeeStatus.status = new Status(true, 400, e.getMessage());
						}

						// Set emp branch details and save to emp branch table
						try {
							//EmployeeBranch employeeBranch2 = employeeBranchRepositories.findByEmployee_EmpIdAndBranch_BrIdAndBranchFromDate(custEmployeeDto.getEmpId(), custEmployeeDto.getBrId(), custEmployeeDto.getEmployeeBranchFromDate());
							EmployeeBranch employeeBranch2 = employeeBranchRepositories.findByEmployee_EmpIdAndBranchFromDate(custEmployeeDto.getEmpId(), custEmployeeDto.getEmployeeBranchFromDate());
							Branch branch1 = branchRepository.findBranchByBrId(custEmployeeDto.getBrId());
							if(employeeBranch2 == null) {
								EmployeeBranch employeeBranch = employeeBranchRepositories.findByEmpId(custEmployeeDto.getEmpId());
								if(employeeBranch != null) {
									Date empBrToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeBranchFromDate());
									employeeBranch.setBranchToDate(empBrToDate);
									employeeBranchRepositories.save(employeeBranch);
								}


								if(branch1 == null) throw new Exception("Cannot find branch id for Employee Department");

								EmployeeBranch employeeBranch1 = new EmployeeBranch();
								employeeBranch1.setEmployee(employee);
								employeeBranch1.setBranch(branch1);
								employeeBranch1.setBranchFromDate(custEmployeeDto.getEmployeeBranchFromDate());

								employeeBranchRepositories.save(employeeBranch1);
							} else {
								employeeBranch2.setEmployee(employee);
								employeeBranch2.setBranch(branch1);
								employeeBranch2.setBranchToDate(null);
								employeeBranch2.setBranchFromDate(custEmployeeDto.getEmployeeBranchFromDate());
								employeeBranchRepositories.save(employeeBranch2);
							}

						}catch (Exception e) {	
							custEmployeeStatus.status = new Status(true, 400, e.getMessage());
						}

						// Set emp dept details and save to emp dept table

						try {
							// EmployeeDepartment employeeDepartment2 = employeeDepartmentRepository.findByEmployee_EmpIdAndDepartment_DeptIdAndEmpFromDate(custEmployeeDto.getEmpId(), custEmployeeDto.getDepartmentId(), custEmployeeDto.getEmployeeDepartmentFromDate());
							EmployeeDepartment employeeDepartment2 = employeeDepartmentRepository.findByEmployee_EmpIdAndEmpFromDate(custEmployeeDto.getEmpId(), custEmployeeDto.getEmployeeDepartmentFromDate());
							Department department = departmentRepository.findDepartmentByDeptIdAndDeptIsActive(custEmployeeDto.getDepartmentId(), true);
							if(employeeDepartment2 == null) {

								EmployeeDepartment employeeDepartment = employeeDepartmentRepository.findByEmpId(custEmployeeDto.getEmpId());

								if(employeeDepartment != null) {
									Date empDeptToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeDepartmentFromDate());
									employeeDepartment.setEmpToDate(empDeptToDate);
									employeeDepartmentRepository.save(employeeDepartment);
								}

								if(department == null) throw new Exception("Cannot find department id for Employee Department");

								EmployeeDepartment employeeDepartment1 = new EmployeeDepartment();
								employeeDepartment1.setEmployee(employee);
								employeeDepartment1.setDepartment(department);
								employeeDepartment1.setEmpFromDate(custEmployeeDto.getEmployeeDepartmentFromDate());

								employeeDepartmentRepository.save(employeeDepartment1);
							} else {
								employeeDepartment2.setEmployee(employee);
								employeeDepartment2.setDepartment(department);
								employeeDepartment2.setEmpFromDate(custEmployeeDto.getEmployeeDepartmentFromDate());
								employeeDepartment2.setEmpToDate(null);
								employeeDepartmentRepository.save(employeeDepartment2);
							}


						}catch (Exception e) {	
							custEmployeeStatus.status = new Status(true, 400, e.getMessage());
						}

						//set Emp desig details and DesignationId to empDesg

						try {

							// EmployeeDesignation employeeDesignation2 = employeeDesignationRepository.findByEmployee_EmpIdAndDesignation_DesignationIdAndEmpFromDate(custEmployeeDto.getEmpId(), custEmployeeDto.getDesignationId(), custEmployeeDto.getEmployeeDesignationFromDate()); 							
							EmployeeDesignation employeeDesignation2 = employeeDesignationRepository.findByEmployee_EmpIdAndEmpFromDate(custEmployeeDto.getEmpId(), custEmployeeDto.getEmployeeDesignationFromDate());
							Designation designation = designationRepository.findDesignationByDesignationId(custEmployeeDto.getDesignationId());
							if(employeeDesignation2 == null) {

								EmployeeDesignation employeeDesignation1 = employeeDesignationRepository.findByEmpId(custEmployeeDto.getEmpId());
								if(employeeDesignation1 != null) {
									Date empDesgToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeDesignationFromDate());
									employeeDesignation1.setEmpToDate(empDesgToDate);
									employeeDesignationRepository.save(employeeDesignation1);
								}

								EmployeeDesignation employeeDesignation = new EmployeeDesignation();

								if(designation == null) throw new Exception("Cannot find designation id for Employee Designation");
								employeeDesignation.setEmployee(employee);
								employeeDesignation.setDesignation(designation);
								employeeDesignation.setEmpFromDate(custEmployeeDto.getEmployeeDesignationFromDate());
								employeeDesignationRepository.save(employeeDesignation);
							} else {
								employeeDesignation2.setEmployee(employee);
								employeeDesignation2.setDesignation(designation);
								employeeDesignation2.setEmpFromDate(custEmployeeDto.getEmployeeDesignationFromDate());
								employeeDesignation2.setEmpToDate(null);
								employeeDesignationRepository.save(employeeDesignation2);
							}

						}catch (Exception e) {	
							custEmployeeStatus.status = new Status(true, 400, e.getMessage());
						}

						//set EmpId and ShiftId to empShift

						try {
							// EmployeeShift employeeShift2 = employeeShiftRepository.findByEmployee_EmpIdAndShift_ShiftIdAndShiftFromDate(custEmployeeDto.getEmpId(), custEmployeeDto.getShiftId(), custEmployeeDto.getEmployeeShiftFromDate());
							EmployeeShift employeeShift2 = employeeShiftRepository.findByEmployee_EmpIdAndShiftFromDate(custEmployeeDto.getEmpId(), custEmployeeDto.getEmployeeShiftFromDate());
							Shift shift1 = shiftRepository.findShiftByShiftIdAndShiftIsActive(custEmployeeDto.getShiftId(), true);
							if(employeeShift2 == null) {

								EmployeeShift employeeShift1 = employeeShiftRepository.findByEmpId(custEmployeeDto.getEmpId());
								if(employeeShift1 != null) {
									Date empShiftToDate = subtractDaysFromDate(custEmployeeDto.getEmployeeShiftFromDate());
									employeeShift1.setShiftToDate(empShiftToDate);
									employeeShiftRepository.save(employeeShift1);
								}

								EmployeeShift employeeShift = new EmployeeShift();

								if(shift1 == null) throw new Exception("Cannot find shift for Employee");
								employeeShift.setEmployee(employee);
								employeeShift.setShift(shift1);
								employeeShift.setShiftFromDate(custEmployeeDto.getEmployeeShiftFromDate());
								employeeShiftRepository.save(employeeShift);
							} else {
								employeeShift2.setEmployee(employee);
								employeeShift2.setShift(shift1);
								employeeShift2.setShiftFromDate(custEmployeeDto.getEmployeeShiftFromDate());
								employeeShift2.setShiftToDate(null);
								employeeShiftRepository.save(employeeShift2);
							}

						}catch (Exception e) {	
							custEmployeeStatus.status = new Status(true, 400, e.getMessage());
						}


						//set EmpId and WeekoffDay to empShift						
						try {

							// EmpWeeklyOffDay empWeeklyOffDay2 = empWeeklyOffDayRepository.findEEmpWeeklyOffDayByEmployee_EmpIdAndDayOfWeekAndFromDate(custEmployeeDto.getEmpId(), custEmployeeDto.getDayOfWeek(), custEmployeeDto.getEmpWeeklyOffDayFromDate());
							EmpWeeklyOffDay empWeeklyOffDay2 = empWeeklyOffDayRepository.findEEmpWeeklyOffDayByEmployee_EmpIdAndFromDate(custEmployeeDto.getEmpId(), custEmployeeDto.getEmpWeeklyOffDayFromDate());
							if(empWeeklyOffDay2 == null) {

								EmpWeeklyOffDay empWeeklyOffDay1 = empWeeklyOffDayRepository.findEEmpWeeklyOffDayByEmpId(custEmployeeDto.getEmpId());								
								if(empWeeklyOffDay1 != null) {
									Date empweeklyOffToDate = subtractDaysFromDate(custEmployeeDto.getEmpWeeklyOffDayFromDate());
									empWeeklyOffDay1.setToDate(empweeklyOffToDate);
									empWeeklyOffDayRepository.save(empWeeklyOffDay1);
								}

								EmpWeeklyOffDay empWeeklyOffDay = new EmpWeeklyOffDay();
								empWeeklyOffDay.setEmployee(employee);
								empWeeklyOffDay.setYearMonth(new Date());
								empWeeklyOffDay.setDayOfWeek(custEmployeeDto.getDayOfWeek());
								empWeeklyOffDay.setFromDate(custEmployeeDto.getEmpWeeklyOffDayFromDate());
								empWeeklyOffDayRepository.save(empWeeklyOffDay);
							} else {
								empWeeklyOffDay2.setEmployee(employee);
								empWeeklyOffDay2.setYearMonth(new Date());
								empWeeklyOffDay2.setDayOfWeek(custEmployeeDto.getDayOfWeek());
								empWeeklyOffDay2.setFromDate(custEmployeeDto.getEmpWeeklyOffDayFromDate());
								empWeeklyOffDay2.setToDate(null);
								empWeeklyOffDayRepository.save(empWeeklyOffDay2);
							}

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

					custEmployeeStatus.status = new Status(false, 200, "updated");

				} else {
					custEmployeeStatus.status = new Status(true, 400, "Employee not found or employee not in service");

				}
			}else {
				custEmployeeStatus.status = new Status(true, 400, "Employee mobile number aleady exist");

			}

		} catch (Exception e) {
			custEmployeeStatus.status = new Status(true, 500, "Opps...! Something went wrong");

		}
		finally {
			displayLock.unlock();
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
				custEmployeeListResponse.status = new Status(false, 4000, "Not found");
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
					custEmployeeDto.setDepartmentId(Integer.valueOf(p[19].toString()));
					custEmployeeDto.setDesignationId(Integer.valueOf(p[20].toString()));
					custEmployeeDtoTwoList.add(custEmployeeDto);
					custEmployeeListResponse.setEmployyeList(custEmployeeDtoTwoList);
					custEmployeeListResponse.status = new Status(false, 2000, "Success");
				}

			}
		} catch (Exception e) {
			custEmployeeListResponse.status = new Status(true, 5000, "Opps...! Something went wrong");
		}
		return custEmployeeListResponse;
	}

	@Override
	public CustEmployeeStatus deleteEmployeeByEmpIdId(Integer empId) {

		CustEmployeeStatus custEmployeeStatus = new CustEmployeeStatus();
		try {
			Employee employee = custEmployeeRepository.findEmployeeByEmpIdAndEmpInService(empId, true);
			if(employee != null) {
				EmployeeReportingTo employeeReportingTo = employeeReportingToRepository.findEmployeeByEmployee_EmpId(empId);
				if(employeeReportingTo.getRefEmpReportingToId() != 0) {
					employee.setEmpInService(false);
					custEmployeeRepository.save(employee);
					custEmployeeStatus.status = new Status(false, 200, "deleted");
				} else {
					custEmployeeStatus.status = new Status(true, 400, "Head of the organization can't be deleted..!");
				}
			}else {
				custEmployeeStatus.status = new Status(true, 400, "Employee not found");
			}
		} catch (Exception e) {
			custEmployeeStatus.status = new Status(true, 5000, "Opps...! Something went wrong");
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
			custEmployeeListResponse.status = new Status(true, 5000, "Opps...! Something went wrong");
		}
		return custEmployeeListResponse;
	}


	@Override
	public CustEmployeeListResponse findEmployeeByCustId(Integer custId) {

		CustEmployeeListResponse custEmployeeListResponse = new CustEmployeeListResponse();
		List<CustEmployeeDtoTwo> custEmployeeDtoTwoList = new ArrayList<>();

		try {

			Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(custId, true);

			if(customer != null) {
				List<Object[]> employeeList = custEmployeeRepository.findByCustomer_CustIdAndEmpInService(custId);

				if(employeeList.isEmpty()) {
					custEmployeeListResponse.status = new Status(false, 4000, "Not Found");
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
						custEmployeeDto.setDepartmentId(Integer.valueOf(p[19].toString()));
						custEmployeeDto.setDesignationId(Integer.valueOf(p[20].toString()));
						custEmployeeDtoTwoList.add(custEmployeeDto);
						custEmployeeListResponse.setEmployyeList(custEmployeeDtoTwoList);
						custEmployeeListResponse.status = new Status(false, 2000, "Success");
					}
				}
			} else {
				custEmployeeListResponse.status = new Status(true, 5000, "Customer not found");
			}

		} catch (Exception e) {
			custEmployeeListResponse.status = new Status(true, 5000, "Opps...! Something went wrong");
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

	public EmpBlockMapDto convertToEmpBlockMapDto(EmployeeBlock employeeBlock) {
		EmpBlockMapDto empBlockMapDto = modelMapper.map(employeeBlock, EmpBlockMapDto.class);
		empBlockMapDto.setBlkId(employeeBlock.getBlock().getBlkId());
		empBlockMapDto.setBlkLogicalName(employeeBlock.getBlock().getBlkLogicalName());
		empBlockMapDto.setRefEmpId(employeeBlock.getEmployee().getEmpId());
		return empBlockMapDto;
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

	@Override
	public CustEmployeeListResponse FindEmployeeByRefLoginId(Integer refLoginId) {
		CustEmployeeListResponse custEmployeeListResponse = new CustEmployeeListResponse();
		List<EmployeeDto2> EmployeeDtoList = new ArrayList<>();
		try {
			List<Object[]> employeeList =  custEmployeeRepository.findEmployeeByLoginDataRight_refLoginId(refLoginId);

			if(employeeList.isEmpty()) {
				custEmployeeListResponse.status = new Status(false,400, " Not found");
			}else {
				for (Object[] p : employeeList) {	

					EmployeeDto2 employeeDto = new EmployeeDto2();
					employeeDto.setEmpId(Integer.valueOf(p[0].toString()));
					employeeDto.setEmpName((p[1].toString()));
					EmployeeDtoList.add(employeeDto);
					custEmployeeListResponse.status = new Status(false,200, "success"); 
				}

			}

		}catch (Exception e){
			custEmployeeListResponse.status = new Status(true,500, e.getMessage());


		}
		custEmployeeListResponse.setData1(EmployeeDtoList);
		return custEmployeeListResponse;
	}

	@Override
	public Status checkEmpMobileNumExistOrNot(String empMobile, Integer custId) {
		Status status = null;
		try {
			List<Employee> employee = custEmployeeRepository.findAllEmployeeByEmpMobileAndCustomer_CustId(empMobile, custId);

			if(employee.isEmpty()) {
				status = new Status(false, 200, "Not exist");
			} else {
				status = new Status(true, 400, "Exist");
			}
		} catch (Exception e) {
			status = new Status(true, 500, "Opps...! Something went wrong");
		}

		return status;
	}

	/*@Override
	public CustEmployeeStatus deleteEmployeeByEmpId(Integer empId) {
		CustEmployeeStatus custEmployeeStatus = new CustEmployeeStatus();
		try {
			Employee employee = custEmployeeRepository.findEmployeeByEmpIdAndEmpInService(empId, true);
			if(employee != null) {
				EmployeeReportingTo employeeReportingTo = employeeReportingToRepository.findEmployeeByEmployee_EmpId(empId);
				if(employeeReportingTo.getRefEmpReportingToId() != 0) {
					employeeReportingToRepository.updateEmpReportingToTopManagerAfterDeleteByEmployee_EmpId(empId);
					employee.setEmpInService(false);
					custEmployeeRepository.save(employee);
					custEmployeeStatus.status = new Status(false, 200, "deleted");
				} else {
					custEmployeeStatus.status = new Status(true, 400, "You can't delete because this is the first employee");
				}
			}else {
				custEmployeeStatus.status = new Status(true, 400, "Employee not found");
			}
		} catch (Exception e) {
			custEmployeeStatus.status = new Status(true, 5000, "Opps...! Something went wrong");
		}
		return custEmployeeStatus;
	}*/

}
