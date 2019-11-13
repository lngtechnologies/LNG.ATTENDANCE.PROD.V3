package com.lng.attendancecustomerservice.repositories.empAppSetup;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.BlockBeaconMap;

@Repository
public interface WelcomeScreenRepository extends PagingAndSortingRepository<BlockBeaconMap, Integer> {

	@Query(value = "CALL getBeaconsByEmpId(?1)", nativeQuery = true)
	List<BlockBeaconMap> findByEmployee_EmpId(Integer empId);
	
	BlockBeaconMap findByBlock_BlkId(Integer blkId);
}
