package com.lng.attendancecustomerservice.entity.masters;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="tmcontractor")
public class Contractor {
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="contractorId")
	private Integer contractorId;
	
	@Column(name="contractorName")
	private String   contractorName;
	
	@ManyToOne
	@JoinColumn(name = "refCustId")
	private Customer customer;
	
	@Column(name="contractorIsActive")
	private Boolean contractorIsActive;
	
	public Integer getContractorId() {
		return contractorId;
	}
	public void setContractorId(Integer contractorId) {
		this.contractorId = contractorId;
	}
	public String getContractorName() {
		return contractorName;
	}
	public void setContractorName(String contractorName) {
		this.contractorName = contractorName;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Boolean getContractorIsActive() {
		return contractorIsActive;
	}
	public void setContractorIsActive(Boolean contractorIsActive) {
		this.contractorIsActive = contractorIsActive;
	}

}
