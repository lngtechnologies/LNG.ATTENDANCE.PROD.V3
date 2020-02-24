package com.lng.attendancecustomerservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.LoginDataRight;
import com.lng.attendancecustomerservice.entity.masters.UserRight;

@Repository
public interface LoginDataRightRepository extends PagingAndSortingRepository<LoginDataRight, Integer> {

	List<LoginDataRight> getByRefLoginId(Integer loginId);

	LoginDataRight findByLoginDataRightId(Integer loginDataRight);

	LoginDataRight findByRefLoginId(Integer loginId);

	@Query(value = "call getBranchLoginDataRightByCustId(?1)", nativeQuery = true)
	List<UserRight> findByCustId(Integer custId);

	@Query(value = "CALL GetEmployeeListByEmpIdAndLoginId(?1,?2)", nativeQuery = true)
	List<Object[]> findEmployeeListByEmployee_EmpIdAndCustomer_CustId(Integer empId,Integer custId);

	@Query(value = " SELECT empId ,empName FROM tmemployee WHERE  empInService = TRUE AND  refCustId = ?1 order by empName", nativeQuery = true)
	List<Object[]> findEmpListByCustId(Integer custId);

}
