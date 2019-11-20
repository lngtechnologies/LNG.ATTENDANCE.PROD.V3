package com.lng.dto.masters.custEmployee;

public class EmpBlockMapDto {
	
	private Integer empBlkId;
	
	private Integer refEmpId;
	
	private String blkLogicalName;
	
	private Integer blkId;

	public Integer getRefEmpId() {
		return refEmpId;
	}

	public void setRefEmpId(Integer refEmpId) {
		this.refEmpId = refEmpId;
	}


	public Integer getBlkId() {
		return blkId;
	}

	public void setBlkId(Integer blkId) {
		this.blkId = blkId;
	}

	public Integer getEmpBlkId() {
		return empBlkId;
	}

	public void setEmpBlkId(Integer empBlkId) {
		this.empBlkId = empBlkId;
	}

	public String getBlkLogicalName() {
		return blkLogicalName;
	}

	public void setBlkLogicalName(String blkLogicalName) {
		this.blkLogicalName = blkLogicalName;
	}

	

}
