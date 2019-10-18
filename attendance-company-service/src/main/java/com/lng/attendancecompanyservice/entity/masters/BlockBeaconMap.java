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
@Table(name = "tmBlockBeaconMap")
public class BlockBeaconMap {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "")
	private Integer blkBeaconMapId;

	@ManyToOne
	@JoinColumn(name = "refBlkId")
	private Block block;

	@Column(name = "beaconCode")
	private String beaconCode;

	@Column(name = "blkBeaconMapIsActive")
	private Boolean blkBeaconMapIsActive;

	@Column(name = "blkBeaconMapCreatedDate")
	private Date blkBeaconMapCreatedDate;

	public Integer getBlkBeaconMapId() {
		return blkBeaconMapId;
	}

	public void setBlkBeaconMapId(Integer blkBeaconMapId) {
		this.blkBeaconMapId = blkBeaconMapId;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public String getBeaconCode() {
		return beaconCode;
	}

	public void setBeaconCode(String beaconCode) {
		this.beaconCode = beaconCode;
	}

	public Boolean getBlkBeaconMapIsActive() {
		return blkBeaconMapIsActive;
	}

	public void setBlkBeaconMapIsActive(Boolean blkBeaconMapIsActive) {
		this.blkBeaconMapIsActive = blkBeaconMapIsActive;
	}

	public Date getBlkBeaconMapCreatedDate() {
		return blkBeaconMapCreatedDate;
	}

	public void setBlkBeaconMapCreatedDate(Date blkBeaconMapCreatedDate) {
		this.blkBeaconMapCreatedDate = blkBeaconMapCreatedDate;
	}

}
