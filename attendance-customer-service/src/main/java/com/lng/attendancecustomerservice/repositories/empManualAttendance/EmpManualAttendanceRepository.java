package com.lng.attendancecustomerservice.repositories.empManualAttendance;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.empManualAttendance.EmpManualAttendance;
@Repository
public interface EmpManualAttendanceRepository extends PagingAndSortingRepository<EmpManualAttendance, Integer> {
	
	@Query(value = "CALL getAbsentAttendanceByDEptIdAndDate(?1,?2)",nativeQuery = true)
	List<Object[]>  findEmpAttendanceByDepartment_deptIdAndEmpAttendanceDatetime(Integer deptId,Date empAttendanceDatetime);
	
	@Query(value = "CALL CheckEmpAttendance(?1,?2,?3)",nativeQuery = true)
	int  checkEmpManualAttnd(Integer refEmpId,String shiftStart,Date empAttendanceDatetime);

}
