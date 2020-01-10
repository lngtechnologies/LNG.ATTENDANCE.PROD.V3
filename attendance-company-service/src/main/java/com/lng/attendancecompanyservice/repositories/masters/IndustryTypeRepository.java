package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.IndustryType;

@Repository
public interface IndustryTypeRepository extends PagingAndSortingRepository<IndustryType, Integer> {

	IndustryType findIndustryTypeByIndustryId(Integer industryId);
	
	IndustryType findIndustryTypeByIndustryName(String industryName);
	
	@Query(value = "SELECT * FROM tmindustry ORDER BY industryName ASC", nativeQuery = true)
	List<IndustryType> findAll();
	
	IndustryType findIndustryTypeByIndustryNameAndIndustryIsActive(String industryName, Boolean industryIsActive);
	
}
