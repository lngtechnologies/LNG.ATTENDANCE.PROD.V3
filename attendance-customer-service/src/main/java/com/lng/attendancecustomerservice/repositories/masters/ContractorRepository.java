package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Contractor;
@Repository
public interface ContractorRepository extends CrudRepository<Contractor,Integer> {

	List<Contractor> findAllByContractorIsActive(Boolean contractorIsActive);
	@Query(value = "select * from tmcontractor where contractorName = ?1", nativeQuery = true)
	Contractor findByContractorName(String contractorName);
	Contractor findContractorByContractorId(Integer contractorId);

	Contractor   getEmployeeByContractorId(Integer contractorId);

	@Query(value = "call ContractorIdIsExistOrNot(?1)",nativeQuery = true)
	int  findEmployeeByContractorContractorId(int contractorId);

	@Query(value = "CALL CheckCustomerExistForContractor(?1, ?2)",nativeQuery = true)
	int  findByRefCustIdAndContractorName(Integer refCustId,String contractorName);


	Contractor findContractorBycontractorNameAndCustomer_custId(String contractorName, int custId);


}
