package com.lng.attendancecompanyservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.masters.CountryService;
import com.lng.dto.masters.country.CountryDto;
import com.lng.dto.masters.country.CountryResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/country")
public class CountryController {
	@Autowired
	CountryService countryService;

	@PostMapping(value="/create")
	public ResponseEntity<CountryResponse>save(@RequestBody CountryDto countryDto){
		CountryResponse countryDto1 = countryService.saveCountry(countryDto);
		if(countryDto!=  null){
			return new ResponseEntity<CountryResponse>(countryDto1,HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "/getAll")
	public ResponseEntity<CountryResponse> getAll() {
		CountryResponse countryDto =  countryService.getAll();
		if(countryDto.getData().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<CountryResponse>(countryDto, HttpStatus.OK);
	}

	@PostMapping(value = "/deleteByCountryId")
	public ResponseEntity<CountryResponse> delete(@RequestBody CountryDto countryDto) {
		CountryResponse countryDto2 = countryService.deleteByCountryId(countryDto.getCountryId());
		if(countryDto!=null){
			return new ResponseEntity<CountryResponse>(countryDto2,HttpStatus.OK);
		} return new ResponseEntity(HttpStatus.NO_CONTENT); 
	}


	@PostMapping(value="/updateByCountryId")
	public ResponseEntity<status.Status> update(@RequestBody CountryDto countryDto){
		status.Status status = countryService.updateCountryByCountryId(countryDto);
		if(countryDto != null){
			return  new ResponseEntity<status.Status>(status, HttpStatus.OK);
		}
		return new ResponseEntity<status.Status>(HttpStatus.NO_CONTENT);
	}

}