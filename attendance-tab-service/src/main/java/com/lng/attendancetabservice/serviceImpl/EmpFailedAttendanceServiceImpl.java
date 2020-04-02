package com.lng.attendancetabservice.serviceImpl;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancetabservice.entity.Branch;
import com.lng.attendancetabservice.entity.Customer;
import com.lng.attendancetabservice.entity.EmpFailedAttendance;
import com.lng.attendancetabservice.repositories.BranchRepository;
import com.lng.attendancetabservice.repositories.CustomerRepository;
import com.lng.attendancetabservice.repositories.EmpFailedAttendanceRepository;
import com.lng.attendancetabservice.service.EmpFailedAttendanceService;
import com.lng.dto.empFailedAttendance.EmpFailedAttendanceDto;

import status.Status;

@Service
public class EmpFailedAttendanceServiceImpl implements EmpFailedAttendanceService {

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	BranchRepository branchRepository;
	
	@Autowired
	EmpFailedAttendanceRepository empFailedAttendanceRepository;
	
	@Override
	public Status saveEmpFailedAttendance(List<EmpFailedAttendanceDto> empFailedAttendanceDtos) {
		Status status = null;
		try {
			for(EmpFailedAttendanceDto empFailedAttendanceDto: empFailedAttendanceDtos) {
				Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(empFailedAttendanceDto.getRefCustId(), true);
				if(customer != null) {
					Branch branch = branchRepository.findByBrIdAndBrIsActive(empFailedAttendanceDto.getRefBrId(), true);
					if(branch != null) {
						EmpFailedAttendance empFailedAttendance = new EmpFailedAttendance();
						empFailedAttendance.setRefCustId(customer);
						empFailedAttendance.setRefBrId(branch);
						empFailedAttendance.setEmpAttendanceFlag(empFailedAttendanceDto.getEmpAttendanceFlag());
						empFailedAttendance.setEmpAttendanceDatetime(empFailedAttendanceDto.getEmpAttendanceDatetime());
						empFailedAttendance.setEmployeePicture(base64ToByte(empFailedAttendanceDto.getEmployeePicture()));
						
						empFailedAttendanceRepository.save(empFailedAttendance);
						
						status = new Status(false, 200, "success");
					} else {
						status = new Status(true, 400, "Branch not found");
					}
					
				} else {
					status = new Status(true, 400, "Customer not found");
				}
			}
		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}

	
	public  byte[] base64ToByte(String base64) {
		byte[] decodedByte = Base64.getDecoder().decode(base64);
		return decodedByte;
	}
}
