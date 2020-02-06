package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lng.attendancecompanyservice.entity.masters.Country;

@Repository
public interface CountryRepository extends CrudRepository<Country,Integer> {

	@Query(value = "SELECT * FROM tmcountry WHERE countryIsActive = TRUE  ORDER BY countryName ASC", nativeQuery = true)
	List<Country> findAllByCountryIsActive();
	Country findCountryByCountryId(Integer countryId);
	// Country findCountry(Integer countryId, String CountryTelCode,String countryName);
	@Query(value = "select * from tmcountry where countryName = ?1", nativeQuery = true)
	Country findByCountryName(String countryName);
	
	Country findByCountryNameAndCountryTelCodeAndCountryIsActive(String countryName, String countryTelCode, Boolean countryIsActive);
	Country  findByCountryTelCode(String countryTelCode);
	Country   getStateByCountryId(Integer countryId);
	Country   getCoustomerByCountryId(Integer countryId );
	Country  findCountryNameByCountryTelCode(String countryTelCode);
	
	@Query(value = "SELECT * FROM tmcountry  ORDER BY countryName ASC", nativeQuery = true)
	List<Country> findAll();
	
	@Transactional
	@Modifying 
	@Query(value = "UPDATE tmstate ts SET ts.stateIsActive = 0 WHERE ts.refCountryId = ?1", nativeQuery = true)
	void findByState_refCountryId(Integer countryId);
	


}
