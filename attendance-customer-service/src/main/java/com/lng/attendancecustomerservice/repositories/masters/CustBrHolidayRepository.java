package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.CustBrHoliday;
@Repository
public interface CustBrHolidayRepository extends PagingAndSortingRepository<CustBrHoliday, Integer> {
	
	List<CustBrHoliday> findByHolidayCalendar_HolidayId(Integer holidayId);
	CustBrHoliday     findCustBrHolidayByCustBrHolidayId(Integer custBrHolidayId);
	//@Query(value = "CALL getCountOfBranchHolidayCalendar(?1,?2,?3)",nativeQuery = true)
	//int     saveCustBrHolidayByRefbrIdAndHolidayCalendar_RefcustIdAndHolidayCalendar_HolidayName(Integer rfebrId,Integer refCustId,String holidayName);
	
	@Query(value = "SELECT tbhc.* FROM tmcustbrholiday tbhc WHERE refHolidayId = ?1", nativeQuery = true)
	List<CustBrHoliday> getCustBrHolidayByRefHolidayId(Integer refHolidayId);
	
	@Query(value = "SELECT tbhc.* FROM tmcustbrholiday tbhc WHERE refbrId = ?1", nativeQuery = true)
	List<CustBrHoliday> getCustBrHolidayByRefbrId(Integer refbrId);
	
	
	@Query(value = "CALL getCountByBranch(?1)",nativeQuery = true)
	int     findCustBrHolidayByRefbrId(Integer refbrId);
	
	@Query(value = "CALL CountByBranchAndHoliday(?1,?2)",nativeQuery = true)
	int  findCustBrHolidayByBranch_BrIdAndHolidayCalendar_HolidayId(Integer brId,Integer holidayId);
	
	CustBrHoliday findCustBrHolidayByBranch_brIdAndHolidayCalendar_HolidayId(Integer refbrId, Integer refHolidayId);
}
