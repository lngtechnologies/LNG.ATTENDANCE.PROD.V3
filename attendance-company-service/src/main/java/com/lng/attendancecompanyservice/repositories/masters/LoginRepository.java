package com.lng.attendancecompanyservice.repositories.masters;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecompanyservice.entity.masters.Login;

public interface LoginRepository extends PagingAndSortingRepository<Login, Integer> {

	 @Query(value = "call generatePassword", nativeQuery = true)
	 String generatePassword();
	 
	 Login findByRefCustId(Integer custId);
	 
	 Login findByRefCustIdAndLoginMobile(Integer custId, String mobileNo);
}
