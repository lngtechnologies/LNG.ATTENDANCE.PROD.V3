package com.lng.attendancecustomerservice.entity.masters;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name="tmholidaycalendar")
public class HolidayCalendar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "holidayId")
	private Integer holidayId;


	@Column(name = "refCustId")
	private Integer refCustId;

	@Column(name = "holidayCalendarYear")
	private String  holidayCalendarYear;

	@Column(name = "holidayDate")
	private Date holidayDate;

	@Column(name = "holidayName")
	private String  holidayName;

	public Integer getHolidayId() {
		return holidayId;
	}

	public void setHolidayId(Integer holidayId) {
		this.holidayId = holidayId;
	}

	public Integer getRefCustId() {
		return refCustId;
	}

	public void setRefCustId(Integer refCustId) {
		this.refCustId = refCustId;
	}

	public String getHolidayCalendarYear() {
		return holidayCalendarYear;
	}

	public void setHolidayCalendarYear(String holidayCalendarYear) {
		this.holidayCalendarYear = holidayCalendarYear;
	}

	public Date getHolidayDate() {
		return holidayDate;
	}

	public void setHolidayDate(Date holidayDate) {
		this.holidayDate = holidayDate;
	}

	public String getHolidayName() {
		return holidayName;
	}

	public void setHolidayName(String holidayName) {
		this.holidayName = holidayName;
	}

}
