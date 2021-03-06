package com.lng.attendancecustomerservice.repositories.masters;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.HolidayCalendar;
@Repository
public interface HolidayCalendarRepository extends PagingAndSortingRepository<HolidayCalendar, Integer> {
	
	@Query(value = "SELECT cl.* FROM tmholidaycalendar cl WHERE cl.refCustId =?1  ORDER BY cl.holidayDate ASC",nativeQuery = true)
	List<HolidayCalendar> findAllByRefCustId(Integer refCustId);
	
	@Query(value = "CALL CheckCustIsExistsForHolidayCalendar(?1, ?2)",nativeQuery = true)
	int  findByRefCustIdAndHolidayName(Integer refCustId,String holidayName);
	
	@Query(value = "CALL CheckDateIsExistsForHolidayName(?1, ?2)",nativeQuery = true)
	int  findByRefCustIdAndHolidayDate(Integer refCustId,Date holidayDate);
	
	HolidayCalendar findHolidayCalendarByHolidayId(Integer holidayId);
	
	HolidayCalendar findHolidayCalendarByHolidayNameAndRefCustId(String holidayName, int refCustId);
	
	List<HolidayCalendar> findAll();
	
	@Query(value = "select  br.brId,br.brName from  tmbranch br where br.refCustomerId = ?1 and br.brIsActive = TRUE ",nativeQuery = true)
	List<Object[]>  findBranchByRefCustomerId(Integer refCustId);
	
	@Query(value = "select  br.brId,br.brName from  tmbranch br where br.refCustomerId = ?1 and br.brIsActive = TRUE  and br.brValidityEnd >= CURDATE()",nativeQuery = true)
	List<Object[]>  findBranchDetailsByRefCustomerId(Integer refCustId);
	
	@Query(value = "CALL getHolidayCalendarByBranch(?1)",nativeQuery = true)
	List<Object[]>  findHolidayCalendarBybrId(Integer brId);
	
	@Query(value = "CALL getRemaingHolidayList(?1,?2)",nativeQuery = true)
	List<HolidayCalendar>  findHolidayCalendarByrefCustId(Integer refCustId);
	
	@Query(value = "CALL GetRemaingHolidayList(?1,?2)",nativeQuery = true)
	List<HolidayCalendar>  findHolidayCalendarByrefCustIdAndRefbrId(Integer refCustId,Integer refbrId);

	HolidayCalendar findHolidayCalendarByHolidayDateAndRefCustId(Date holidayDate, Integer refCustId);
	
	@Query(value = "CALL getBranchHolidayBetweenTwoDates(?1,?2,?3)",nativeQuery = true)
	List<String>  findHolidayCalendarByrefBrIdFromDateAndToDate(Integer brId, Date fromDate, Date todate);

}
