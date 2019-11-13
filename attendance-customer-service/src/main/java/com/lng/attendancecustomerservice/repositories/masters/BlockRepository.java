package com.lng.attendancecustomerservice.repositories.masters;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.Block;

public interface BlockRepository extends PagingAndSortingRepository<Block, Integer> {

	Block findByBlkId(Integer blkId);

}
