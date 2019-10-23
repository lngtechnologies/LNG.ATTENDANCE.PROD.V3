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
@Table(name = "ttempmonthlynoofday")
public class MonthlyNoOfDays {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "empMonthlyNoOfDayId")
	private Integer empMonthlyNoOfDayId;
	
	@ManyToOne
	@JoinColumn(name = "refEmpId")
	private Employee employee;
	
	@Column(name = "yearMonth")
	private Date yearMonth;
	
	@Column(name = "noOfDays")
	private Integer noOfDays;

	public Integer getEmpMonthlyNoOfDayId() {
		return empMonthlyNoOfDayId;
	}

	public void setEmpMonthlyNoOfDayId(Integer empMonthlyNoOfDayId) {
		this.empMonthlyNoOfDayId = empMonthlyNoOfDayId;
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

	public Integer getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(Integer noOfDays) {
		this.noOfDays = noOfDays;
	}

}
