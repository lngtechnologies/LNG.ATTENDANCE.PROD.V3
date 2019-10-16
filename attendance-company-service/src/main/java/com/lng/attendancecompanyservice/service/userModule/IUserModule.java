package com.lng.attendancecompanyservice.service.userModule;

import com.lng.dto.userModule.ModuleResponse;

public interface IUserModule {
	ModuleResponse GetUserModuleMapping(int loginId);
}
