package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecompanyservice.entity.masters.EmployeeBranch;

public interface EmployeeBranchRepositories extends PagingAndSortingRepository<EmployeeBranch, Integer> {

	List<EmployeeBranch> findByBranch_BrId(Integer brId);
}
