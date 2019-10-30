package com.lng.attendancecompanyservice.repositories.smsvendor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.smsVendor.SMSVendor;

@Repository
public interface SMSVendorRepository extends PagingAndSortingRepository<SMSVendor, Integer> {

	@Query(value = "CALL getAllSMSVendorIsActive()", nativeQuery = true)
	SMSVendor getAllBySmsVndrIsActive();
}
