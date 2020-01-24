package com.lng.dto.employeeAppSetup;

import status.Status;

public class CustomerValidityDto {

	private Boolean isValidCustomer;
	
	public Status status;

	public Boolean getIsValidCustomer() {
		return isValidCustomer;
	}

	public void setIsValidCustomer(Boolean isValidCustomer) {
		this.isValidCustomer = isValidCustomer;
	}
}
