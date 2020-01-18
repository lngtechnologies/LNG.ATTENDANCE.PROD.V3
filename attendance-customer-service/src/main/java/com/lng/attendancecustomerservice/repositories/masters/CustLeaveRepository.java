package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.CustLeave;

@Repository
public interface CustLeaveRepository extends PagingAndSortingRepository<CustLeave, Integer> {
	
	List<CustLeave>  findAll();

	CustLeave findCustLeaveByCustLeaveIdAndcustLeaveIsActive(Integer custLeaveId, Boolean custLeaveIsActive);

	List<CustLeave>  findCustLeaveByCustomer_custIdAndcustLeaveIsActive(Integer custId, Boolean custLeaveIsActive);
	
	@Query(value = "CALL CheckCustLeaveIsExistForCustomer(?1, ?2)",nativeQuery = true)
	int findByRefCustIdAndCustLeaveName(Integer refCustId,String custLeaveName);
	
	CustLeave findCustLeaveBycustLeaveNameAndCustomer_custId(String custLeaveName, int custId);
	
	// CustLeave findByRefCustIdAndCustLeaveNameAnd(Integer refCustId,String custLeaveName);
	
	@Query(value = "SELECT cl.* FROM tmcustleave cl where cl.refCustId = ?1 and custLeaveIsActive = true ORDER BY cl.custLeaveName ASC",nativeQuery = true)
	List<CustLeave> findAllByCustomer_CustId(Integer refCustId);
     
}
