package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.LoginDataRight;

@Repository
public interface LoginDataRightRepository extends PagingAndSortingRepository<LoginDataRight, Integer> {

	List<LoginDataRight> findByBranch_BrId(Integer brId);
}
