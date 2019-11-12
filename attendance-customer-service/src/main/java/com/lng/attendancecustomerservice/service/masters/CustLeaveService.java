package com.lng.attendancecustomerservice.service.masters;

import com.lng.dto.masters.custLeave.CustLeaveResponse;
import com.lng.dto.masters.custLeave.custLeaveDto;

import status.Status;

public interface CustLeaveService {
	
	CustLeaveResponse  saveCustLeave(custLeaveDto custLeaveDto);
	 
	CustLeaveResponse  getAll();
	Status updateCustLeaveByCustLeaveId(custLeaveDto custLeaveDto); 
	CustLeaveResponse deleteByCustLeaveId(Integer custLeaveId);
	CustLeaveResponse getCustLeaveByCustLeaveId(Integer custLeaveId);
	CustLeaveResponse getAllByCustId(Integer custId);
	

}
