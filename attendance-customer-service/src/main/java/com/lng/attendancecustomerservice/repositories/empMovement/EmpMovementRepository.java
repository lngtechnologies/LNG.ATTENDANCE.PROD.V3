package com.lng.attendancecustomerservice.repositories.empMovement;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.empMovement.EmpMovement;
@Repository
public interface EmpMovementRepository extends PagingAndSortingRepository<EmpMovement, Integer> {
	
	List<EmpMovement>  findEmpMovementByEmployee_EmpIdAndEmpMovementDate(Integer refEmpId,Date empMovementDate);
	
	@Query(value = "SELECT empPlaceOfVisit FROM  ttempmovement WHERE   refEmpId = ?1 GROUP BY  empPlaceOfVisit",nativeQuery = true)
	List<Object[]>  getAllEmpPlaceVisitListByrefEmpId(Integer empId);

}
