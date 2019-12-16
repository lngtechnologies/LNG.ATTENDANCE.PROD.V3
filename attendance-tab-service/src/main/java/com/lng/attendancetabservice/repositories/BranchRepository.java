package com.lng.attendancetabservice.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancetabservice.entity.Branch;
@Repository
public interface BranchRepository extends PagingAndSortingRepository<Branch,Integer> {

}
