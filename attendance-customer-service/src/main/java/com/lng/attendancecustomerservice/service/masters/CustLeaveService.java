package com.lng.attendancecustomerservice.service.masters;

import com.lng.dto.masters.custLeave.CustLeaveResponse;

import status.Status;


import com.lng.dto.masters.custLeave.custLeaveDto;

public interface CustLeaveService {
	
	CustLeaveResponse  saveCustLeave(custLeaveDto custLeaveDto);
	 
	CustLeaveResponse  getAll();
	Status updateCustLeaveByCustLeaveId(custLeaveDto custLeaveDto); 
	CustLeaveResponse deleteByCustLeaveId(Integer custLeaveId);
	CustLeaveResponse getCustLeaveByCustLeaveId(Integer custLeaveId);
	CustLeaveResponse getAllByCustId(Integer custId);
	

}
