package com.lng.attendancecompanyservice.service.masters;

import com.lng.dto.masters.branch.BranchDto;
import com.lng.dto.masters.branch.BranchResponse;

import status.Status;

public interface BranchService {
	BranchResponse saveBranch(BranchDto branchDto);

	BranchResponse getAll();
	Status updateBranchByBrId(BranchDto branchDto); 
	BranchResponse deleteByBrId(Integer brId);

}
