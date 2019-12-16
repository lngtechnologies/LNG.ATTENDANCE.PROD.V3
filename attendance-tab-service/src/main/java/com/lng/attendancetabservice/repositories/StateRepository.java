package com.lng.attendancetabservice.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancetabservice.entity.State;

@Repository
public interface StateRepository extends PagingAndSortingRepository<State,Integer> {	

}
