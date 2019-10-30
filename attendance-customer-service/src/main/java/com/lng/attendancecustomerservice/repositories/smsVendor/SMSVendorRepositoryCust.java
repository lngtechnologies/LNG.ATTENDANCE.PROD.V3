package com.lng.attendancecustomerservice.repositories.smsVendor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.smsVendor.SMSVendorCust;

@Repository
public interface SMSVendorRepositoryCust extends PagingAndSortingRepository<SMSVendorCust, Integer> {

	@Query(value = "CALL getAllSMSVendorIsActive()", nativeQuery = true)
	SMSVendorCust getAllBySmsVndrIsActive();
}
