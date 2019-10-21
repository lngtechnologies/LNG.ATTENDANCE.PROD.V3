package com.lng.attendancecompanyservice.controllers.userModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.userModule.IUserModule;
import com.lng.dto.userModule.ModuleResponse;
import com.lng.dto.userModule.Param;

/**
 * @author Sachin
 *
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value="/module/user")
public class UserModuleController {
	
	@Autowired
	IUserModule IUserInterface;
	
	@PostMapping(value="/getUserModuleMapping")
	public ResponseEntity<ModuleResponse> UserModuleMappingByLoginId(@RequestBody Param param) {
		return new ResponseEntity<>(IUserInterface.GetUserModuleMapping(param.getLoginId()), HttpStatus.OK);
	}

}
