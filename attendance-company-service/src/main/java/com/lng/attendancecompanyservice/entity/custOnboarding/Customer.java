package com.lng.attendancecompanyservice.entity.custOnboarding;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.lng.attendancecompanyservice.entity.masters.Country;
import com.lng.attendancecompanyservice.entity.masters.IndustryType;
import com.lng.attendancecompanyservice.entity.masters.State;

@Entity
@Table(name = "tmcustomer")
public class Customer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "custId")
	private Integer custId;

	@ManyToOne
    @JoinColumn(name = "refCountryId")
	private Country country;

	@ManyToOne
    @JoinColumn(name = "refStateId")
	private State state;

	@ManyToOne
    @JoinColumn(name = "refIndustryTypeId")
	private IndustryType industryType;

	@Column(name = "custName")
	@Size(max = 50)
	@NotNull(message = "This field should not be an empty")
	private String custName;

	
	@Column(name = "custAddress")
	@Size(max = 100)
	@NotNull(message = "This field should not be an empty")
	private String custAddress;

	@Size(max = 50)
	@NotNull(message = "This field should not be an empty")
	@Column(name = "custCity")
	private String custCity;

	@Column(name = "custPincode")
	@Size(max = 7)
	@NotNull(message = "This field should not be an empty")
	private String custPincode;

	@Column(name = "custCode")
	@Size(min = 3, max = 6)
	@NotNull(message = "This field should not be an empty")
	private String custCode;

	@Column(name = "custMobile")
	@Size(max = 10)
	@NotNull(message = "This field should not be an empty")
	private String custMobile;

	@Column(name = "custLandline")
	@Size(max = 50)
	private String custLandline;

	@Column(name = "custEmail")
	@Size(max = 50)
	@NotNull(message = "This field should not be an empty")
	private String custEmail;

	@Column(name = "custNoOfBranch")
	@NotNull(message = "This field should not be an empty")
	private Integer custNoOfBranch;

	@Column(name = "custIsActive")
	private Boolean custIsActive;

	@Column(name = "custValidityStart")
	@NotNull(message = "This field should not be an empty")
	private Date custValidityStart;

	@Column(name = "custValidityEnd")
	@NotNull(message = "This field should not be an empty")
	private Date custValidityEnd;

	@Column(name = "custCreatedDate")
	private Date custCreatedDate;
		
	@Column(name = "custLogoFile")
	private byte[] custLogoFile;
	
	@Column(name = "custGSTIN")
	@Size(max = 50)
	private String custGSTIN;

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public IndustryType getIndustryType() {
		return industryType;
	}

	public void setIndustryType(IndustryType industryType) {
		this.industryType = industryType;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustAddress() {
		return custAddress;
	}

	public void setCustAddress(String custAddress) {
		this.custAddress = custAddress;
	}

	public String getCustCity() {
		return custCity;
	}

	public void setCustCity(String custCity) {
		this.custCity = custCity;
	}

	public String getCustPincode() {
		return custPincode;
	}

	public void setCustPincode(String custPincode) {
		this.custPincode = custPincode;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getCustMobile() {
		return custMobile;
	}

	public void setCustMobile(String custMobile) {
		this.custMobile = custMobile;
	}

	public String getCustLandline() {
		return custLandline;
	}

	public void setCustLandline(String custLandline) {
		this.custLandline = custLandline;
	}

	public String getCustEmail() {
		return custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public Integer getCustNoOfBranch() {
		return custNoOfBranch;
	}

	public void setCustNoOfBranch(Integer custNoOfBranch) {
		this.custNoOfBranch = custNoOfBranch;
	}

	public Boolean getCustIsActive() {
		return custIsActive;
	}

	public void setCustIsActive(Boolean custIsActive) {
		this.custIsActive = custIsActive;
	}

	public Date getCustValidityStart() {
		return custValidityStart;
	}

	public void setCustValidityStart(Date custValidityStart) {
		this.custValidityStart = custValidityStart;
	}

	public Date getCustValidityEnd() {
		return custValidityEnd;
	}

	public void setCustValidityEnd(Date custValidityEnd) {
		this.custValidityEnd = custValidityEnd;
	}

	public Date getCustCreatedDate() {
		return custCreatedDate;
	}

	public void setCustCreatedDate(Date custCreatedDate) {
		this.custCreatedDate = custCreatedDate;
	}

	public byte[] getCustLogoFile() {
		return custLogoFile;
	}

	public void setCustLogoFile(byte[] custLogoFile) {
		this.custLogoFile = custLogoFile;
	}

	public String getCustGSTIN() {
		return custGSTIN;
	}

	public void setCustGSTIN(String custGSTIN) {
		this.custGSTIN = custGSTIN;
	}
	
	

}
