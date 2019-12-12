package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.authentication.Login;
import com.lng.attendancecustomerservice.entity.masters.UserRight;
import com.lng.attendancecustomerservice.entity.userModule.Module;

@Repository
public interface UserRightRepository extends PagingAndSortingRepository<UserRight, Integer> {

	UserRight findByRefModuleId(Integer moduleId);
	
	List<UserRight> findByRefLoginId(Integer loginId);
	
	List<UserRight> getByRefLoginId(Integer loginId);
	
	UserRight findByUserRightId(Integer userRightId);
	
	@Query(value = "call getModulesByCustId(?1)", nativeQuery = true)
	List<Module> findByCustId(Integer custId);
	
	UserRight findByRefLoginIdAndRefModuleId(Integer loginId, Integer moduleId);
	
	// List<UserRight> findByRefLoginId(Integer loginId);
}
