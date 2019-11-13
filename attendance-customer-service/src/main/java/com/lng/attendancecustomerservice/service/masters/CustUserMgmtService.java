package com.lng.attendancecustomerservice.service.masters;

import com.lng.dto.masters.custUserMgmt.CustUserMgmtDto;

import status.Status;

public interface CustUserMgmtService {

	Status save(CustUserMgmtDto custUserMgmtDto);
}
