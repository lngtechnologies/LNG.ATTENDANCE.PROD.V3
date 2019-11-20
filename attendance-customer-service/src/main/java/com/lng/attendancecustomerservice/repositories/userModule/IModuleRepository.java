package com.lng.attendancecustomerservice.repositories.userModule;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.userModule.Module;


@Repository
public interface IModuleRepository extends JpaRepository<Module, Integer> {

	@Query(value = "call Sp_getUserModuleMap(?1)", nativeQuery = true)
	List<Module> getUserModuleMap(int loginId);
	
	@Query(value = "call Sp_getUserSubModuleMap(?1)", nativeQuery = true)
	List<Module> getUserSubModuleMap(int loginId);
	
	@Query(value = "call getAssignedModulesInUserRight(?1, ?2)", nativeQuery = true)
	List<Module> getAssignedModuleByLogin_LoginIdAndCustomer_CustId(Integer LoginId, Integer custId);
	
	@Query(value = "call getUnAssignedModulesInUserRight(?1)", nativeQuery = true)
	List<Module> getUnAssignedModuleByLogin_LoginId(Integer LoginId);
	
	Module findByModuleId(Integer moduleId);
	
	@Query(value = "call getModulesByCustId(?1,?2)", nativeQuery = true)
	List<Object[]> findByCustomer_CustIdAndLogin_LoginId(Integer custId, Integer loginId);
	
	@Query(value = "call getModules(?1)", nativeQuery = true)
	List<Object[]> findAllModules(Integer loginId);
	
	//List<Module> getAllByModuleId();

}
