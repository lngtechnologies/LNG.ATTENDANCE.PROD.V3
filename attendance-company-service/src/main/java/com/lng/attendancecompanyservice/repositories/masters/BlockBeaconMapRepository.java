package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.BlockBeaconMap;

@Repository
public interface BlockBeaconMapRepository extends PagingAndSortingRepository<BlockBeaconMap, Integer> {

	BlockBeaconMap findBlockBeaconMapByBeaconCode(String beaconCode);
	
	BlockBeaconMap findBlockBeaconMapByblkBeaconMapId(Integer blkBeaconMapId);
	
	List<BlockBeaconMap> findAll();
}
