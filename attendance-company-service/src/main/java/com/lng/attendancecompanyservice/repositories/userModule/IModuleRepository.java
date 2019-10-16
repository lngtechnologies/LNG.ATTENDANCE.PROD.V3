package com.lng.attendancecompanyservice.repositories.userModule;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.userModule.Module;


@Repository
public interface IModuleRepository extends JpaRepository<Module, Integer> {

	@Query(value = "call Sp_getUserModuleMap(?1)", nativeQuery = true)
	List<Module> getUserModuleMap(int loginId);
	
	@Query(value = "call Sp_getUserSubModuleMap(?1)", nativeQuery = true)
	List<Module> getUserSubModuleMap(int loginId);

}
