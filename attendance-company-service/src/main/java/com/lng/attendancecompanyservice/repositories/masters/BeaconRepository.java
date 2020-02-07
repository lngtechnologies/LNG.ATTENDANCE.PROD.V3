package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.Beacon;

@Repository
public interface BeaconRepository extends PagingAndSortingRepository<Beacon, Integer> {

	Beacon findBeaconByBeaconCode(String beaconCode);
	
	Beacon findBeaconByBeaconId(Integer beaconId);
	

	@Query(value = "SELECT be.* FROM tmbeacon be where be.beaconIsActive = true ORDER BY beaconCode ASC", nativeQuery = true)
	List<Beacon> findAll();
	
	@Query(value = "CALL getAvailableBeaconCode()", nativeQuery = true)
	List<Beacon> findAllAvailableBeacons();
	
	Beacon findBeaconByBeaconCodeAndBeaconIsActive(String beaconCode, Boolean beaconIsActive);
	

	@Query(value = " CALL CheckBeconCodeIsUsedOrNot()", nativeQuery = true)
	List<Object[]> findAllUsedAndUnUsedBecon();
}
