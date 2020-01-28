package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Branch;
@Repository
public interface BranchRepository extends PagingAndSortingRepository<Branch,Integer> {

	Branch findBranchByBrId(Integer brId);
	
	Branch findBranchByBrIdAndBrIsActive(Integer brId, Boolean isActive);

	Branch findBranchByCustomer_custId(Integer custId);

	@Query(value = "call getAssignedBranchInLoginDataRight(?1, ?2);", nativeQuery = true)
	List<Branch> getAssignedDataRights(Integer loginId, Integer custId);

	@Query(value = "call getUnAssignedBranchInLoginDataRight(?1, ?2);", nativeQuery = true)
	List<Branch> getUnAssignedDataRights(Integer loginId, Integer custId);

	@Query(value = "call getBranchLoginDataRightByCustId(?1, ?2)", nativeQuery = true)
	List<Object[]> findByCustId(Integer custId, Integer loginId);

	@Query(value = "call getBranchLoginDataRight(?1)", nativeQuery = true)
	List<Object[]> findAllBranches(Integer loginId);

	List<Branch> getBranchByCustomer_custIdAndBrIsActive(Integer custId, Boolean brIsActive);
	
	@Query(value = "call getBranchDetailsByCustIdAndLoginId(?1,?2)", nativeQuery = true)
	List<Branch> getBranchByCustomer_custIdAndUser_loginId(Integer custId,Integer loginId);
	
	@Query(value = "SELECT brId, brName FROM tmbranch br LEFT JOIN tmcustomer c ON c.custId = br.refCustomerId WHERE br.refCustomerId = ?1 AND br.brIsActive = TRUE AND c.custIsActive = TRUE", nativeQuery = true)
	List<Object[]> getAllBranchesByCustId(Integer custId);
	
	@Query(value = "SELECT COUNT(*) AS cunt FROM tmbranch WHERE brValidityEnd > CURDATE() AND brId = ?1 AND brIsActive = TRUE", nativeQuery = true)
	int checkBranchValidity(Integer brId);
}
