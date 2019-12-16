package com.lng.attendancetabservice.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancetabservice.entity.Country;

@Repository
public interface CountryRepository extends PagingAndSortingRepository<Country,Integer> {	

}
