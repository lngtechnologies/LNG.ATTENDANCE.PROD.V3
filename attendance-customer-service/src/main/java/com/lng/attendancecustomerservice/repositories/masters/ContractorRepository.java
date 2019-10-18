package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Contractor;
@Repository
public interface ContractorRepository extends PagingAndSortingRepository<Contractor,Integer> {

	List<Contractor> findAll();
	@Query(value = "select * from tmcontractor where contractorName = ?1", nativeQuery = true)
	Contractor findByContractorName(String contractorName);
	Contractor findContractorByContractorId(Integer contractorId);

	Contractor   getEmployeeByContractorId(Integer contractorId);
	
	 @Query(value = "call ContractorIdIsExistOrNot(?1)",nativeQuery = true)
	 int  findEmployeeByContractorContractorId(int contractorId);

}
