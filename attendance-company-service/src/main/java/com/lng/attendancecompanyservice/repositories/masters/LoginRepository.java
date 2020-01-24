package com.lng.attendancecompanyservice.repositories.masters;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecompanyservice.entity.masters.Login;

public interface LoginRepository extends PagingAndSortingRepository<Login, Integer> {

	 @Query(value = "call generatePassword", nativeQuery = true)
	 String generatePassword();
	 
	 Login findByRefCustId(Integer custId);
	 
	// Login findByRefCustIdAndLoginMobileAndLoginIsActiveAndEmployee_EmpId(Integer custId, String mobileNo, Boolean isActive, Integer empId);
	 
	 Login findByLoginName(Integer custId, String loginName);
}
