package com.lng.dto.masters.block;

import java.util.Date;

public class BlockDto {
	private Integer blkId;
	private Integer refBranchId;
	private String blkLogicalName;
	private Integer blkGPSRadius;
	private String blkLatLong;
	private Boolean blkIsActive;
	private Date blkCreatedDate;
	private Integer custId;

	private String brCode;
	private String brName;
	
	public Integer getBlkId() {
		return blkId;
	}
	public void setBlkId(Integer blkId) {
		this.blkId = blkId;
	}
	public Integer getRefBranchId() {
		return refBranchId;
	}
	public void setRefBranchId(Integer refBranchId) {
		this.refBranchId = refBranchId;
	}
   
	public String getBlkLogicalName() {
		return blkLogicalName;
	}
	public void setBlkLogicalName(String blkLogicalName) {
		this.blkLogicalName = blkLogicalName;
	}
	public Integer getBlkGPSRadius() {
		return blkGPSRadius;
	}
	public void setBlkGPSRadius(Integer blkGPSRadius) {
		this.blkGPSRadius = blkGPSRadius;
	}
	
    
	public String getBlkLatLong() {
		return blkLatLong;
	}
	public void setBlkLatLong(String blkLatLong) {
		this.blkLatLong = blkLatLong;
	}
	public Boolean getBlkIsActive() {
		return blkIsActive;
	}
	public void setBlkIsActive(Boolean blkIsActive) {
		this.blkIsActive = blkIsActive;
	}
	public Date getBlkCreatedDate() {
		return blkCreatedDate;
	}
	public void setBlkCreatedDate(Date blkCreatedDate) {
		this.blkCreatedDate = blkCreatedDate;
	}
	public Integer getCustId() {
		return custId;
	}
	public void setCustId(Integer custId) {
		this.custId = custId;
	}
	public String getBrCode() {
		return brCode;
	}
	public void setBrCode(String brCode) {
		this.brCode = brCode;
	}
	public String getBrName() {
		return brName;
	}
	public void setBrName(String brName) {
		this.brName = brName;
	}
	

}
