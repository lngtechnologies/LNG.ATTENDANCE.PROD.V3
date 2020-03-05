package com.lng.attendancecustomerservice.repositories.empAppSetup;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.HolidayCalendar;

public interface BranchHolidayRepository extends PagingAndSortingRepository<HolidayCalendar, Integer> {
	
	@Query(value = "CALL getHolidayCalendarByBranch(?1)",nativeQuery = true)
	List<Object[]>  findHolidayCalendarBybrId(Integer brId);

}
