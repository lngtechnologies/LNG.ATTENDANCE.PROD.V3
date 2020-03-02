package com.lng.attendancecustomerservice.controllers.empAppSetup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.empAppSetup.EmployeeService;
import com.lng.dto.employeeAppSetup.AbsentDetailsResponse;
import com.lng.dto.employeeAppSetup.AppLeaveResponse;
import com.lng.dto.employeeAppSetup.AttendanceParamDto;
import com.lng.dto.employeeAppSetup.EarlyLeaversResponse;
import com.lng.dto.employeeAppSetup.EmployeeDto;
import com.lng.dto.employeeAppSetup.LateComersResponse;
import com.lng.dto.employeeAppSetup.OtpResponseDto;
import com.lng.dto.employeeAppSetup.ResponseDto;

import status.StatusDto;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/employee/setup")
public class EmployeeSetup1Controller {

	@Autowired
	EmployeeService employeeService;
	
	
	@PostMapping(value = "/findByCustCodeAndEmpMobile")
    public ResponseEntity<ResponseDto> findByCustomerId(@RequestBody EmployeeDto employeeDto) {
		ResponseDto responseDto = employeeService.getByCustCodeAndEmpMobile(employeeDto.getCustCode(), employeeDto.getEmpMobile());
        if (responseDto !=null){
            return new ResponseEntity<ResponseDto>(responseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	
	@PostMapping(value = "/generateOtp")
    public ResponseEntity<OtpResponseDto> generateOtp(@RequestBody EmployeeDto employeeDto) {
		OtpResponseDto otpResponseDto = employeeService.generateOtp(employeeDto.getRefCustId(), employeeDto.getEmpId());
        if (otpResponseDto !=null){
            return new ResponseEntity<OtpResponseDto>(otpResponseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	
	@PostMapping(value = "/updateEmpAppStatus")
    public ResponseEntity<StatusDto> updateEmpAppStatus(@RequestBody EmployeeDto employeeDto) {
		StatusDto statusDto = employeeService.updateEmpAppStatus(employeeDto);
        if (statusDto !=null){
            return new ResponseEntity<StatusDto>(statusDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	@PostMapping(value = "/getlateComersDetails")
    public ResponseEntity<LateComersResponse> getShiftAndLateComersDetails(@RequestBody AttendanceParamDto attendanceParamDto) {
		LateComersResponse lateComersResponse = employeeService.getLateComersDetails(attendanceParamDto.getDates(), attendanceParamDto.getCustId(),attendanceParamDto.getEmpId());
        if (lateComersResponse !=null){
            return new ResponseEntity<LateComersResponse>(lateComersResponse, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	@PostMapping(value = "/getEarlyLeaversDetails")
    public ResponseEntity<EarlyLeaversResponse> getEarlyLeavers(@RequestBody AttendanceParamDto attendanceParamDto) {
		EarlyLeaversResponse earlyLeaversResponse = employeeService.getEarlyLeaversDetails(attendanceParamDto.getDates(), attendanceParamDto.getCustId(),attendanceParamDto.getEmpId());
        if (earlyLeaversResponse !=null){
            return new ResponseEntity<EarlyLeaversResponse>(earlyLeaversResponse, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	
	@PostMapping(value = "/getAbsentEmployeeDetails")
    public ResponseEntity<AbsentDetailsResponse> getAbsentList(@RequestBody AttendanceParamDto attendanceParamDto) {
		AbsentDetailsResponse absentDetailsResponse = employeeService.getAbsentEmployeeDetails(attendanceParamDto.getCustId(),attendanceParamDto.getEmpId(),attendanceParamDto.getDates());
        if (absentDetailsResponse !=null){
            return new ResponseEntity<AbsentDetailsResponse>(absentDetailsResponse, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	
	@PostMapping(value = "/getAppLeaveEmployeeDetails")
    public ResponseEntity<AppLeaveResponse> getAppLeave(@RequestBody AttendanceParamDto attendanceParamDto) {
		AppLeaveResponse appLeaveResponse = employeeService.getAppLeaveDetails(attendanceParamDto.getCustId(),attendanceParamDto.getEmpId(),attendanceParamDto.getDates());
        if (appLeaveResponse !=null){
            return new ResponseEntity<AppLeaveResponse>(appLeaveResponse, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
