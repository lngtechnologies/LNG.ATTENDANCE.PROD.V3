package com.lng.attendancetabservice.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancetabservice.entity.IndustryType;

@Repository
public interface IndustryRepository extends PagingAndSortingRepository<IndustryType,Integer> {	

}
