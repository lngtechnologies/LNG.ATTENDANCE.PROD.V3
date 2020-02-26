package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Location;

@Repository
public interface LocationRepository extends PagingAndSortingRepository<Location, Integer> {
	
	Location findLocationByLocationId(Integer locationId);
	
	Location findLocationByCustomer_CustIdAndLocation(Integer refCustId,String location);
	
	
	@Query(value = "SELECT tl.* FROM tmlocation tl WHERE tl.refCustId =?1  ORDER BY tl.location ASC",nativeQuery = true)
	List<Location> findAllByCustomer_CustId(Integer refCustId);

}
