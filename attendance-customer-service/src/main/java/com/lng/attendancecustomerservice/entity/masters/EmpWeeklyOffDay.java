package com.lng.attendancecustomerservice.entity.masters;

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
@Table(name = "ttempweeklyoffday")
public class EmpWeeklyOffDay {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "empWeeklyOffDayId")
	private Integer empWeeklyOffDayId;
	
	@ManyToOne
	@JoinColumn(name = "refEmpId")
	private Employee employee;
	
	@Column(name = "yearMonth")
	private Date yearMonth;
	
	@Column(name = "dayOfWeek")
	private String dayOfWeek;
	
	@Column(name = "fromDate")
	private Date fromDate;
	
	@Column(name = "toDate")
	private Date toDate;

	public Integer getEmpWeeklyOffDayId() {
		return empWeeklyOffDayId;
	}

	public void setEmpWeeklyOffDayId(Integer empWeeklyOffDayId) {
		this.empWeeklyOffDayId = empWeeklyOffDayId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Date getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(Date yearMonth) {
		this.yearMonth = yearMonth;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

}
