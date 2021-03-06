package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.State;
@Repository
public interface StateRepository extends CrudRepository<State,Integer> {	


	State findByStateId(Integer stateId);

	List<State> findStateByCountryCountryId(int countryId);

	@Query(value = "SELECT * FROM tmstate WHERE stateIsActive = TRUE  ORDER BY stateName ASC", nativeQuery = true)
	List<State> findAllByStateIsActive();
	@Query(value = "select * from tmstate where stateName = ?1", nativeQuery = true)
	State findByStateName(String stateName);
	State findStateByStateId(Integer stateId);
	//State findCountryByCountryId(Integer countryId);
	@Query(value = "SELECT stateId,stateName,stateIsActive FROM   tmstate WHERE  refCountryId=?1 and stateIsActive=TRUE",nativeQuery = true)
	List<Object[]> findStateDetailsByCountry_RefCountryId(int refCountryId);

	@Query(value = "CALL CheckStateIdIsExistOrNot(?1)",nativeQuery = true)
	int  findBranchByStateStateId(int stateId);

	State findStateBystateNameAndCountry_countryId(String stateName,int countryId);

	@Query(value = "CALL CheckStateExistsForCountry(?1, ?2);",nativeQuery = true)
	int  findByRefCountryIdAndStateName(Integer refCountryId,String stateName);
	
	State findByCountry_CountryIdAndStateNameAndStateIsActive(Integer refCountryId,String stateName, Boolean stateIsActive);
	
	@Query(value = "SELECT * FROM tmstate ORDER BY stateName ASC", nativeQuery = true)
	List<State> findAll();
	


}
