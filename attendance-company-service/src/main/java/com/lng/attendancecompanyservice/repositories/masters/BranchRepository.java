package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecompanyservice.entity.masters.Branch;

public interface BranchRepository extends PagingAndSortingRepository<Branch, Integer> {

	@Query(value = "CALL generateBranchCodeForCustomer(?1)", nativeQuery = true)
	String generateBranchForCustomer(Integer custId);
	
	@Query(value = "SELECT * FROM tmbranch where brIsActive = true ORDER BY brName ASC", nativeQuery = true)
	List<Branch> findAll();
	
	Branch findBranchByBrId(Integer brId);

	@Query(value = "CALL CheckBranchIsExistForCustomer(?1, ?2)",nativeQuery = true)
	int findByRefCustomerIdAndBrName(Integer refCustomerId,String brName);

	Branch findBranchBybrNameAndCustomer_custId(String brName, int custId);
	
	List<Branch> findByState_StateId(Integer stateId);
	
	@Query(value = "SELECT br.* FROM tmbranch br where br.brIsActive = true and br.refCustomerId =?1 ORDER BY br.brName ASC", nativeQuery = true)
	List<Branch> findAllByCustomer_CustId(Integer custId);
	
	@Query(value = "SELECT br.* FROM tmbranch br where br.brIsActive = true and br.refCustomerId =?1 and br.brValidityEnd >= curdate() ORDER BY br.brName ASC", nativeQuery = true)
	List<Branch> findAllBranchesByCustomer_CustId(Integer custId);
	
	@Query(value = "CALL chechNoOfBranchesCreatedByCustomer(?1);",nativeQuery = true)
	int chechNoOfBranchesCreatedByCustomer(int custId);
	
	Branch findBranchBybrNameAndBrIsActive(String brName, Boolean brIsActive);
	
	@Query(value = "select  br.brId,br.brName from  tmbranch br where br.refCustomerId = ?1",nativeQuery = true)
	List<Object[]>  findBranchByRefCustomerId(Integer refCustomerId);
	
	@Query(value = "call checkBranchValidityByLoginId(?1)", nativeQuery = true)
	int checkBranchValidity(Integer brId);
	
	Branch getBranchByBrCode(String brCode);
}
