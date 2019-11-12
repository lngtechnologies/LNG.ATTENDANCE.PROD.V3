package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import com.lng.attendancecustomerservice.entity.masters.CustBrHoliday;

public interface CustBrHolidayRepository extends PagingAndSortingRepository<CustBrHoliday, Integer> {
	
	List<CustBrHoliday> findByHolidayCalendar_HolidayId(Integer holidayId);

}
