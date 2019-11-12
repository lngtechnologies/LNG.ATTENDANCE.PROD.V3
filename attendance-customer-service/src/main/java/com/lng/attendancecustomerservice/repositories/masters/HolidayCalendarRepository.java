package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.HolidayCalendar;
@Repository
public interface HolidayCalendarRepository extends PagingAndSortingRepository<HolidayCalendar, Integer> {
	
	List<HolidayCalendar> findAllByRefCustId(Integer refCustId);
	
	@Query(value = "CALL CheckCustIsExistsForHolidayCalendar(?1, ?2)",nativeQuery = true)
	int  findByRefCustIdAndHolidayName(Integer refCustId,String holidayName);
	
	HolidayCalendar findHolidayCalendarByHolidayId(Integer holidayId);
	
	HolidayCalendar findHolidayCalendarByHolidayNameAndRefCustId(String holidayName, int refCustId);
	
	List<HolidayCalendar> findAll();

}
