package com.lng.attendancecompanyservice.service.custOnboarding;

import com.lng.dto.customer.CustomerDto;
import com.lng.dto.customer.CustomerListResponse;
import com.lng.dto.customer.CustomerResponse;
import com.lng.dto.customer.StatusDto;

public interface CustomerService {
	
	//CustomerDto saveCustomer(CustomerDto customerDto);
	
	StatusDto saveCustomer(CustomerDto customerDto);
	
	CustomerListResponse findAll();
	
	CustomerResponse getCustomerByCustomerId(int custId);
	
	CustomerResponse updateCustomerByCustomerId(CustomerDto customerDto);
	
	CustomerResponse deleteCustomerByCustomerId(int custId);
	
	CustomerListResponse searchCustByNameOrCode(String cust);

}
