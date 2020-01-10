package com.lng.attendancecompanyservice.controllers.custOnboarding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.custOnboarding.CustomerService;
import com.lng.dto.customer.CustomerDto;
import com.lng.dto.customer.CustomerListResponse;
import com.lng.dto.customer.CustomerResponse;
import com.lng.dto.customer.StatusDto;


/**
 * @author : Rahul.
 * @Created Date : 25-09-2019.
 * @Description : First Customer details has been inserted after that Branch details has been inserted
 * 				 after that Login Details has been inserted according to Customer details. 
 *
 */
@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/customer/onboarding")
public class CustomerController {
	
	@Autowired
	CustomerService customerService;
	

	@PostMapping(value = "/create")
    public ResponseEntity<StatusDto> save(@RequestBody CustomerDto customerDto) {
		StatusDto statusDto = customerService.saveCustomer(customerDto);
        if (customerDto !=null){
            return new ResponseEntity<StatusDto>(statusDto, HttpStatus.CREATED);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
	
	@GetMapping(value = "/findAll")
	public ResponseEntity<CustomerListResponse> findAll() {
		CustomerListResponse customerListResponse = customerService.findAll(); 
       if(customerListResponse.getDataList().isEmpty()) {
           return new ResponseEntity(HttpStatus.NO_CONTENT);
       }
       return new ResponseEntity<CustomerListResponse>(customerListResponse, HttpStatus.OK);
   }
	
	@PostMapping(value = "/findByCustomerId")
    public ResponseEntity<CustomerListResponse> findByCustomerId(@RequestBody CustomerDto customerDto) {
		CustomerListResponse customerResponse = customerService.getCustomerByCustomerId(customerDto.getCustId());
        if (customerResponse !=null){
            return new ResponseEntity<CustomerListResponse>(customerResponse, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	
	@PostMapping(value = "/updateCustomer")
	  public ResponseEntity<CustomerResponse> updateCustomer(@RequestBody CustomerDto customerDto) {
		CustomerResponse customerResponse = customerService.updateCustomerByCustomerId(customerDto);
	    if (customerResponse != null) {
	      return new ResponseEntity<CustomerResponse>(customerResponse, HttpStatus.OK);
	    }
	    return new ResponseEntity(HttpStatus.NO_CONTENT);
	  }
	
	@PostMapping(value = "/deleteCustomer")
	  public ResponseEntity<CustomerResponse> deleteCustomer(@RequestBody CustomerDto customerDto) {
		CustomerResponse customerResponse = customerService.deleteCustomerByCustomerId(customerDto.getCustId());
	    if (customerResponse != null) {
	      return new ResponseEntity<CustomerResponse>(customerResponse, HttpStatus.OK);
	    }
	    return new ResponseEntity(HttpStatus.NO_CONTENT);
	  }
	
	@PostMapping(value = "/searchCustByNameOrCode")
	public ResponseEntity<CustomerListResponse> searchByCustNameOrCode(@RequestBody String cust) {
		CustomerListResponse customerListResponse = customerService.searchCustByNameOrCode(cust); 
       if(customerListResponse.getDataList().isEmpty()) {
           return new ResponseEntity(HttpStatus.NO_CONTENT);
       }
       return new ResponseEntity<CustomerListResponse>(customerListResponse, HttpStatus.OK);
   }
	
	@GetMapping(value="/test")
	public String get() {
		return "Working";
	}

}
