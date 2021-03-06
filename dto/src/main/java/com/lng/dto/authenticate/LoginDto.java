package com.lng.dto.authenticate;


/**
 * @author Sachin Kulkarni
 * @created 27-sept-2019
 * @description Login response dto
 */
public class LoginDto {

	private Integer loginId;
	private Integer refCustId;
	private Integer refEmpId;
	private String loginName;
	private String token;
	private String custLogo;
	
	
	public LoginDto(Integer id, Integer custId, String name, String token, String logo, Integer refEmpId) {
		this.loginId = id;
		this.refCustId = custId;
		this.refEmpId = refEmpId;
		this.loginName = name;
		this.token = token;
		this.custLogo = logo;
		
	}
	
	public Integer getRefCustId() {
		return refCustId;
	}

	public void setRefCustId(Integer refCustId) {
		this.refCustId = refCustId;
	}
	public String getCustLogo() {
		return custLogo;
	}

	public void setCustLogo(String custLogo) {
		this.custLogo = custLogo;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

	public Integer getRefEmpId() {
		return refEmpId;
	}

	public void setRefEmpId(Integer refEmpId) {
		this.refEmpId = refEmpId;
	}

}
