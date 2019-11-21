package com.lng.attendancecompanyservice.repositories.masters;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.masters.PolicyAndFaq;
import com.sun.mail.imap.protocol.ID;
@Repository
public interface PolicyAndFaqRepository extends PagingAndSortingRepository<PolicyAndFaq, ID> {
	

	List<PolicyAndFaq> findAll();

	//PolicyAndFaq updatePolicyAndFaqByPolicyAndFaq_FaqTextAndPolicyAndFaq_PolicyText(String faqText,String policyText );

}
