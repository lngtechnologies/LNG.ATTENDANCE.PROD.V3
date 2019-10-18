package com.lng.attendancecompanyservice.repositories.masters;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecompanyservice.entity.masters.BlockBeaconMap;

public interface BlockBeaconMapRepository extends PagingAndSortingRepository<BlockBeaconMap, Integer> {

	BlockBeaconMap findBlockBeaconMapByBeaconCode(String beaconCode);
}
