package com.lng.attendancecustomerservice.repositories.masters;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Shift;
@Repository
public interface ShiftRepository extends PagingAndSortingRepository<Shift,Integer> {

}
