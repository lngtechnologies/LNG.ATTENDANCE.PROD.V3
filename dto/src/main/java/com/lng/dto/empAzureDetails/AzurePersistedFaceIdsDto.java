package com.lng.dto.empAzureDetails;

public class AzurePersistedFaceIdsDto {

	private String empName;
	
	private String mobileNo;
	
	private Boolean empInService;
	
	private String persistedFaceId;

	private String userData;
	
	public String getUserData() {
		return userData;
	}

	public void setUserData(String userData) {
		this.userData = userData;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Boolean getEmpInService() {
		return empInService;
	}

	public void setEmpInService(Boolean empInService) {
		this.empInService = empInService;
	}

	public String getPersistedFaceId() {
		return persistedFaceId;
	}

	public void setPersistedFaceId(String persistedFaceId) {
		this.persistedFaceId = persistedFaceId;
	}

}
