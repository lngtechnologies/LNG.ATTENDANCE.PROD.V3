package com.lng.attendancecompanyservice.entity.masters;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tmBlock")
public class Block {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "blkId")
	private Integer blkId;
	
	@ManyToOne
	@JoinColumn(name = "refBranchId")
	private Branch branch;
	
	@Column(name = "blkLogicalName")
	private String blkLogicalName;
	
	@Column(name = "blkGPSRadius")
	private Integer blkGPSRadius;
	
	@Column(name = "blkLatitude")
	private Double blkLatitude;
	
	@Column(name = "blkLongitude")
	private Double blkLongitude;
	
	@Column(name = "blkCreatedDate")
	private Date blkCreatedDate;
	
	@Column(name = "blkIsActive")
	private Boolean blkIsActive;

	public Integer getBlkId() {
		return blkId;
	}

	public void setBlkId(Integer blkId) {
		this.blkId = blkId;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
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
	public Double getBlkLatitude() {
		return blkLatitude;
	}

	public void setBlkLatitude(Double blkLatitude) {
		this.blkLatitude = blkLatitude;
	}

	public Double getBlkLongitude() {
		return blkLongitude;
	}

	public void setBlkLongitude(Double blkLongitude) {
		this.blkLongitude = blkLongitude;
	}

	public Date getBlkCreatedDate() {
		return blkCreatedDate;
	}

	public void setBlkCreatedDate(Date blkCreatedDate) {
		this.blkCreatedDate = blkCreatedDate;
	}

	public Boolean getBlkIsActive() {
		return blkIsActive;
	}

	public void setBlkIsActive(Boolean blkIsActive) {
		this.blkIsActive = blkIsActive;
	}

}
