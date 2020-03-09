package com.lng.attendancecustomerservice.entity.notification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ttbranchnotificationmap")
public class BranchNotification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mapId")
	private Integer mapId;

	@Column(name = "refBrId")
	private Integer  refBrId;

	@Column(name = "refNotificationId")
	private Integer  refNotificationId;

	public Integer getMapId() {
		return mapId;
	}

	public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}

	public Integer getRefBrId() {
		return refBrId;
	}

	public void setRefBrId(Integer refBrId) {
		this.refBrId = refBrId;
	}

	public Integer getRefNotificationId() {
		return refNotificationId;
	}

	public void setRefNotificationId(Integer refNotificationId) {
		this.refNotificationId = refNotificationId;
	}

}
