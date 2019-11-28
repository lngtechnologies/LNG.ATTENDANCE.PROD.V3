package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.masters.Country;
import com.lng.attendancecompanyservice.entity.masters.State;
import com.lng.attendancecompanyservice.repositories.masters.CountryRepository;
import com.lng.attendancecompanyservice.repositories.masters.StateRepository;
import com.lng.attendancecompanyservice.service.masters.CountryService;
import com.lng.dto.masters.country.CountryDto;
import com.lng.dto.masters.country.CountryResponse;

import status.Status;
@Service
public class CountryServiceImpl implements CountryService {

	ModelMapper modelMapper=new ModelMapper();
	@Autowired
	CountryRepository countryRepositary;

	@Autowired
	StateRepository stateRepository;



	@Override
	public CountryResponse saveCountry(CountryDto countryDto) {

		//CountryDto  countryDto1 = new CountryDto();
		CountryResponse response = new CountryResponse();
		try{
			if(countryDto.getCountryName() == null || countryDto.getCountryName().isEmpty()) throw new Exception("Please enter country name");
			if(countryDto.getCountryTelCode() ==  null || countryDto.getCountryTelCode().isEmpty()) throw new Exception("Please enter country Telcode");

			// if(CheckCountryExists(countryDto.getCountryName())) throw new Exception("Country already exists");

			// if(CheckCoutryTelExists(countryDto.getCountryTelCode())) throw new Exception("Country Tel code already exists");
			Country countryTel =  countryRepositary.findByCountryTelCode(countryDto.getCountryTelCode());
			Country country =  countryRepositary.findByCountryName(countryDto.getCountryName());
			Country country1 = countryRepositary.findByCountryNameAndCountryTelCodeAndCountryIsActive(countryDto.getCountryName(), countryDto.getCountryTelCode(), false);
			
			if(country1 != null) {
				country1.setCountryName(countryDto.getCountryName());
				country1.setCountryTelCode(countryDto.getCountryTelCode());
				country1.setCountryIsActive(true);
				countryRepositary.save(country1);
				response.status = new Status(false,200, "successfully created");
			} else {
				if(country == null) {
					if(countryTel == null) {
						if(countryDto.getCountryName() != null) {
							
							country.setCountryName(countryDto.getCountryName());
							country.setCountryTelCode(countryDto.getCountryTelCode());
							country.setCountryIsActive(true);
							countryRepositary.save(country);
							response.status = new Status(false,200, "successfully created");
						}
					} else {
						response.status = new Status(false,400, "Country Tel code already exists");
					}
					
				} else {
					response.status = new Status(false,400, "Country already exists");
				} 
			}
			
		}catch(Exception ex){
			response.status = new Status(true,3000, ex.getMessage()); 
		}

		return response;
	}

	private Boolean CheckCountryExists(String countryName) {
		Country country =  countryRepositary.findByCountryName(countryName);
		//Country country1 = countryRepositary.findByCountryNameAndCountryIsActive(countryName, false);
		if(country != null) {
			return true;
		
		}else {

			return false;
		}
	}

	private Boolean CheckCoutryTelExists(String CountryTelCode) {
		Country countryTel =  countryRepositary.findByCountryTelCode(CountryTelCode);

		if(countryTel != null) {
			return true;
		} else  {
			return false;
		}
	}

	@Override
	public CountryResponse getAll() {
		CountryResponse response = new CountryResponse();
		try {
			List<Country> countryList=countryRepositary.findAll();
			response.setData1(countryList.stream().map(country -> convertToCountryDto(country)).collect(Collectors.toList()));
			response.status = new Status(false,200, "Success");

		}catch(Exception e) {
			response.status = new Status(false,4000, e.getMessage()); 
		}
		return response;
	}

	@Override
	public Status updateCountryByCountryId(CountryDto countryDto) {
		Status status = null;
		try {
			if(countryDto.getCountryName() == null || countryDto.getCountryName().isEmpty()) throw new Exception("Please enter country name");
			if(countryDto.getCountryTelCode() ==  null || countryDto.getCountryTelCode().isEmpty()) throw new Exception("Please enter country Telcode");
			if(countryDto.getCountryId() == null || countryDto.getCountryId() == 0) throw new Exception("Country id is null or zero");

			Country country = countryRepositary.findCountryByCountryId(countryDto.getCountryId());			
			//if(CheckCountryExists(countryDto.getCountryName())) throw new Exception("Country already exists");

			//if(CheckCoutryTelExistsForUpdate(countryDto.getCountryTelCode(), countryDto.getCountryId())) throw new Exception("Country Tel code already exists");
			if(country != null) {
				Country ct = countryRepositary.findCountryNameByCountryTelCode(countryDto.getCountryTelCode());
				if(ct == null) {
					country = modelMapper.map(countryDto,Country.class);
					country.setCountryIsActive(true);
					countryRepositary.save(country);
					status = new Status(false, 200, "Updated successfully");
				} else if (ct.getCountryId() == countryDto.getCountryId()) { 

					country = modelMapper.map(countryDto,Country.class);
					country.setCountryIsActive(true);
					countryRepositary.save(country);
					status = new Status(false, 200, "Updated successfully");
				}
				else{ 
					status = new Status(true,400,"CountryName already Exist");

				}
			}

			else {
				status = new Status(true, 400, "Country Not Found");

			}
		}
		catch(Exception e) {
			status = new Status(true, 4000, e.getMessage());
		}
		return status;
	}

	@Override 
	public CountryResponse deleteByCountryId(Integer countryId) {
		CountryResponse countryResponse=new CountryResponse(); 
		try { 
			Country country=countryRepositary.findCountryByCountryId(countryId);
			List<State> state = stateRepository.findStateByCountryCountryId(countryId);
			if(country!= null) {
				if(state.size() == 0 || state == null) {

					countryRepositary.delete(country);					
					countryResponse.status = new Status(false,200, "successfully deleted");
				}else {
					country.setCountryIsActive(false);
					countryRepositary.save(country);
					countryResponse.status = new Status(true,400, "The record has been disabled, since it has been used in another transactions");
				}
			} else  {
				countryResponse.status = new Status(true,400, "Country Not Found");
			}

		}catch(Exception e) { 
			countryResponse.status = new Status(true,400, e.getMessage());
		}

		return countryResponse;
	}

	public CountryDto convertToCountryDto(Country country) {
		CountryDto countryDto = modelMapper.map(country,CountryDto.class); 
		countryDto.setCountryId(country.getCountryId());
		return countryDto;
	}



}