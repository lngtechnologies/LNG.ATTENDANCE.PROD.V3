package com.lng.attendancecustomerservice.repositories.authentication;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.authentication.Login;

@Repository
public interface ILoginRepository extends JpaRepository<Login, Integer> {
	Login findByLoginName(String loginName);

	@Query(value = "call generatePassword", nativeQuery = true)
	String generatePassword();

	List<Login> findByRefCustId(Integer custId);

	Login findByLoginNameAndRefCustIdAndLoginIsActive(String loginName, Integer custId, Boolean loginIsActive);

	Login findByLoginMobileAndRefCustId(String mobileNumber, Integer custId);

	Login getByRefCustId(Integer custId);
	
	@Query(value = "select * from ttlogin where refCustId = ?1 and loginName like '%admin%'", nativeQuery = true)
	Login findByCustomet_CustId(Integer custId);

	List<Login> findAll();

	Login findByLoginId(Integer loginId);

	@Query(value = "call getLoginDetailsByCustId(?1)", nativeQuery = true)
	List<Object[]> findByCustId(Integer custId);

	@Query(value = "call getLoginDetails()", nativeQuery = true)
	List<Object[]> findAllDetails();

	@Query(value = "call getLoginDetailsByLoginId(?1)", nativeQuery = true)
	List<Object[]> findLogindDetailsByLoginId(Integer loginId);

	@Query(value = "SELECT * FROM ttlogin WHERE refEmpId =?1 AND loginIsActive = TRUE", nativeQuery = true)
	List<Login> findAllByLoginIsActiveAndRefEmpId(Integer empId);

	@Query(value = "call getUserDetailsByCustId(?1)", nativeQuery = true)
	List<Object[]> findAllUsersByCustId(Integer custId);

	Login findByRefEmpIdAndLoginIsActive(Integer empId,Boolean loginIsActive);
}
