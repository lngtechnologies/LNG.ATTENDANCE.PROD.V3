package com.lng.dto.masters.designation;

public class DesignationDto {
	private Integer designationId;
	private Integer refCustId;
	private String designationName;
	private String custName;
	private Boolean desigIsActive;


	public Integer getDesignationId() {
		return designationId;
	}
	public void setDesignationId(Integer designationId) {
		this.designationId = designationId;
	}
	public Integer getRefCustId() {
		return refCustId;
	}
	public void setRefCustId(Integer refCustId) {
		this.refCustId = refCustId;
	}
	public String getDesignationName() {
		return designationName;
	}
	public void setDesignationName(String designationName) {
		this.designationName = designationName;

	}
	public Boolean getDesigIsActive() {
		return desigIsActive;
	}
	public void setDesigIsActive(Boolean desigIsActive) {
		this.desigIsActive = desigIsActive;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}

}
