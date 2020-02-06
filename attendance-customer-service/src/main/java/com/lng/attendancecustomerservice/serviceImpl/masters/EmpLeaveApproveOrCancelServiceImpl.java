package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.authentication.Login;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.masters.EmployeeLeave;
import com.lng.attendancecustomerservice.entity.notification.EmpToken;
import com.lng.attendancecustomerservice.repositories.authentication.ILoginRepository;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeLeaveRepository;
import com.lng.attendancecustomerservice.repositories.notification.EmpTokenRepository;
import com.lng.attendancecustomerservice.service.masters.EmpLeaveApproveOrCancelService;
import com.lng.attendancecustomerservice.utils.PushNotificationUtil;
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

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	EmpTokenRepository  empTokenRepository;

	ModelMapper ModelMapper = new ModelMapper();

	PushNotificationUtil pushNotificationUtil = new PushNotificationUtil();

	@Override
	public EmpLeaveResponseDto getByLoginIdAndCustID(Integer loginId, Integer custId) {

		EmpLeaveResponseDto empLeaveResponseDto = new EmpLeaveResponseDto();
		List<EmpLeaveDto> empLeaveDtoList = new ArrayList<>();
		try {
			Login login = iLoginRepository.findByLoginId(loginId);
			if(login != null) {

				List<Object[]> employeeList = employeeLeaveRepository.getEmpPendingLeaveByLoginIdAndCustIdAndEmpId(loginId, custId,login.getRefEmpId());
				if(!employeeList.isEmpty()) {

					for(Object[] p: employeeList) {	

						if(login.getRefEmpId() != 0 && Integer.valueOf(p[0].toString()) != 0) {

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
							empLeaveDto.setEmpLeaveRemarks(p[11].toString());
							empLeaveDto.setLeaveType(p[12].toString());
							empLeaveDtoList.add(empLeaveDto);
							empLeaveResponseDto.setEmpLeaveDtoList(empLeaveDtoList);
							empLeaveResponseDto.status = new Status(false, 200, "Success");
						} else {
							empLeaveResponseDto.status = new Status(true, 400, "Not authorized user to approve leave");
						}
					}
				}else {
					empLeaveResponseDto.status = new Status(false, 400, "No records found");
				}
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
				Login login = iLoginRepository.findByLoginId(employeeLeaveDto.getLoginId());
				if(login != null) {
					Employee employee = employeeRepository.getByEmpId(employeeLeave.getEmployee().getEmpId());
					if(employee != null) {
						EmpToken empToken = empTokenRepository.findByEmployee_EmpIdAndIsActive(employee.getEmpId(),true);
						if(empToken != null) {
							employeeLeave.setEmpLeaveAppRejBy(login.getRefEmpId());
							employeeLeave.setEmpLeaveStatus("App");
							employeeLeaveRepository.save(employeeLeave);
							Date date =  employeeLeave.getEmpLeaveFrom();
							Date date1 =  employeeLeave.getEmpLeaveTo();
							SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
							String strDate = formatter.format(date); 
							String strDate1 = formatter.format(date1); 
							pushNotificationUtil.SendPushNotification(empToken.getToken(),"Leave for "+strDate+" - "+strDate1+" has been Approved..!","Leave");

							status = new Status(false, 200, "Leave Approved for "+ employeeLeave.getEmployee().getEmpName());
						}else if(empToken == null) {
							employeeLeave.setEmpLeaveAppRejBy(login.getRefEmpId());
							employeeLeave.setEmpLeaveStatus("App");
							employeeLeaveRepository.save(employeeLeave);
							status = new Status(false, 200, "Leave Approved for "+ employeeLeave.getEmployee().getEmpName());	
						}
					}else {
						status = new Status(true, 400, "Employee not found");
					}
				} else {
					status = new Status(true, 400, "Login id not found");
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
				Login login = iLoginRepository.findByLoginId(employeeLeaveDto.getLoginId());
				if(login != null) {
					Employee employee = employeeRepository.getByEmpId(employeeLeave.getEmployee().getEmpId());
					if(employee != null) {
						EmpToken empToken = empTokenRepository.findByEmployee_EmpIdAndIsActive(employee.getEmpId(),true);
						if(empToken != null) {
							employeeLeave.setEmpLeaveAppRejBy(login.getRefEmpId());
							employeeLeave.setEmpLeaveStatus("Rej");
							employeeLeave.setEmpLeaveRejectionRemarks(employeeLeaveDto.getEmpLeaveRejectionRemarks());
							employeeLeaveRepository.save(employeeLeave);
							Date date =  employeeLeave.getEmpLeaveFrom();
							Date date1 =  employeeLeave.getEmpLeaveTo();
							SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
							String strDate = formatter.format(date); 
							String strDate1 = formatter.format(date1); 
							pushNotificationUtil.SendPushNotification(empToken.getToken(),"Leave for "+strDate+" - "+strDate1+" has been Rejected..!","Leave");

							status = new Status(false, 200, "Leave Rejected for "+ employeeLeave.getEmployee().getEmpName());
						}else if(empToken == null) {
							employeeLeave.setEmpLeaveAppRejBy(login.getRefEmpId());
							employeeLeave.setEmpLeaveStatus("Rej");
							employeeLeave.setEmpLeaveRejectionRemarks(employeeLeaveDto.getEmpLeaveRejectionRemarks());
							employeeLeaveRepository.save(employeeLeave);
							status = new Status(false, 200, "Leave Rejected for "+ employeeLeave.getEmployee().getEmpName());
						}
					}else {
						status = new Status(true, 400, "Employee not found");
					}
				} else {
					status = new Status(true, 400, "Login id not found");
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
				Login login = iLoginRepository.findByLoginId(employeeLeaveDto.getLoginId());
				if(login != null) {
					Employee employee = employeeRepository.getByEmpId(employeeLeave.getEmployee().getEmpId());
					if(employee != null) {
						EmpToken empToken = empTokenRepository.findByEmployee_EmpIdAndIsActive(employee.getEmpId(),true);
						if(empToken != null) {
							employeeLeave.setEmpLeaveAppRejBy(login.getRefEmpId());
							employeeLeave.setEmpLeaveStatus("AppCan");
							employeeLeave.setEmpLeaveRejectionRemarks(employeeLeaveDto.getEmpLeaveRejectionRemarks());
							employeeLeaveRepository.save(employeeLeave);
							Date date =  employeeLeave.getEmpLeaveFrom();
							Date date1 =  employeeLeave.getEmpLeaveTo();
							SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
							String strDate = formatter.format(date); 
							String strDate1 = formatter.format(date1);
							pushNotificationUtil.SendPushNotification(empToken.getToken(),"Leave for "+strDate+" - "+strDate1+" has been Cancelled..!","Leave");
							status = new Status(false, 200, "Leave Cancelled for "+ employeeLeave.getEmployee().getEmpName());
						}else if(empToken == null){
							employeeLeave.setEmpLeaveAppRejBy(login.getRefEmpId());
							employeeLeave.setEmpLeaveStatus("AppCan");
							employeeLeave.setEmpLeaveRejectionRemarks(employeeLeaveDto.getEmpLeaveRejectionRemarks());
							employeeLeaveRepository.save(employeeLeave);
							status = new Status(false, 200, "Leave Cancelled for "+ employeeLeave.getEmployee().getEmpName());	
						}
					}else {
						status = new Status(true, 400, "Employee not found");
					}
				} else {
					status = new Status(true, 400, "Login id not found");
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
	public EmpLeaveResponseDto getByLoginIdAndCustIDAndEmpId(Integer loginId, Integer custId,Date empLeaveFrom,Date empLeaveTo) {
		EmpLeaveResponseDto empLeaveResponseDto = new EmpLeaveResponseDto();
		List<EmpLeaveDto> empLeaveDtoList = new ArrayList<>();
		try {
			Login login = iLoginRepository.findByLoginId(loginId);
			if(login != null) {

				List<Object[]> employeeList = employeeLeaveRepository.getEmpLeaveByLoginIdAndCustIdAndEmpId(loginId, custId,login.getRefEmpId(),empLeaveFrom, empLeaveTo);
				if(!employeeList.isEmpty()) {

					for(Object[] p: employeeList) {	

						if(login.getRefEmpId() != 0 && Integer.valueOf(p[0].toString()) != 0) {

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
							empLeaveDto.setEmpLeaveRemarks(p[11].toString());
							empLeaveDto.setEmpLeaveRejectionRemarks(p[12].toString());
							empLeaveDto.setLeaveType(p[13].toString());

							empLeaveDtoList.add(empLeaveDto);

							empLeaveResponseDto.setEmpLeaveDtoList(empLeaveDtoList);
							empLeaveResponseDto.status = new Status(false, 200, "Success");

						} else {
							empLeaveResponseDto.status = new Status(true, 400, "Not authorized user to approve leave");
						}
					}
				}else {
					empLeaveResponseDto.status = new Status(false, 400, "No records found");
				}
			}

		} catch (Exception e) {
			empLeaveResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return empLeaveResponseDto;
	}

}
