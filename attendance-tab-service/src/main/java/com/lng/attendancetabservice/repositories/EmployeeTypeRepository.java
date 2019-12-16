package com.lng.attendancetabservice.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancetabservice.entity.EmployeeType;

public interface EmployeeTypeRepository extends PagingAndSortingRepository<EmployeeType, Integer> {

}
