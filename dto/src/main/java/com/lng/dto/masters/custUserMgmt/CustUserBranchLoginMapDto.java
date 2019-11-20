package com.lng.dto.masters.custUserMgmt;

import java.util.List;

public class CustUserBranchLoginMapDto {

	private Integer loginId;
	
	private List<CustUserBranchDto> branchIds;

	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

	public List<CustUserBranchDto> getBranchIds() {
		return branchIds;
	}

	public void setBranchIds(List<CustUserBranchDto> branchIds) {
		this.branchIds = branchIds;
	}

}
