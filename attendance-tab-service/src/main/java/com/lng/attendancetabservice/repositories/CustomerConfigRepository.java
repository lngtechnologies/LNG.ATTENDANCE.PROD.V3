package com.lng.attendancetabservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancetabservice.entity.CustomerConfig;


@Repository
public interface CustomerConfigRepository extends PagingAndSortingRepository<CustomerConfig, Integer> {
	
	
	@Query(value = "SELECT cf.config,cf.stausFlag FROM ttcustomerconfig cf WHERE cf.refCustId = ?1 AND cf.refBrId = ?2", nativeQuery = true)
	List<Object[]> findByCustomer_CustIdAndBranch_BrId(Integer custId, Integer brId);
}
