package com.lng.attendancecustomerservice.entity.empMovement;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.lng.attendancecustomerservice.entity.masters.Employee;
@Entity
@Table(name = "ttEmpMovement")
public class EmpMovement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "empMovementId")
	private Integer empMovementId;

	@ManyToOne
	@JoinColumn(name = "refEmpId")
	private Employee employee;

	@Column(name = "empMovementType")
	private String  empMovementType;

	@Column(name = "empMovementDate")
	private Date  empMovementDate;

	@Column(name = "empMovementMode")
	private  String empMovementMode;

	@Column(name = "empMovementDatetime")
	private  Date empMovementDatetime;

	@Column(name = "empMovementLatLong")
	private String  empMovementLatLong;
	
	@Column(name = "empMovementLocation")
	private String   empMovementLocation;
	
	@Column(name = "empPlaceOfVisit")
	private String   empPlaceOfVisit;

	public Integer getEmpMovementId() {
		return empMovementId;
	}

	public void setEmpMovementId(Integer empMovementId) {
		this.empMovementId = empMovementId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getEmpMovementType() {
		return empMovementType;
	}

	public void setEmpMovementType(String empMovementType) {
		this.empMovementType = empMovementType;
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
