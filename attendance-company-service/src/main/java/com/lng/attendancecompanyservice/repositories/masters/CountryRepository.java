package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.Country;

@Repository
public interface CountryRepository extends CrudRepository<Country,Integer> {

	List<Country> findAllByCountryIsActive(Boolean countryIsActive);
	Country findCountryByCountryId(Integer countryId);
	// Country findCountry(Integer countryId, String CountryTelCode,String countryName);
	@Query(value = "select * from tmcountry where countryName = ?1", nativeQuery = true)
	Country findByCountryName(String countryName);

	Country  findByCountryTelCode(String countryTelCode);
	Country   getStateByCountryId(Integer countryId);
	Country   getCoustomerByCountryId(Integer countryId );
	Country  findCountryNameByCountryTelCode(String countryTelCode);


}
