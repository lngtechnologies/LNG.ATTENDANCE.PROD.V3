package com.lng.attendancetabservice.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancetabservice.entity.Shift;
@Repository
public interface ShiftRepository extends CrudRepository<Shift,Integer> {
	
	
	
}
