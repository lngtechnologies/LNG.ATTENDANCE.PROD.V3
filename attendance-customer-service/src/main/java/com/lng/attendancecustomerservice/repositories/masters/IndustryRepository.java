package com.lng.attendancecustomerservice.repositories.masters;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.IndustryType;

@Repository
public interface IndustryRepository extends PagingAndSortingRepository<IndustryType,Integer> {	

}
