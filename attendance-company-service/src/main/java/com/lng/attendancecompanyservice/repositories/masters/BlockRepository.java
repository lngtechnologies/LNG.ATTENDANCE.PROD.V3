package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecompanyservice.entity.masters.Block;

public interface BlockRepository extends PagingAndSortingRepository<Block, Integer> {

	Block findByBlkId(Integer blkId);

	List<Block> findAll();
	
	Block findBlockByBlkId(Integer blkId);
	
	@Query(value = "select * from tmblock where blkLogicalName = ?1", nativeQuery = true)
	Block findByBlkLogicalName(String blkLogicalName);
	
	Block findBlockByblkId(Integer blkId);
	
	@Query(value = "select  tc.custId,tb.brId,tb.brCode,tb.brName FROM   tmcustomer tc LEFT JOIN  tmstate ts on ts.stateId = tc.refStateId LEFT JOIN  tmbranch tb on tb.refCustomerId = tc.custId  where tc.custId= ?1 and tc.custIsActive = true and tb.brIsActive = true", nativeQuery = true)
	List<Object[]> findBranchDetailsByCustomer_CustId(int custId);
	@Query(value = "select  tc.custId,tb.brId,tb.brCode,tb.brName FROM   tmcustomer tc LEFT JOIN  tmstate ts on ts.stateId = tc.refStateId LEFT JOIN  tmbranch tb on tb.refCustomerId = tc.custId  where tc.custId= ?1 and tc.custIsActive = true and tb.brIsActive = true and tb.brValidityEnd >= curdate()", nativeQuery = true)
	List<Object[]> findBranchListByCustomer_CustId(int custId);

	@Query(value = "CALL CheckBlockExistOrNot(?1)",nativeQuery = true)
	int  findEmployeeBlockByBlockBlkId(int blkId);


	@Query(value = "CALL CheckBlockExistForBranch(?1, ?2)",nativeQuery = true)
	int  findByRefBranchIdAndBlkLogicalName(Integer refBranchId,String blkLogicalName);

	@Query(value = "SELECT tb.blkId,tb.blkLogicalName,tb.blkGPSRadius,tb.blkLatitude, tb.blkLongitude, tmc.custId, tbr.brId FROM  tmblock tb LEFT JOIN tmbranch  tbr ON tbr.brId = tb.refBranchId LEFT JOIN  tmcustomer tmc ON tmc.custId = tbr.refCustomerId WHERE  tmc.custId=?1 AND tb.refBranchId=?2 AND tb.blkIsActive = true  ORDER BY  blkLogicalName ASC " ,nativeQuery = true) 
	List<Object[]> findBlockDetailsByCustomer_CustIdAndBranch_RefBranchId(int custId, int refBranchId);


	Block findBlockhByblkLogicalNameAndBranch_brId(String blkLogicalName, int brId);
	
	
	@Query(value = "CALL CheckBlockExistOrNotForBeaconmap(?1)",nativeQuery = true)
	int  findBlockBeaconMapByBlockBlkId(int blkId);
	
	List<Block> findByBranch_BrId(Integer brId);
	

	@Query(value ="SELECT tb.blkId,tb.refBranchId,tbr.refCustomerId,tbr.brName,tb.blkLogicalName,tb.blkGPSRadius,tb.blkLatLong,tb.blkCreatedDate,tb.blkIsActive FROM tmblock tb LEFT JOIN tmbranch tbr ON tbr.brId=tb.refBranchId WHERE  tb.blkIsActive=TRUE AND tbr.refCustomerId=?1",nativeQuery = true)
	List<Object[]> findAllByCustomer_CustIdAndBlkIsActive(Integer custId, Boolean blkIsActive);

	@Query(value = "CALL getBlockByCustId(?1)",nativeQuery = true)
	List<Block> findByCustomer_CustId(Integer custId);

	Block findByBlkLogicalNameAndBlkIsActive(String blkLogicalName, Boolean blkIsActive);
}
