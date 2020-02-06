package com.lng.attendancecompanyservice.repositories.exception;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.exception.ApplicationException;

@Repository
public interface ApplicationExceptionRepository extends PagingAndSortingRepository<ApplicationException, Integer> {

}
