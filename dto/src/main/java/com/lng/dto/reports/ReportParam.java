package com.lng.dto.reports;

public class ReportParam {
	private Integer custId;
	private Integer deptId;
	private Integer brId;
	private Integer blkId;
	private String fromDate;
	private String toDate;

	public Integer getCustId() {
		return custId;
	}
	public void setCustId(Integer custId) {
		this.custId = custId;
	}
	public Integer getDeptId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public Integer getBrId() {
		return brId;
	}
	public void setBrId(Integer brId) {
		this.brId = brId;
	}
	public Integer getBlkId() {
		return blkId;
	}
	public void setBlkId(Integer blkId) {
		this.blkId = blkId;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
}
