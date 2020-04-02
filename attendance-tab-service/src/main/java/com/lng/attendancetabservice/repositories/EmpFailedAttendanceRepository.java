package com.lng.attendancetabservice.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancetabservice.entity.EmpFailedAttendance;

@Repository
public interface EmpFailedAttendanceRepository extends PagingAndSortingRepository<EmpFailedAttendance, Integer> {

}
