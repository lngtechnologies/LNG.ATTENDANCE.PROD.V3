package com.lng.attendancecompanyservice.repositories.authentication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.Login;



@Repository
public interface ILoginRepository extends JpaRepository<Login, Integer> {
	
	Login findByLoginNameAndLoginIsActive(String loginName, boolean LoginIsActive);
	
	 @Query(value = "call generatePassword", nativeQuery = true)
	 String generatePassword();
}
