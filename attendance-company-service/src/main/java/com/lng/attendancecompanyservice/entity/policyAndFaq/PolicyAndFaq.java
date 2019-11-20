package com.lng.attendancecompanyservice.entity.policyAndFaq;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tmstaticpage")
public class PolicyAndFaq {
	@Id
	
	@Column(name = "faqText")
	private String faqText;
	
	@Column(name = "policyText")
	private  String policyText;

	public String getFaqText() {
		return faqText;
	}

	public void setFaqText(String faqText) {
		this.faqText = faqText;
	}

	public String getPolicyText() {
		return policyText;
	}

	public void setPolicyText(String policyText) {
		this.policyText = policyText;
	}
	

}
