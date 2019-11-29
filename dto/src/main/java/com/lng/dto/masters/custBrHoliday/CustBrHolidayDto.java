package com.lng.dto.masters.custBrHoliday;

public class CustBrHolidayDto {
	
	private  Integer custBrHolidayId;

	private Integer  refbrId;

	private  Integer holidayId;
	
	private Integer  refCustId;
	
	private String  holidayName;
	
	private String  brName;

	public Integer getCustBrHolidayId() {
		return custBrHolidayId;
	}

	public void setCustBrHolidayId(Integer custBrHolidayId) {
		this.custBrHolidayId = custBrHolidayId;
	}

	public Integer getRefbrId() {
		return refbrId;
	}

	public void setRefbrId(Integer refbrId) {
		this.refbrId = refbrId;
	}


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

	public String getHolidayName() {
		return holidayName;
	}

	public void setHolidayName(String holidayName) {
		this.holidayName = holidayName;
	}

	public String getBrName() {
		return brName;
	}

	public void setBrName(String brName) {
		this.brName = brName;
	}
	
	

}
