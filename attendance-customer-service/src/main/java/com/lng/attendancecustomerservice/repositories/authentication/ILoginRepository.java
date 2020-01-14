package com.lng.attendancecustomerservice.repositories.authentication;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.authentication.Login;

@Repository
public interface ILoginRepository extends JpaRepository<Login, Integer> {
	Login findByLoginName(String loginName);
	
	 @Query(value = "call generatePassword", nativeQuery = true)
	 String generatePassword();
	 
	 List<Login> findByRefCustId(Integer custId);
	 
	 Login findByLoginNameAndRefCustId(String loginName, Integer custId);
	 
	 Login findByLoginMobileAndRefCustId(String mobileNumber, Integer custId);
	 
	 Login getByRefCustId(Integer custId);
	 
	 List<Login> findAll();
	 
	 Login findByLoginId(Integer loginId);
	 
	 @Query(value = "call getLoginDetailsByCustId(?1)", nativeQuery = true)
	 List<Object[]> findByCustId(Integer custId);
	 
	 @Query(value = "call getLoginDetails()", nativeQuery = true)
	 List<Object[]> findAllDetails();
	 
	 @Query(value = "call getLoginDetailsByLoginId(?1)", nativeQuery = true)
	 List<Object[]> findLogindDetailsByLoginId(Integer loginId);
	 
	 @Query(value = "SELECT * FROM ttlogin WHERE refEmpId != 0 AND loginIsActive = TRUE", nativeQuery = true)
	 List<Login> findAllByLoginIsActive();
}
