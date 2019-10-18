package com.lng.attendancecompanyservice.entity.masters;

import java.awt.Point;
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
	@JoinColumn(name = "branch")
	private Branch branch;
	
	@Column(name = "blkLogicalName")
	private String blkLogicalName;
	
	@Column(name = "blkGPSRadius")
	private Integer blkGPSRadius;
	
	@Column(name = "blkLatLong")
	private Point blkLatLong;
	
	@Column(name = "blkCreatedDate")
	private Date blkCreatedDate;

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

	public Point getBlkLatLong() {
		return blkLatLong;
	}

	public void setBlkLatLong(Point blkLatLong) {
		this.blkLatLong = blkLatLong;
	}

	public Date getBlkCreatedDate() {
		return blkCreatedDate;
	}

	public void setBlkCreatedDate(Date blkCreatedDate) {
		this.blkCreatedDate = blkCreatedDate;
	}

}
