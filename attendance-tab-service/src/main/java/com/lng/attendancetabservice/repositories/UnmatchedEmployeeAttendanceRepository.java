package com.lng.attendancetabservice.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancetabservice.entity.UnmatchedEmployeeAttendance;

public interface UnmatchedEmployeeAttendanceRepository extends PagingAndSortingRepository<UnmatchedEmployeeAttendance, Integer> {

}
