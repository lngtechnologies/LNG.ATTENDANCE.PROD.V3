package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.EmployeeBlock;
@Repository
public interface EmployeeBlockRepository extends CrudRepository<EmployeeBlock,Integer>{
	

	EmployeeBlock findByEmpBlkId(Integer empBlkId);
	
	List<EmployeeBlock> findEmployeeBlockByBlockBlkId(int blkId);


}
