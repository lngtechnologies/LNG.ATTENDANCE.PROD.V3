package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.Beacon;

@Repository
public interface BeaconRepository extends PagingAndSortingRepository<Beacon, Integer> {

	Beacon findBeaconByBeaconCode(String beaconCode);
	
	Beacon findBeaconByBeaconId(Integer beaconId);
	
	List<Beacon> findAll();
}
