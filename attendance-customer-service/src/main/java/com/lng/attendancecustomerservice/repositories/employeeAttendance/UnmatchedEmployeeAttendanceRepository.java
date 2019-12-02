package com.lng.attendancecustomerservice.repositories.employeeAttendance;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.employeeAttendance.UnmatchedEmployeeAttendance;

public interface UnmatchedEmployeeAttendanceRepository extends PagingAndSortingRepository<UnmatchedEmployeeAttendance, Integer> {

}
