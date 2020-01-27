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
@Table(name = "ttcustomerconfig")
public class CustomerConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "custConfigId")
	private Integer custConfigId;
	
	@ManyToOne
	@JoinColumn(name = "refCustId")
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "refBrId")
	private Branch branch;

	@Column(name = "config")
	private String config;
	
	@Column(name = "stausFlag")
	private Boolean statusFlag;

	public Integer getCustConfigId() {
		return custConfigId;
	}

	public void setCustConfigId(Integer custConfigId) {
		this.custConfigId = custConfigId;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public Boolean getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(Boolean statusFlag) {
		this.statusFlag = statusFlag;
	}
}
