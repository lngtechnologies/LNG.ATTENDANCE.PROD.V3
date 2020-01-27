package com.lng.attendancecompanyservice.repositories.masters;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.UserRight;


@Repository
public interface UserRightRepository extends PagingAndSortingRepository<UserRight, Integer> {
	@Query(value = "call assignDefaultModulesToDefaultCustomerAdmin(?1)", nativeQuery = true)
	List<UserRight> assignDefaultModulesToDefaultCustomerAdmin(int loginId);
	
	List<UserRight> findByRefLoginId(Integer loginId);
	
	List<UserRight> getByRefLoginId(Integer loginId);
	
	UserRight findByUserRightId(Integer userRightId);
}
