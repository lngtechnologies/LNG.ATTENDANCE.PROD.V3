package com.lng.attendancecustomerservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.masters.CustEmployeeService;
import com.lng.dto.employee.EmployeeDto2;
import com.lng.dto.masters.custEmployee.CustEmployeeDto;
import com.lng.dto.masters.custEmployee.CustEmployeeDtoTwo;
import com.lng.dto.masters.custEmployee.CustEmployeeListResponse;
import com.lng.dto.masters.custEmployee.CustEmployeeStatus;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/employee/")
public class CustEmplyeeController {
	
	@Autowired
	CustEmployeeService custEmployeeService;

	@PostMapping(value = "/create")
    public ResponseEntity<CustEmployeeStatus> saveEmployee(@RequestBody CustEmployeeDto custEmployeeDto) {
		CustEmployeeStatus custEmployeeStatus = custEmployeeService.save(custEmployeeDto);
        if (custEmployeeDto !=null){
            return new ResponseEntity<CustEmployeeStatus>(custEmployeeStatus, HttpStatus.CREATED);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
	
	@GetMapping(value = "/findAll")
	public ResponseEntity<CustEmployeeListResponse> findAll() {
		CustEmployeeListResponse custEmployeeListResponse = custEmployeeService.findAll(); 
       return new ResponseEntity<CustEmployeeListResponse>(custEmployeeListResponse, HttpStatus.OK);
   }
	
	@PostMapping(value = "/findAllByCustomerId")
    public ResponseEntity<CustEmployeeListResponse> findByCustomerId(@RequestBody CustEmployeeDtoTwo custEmployeeDtoTwo) {
		CustEmployeeListResponse custEmployeeListResponse = custEmployeeService.findEmployeeByCustId(custEmployeeDtoTwo.getCustId());
        if (custEmployeeListResponse !=null){
            return new ResponseEntity<CustEmployeeListResponse>(custEmployeeListResponse, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
		
	@PostMapping(value = "/findByEmployeeId")
    public ResponseEntity<CustEmployeeListResponse> findByEmployeeId(@RequestBody CustEmployeeDtoTwo custEmployeeDtoTwo) {
		CustEmployeeListResponse custEmployeeListResponse = custEmployeeService.findEmployeeByEmpId(custEmployeeDtoTwo.getEmpId());
        if (custEmployeeListResponse !=null){
            return new ResponseEntity<CustEmployeeListResponse>(custEmployeeListResponse, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	
	
	
	@PostMapping(value = "/updateEmployee")
	  public ResponseEntity<CustEmployeeStatus> updateEmployee(@RequestBody CustEmployeeDto custEmployeeDto) {
		CustEmployeeStatus custEmployeeStatus = custEmployeeService.updateEmployee(custEmployeeDto);
	    if (custEmployeeStatus != null) {
	      return new ResponseEntity<CustEmployeeStatus>(custEmployeeStatus, HttpStatus.OK);
	    }
	    return new ResponseEntity(HttpStatus.NO_CONTENT);
	  }
	
	@PostMapping(value = "/deleteEmployee")
	  public ResponseEntity<CustEmployeeStatus> deleteEmployee(@RequestBody CustEmployeeDto custEmployeeDto) {
		CustEmployeeStatus custEmployeeStatus = custEmployeeService.deleteEmployeeByEmpIdId(custEmployeeDto.getEmpId());
	    if (custEmployeeStatus != null) {
	      return new ResponseEntity<CustEmployeeStatus>(custEmployeeStatus, HttpStatus.OK);
	    }
	    return new ResponseEntity(HttpStatus.NO_CONTENT);
	  }
	
	@PostMapping(value = "/searchEmployeeByEmpName")
	public ResponseEntity<CustEmployeeListResponse> searchEmployee(@RequestBody String empName) {
		CustEmployeeListResponse custEmployeeListResponse = custEmployeeService.searchEmployeeByEmpName(empName);
       if(custEmployeeListResponse.getEmployyeList().isEmpty()) {
           return new ResponseEntity(HttpStatus.NO_CONTENT);
       }
       return new ResponseEntity<CustEmployeeListResponse>(custEmployeeListResponse, HttpStatus.OK);
   }
	
	@PostMapping(value = "/findByLoginId")
    public ResponseEntity<CustEmployeeListResponse> findByLoginId(@RequestBody EmployeeDto2 employeeDto) {
		CustEmployeeListResponse custEmployeeListResponse = custEmployeeService.FindEmployeeByRefLoginId(employeeDto.getRefLoginId());
        if (custEmployeeListResponse !=null){
            return new ResponseEntity<CustEmployeeListResponse>(custEmployeeListResponse, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
