package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecompanyservice.entity.masters.Login;

public interface LoginRepository extends PagingAndSortingRepository<Login, Integer> {

	 @Query(value = "call generatePassword", nativeQuery = true)
	 String generatePassword();
	 
	 Login findByRefCustId(Integer custId);
	 
	// Login findByRefCustIdAndLoginMobileAndLoginIsActiveAndEmployee_EmpId(Integer custId, String mobileNo, Boolean isActive, Integer empId);
	 
	 Login findByRefCustIdAndLoginName(Integer custId, String loginName);
	 
	 @Query(value = "SELECT * FROM ttlogin WHERE refEmpId =?1 AND loginIsActive = TRUE", nativeQuery = true)
	 List<Login> findAllByLoginIsActiveAndRefEmpId(Integer empId);
	 
	 
	 Login findByLoginNameAndRefCustIdAndLoginIsActive(String loginName, Integer custId, Boolean loginIsActive);
	 
	 Login findByLoginId(Integer loginId);
	 
	 Login findByLoginMobileAndRefCustId(String mobileNumber, Integer custId);
	 
	 
	 @Query(value = "call getLoginDetailsByLoginId(?1)", nativeQuery = true)
	 List<Object[]> findAllUsersByloginId(Integer loginId);
	 
	 @Query(value = "call getUserDetailsByCustomerId(?1)", nativeQuery = true)
	 List<Object[]> findAllUsersByCustId(Integer custId);
}
