package com.lng.attendancecustomerservice.repositories.masters;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.MonthlyNoOfDays;

public interface EmpMonthlyNoOfDaysRepository extends PagingAndSortingRepository<MonthlyNoOfDays, Integer>{

}
