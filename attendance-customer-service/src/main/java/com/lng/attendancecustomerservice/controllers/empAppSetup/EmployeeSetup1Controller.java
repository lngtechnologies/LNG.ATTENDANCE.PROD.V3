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
import com.lng.dto.employee.EmpAppStatusResponseDto;
import com.lng.dto.employee.EmployeeDto;
import com.lng.dto.employee.OtpResponseDto;
import com.lng.dto.employee.ResponseDto;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/employee/setupone")
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
    public ResponseEntity<EmpAppStatusResponseDto> updateEmpAppStatus(@RequestBody EmployeeDto employeeDto) {
		EmpAppStatusResponseDto empAppStatusResponseDto = employeeService.updateEmpAppStatus(employeeDto.getRefCustId(), employeeDto.getEmpId());
        if (empAppStatusResponseDto !=null){
            return new ResponseEntity<EmpAppStatusResponseDto>(empAppStatusResponseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
