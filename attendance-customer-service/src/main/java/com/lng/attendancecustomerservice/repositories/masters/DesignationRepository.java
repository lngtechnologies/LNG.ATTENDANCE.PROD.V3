package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Designation;
@Repository
public interface DesignationRepository extends CrudRepository<Designation,Integer> {
	List<Designation> findAll();
	Designation findDesignationByDesignationId(Integer designationId);
	@Query(value = "select * from tmdesignation where designationName = ?1", nativeQuery = true)
	Designation findByDesignationName(String designationName);

	Designation   getDesignationBydesignationId(Integer designationId);
	
	 @Query(value = "CALL DesignationIdIsExistOrNot(?1)",nativeQuery = true)
	 
	 int  findEmployeeDesignationByDesignationDesignationId(int designationId);
	 
		@Query(value = "CALL  CheckCustomerExistForDesignation(?1, ?2);",nativeQuery = true)
		int  findByRefCustIdAndDesignationName(Integer refCustId,String designationName);
		
		Designation findDesignationBydesignationNameAndCustomer_custId(String designationName, int custId);

}
