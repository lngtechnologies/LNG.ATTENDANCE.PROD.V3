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

			if(CheckCountryExists(countryDto.getCountryName())) throw new Exception("Country already exists");

			if(CheckCoutryTelExists(countryDto.getCountryTelCode())) throw new Exception("Country Tel code already exists");

			//System.out.println("country name "+countryName.getCountryName());
			if(countryDto.getCountryName() != null) {
				Country country = new Country();
				country = modelMapper.map(countryDto, Country.class);
				response = modelMapper.map(countryRepositary.save(country),CountryResponse.class);
				response.status = new Status(false,200, "successfully created");
			}


		}catch(Exception ex){
			response.status = new Status(true,3000, ex.getMessage()); 
		}

		return response;
	}

	private Boolean CheckCountryExists(String countryName) {
		Country country =  countryRepositary.findByCountryName(countryName);
		if(country != null) {
			return true;
		} else {
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
		//CountryDto countryDto2=new CountryDto();
		CountryResponse response = new CountryResponse();
		try {
			List<Country> countryList=countryRepositary.findAll();
			response.setData(countryList.stream().map(country -> convertToCountryDto(country)).collect(Collectors.toList()));
			response.status = new Status(false,200, "GetAll Successfully ");

		}catch(Exception e) {
			response.status = new Status(true,3000, e.getMessage()); 
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
			if(CheckCountryExists(countryDto.getCountryName())) throw new Exception("Country already exists");

			if(CheckCoutryTelExists(countryDto.getCountryTelCode())) throw new Exception("Country Tel code already exists");


			country = modelMapper.map(countryDto,Country.class);
			countryRepositary.save(country);
			status = new Status(false, 200, "Updated successfully");


		}
		
		catch(Exception e) {
			status = new Status(true, 4000, e.getMessage());
		}
		return status;
	}


	@Override 
	public CountryResponse deleteByCountryId(Integer countryId) {
		CountryResponse countryResponse=new CountryResponse(); 
		//CountryDto  countryDto = new CountryDto();
		try { 

			//if(countryDto.getCountryId() == null || countryDto.getCountryId() == 0) throw new Exception("Country id is not found");
			// Get state by countryId
			List<State> state = stateRepository.findStateByCountryCountryId(countryId);

			if(state.size() == 0 || state == null) {
				Country country=countryRepositary.findCountryByCountryId(countryId);
				if(country!= null) {
					countryRepositary.delete(country);					
					countryResponse.status = new Status(false,200, "successfully deleted");
				}
				else {
					throw new Exception("CountryId Not Found");
				}
				
			} else  {
				countryResponse.status = new Status(true,400, "Cannot Delete");
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
