package com.lng.dto.masters.custEmployee;

public class EmpBlockMapDto {
	
	private Integer empBlkId;
	
	private Integer refEmpId;
	
	private String blockName;
	
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

	public String getBlockName() {
		return blockName;
	}

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

}
