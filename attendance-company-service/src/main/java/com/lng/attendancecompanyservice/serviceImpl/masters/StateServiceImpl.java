package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.masters.Country;
import com.lng.attendancecompanyservice.entity.masters.State;
import com.lng.attendancecompanyservice.repositories.masters.BranchRepository;
import com.lng.attendancecompanyservice.repositories.masters.CountryRepository;
import com.lng.attendancecompanyservice.repositories.masters.StateRepository;
import com.lng.attendancecompanyservice.service.masters.StateService;
import com.lng.dto.masters.country.CountryDto;
import com.lng.dto.masters.state.StateDto;
import com.lng.dto.masters.state.StateResponse;

import status.Status;
@Service
public class StateServiceImpl implements StateService {
	ModelMapper modelMapper=new ModelMapper();

	@Autowired
	StateRepository stateRepository;
	@Autowired
	BranchRepository branchRepository;
	@Autowired
	CountryRepository  countryRepository;

	@Override
	public StateResponse saveState(StateDto stateDto) {
		StateResponse response = new StateResponse();
		State state = new State();
		try{
			if(stateDto.getStateName() == null || stateDto.getStateName().isEmpty()) throw new Exception("Please enter State name");

			int a = stateRepository.findByRefCountryIdAndStateName(stateDto.getRefCountryId(), stateDto.getStateName());
			if(a == 0) {
				Country country = countryRepository.findCountryByCountryId(stateDto.getRefCountryId());
				if(country != null) {
				state.setCountry(country);
				state.setStateName(stateDto.getStateName());
				stateRepository.save(state);
              response.status = new Status(false,200, "successfully created");

				}
				else{ 
					response.status = new Status(true,400, "CountryId Not Found");
				}
			}
			else{ 
				response.status = new Status(true,400,"StateName already exist for branch :" + stateDto.getRefCountryId());
			}
		} catch (Exception e) {
			response.status = new Status(true, 4000, e.getMessage());
		}

		return response;
	}



	/*
	 * private Boolean CheckStateExists(String stateName) { State state =
	 * stateRepository.findByStateName(stateName); if(state != null) { return true;
	 * } else { return false; } }
	 */


	@Override
	public StateResponse getAll() {
		StateResponse response = new StateResponse();
		try {
			List<State> stateList=stateRepository.findAll();
			response.setData(stateList.stream().map(state -> convertToStateDto(state)).collect(Collectors.toList()));
			response.status = new Status(false,200, "successfully  GetAll");
		}catch(Exception e) {
			response.status = new Status(true,3000, e.getMessage()); 

		}
		return response;
	}

	@Override
	public Status updateSateByStateId(StateDto stateDto) {
		Status status = null;
		try {
			if(stateDto.getStateName() == null || stateDto.getStateName().isEmpty()) throw new Exception("Please enter State name");
			if(stateDto.getStateId() == null || stateDto.getStateId() == 0) throw new Exception("State id is null or zero");
			if(stateDto.getRefCountryId() == null || stateDto.getRefCountryId() == 0) throw new Exception("RefCountryId id is null or zero");
			State 	state = stateRepository.findStateByStateId(stateDto.getStateId())	;		

			Country country = countryRepository.findCountryByCountryId(stateDto.getRefCountryId());
			if(country != null) {
				State st = stateRepository.findStateBystateNameAndCountry_countryId(stateDto.getStateName(), stateDto.getRefCountryId());
				if(st == null) {

					state = modelMapper.map(stateDto,State.class);
					state.setCountry(country);
					stateRepository.save(state);
					status = new Status(false, 200, "Updated successfully");
				} else if (st.getStateId() == stateDto.getStateId()) { 

					state = modelMapper.map(stateDto,State.class);
					state.setCountry(country);
					stateRepository.save(state);
					status = new Status(false, 200, "Updated successfully");
				}
				else{ 
					status = new Status(true,400,"StateName already exist for CountryId :" + stateDto.getRefCountryId());
				}
			}

			else {
				status = new Status(false, 200, "CountryId Not Found");

			}
		}
		catch(Exception e) {
			status = new Status(true, 4000, e.getMessage());
		}
		return status;
	}
	@Override
	public StateResponse deleteByStateId(Integer stateId) {
		StateResponse stateResponse=new StateResponse();
		try {

			int a = stateRepository.findBranchByStateStateId(stateId);
			if(a == 0) {
				State state = stateRepository.findStateByStateId(stateId);
				if(state!= null) {
					stateRepository.delete(state);					
					stateResponse.status = new Status(false,200, "successfully deleted");
				}

			} else  {
				stateResponse.status = new Status(true,400, "Cannot Delete");
			}

		}catch(Exception e) { 
			stateResponse.status = new Status(true,400, "StateId Not Found");
		}

		return stateResponse;
	}


	public StateDto convertToStateDto(State state) {
		StateDto stateDto = modelMapper.map(state,StateDto.class);
		stateDto.setStateId(state.getStateId());
		stateDto.setRefCountryId(state.getCountry().getCountryId());
		stateDto.setCountryName(state.getCountry().getCountryName());
		CountryDto countryDto = modelMapper.map(state.getCountry(),CountryDto.class);
		return stateDto;
	}


	@Override
	public StateResponse getStateDetailsByRefCountryId(Integer refCountryId) {
		StateResponse stateResponse=new StateResponse();
		List<StateDto> stateDtoList = new ArrayList<>();
		try {

			List<Object[]> stateList = stateRepository.findStateDetailsByCountry_RefCountryId(refCountryId);

			for (Object[] p : stateList) {	
				StateDto stateDto1 = new StateDto();
				stateDto1.setStateId(Integer.valueOf(p[0].toString()));
				stateDto1.setStateName(p[1].toString());
				stateDtoList.add(stateDto1);
				stateResponse.status = new Status(false,200, "successfully GetStateDetails");

			}

		}catch (Exception e){
			stateResponse.status = new Status(true,3000, e.getMessage());
		}
		stateResponse.setData(stateDtoList);
		return stateResponse;
	}

}


