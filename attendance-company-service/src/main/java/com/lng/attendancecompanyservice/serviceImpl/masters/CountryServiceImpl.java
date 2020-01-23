package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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

	private final Lock displayQueueLock = new ReentrantLock();

	@Override
	public CountryResponse saveCountry(CountryDto countryDto) {
		final Lock displayLock = this.displayQueueLock; 

		CountryResponse response = new CountryResponse();

		try{
			displayLock.lock();
			// Thread.sleep(3000L);

			if(countryDto.getCountryName() == null || countryDto.getCountryName().isEmpty()) throw new Exception("Please enter country name");
			if(countryDto.getCountryTelCode() ==  null || countryDto.getCountryTelCode().isEmpty()) throw new Exception("Please enter country code");


			// Country countryTel =  countryRepositary.findByCountryTelCode(countryDto.getCountryTelCode());
			// Country country =  countryRepositary.findByCountryName(countryDto.getCountryName());
			Country country1 = countryRepositary.findByCountryNameAndCountryTelCodeAndCountryIsActive(countryDto.getCountryName(), countryDto.getCountryTelCode(), false);

			if(country1 != null) {
				country1.setCountryName(countryDto.getCountryName());
				country1.setCountryTelCode(countryDto.getCountryTelCode());
				country1.setCountryIsActive(true);
				countryRepositary.save(country1);
				response.status = new Status(false,200, "created");
			} else {
				if(countryDto.getCountryName() != null) {

					if(CheckCountryExists(countryDto.getCountryName())) throw new Exception("Country already exists");
					if(CheckCoutryTelExists(countryDto.getCountryTelCode())) throw new Exception("Country code already exists");

					Country country = new Country();
					country.setCountryName(countryDto.getCountryName());
					country.setCountryTelCode(countryDto.getCountryTelCode());
					country.setCountryIsActive(true);
					countryRepositary.save(country);
					response.status = new Status(false,200, "created");
				}
			}
		}catch(Exception ex){
			response.status = new Status(true,500, ex.getMessage()); 
		}

		finally {
			displayLock.unlock();
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
			if(response.getData1().isEmpty()) {
				response.status = new Status(false,400, "Not found");

			}else {
				response.status = new Status(false,200, "Success");
			}

		}catch(Exception e) {
			response.status = new Status(true,500, "Oops..! Something went wrong.."); 

		}
		return response;
	}
	@Override
	public CountryResponse getAllByCountryIsActive() {
		CountryResponse response = new CountryResponse();
		try {
			List<Country> countryList=countryRepositary.findAllByCountryIsActive();
			response.setData1(countryList.stream().map(country -> convertToCountryDto(country)).collect(Collectors.toList()));
			if(!countryList.isEmpty()) {
				response.status = new Status(false,200, "Success");
			}else {
				response.status = new Status(false,400, "Not found");
			}

		}catch(Exception e) {
			response.status = new Status(true,500, "Oops..! Something went wrong.."); 

		}
		return response;
	}
	@Override
	public Status updateCountryByCountryId(CountryDto countryDto) {
		final Lock displayLock = this.displayQueueLock; 
		Status status = null;
		try {
			displayLock.lock();
			if(countryDto.getCountryName() == null || countryDto.getCountryName().isEmpty()) throw new Exception("Please enter country name");
			if(countryDto.getCountryTelCode() ==  null || countryDto.getCountryTelCode().isEmpty()) throw new Exception("Please enter country code");
			if(countryDto.getCountryId() == null || countryDto.getCountryId() == 0) throw new Exception("Country id is null or zero");

			Country country = countryRepositary.findCountryByCountryId(countryDto.getCountryId());			
			Country cu1 = countryRepositary.findByCountryName(countryDto.getCountryName());
			Country  cu2 = countryRepositary.findByCountryTelCode(countryDto.getCountryTelCode());
			if(country != null ) {
				if(cu1 == null ) {
					if(cu2 == null  ) {
						country = modelMapper.map(countryDto,Country.class);
						country.setCountryIsActive(true);
						countryRepositary.save(country);
						status = new Status(false, 200, "updated");

					} else if ( cu2.getCountryId() == countryDto.getCountryId()) { 

						country = modelMapper.map(countryDto,Country.class);
						country.setCountryIsActive(true);
						countryRepositary.save(country);
						status = new Status(false, 200, "updated");
					}
				}
				else if( cu1!= null && cu1.getCountryId() == countryDto.getCountryId() ) {
					Country te1 = countryRepositary.findByCountryTelCode(countryDto.getCountryTelCode());
					if(te1 == null) {
						country = modelMapper.map(countryDto,Country.class);
						country.setCountryIsActive(true);
						countryRepositary.save(country);
						status = new Status(false, 200, "updated");
					} else if (te1.getCountryId() == countryDto.getCountryId()) { 
						country = modelMapper.map(countryDto,Country.class);
						country.setCountryIsActive(true);
						countryRepositary.save(country);
						status = new Status(false, 200, "updated");

					}else{ 

						status = new Status(true,400,"Country code already exists");

					}
				}
				else{ 

					status = new Status(true,400,"Country already exists");
				}
			}
			else{ 
				status = new Status(false,400,"Country not found");
			}
		}
		catch(Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		finally {
			displayLock.unlock();
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
					countryResponse.status = new Status(false,200, "deleted");
				}else {
					country.setCountryIsActive(false);
					countryRepositary. findByState_refCountryId(countryId);
					countryRepositary.save(country);
					countryResponse.status = new Status(false,200, "The record has been disabled since it has been used in other transactions");
				}
			} else  {
				countryResponse.status = new Status(true,400, "Country not found");
			}

		}catch(Exception e) { 
			countryResponse.status = new Status(true,500, "Oops..! Something went wrong..");
		}

		return countryResponse;
	}

	public CountryDto convertToCountryDto(Country country) {
		CountryDto countryDto = modelMapper.map(country,CountryDto.class); 
		countryDto.setCountryId(country.getCountryId());
		return countryDto;
	}



}