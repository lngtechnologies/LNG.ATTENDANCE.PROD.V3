package com.lng.attendancecompanyservice.entity.exception;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tt_application_exception")
public class ApplicationException {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "exceptionId")
	private Integer exceptionId;
	
	@Column(name = "refCustId")
	private Integer refCustId;
	
	@Column(name = "moduleName")
	private String moduleName;
	
	@Column(name = "exMessage")
	private String exMessage;
	
	@Column(name = "stackTrace")
	private String stackTrace;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "dateTime")
	private Date dateTime;

	public Integer getExceptionId() {
		return exceptionId;
	}

	public void setExceptionId(Integer exceptionId) {
		this.exceptionId = exceptionId;
	}

	public Integer getRefCustId() {
		return refCustId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public void setRefCustId(Integer refCustId) {
		this.refCustId = refCustId;
	}

	public String getExMessage() {
		return exMessage;
	}

	public void setExMessage(String exMessage) {
		this.exMessage = exMessage;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

}
