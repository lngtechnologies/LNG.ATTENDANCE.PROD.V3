package com.lng.attendancecustomerservice.serviceImpl.empAppSetup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.BlockBeaconMap;
import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.entity.masters.CustomerConfig;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.masters.Shift;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.empAppSetup.WelcomeScreenRepository;
import com.lng.attendancecustomerservice.repositories.masters.BranchRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustLeaveRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustomerConfigRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeLeaveRepository;
import com.lng.attendancecustomerservice.repositories.masters.ShiftRepository;
import com.lng.attendancecustomerservice.service.empAppSetup.DashboardService;
import com.lng.dto.employeeAppSetup.CustomerValidityDto;
import com.lng.dto.employeeAppSetup.DashboardDto;
import com.lng.dto.employeeAppSetup.EmpAttndStatusDto;
import com.lng.dto.employeeAttendance.ShiftDetailsDto;
import com.lng.dto.employeeAttendance.ShiftResponseDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapListResponse;
import com.lng.dto.masters.customerConfig.DashboardCustConfigDto;
import com.lng.dto.masters.customerConfig.DashboardCustConfigResponse;
import com.lng.dto.masters.employeeLeave.EmpAppLeaveDto;
import com.lng.dto.masters.employeeLeave.EmpLeaveResponseDto;

import status.Status;

