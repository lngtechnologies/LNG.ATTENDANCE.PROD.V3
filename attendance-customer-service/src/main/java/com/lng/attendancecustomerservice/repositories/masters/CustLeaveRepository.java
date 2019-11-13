package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.CustLeave;

@Repository
public interface CustLeaveRepository extends PagingAndSortingRepository<CustLeave, Integer> {
	
	List<CustLeave>  findAll();

	CustLeave findCustLeaveByCustLeaveId(Integer custLeaveId);

	List<CustLeave>  findCustLeaveByCustomer_custId(Integer custId);
	
	@Query(value = "CALL CheckCustLeaveIsExistForCustomer(?1, ?2)",nativeQuery = true)
	int findByRefCustIdAndCustLeaveName(Integer refCustId,String custLeaveName);
	
	CustLeave findCustLeaveBycustLeaveNameAndCustomer_custId(String custLeaveName, int custId);
     
}
