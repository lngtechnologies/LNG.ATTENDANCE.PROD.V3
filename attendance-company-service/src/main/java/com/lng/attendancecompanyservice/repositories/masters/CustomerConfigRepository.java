package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.CustomerConfig;

@Repository
public interface CustomerConfigRepository extends PagingAndSortingRepository<CustomerConfig, Integer> {

	@Query(value = "call assignCustConfifToBranch(?1, ?2, ?3)", nativeQuery = true)
	List<CustomerConfig> assignConfigToBranch(int custId, int brId, String config);
}
