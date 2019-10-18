package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.IndustryType;

@Repository
public interface IndustryTypeRepository extends PagingAndSortingRepository<IndustryType, Integer> {

	IndustryType findIndustryTypeByIndustryId(Integer industryId);
	
	IndustryType findIndustryTypeByIndustryName(String industryName);
	
	List<IndustryType> findAll();
}
