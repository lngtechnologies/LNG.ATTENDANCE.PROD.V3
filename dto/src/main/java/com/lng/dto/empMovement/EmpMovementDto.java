package com.lng.dto.empMovement;

import java.util.Date;

public class EmpMovementDto {

	private Integer empMovementId;

	private  Integer refEmpId;

	private Date  empMovementDate;

	private  String empMovementMode;

	private String  empMovementType;

	private  Date empMovementDatetime;

	private String  empMovementLatLong;

	private String   empMovementLocation;

	private String   empPlaceOfVisit;

	private String empName;

	public Integer getEmpMovementId() {
		return empMovementId;
	}

	public void setEmpMovementId(Integer empMovementId) {
		this.empMovementId = empMovementId;
	}

	public Integer getRefEmpId() {
		return refEmpId;
	}

	public void setRefEmpId(Integer refEmpId) {
		this.refEmpId = refEmpId;
	}

	public Date getEmpMovementDate() {
		return empMovementDate;
	}

	public void setEmpMovementDate(Date empMovementDate) {
		this.empMovementDate = empMovementDate;
	}

	public String getEmpMovementMode() {
		return empMovementMode;
	}

	public void setEmpMovementMode(String empMovementMode) {
		this.empMovementMode = empMovementMode;
	}

	public String getEmpMovementType() {
		return empMovementType;
	}

	public void setEmpMovementType(String empMovementType) {
		this.empMovementType = empMovementType;
	}

	public Date getEmpMovementDatetime() {
		return empMovementDatetime;
	}

	public void setEmpMovementDatetime(Date empMovementDatetime) {
		this.empMovementDatetime = empMovementDatetime;
	}

	public String getEmpMovementLatLong() {
		return empMovementLatLong;
	}

	public void setEmpMovementLatLong(String empMovementLatLong) {
		this.empMovementLatLong = empMovementLatLong;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEmpMovementLocation() {
		return empMovementLocation;
	}

	public void setEmpMovementLocation(String empMovementLocation) {
		this.empMovementLocation = empMovementLocation;
	}

	public String getEmpPlaceOfVisit() {
		return empPlaceOfVisit;
	}

	public void setEmpPlaceOfVisit(String empPlaceOfVisit) {
		this.empPlaceOfVisit = empPlaceOfVisit;
	}
}
