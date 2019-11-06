package com.lng.attendancecompanyservice.service.masters;


import com.lng.dto.masters.block.BlockDto;
import com.lng.dto.masters.block.BlockResponse;
import com.lng.dto.masters.branch.BranchResponse;

import status.Status;

public interface BlockService {
	
	BlockResponse saveBlock(BlockDto blockDto);
	BlockResponse getAll();
	Status updateBlockByBlkId(BlockDto blockDto);
	BlockResponse deleteByBlkId(Integer blkId);
	BlockResponse getBranchDetailsByCustId(Integer custId);
	BlockResponse getBlockDetailsByCustIdANDRefBranchId(int custId, int refBranchId);
	BlockResponse getBlockByBlkId(Integer blkId);
<<<<<<< HEAD
	BlockResponse getAllByCustId(Integer custId);
=======
	BlockResponse getByCustomer_CustId(Integer custId);
>>>>>>> branch 'develop' of https://github.com/lngtechnologies/LNG.ATTENDANCE.PROD.V3
}
