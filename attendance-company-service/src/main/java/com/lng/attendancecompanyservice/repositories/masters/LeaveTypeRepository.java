package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.LeaveType;

@Repository
public interface LeaveTypeRepository extends PagingAndSortingRepository<LeaveType, Integer> {

	List<LeaveType> findAll();
}
