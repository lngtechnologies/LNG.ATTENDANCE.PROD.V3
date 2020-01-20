package com.lng.attendancecompanyservice.entity.masters;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;

@Entity
@Table(name = "tmcustleave")
public class CustLeave {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "custLeaveId")
	private Integer custLeaveId;

	@ManyToOne
	@JoinColumn(name = "refCustId")
	private Customer customer;

	@Column(name = "custLeaveName")
	private String custLeaveName;
	
	@Column(name = "custLeaveIsActive")
	private Boolean custLeaveIsActive;
	

	public Boolean getCustLeaveIsActive() {
		return custLeaveIsActive;
	}

	public void setCustLeaveIsActive(Boolean custLeaveIsActive) {
		this.custLeaveIsActive = custLeaveIsActive;
	}

	public Integer getCustLeaveId() {
		return custLeaveId;
	}

	public void setCustLeaveId(Integer custLeaveId) {
		this.custLeaveId = custLeaveId;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getCustLeaveName() {
		return custLeaveName;
	}

	public void setCustLeaveName(String custLeaveName) {
		this.custLeaveName = custLeaveName;
	}

}
