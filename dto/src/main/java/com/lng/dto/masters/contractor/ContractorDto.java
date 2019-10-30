package com.lng.dto.masters.contractor;

public class ContractorDto {
	private Integer contractorId;
	private String contractorName;
	private Integer  refCustId;
	private String  custName;
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
	public Integer getRefCustId() {
		return refCustId;
	}
	public void setRefCustId(Integer refCustId) {
		this.refCustId = refCustId;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public Boolean getContractorIsActive() {
		return contractorIsActive;
	}
	public void setContractorIsActive(Boolean contractorIsActive) {
		this.contractorIsActive = contractorIsActive;
	}

}
