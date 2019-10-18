package com.lng.attendancecompanyservice.repositories.masters;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecompanyservice.entity.masters.Block;

public interface BlockRepository extends PagingAndSortingRepository<Block, Integer> {
	
	Block findByBlkId(Integer blkId);
}
