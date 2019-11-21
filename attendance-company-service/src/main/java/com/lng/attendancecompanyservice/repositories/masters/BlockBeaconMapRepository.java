package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.BlockBeaconMap;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapDto;

@Repository
public interface BlockBeaconMapRepository extends PagingAndSortingRepository<BlockBeaconMap, Integer> {

	List<BlockBeaconMap> findBlockBeaconMapByBeaconCodeAndBlkBeaconMapIsActive(String beaconCode, Boolean blkBeaconMapIsActive);
	
	BlockBeaconMap findByBeaconCode(String beaconCode);
	
	BlockBeaconMap findByBeaconCodeAndBlkBeaconMapIsActive(String beaconCode, Boolean blkBeaconMapIsActive);
	
	BlockBeaconMap findBlockBeaconMapByblkBeaconMapId(Integer blkBeaconMapId);
	
	@Query(value = "SELECT bm.* FROM tmblockbeaconmap bm WHERE refBlkId = ?1", nativeQuery = true)
	List<BlockBeaconMap> getBlockBeanMapByBlkId(Integer blkId);
	
	@Query(value = "SELECT beaconCode FROM tmblockbeaconmap WHERE blkBeaconMapId IN(?1);", nativeQuery = true)
	List<String> getBeaconCodeByBlkBeaconMapId(List<Integer> mapId);
	
	List<BlockBeaconMap> findAllByBlkBeaconMapIsActive(Boolean blkBeaconMapIsActive);
	
	/*@Query(value = "CALL getBlockBeaconMapDetails();", nativeQuery = true)
	List<Object[]> findAllByBlkBeaconMapIsActive();
	
	@Query(value = "call getBeaconCodeByBlkId(?1);", nativeQuery = true)
	List<Object[]> findBeaconCodeByBlkId(Integer blkId);*/
	
	@Query(value = "CALL getBlockBeaconMapByCustId(?1)", nativeQuery = true)
	List<BlockBeaconMap> findByCustomer_CustId(Integer custId);
	
	List<BlockBeaconMap> findByBlock_BlkIdAndBlkBeaconMapIsActive(Integer blkId, Boolean blkBeaconMapIsActive);
}
