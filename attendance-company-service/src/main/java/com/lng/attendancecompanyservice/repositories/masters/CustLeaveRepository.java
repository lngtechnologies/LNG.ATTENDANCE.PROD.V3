package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.CustLeave;

@Repository
public interface CustLeaveRepository extends PagingAndSortingRepository<CustLeave, Integer> {

	@Query(value = "CALL assignCustLeaveToCustomer(?1)", nativeQuery = true)
	List<CustLeave> assignCustLeaveToCustomer(Integer custId);
}
