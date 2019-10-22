package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Shift;
@Repository
public interface ShiftRepository extends PagingAndSortingRepository<Shift,Integer> {

	List<Shift> findAll();

	@Query(value = "select * from tmshift where shiftName = ?1", nativeQuery = true)
	Shift findByShiftName(String shiftName);

	Shift findShiftByShiftId(Integer shiftId);
	@Query(value = "SELECT shiftName,shiftStart,shiftEnd,refBrId FROM  tmshift ts WHERE  refBrId=  ?1", nativeQuery = true)
	List<Object[]>  findShiftDetailsByBranch_RefBrId(int refBrId);

	@Query(value = "CALL CheckShiftIdIsExistsOrNot(?1)",nativeQuery = true)
	int  findEmployeeByShiftShiftId(int shiftId);
	
	@Query(value = "CALL CheckShiftExistsForBranch(?1, ?2);",nativeQuery = true)
	int  findByRefBrIdAndShiftName(Integer refBrId,String shiftName);
}