@Service
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	EmployeeLeaveRepository employeeLeaveRepository;

	@Autowired
	CustLeaveRepository custLeaveRepository;

	@Autowired
	EmployeeRepository employeeRepository;	

	@Autowired
	WelcomeScreenRepository welcomeScreenRepository;

	@Autowired
	ShiftRepository shiftRepository;

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	CustomerConfigRepository customerConfigRepository;
	
	@Autowired
	BranchRepository branchRepository;

	ModelMapper modelMapper = new ModelMapper();

	@SuppressWarnings("unused")
	@Override
	public DashboardDto getEmployeeDetails(Integer custId, Integer empId) {
		DashboardDto dashboardDto = new DashboardDto();
		try {
			Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(custId, true);
			if(customer != null) {
				int custValidityFlag = customerRepository.checkCustValidationByCustId(custId);
				if(custValidityFlag == 1) {
					dashboardDto.setIsValidCustomer(true);
					
					Employee employee = employeeRepository.getByEmpIdAndRefCustId(custId, empId);
					if(employee != null) {
						int branchValidity = branchRepository.checkBranchValidity(employee.getBranch().getBrId());
						if(branchValidity == 1) {
							dashboardDto.setIsValidBranch(true);
							
							if(employee != null) {
								dashboardDto.setIsEmployeeInService(true);
								
								Employee employee1 = employeeRepository.getByEmpIdAndRefCustId(custId, empId);
								if(employee1.getEmpPresistedFaceId() != null ) {
									dashboardDto.setIsFaceregistered(true);
									
									//dashboardDto.setCustomerValidity(checkValidationByCustId(custId));
									dashboardDto.setConfig(getConfigDetails(empId, custId));
									dashboardDto.setEmpAttendanceStatus(getAttndStatusByEmployee(empId, custId));
									dashboardDto.setEmpShiftDetails(getShiftDetailsByEmpIdAndCustId(empId, custId));
									dashboardDto.setEmpBeacons(getBeaconsByEmpId(empId, custId));
									dashboardDto.setEmpLeaveData(getEmpLeaveByEmpIdAndCustId(empId, custId));
									
									Shift shift = shiftRepository.getByEmpId(empId);
									if(shift != null) {
										dashboardDto.setIsShiftAllotted(true);
										dashboardDto.status = new Status(false, 200, "Success");
									} else {
										dashboardDto.setIsValidCustomer(true);
										dashboardDto.setIsValidBranch(true);
										dashboardDto.setIsEmployeeInService(true);
										dashboardDto.setIsFaceregistered(true);
										dashboardDto.setIsShiftAllotted(false);
										dashboardDto.status = new Status(true, 400, "Shif not found");
									}
									
								} else {
									dashboardDto.setIsValidCustomer(true);
									dashboardDto.setIsValidBranch(true);
									dashboardDto.setIsEmployeeInService(true);
									dashboardDto.setIsFaceregistered(false);
									dashboardDto.setIsShiftAllotted(false);
									dashboardDto.status = new Status(true, 400, "Employee face not registered");
								}
							} else {
								dashboardDto.setIsValidCustomer(true);
								dashboardDto.setIsValidBranch(true);
								dashboardDto.setIsEmployeeInService(false);
								dashboardDto.setIsFaceregistered(false);
								dashboardDto.setIsShiftAllotted(false);
								dashboardDto.status = new Status(true, 400, "Employee not in service");
							}
						} else {
							
							dashboardDto.setIsValidCustomer(true);
							dashboardDto.setIsValidBranch(false);
							dashboardDto.setIsEmployeeInService(false);
							dashboardDto.setIsFaceregistered(false);
							dashboardDto.setIsShiftAllotted(false);
							dashboardDto.status = new Status(true, 400, "Subscription expired, please contact admin");
						}
					} else {
						dashboardDto.setIsValidCustomer(true);
						dashboardDto.setIsValidBranch(false);
						dashboardDto.setIsEmployeeInService(false);
						dashboardDto.setIsFaceregistered(false);
						dashboardDto.setIsShiftAllotted(false);
						dashboardDto.status = new Status(true, 400, "Employee not found");
					}
			
				} else {
					dashboardDto.setIsValidCustomer(false);
					dashboardDto.setIsValidBranch(false);
					dashboardDto.setIsEmployeeInService(false);
					dashboardDto.setIsFaceregistered(false);
					dashboardDto.setIsShiftAllotted(false);
					dashboardDto.status = new Status(true, 400, "Subscription expired, please contact admin");
				}
			} else {
				dashboardDto.setIsValidCustomer(false);
				dashboardDto.setIsValidBranch(false);
				dashboardDto.setIsEmployeeInService(false);
				dashboardDto.setIsFaceregistered(false);
				dashboardDto.setIsShiftAllotted(false);
				dashboardDto.status = new Status(true, 400, "Customer not found");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dashboardDto;
	}

	// Check customer validity
	public CustomerValidityDto checkValidationByCustId(Integer custId) {
		CustomerValidityDto customerValidityDto = new CustomerValidityDto();
		try {
			int validityFlag = customerRepository.checkCustValidationByCustId(custId);
			if(validityFlag == 1) {
				customerValidityDto.setIsValidCustomer(true);
				customerValidityDto.status = new Status(false, 200, "Valid Customer");
			} else {
				customerValidityDto.setIsValidCustomer(false);
				customerValidityDto.status = new Status(true, 400, "Validity expired");
			}
		} catch (Exception e) {
			customerValidityDto.status = new Status(true, 500, "Opps..! Something went wrong..");
		}

		return customerValidityDto;
	}


	// Get All Beacons By Employee Id
	public BlockBeaconMapListResponse getBeaconsByEmpId(Integer empId, Integer custId) {
		BlockBeaconMapListResponse response = new BlockBeaconMapListResponse();
		try {

			Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(custId, true);
			if(customer != null) {
				Employee employee = employeeRepository.getByEmpIdAndRefCustId(custId, empId);
				if(employee != null) {
					List<BlockBeaconMap> blockBeaconMapList = welcomeScreenRepository.findByEmployee_EmpId(empId);
					response.setBeaconMapDtolist(blockBeaconMapList.stream().map(blockBeaconMap -> convertToBlockBeaconMapDto(blockBeaconMap)).collect(Collectors.toList()));
					if(!response.getBeaconMapDtolist().isEmpty()) {
						response.status = new Status(false,200, "Success");
					} else {
						response.status = new Status(true,400, "No beacons mapped for this employee"); 
					}
				} else {
					response.status = new Status(true, 400, "Employee not found or not in service");
				}

			} else {
				response.status = new Status(true,400, "Customer not found"); 
			}


		}catch(Exception e) {
			response.status = new Status(true,500, e.getMessage()); 

		}
		return response;
	}

	// Get Shift details By Employee Id
	public ShiftResponseDto getShiftDetailsByEmpIdAndCustId(Integer empId, Integer custId) {
		ShiftResponseDto shiftResponseDto = new ShiftResponseDto();
		ShiftDetailsDto shiftDetailsDto = new ShiftDetailsDto();
		String shiftStartTime = null;
		String shiftEndTime = null;

		try {
			String time = employeeRepository.getOutPermissibleTimeByEmployee_EmpIdAndCustomer_CustId(empId,custId);
			Shift shift = shiftRepository.getByEmpId(empId);
			Employee employee = employeeRepository.getByEmpIdAndRefCustId(custId, empId);
			Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(custId, true);
			if(customer != null) {
				if(employee != null) {
					if(shift != null) {
						shiftStartTime = shift.getShiftStart().substring(5).trim().toUpperCase();
						shiftEndTime = shift.getShiftEnd().substring(5).trim().toUpperCase();

						if(shiftStartTime.equalsIgnoreCase("PM") && shiftEndTime.equalsIgnoreCase("AM")) {
							shiftDetailsDto.setShiftType("D1D2");
						} else {
							shiftDetailsDto.setShiftType("D1D1");
						}
						shiftDetailsDto.setShiftStartTime(shift.getShiftStart());
						shiftDetailsDto.setShiftEndTime(shift.getShiftEnd());
						shiftDetailsDto.setEmpId(employee.getEmpId());
						shiftDetailsDto.setOutPermissibleTime(time);
						shiftResponseDto.setDetailsDto(shiftDetailsDto);
						shiftResponseDto.status = new Status(false, 200, "Success");
					} else {
						shiftResponseDto.status = new Status(true, 400, "Shift not found for this employee");
					}
				} else {
					shiftResponseDto.status = new Status(true, 400, "Employee not found or not in service");
				}
			} else {
				shiftResponseDto.status = new Status(true, 400, "Customer not found");
			}


		} catch (Exception e) {
			shiftResponseDto.status = new Status(true, 500, "Opps..! Something went wrong..");
		}

		return shiftResponseDto;
	}

	// Get Employee Leave details By Employee Id
	public EmpLeaveResponseDto getEmpLeaveByEmpIdAndCustId(Integer empId, Integer custId) {
		EmpLeaveResponseDto empLeaveResponseDto = new EmpLeaveResponseDto();
		try {

			Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(custId, true);
			if(customer != null) {
				Employee employee = employeeRepository.getByEmpIdAndRefCustId(custId, empId);
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
					empLeaveResponseDto.status = new Status(true, 400, "Employee not found or not in service");
				}
			} else {
				empLeaveResponseDto.status = new Status(true, 400, "Customer not found");
			}

		} catch (Exception e) {
			empLeaveResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return empLeaveResponseDto;
	}

	// Get Employee Attnd Status By Employee Id
	public EmpAttndStatusDto getAttndStatusByEmployee(Integer empId, Integer custId) {
		EmpAttndStatusDto empAttndStatusDto = new EmpAttndStatusDto();

		try {
			Employee employee = employeeRepository.getByEmpIdAndRefCustId(custId, empId);
			Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(custId, true);
			if(customer != null) {
				if(employee != null) {

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
					formatter.setTimeZone(TimeZone.getTimeZone("IST"));
					Date date = new Date();
					String strDate = formatter.format(date);
					
					String inDate = "NA";
					String outDate = "NA";
					String attndDate = "NA";

					Date empInAttndDate = employeeRepository.getRecentInDateByAttndDateAndEmpId(strDate, employee.getEmpId());

					Date empOutAttndDate = employeeRepository.getRecentOutDateByAttndDateAndEmpId(strDate, employee.getEmpId());

					Date empAttndDate = employeeRepository.getRecentAttndDate(strDate, employee.getEmpId());

					if(empInAttndDate != null || empOutAttndDate != null) {

						String pattern = "yyyy-MM-dd'T'HH:mm:ss";
						SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
						dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

						if(empInAttndDate != null) {
							inDate = dateFormat.format(empInAttndDate);
						}

						if(empOutAttndDate != null) {
							outDate = dateFormat.format(empOutAttndDate);
						}

						if(empAttndDate != null) {
							String pattern1 = "yyyy-MM-dd";
							SimpleDateFormat dateFormat1 = new SimpleDateFormat(pattern1);
							attndDate = dateFormat1.format(empAttndDate);
						}
											
					} 
					empAttndStatusDto.setEmpAttndDate(attndDate);
					empAttndStatusDto.setEmpAttndInDateTime(inDate);
					empAttndStatusDto.setEmpAttndOutDateTime(outDate);
					empAttndStatusDto.status = new Status(false, 200, "Success");
					
				} else {
					empAttndStatusDto.status = new Status(true, 400, "Employee not found or not in service");
				}

			} else {
				empAttndStatusDto.status = new Status(true, 400, "Customer not found");
			}

		} catch (Exception e) {
			empAttndStatusDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}

		return empAttndStatusDto;
	}
	
	public DashboardCustConfigResponse getConfigDetails(Integer empId, Integer custId){
		List<DashboardCustConfigDto> dashboardCustConfigList = new ArrayList<DashboardCustConfigDto>();
		DashboardCustConfigResponse dashboardCustConfigResponse = new DashboardCustConfigResponse();
		try {
			Employee employee = employeeRepository.getByEmpIdAndRefCustId(custId, empId);
			if(employee != null) {
				List<CustomerConfig> configList = customerConfigRepository.findByCustomer_CustIdAndBranch_BrId(employee.getCustomer().getCustId(), employee.getBranch().getBrId());
				if(!configList.isEmpty()) {
					for(CustomerConfig customerConfig: configList) {
						DashboardCustConfigDto dashboardCustConfigDto = new DashboardCustConfigDto();
						dashboardCustConfigDto.setConfigValue(customerConfig.getConfig());
						dashboardCustConfigDto.setStatusFlag(customerConfig.getStatusFlag());
						dashboardCustConfigList.add(dashboardCustConfigDto);
					}
					dashboardCustConfigResponse.setConfigList(dashboardCustConfigList);
					dashboardCustConfigResponse.status = new Status(false, 200, "Success");
				} else {
					dashboardCustConfigResponse.status = new Status(true, 400, "Configurations not found");
				}
			} else {
				dashboardCustConfigResponse.status = new Status(true, 400, "Employee not found");
			}
		} catch (Exception e) {
			dashboardCustConfigResponse.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return dashboardCustConfigResponse;
	}

	public BlockBeaconMapDto convertToBlockBeaconMapDto(BlockBeaconMap blockBeaconMap) {
		BlockBeaconMapDto blockBeaconMapDto = modelMapper.map(blockBeaconMap,BlockBeaconMapDto.class);
		blockBeaconMapDto.setRefBlkId(blockBeaconMap.getBlock().getBlkId());
		blockBeaconMapDto.setBlkLogicalName(blockBeaconMap.getBlock().getBlkLogicalName());
		blockBeaconMapDto.setBrId(blockBeaconMap.getBlock().getBranch().getBrId());
		blockBeaconMapDto.setCustId(blockBeaconMap.getBlock().getBranch().getCustomer().getCustId());
		return blockBeaconMapDto;
	}
}
