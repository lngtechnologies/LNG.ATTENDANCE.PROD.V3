package com.lng.attendancecustomerservice.repositories.exception;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.exception.ApplicationException;

public interface ApplicationExceptionRepository extends PagingAndSortingRepository<ApplicationException, Integer> {

}
