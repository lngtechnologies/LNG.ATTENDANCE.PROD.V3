package com.lng.attendancecustomerservice.serviceImpl.empAppSetup;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.authentication.Login;
import com.lng.attendancecustomerservice.entity.masters.CustLeave;
import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.entity.masters.EmpWeeklyOffDay;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.masters.EmployeeLeave;
import com.lng.attendancecustomerservice.entity.notification.EmpToken;
import com.lng.attendancecustomerservice.repositories.authentication.ILoginRepository;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustLeaveRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmpWeeklyOffDayRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeLeaveRepository;
import com.lng.attendancecustomerservice.repositories.masters.HolidayCalendarRepository;
import com.lng.attendancecustomerservice.repositories.notification.EmpTokenRepository;
import com.lng.attendancecustomerservice.service.empAppSetup.EmpLeaveService;
import com.lng.attendancecustomerservice.utils.PushNotificationUtil;
import com.lng.dto.employeeAppSetup.EmpLaveResponse;
import com.lng.dto.employeeAppSetup.EmpLeavesDto;
import com.lng.dto.masters.empLeaveApproveOrCancel.EmpLeaveDto;
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

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	EmpWeeklyOffDayRepository empWeeklyOffDayRepository;

	@Autowired
	HolidayCalendarRepository holidayCalendarRepository;

	@Autowired
	EmpTokenRepository  empTokenRepository;

	@Autowired
	ILoginRepository iLoginRepository;

	private final Lock displayQueueLock = new ReentrantLock();

	PushNotificationUtil pushNotificationUtil = new PushNotificationUtil();

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


	@SuppressWarnings("unused")
	@Override
	public Status saveEmpLeave(EmployeeLeaveDto employeeLeaveDto) {
		Status status = null;
		int count = 0;
		final Lock displayLock = this.displayQueueLock;
		LocalDate date = null;
		DayOfWeek days = null;
		String weekOffDay = "";
		String weekOfDay = "";
		Date dates = null;
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


						ZoneId defaultZoneId = ZoneId.systemDefault();
						Date startDate = employeeLeaveDto.getEmpLeaveFrom();
						Instant fInstant = startDate.toInstant();
						LocalDate fromDate = fInstant.atZone(defaultZoneId).toLocalDate();

						Date endDate = employeeLeaveDto.getEmpLeaveTo();
						Instant tInstant = endDate.toInstant();
						LocalDate toDate = tInstant.atZone(defaultZoneId).toLocalDate();

						EmpWeeklyOffDay empWeeklyOffDay = empWeeklyOffDayRepository.findEEmpWeeklyOffDayByEmpId(employeeLeaveDto.getEmpId());
						weekOffDay = empWeeklyOffDay.getDayOfWeek();		
						String[] values = weekOffDay.toUpperCase().split(",");

						List<String> holidays = holidayCalendarRepository.findHolidayCalendarByrefBrIdFromDateAndToDate(employee.getBranch().getBrId(), employeeLeaveDto.getEmpLeaveFrom(), employeeLeaveDto.getEmpLeaveTo());

						for (date = fromDate; date.isBefore(toDate.plusDays(1)); date = date.plusDays(1)) {
							Date appliedDate = Date.from(date.atStartOfDay(defaultZoneId).toInstant());
							SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
							String aDate = sdf1.format(appliedDate);
							// System.out.println("Applied date " + aDate);
							days = date.getDayOfWeek();
							if(!holidays.isEmpty()) {
								if(!holidays.contains(aDate)) {		


									List<String> wkOff = Arrays.asList(values);
									// System.out.println(wkOff);
									if(!wkOff.contains(days.toString())) {
										count++;	
									}
								}
							} else {
								List<String> wkOff = Arrays.asList(values);
								// System.out.println(wkOff);
								if(!wkOff.contains(days.toString())) {
									count++;	
								}
							}
						}
						if(count > 0) {
							EmployeeLeave employeeLeave = modelMapper.map(employeeLeaveDto, EmployeeLeave.class);
							Integer countNoOfDays = employeeLeaveRepository.getNoOfDaysCount(employeeLeaveDto.getEmpLeaveFrom(), employeeLeaveDto.getEmpLeaveTo());
							employeeLeave.setEmployee(employee);
							employeeLeave.setCustLeave(custLeave);
							employeeLeave.setEmpLeaveDaysCount(count);
							employeeLeave.setEmpLeaveAppliedDatetime(new Date());
							employeeLeave.setEmpLeaveStatus("");
							employeeLeaveRepository.save(employeeLeave);
							status = new Status(false, 200, "Leave applied successfully");

						} else {
							status = new Status(true, 400, "Cannot apply leave on week off days or holidays");
						}

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


	@Override
	public Status cancelLeave(Integer custId, Integer empLeaveId) {
		Status status = null;
		try {
			Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(custId, true);
			if(customer != null) {
				EmployeeLeave employeeLeave = employeeLeaveRepository.findByEmpLeaveId(empLeaveId);
				if(employeeLeave != null) {
					employeeLeaveRepository.delete(employeeLeave);
					status = new Status(false, 200, "Success");
				} else {
					status = new Status(true, 400, "Employee leave not found");
				}
			} else {
				status = new Status(true, 400, "Customer not found");
			}
		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}


	@Override
	public Status empApproveLeave(EmployeeLeaveDto employeeLeaveDto) {
		Status status = null;
		try {

			EmployeeLeave employeeLeave = employeeLeaveRepository.findByEmpLeaveId(employeeLeaveDto.getEmpLeaveId());
			if(employeeLeave != null) {
				Employee employee = employeeRepository.getByEmpId(employeeLeave.getEmployee().getEmpId());
				if(employee != null) {
					EmpToken empToken = empTokenRepository.findByEmployee_EmpIdAndIsActive(employee.getEmpId(),true);
					if(empToken != null) {
						employeeLeave.setEmpLeaveAppRejBy(employeeLeaveDto.getEmpId());
						employeeLeave.setEmpLeaveStatus("App");
						employeeLeaveRepository.save(employeeLeave);
						Date date =  employeeLeave.getEmpLeaveFrom();
						Date date1 =  employeeLeave.getEmpLeaveTo();
						SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
						String strDate = formatter.format(date); 
						String strDate1 = formatter.format(date1); 
						pushNotificationUtil.SendPushNotification(empToken.getToken(),"Leave for "+strDate+" - "+strDate1+" has been Approved..!","Leave");

						status = new Status(false, 200, "Leave Approved");
					}else if(empToken == null) {
						employeeLeave.setEmpLeaveAppRejBy(employeeLeaveDto.getEmpId());
						employeeLeave.setEmpLeaveStatus("App");
						employeeLeaveRepository.save(employeeLeave);
						status = new Status(false, 200, "Leave Approved");	
					}
				}else {
					status = new Status(true, 400, "Employee not found");
				}
			} else {
				status = new Status(false, 400, "Leave id not found");
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
				Employee employee = employeeRepository.getByEmpId(employeeLeave.getEmployee().getEmpId());
				if(employee != null) {
					EmpToken empToken = empTokenRepository.findByEmployee_EmpIdAndIsActive(employee.getEmpId(),true);
					if(empToken != null) {
						employeeLeave.setEmpLeaveAppRejBy(employeeLeaveDto.getEmpId());
						employeeLeave.setEmpLeaveStatus("Rej");
						employeeLeave.setEmpLeaveRejectionRemarks(employeeLeaveDto.getEmpLeaveRejectionRemarks());
						employeeLeaveRepository.save(employeeLeave);
						Date date =  employeeLeave.getEmpLeaveFrom();
						Date date1 =  employeeLeave.getEmpLeaveTo();
						SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
						String strDate = formatter.format(date); 
						String strDate1 = formatter.format(date1); 
						pushNotificationUtil.SendPushNotification(empToken.getToken(),"Leave for "+strDate+" - "+strDate1+" has been Rejected..!","Leave");

						status = new Status(false, 200, "Leave Rejected");
					}else if(empToken == null) {
						employeeLeave.setEmpLeaveAppRejBy(employeeLeaveDto.getEmpId());
						employeeLeave.setEmpLeaveStatus("Rej");
						employeeLeave.setEmpLeaveRejectionRemarks(employeeLeaveDto.getEmpLeaveRejectionRemarks());
						employeeLeaveRepository.save(employeeLeave);
						status = new Status(false, 200, "Leave Rejected");
					}
				}else {
					status = new Status(true, 400, "Employee not found");
				}
			} else {
				status = new Status(true, 400, "Leave id not found");
			}

		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}


	@Override
	public EmpLaveResponse getByCustIDAndEmpId(Integer custId, Integer empId) {
		EmpLaveResponse empLaveResponse = new EmpLaveResponse();
		List<EmpLeavesDto> empLeaveDtoList = new ArrayList<>();
		try {
			Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(custId, true);
			if(customer != null) {

				List<Object[]> employeeList = employeeLeaveRepository.getPendingLeaveByEmployee_EmpIdAndCustomer_custId(empId,custId);
				if(!employeeList.isEmpty()) {

					for(Object[] p: employeeList) {	

						EmpLeavesDto empLeavesDto = new EmpLeavesDto();
						empLeavesDto.setEmpId(Integer.valueOf(p[0].toString()));
						empLeavesDto.setEmpName(p[1].toString());
						empLeavesDto.setEmpLeaveFrom((Date)p[2]);
						empLeavesDto.setEmpLeaveTo((Date)p[3]);
						empLeavesDto.setEmpLeaveDaysCount(Integer.valueOf(p[4].toString()));
						empLeavesDto.setEmpLeaveStatus(p[5].toString());
						empLeavesDto.setEmpLeaveRemarks(p[6].toString());
						empLeavesDto.setLeaveType(p[7].toString());
						empLeavesDto.setCustId(Integer.valueOf(p[8].toString()));
						empLeavesDto.setEmpLeaveId(Integer.valueOf(p[9].toString()));
						empLeaveDtoList.add(empLeavesDto);
						empLaveResponse.setEmpLeaveDtoList(empLeaveDtoList);
						empLaveResponse.status = new Status(false, 200, "Success");
					} 
				}else {
					empLaveResponse.status = new Status(false, 400, "No records found");
				}
			}else {
				empLaveResponse.status = new Status(true, 400, "Customer not found");
			}
		} catch (Exception e) {
			empLaveResponse.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return empLaveResponse;
	}




}
