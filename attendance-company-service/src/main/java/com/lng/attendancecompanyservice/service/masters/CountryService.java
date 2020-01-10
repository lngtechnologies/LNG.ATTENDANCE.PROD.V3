package com.lng.attendancecompanyservice.service.masters;

import com.lng.dto.masters.country.CountryDto;
import com.lng.dto.masters.country.CountryResponse;

import status.Status;

public interface CountryService {
	
	CountryResponse saveCountry(CountryDto countryDto);
	CountryResponse getAllByCountryIsActive();
	Status updateCountryByCountryId(CountryDto countryDto);
	CountryResponse deleteByCountryId(Integer countryId);
	CountryResponse getAll();
	

}
