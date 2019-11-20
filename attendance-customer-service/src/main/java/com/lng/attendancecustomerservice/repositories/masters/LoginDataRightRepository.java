package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.LoginDataRight;
import com.lng.attendancecustomerservice.entity.masters.UserRight;

@Repository
public interface LoginDataRightRepository extends PagingAndSortingRepository<LoginDataRight, Integer> {

	List<LoginDataRight> getByRefLoginId(Integer loginId);
	
	LoginDataRight findByLoginDataRightId(Integer loginDataRight);
	
	LoginDataRight findByRefLoginId(Integer loginId);
	
	@Query(value = "call getBranchLoginDataRightByCustId(?1)", nativeQuery = true)
	List<UserRight> findByCustId(Integer custId);
}
