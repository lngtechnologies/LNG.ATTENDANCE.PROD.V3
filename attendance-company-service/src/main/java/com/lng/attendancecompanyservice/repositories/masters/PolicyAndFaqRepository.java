package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lng.attendancecompanyservice.entity.masters.PolicyAndFaq;

@Repository
public interface PolicyAndFaqRepository extends PagingAndSortingRepository<PolicyAndFaq, Integer> {

	List<PolicyAndFaq>  findAllByKey(String key);
	List<PolicyAndFaq> findAll();
	PolicyAndFaq   findPolicyAndFaqBykey(String key);
	PolicyAndFaq   findPolicyAndFaqByValue(String value);
	PolicyAndFaq   findPolicyAndFaqByPageId(Integer pageId);

	@Transactional
	@Modifying 
	@Query(value = "Update tmstaticpage s SET  s.Value =?1 Where s.pageId= ?2", nativeQuery = true) 
	void updatePolicyAndFaqByValueAndPageId(String value,Integer pageId);


	//PolicyAndFaq   findPolicyAndFaqByKey(String key);
}
