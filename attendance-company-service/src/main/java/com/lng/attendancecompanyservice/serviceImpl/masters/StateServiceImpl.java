package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.masters.State;
import com.lng.attendancecompanyservice.repositories.masters.BranchRepository;
import com.lng.attendancecompanyservice.repositories.masters.StateRepository;
import com.lng.attendancecompanyservice.service.masters.StateService;
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

	@Override
	public StateResponse saveState(StateDto stateDto) {
		StateResponse response = new StateResponse();
		try{
			if(stateDto.getStateName() == null || stateDto.getStateName().isEmpty()) throw new Exception("Please enter State name");

			if(CheckStateExists(stateDto.getStateName())) throw new Exception("State already exists");

			if(stateDto.getStateName() != null) {
				State state = new State();
				state = modelMapper.map(stateDto, State.class);
				response = modelMapper.map(stateRepository.save(state),StateResponse.class);
				response.status = new Status(false,200, "successfully created");
			}


		}catch(Exception ex){
			response.status = new Status(true,3000, ex.getMessage()); 
		}

		return response;
	}


	private Boolean CheckStateExists(String stateName) {
		State state =  stateRepository.findByStateName(stateName);
		if(state != null) {
			return true;
		} else {
			return false;
		}
	}


	@Override
	public StateResponse getAll() {
		//StateDto stateDto2=new StateDto();
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
			//State state=stateRepository.findCountryByCountryId(stateDto.getRefCountryId());
			State 	state = stateRepository.findStateByStateId(stateDto.getStateId())	;		
			if(CheckStateExists(stateDto.getStateName())) throw new Exception("State already exists");


			state = modelMapper.map(stateDto,State.class);
			stateRepository.save(state);
			status = new Status(false, 200, "Updated successfully");


		}
		catch(Exception e) {
			status = new Status(true, 4000, e.getMessage());
		}
		return status;
	}



	/*
	 * @Override public StateResponse deleteByStateId(Integer stateId) {
	 * StateResponse stateResponse=new StateResponse(); try { List<Branch> branch =
	 * branchRepository.findBranchByStateStateId(stateId);
	 * 
	 * if(branch.size() == 0 || branch == null) { State
	 * state=stateRepository.findStateByStateId(stateId); if(state!= null) {
	 * stateRepository.delete(state); stateResponse.status = new Status(false,200,
	 * "successfully deleted"); } else { throw new Exception("StateId Not Found"); }
	 * 
	 * } else { stateResponse.status = new Status(true,400, "Cannot Delete"); }
	 * 
	 * }catch(Exception e) { stateResponse.status = new Status(true,400,
	 * e.getMessage()); }
	 * 
	 * return stateResponse; }
	 */

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
	


	@Override
	public StateResponse getStateDetailsByRefCountryId(Integer refCountryId) {
		//StateDto stateDto = new StateDto();
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

	public StateDto convertToStateDto(State state) {
		StateDto stateDto = modelMapper.map(state,StateDto.class);
		stateDto.setStateId(state.getStateId());
		stateDto.setRefCountryId(state.getCountry().getCountryId());
		stateDto.setCountryName(state.getCountry().getCountryName());
		return stateDto;
	}
}


