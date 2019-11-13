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
}
