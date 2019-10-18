package com.lng.attendancecustomerservice.repositories.masters;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.EmployeeDepartment;
@Repository
public interface EmployeeDepartmentRepository extends CrudRepository<EmployeeDepartment,Integer> {

}
