package com.lng.attendancecompanyservice.serviceImpl.userModule;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.userModule.Module;
import com.lng.attendancecompanyservice.repositories.userModule.IModuleRepository;
import com.lng.attendancecompanyservice.service.userModule.IUserModule;
import com.lng.dto.userModule.ModuleDto;
import com.lng.dto.userModule.ModuleResponse;

import status.Status;

@Service
public class UserModuleServiceImpl implements IUserModule{

	@Autowired
	IModuleRepository moduleRepo;
	
	ModelMapper mapper = new ModelMapper();
	
	@Override
	public ModuleResponse GetUserModuleMapping(int loginId) {
		ModuleResponse response = new ModuleResponse();
		try {
			// TODO 
			if(loginId == 0) throw new Exception("Login Id is zero");
			
			response.mainMenu =  moduleRepo.getUserModuleMap(loginId).stream().map(m -> convertToModuleDto(m)).collect(Collectors.toList());
			response.subMenu = moduleRepo.getUserSubModuleMap(loginId).stream().map(m -> convertToModuleDto(m)).collect(Collectors.toList());
			response.status = new Status(false, 200, "success");
		} catch (Exception ex) {
			response.status = new Status(true, 4000, ex.getMessage());
		}
		
		return response;
	}
	
	public ModuleDto convertToModuleDto(Module mod) {
		ModuleDto moduleDto = mapper.map(mod, ModuleDto.class);
        return moduleDto;
    }

}
