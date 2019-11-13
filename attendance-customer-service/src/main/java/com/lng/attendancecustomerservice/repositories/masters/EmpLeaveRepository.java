package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.EmpLeave;

public interface EmpLeaveRepository extends PagingAndSortingRepository<EmpLeave, Integer> {
	
	List<EmpLeave> findByCustLeave_CustLeaveId(Integer custLeaveId);

}
