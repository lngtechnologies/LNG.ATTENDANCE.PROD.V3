package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecompanyservice.entity.masters.UserRight;

public interface UserRightRepository extends PagingAndSortingRepository<UserRight, Integer> {

	@Query(value = "call assignDefaultModulesToDefaultCustomerAdmin(?1)", nativeQuery = true)
	List<UserRight> assignDefaultModulesToDefaultCustomerAdmin(int loginId);
}
