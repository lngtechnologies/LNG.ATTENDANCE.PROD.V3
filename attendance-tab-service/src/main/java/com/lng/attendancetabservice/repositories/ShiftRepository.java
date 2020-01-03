package com.lng.attendancetabservice.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancetabservice.entity.Shift;
@Repository
public interface ShiftRepository extends CrudRepository<Shift,Integer> {
	
	@Query(value = "select s.*  from tmshift s left join ttempshift es on s.shiftId = es.refShiftId  Left join tmemployee e on e.empId = es.refEmpId WHERE e.empMobile = ?1 AND es.shiftToDate IS NULL AND s.shiftIsActive = TRUE", nativeQuery = true)
	Shift  findShiftByEmployee_EmpMobile(String empMobile);
	
	@Query(value = "select s.*  from tmshift s left join ttempshift es on s.shiftId = es.refShiftId  Left join tmemployee e on e.empId = es.refEmpId WHERE e.empId = ?1 AND es.shiftToDate IS NULL AND s.shiftIsActive = TRUE", nativeQuery = true)
	Shift  findShiftByEmployee_EmpId(Integer empId);
	
	
}
