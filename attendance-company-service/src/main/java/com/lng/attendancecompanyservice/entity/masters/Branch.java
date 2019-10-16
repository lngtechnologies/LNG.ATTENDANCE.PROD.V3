package com.lng.attendancecompanyservice.entity.masters;



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

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;

@Entity
@Table(name = "tmbranch")
public class Branch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "brId")
	private Integer brId;

	@ManyToOne
    @JoinColumn(name = "refCustomerId")
	private Customer customer;

	@ManyToOne
    @JoinColumn(name = "refCountryId")
	private Country Country;

	@ManyToOne
    @JoinColumn(name = "refStateId")
	private State state;

	@Column(name = "brCity")
	@Size(max = 50)
	@NotNull(message = "This field should not be an empty")
	private String brCity;

	@Size(max = 50)
	@NotNull(message = "This field should not be an empty")
	@Column(name = "brName")
	private String brName;

	@Size(max = 100)
	@NotNull(message = "This field should not be an empty")
	@Column(name = "brAddress")
	private String brAddress;

	@Size(max = 7)
	@NotNull(message = "This field should not be an empty")
	@Column(name = "brPincode")
	private String brPincode;

	@Size(max = 8)
	@NotNull(message = "This field should not be an empty")
	@Column(name = "brCode")
	private String brCode;

	@Size(max = 10)
	@NotNull(message = "This field should not be an empty")
	@Column(name = "brMobile")
	private String brMobile;

	@Size(max = 30)
	@Column(name = "brLandline")
	private String brLandline;

	@Size(max = 50)
	@NotNull(message = "This field should not be an empty")
	@Column(name = "brEmail")
	private String brEmail;

	@Column(name = "brLatLong")
	private String brLatLong;

	@Column(name = "brIsBillable")
	@NotNull(message = "This field should not be an empty")
	private Boolean brIsBillable;

	@Column(name = "brIsActive")
	@NotNull(message = "This field should not be an empty")
	private Boolean brIsActive;

	@Column(name = "brValidityStart")
	@NotNull(message = "This field should not be an empty")
	private Date brValidityStart;

	@Column(name = "brValidityEnd")
	@NotNull(message = "This field should not be an empty")
	private Date brValidityEnd;

	@Column(name = "brCreatedDate")
	@NotNull(message = "This field should not be an empty")
	private Date brCreatedDate;

	public Integer getBrId() {
		return brId;
	}

	public void setBrId(Integer brId) {
		this.brId = brId;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Country getCountry() {
		return Country;
	}

	public void setCountry(Country country) {
		Country = country;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getBrCity() {
		return brCity;
	}

	public void setBrCity(String brCity) {
		this.brCity = brCity;
	}

	public String getBrName() {
		return brName;
	}

	public void setBrName(String brName) {
		this.brName = brName;
	}

	public String getBrAddress() {
		return brAddress;
	}

	public void setBrAddress(String brAddress) {
		this.brAddress = brAddress;
	}

	public String getBrPincode() {
		return brPincode;
	}

	public void setBrPincode(String brPincode) {
		this.brPincode = brPincode;
	}

	public String getBrCode() {
		return brCode;
	}

	public void setBrCode(String brCode) {
		this.brCode = brCode;
	}

	public String getBrMobile() {
		return brMobile;
	}

	public void setBrMobile(String brMobile) {
		this.brMobile = brMobile;
	}

	public String getBrLandline() {
		return brLandline;
	}

	public void setBrLandline(String brLandline) {
		this.brLandline = brLandline;
	}

	public String getBrEmail() {
		return brEmail;
	}

	public void setBrEmail(String brEmail) {
		this.brEmail = brEmail;
	}

	public String getBrLatLong() {
		return brLatLong;
	}

	public void setBrLatLong(String brLatLong) {
		this.brLatLong = brLatLong;
	}

	public Boolean getBrIsBillable() {
		return brIsBillable;
	}

	public void setBrIsBillable(Boolean brIsBillable) {
		this.brIsBillable = brIsBillable;
	}

	public Boolean getBrIsActive() {
		return brIsActive;
	}

	public void setBrIsActive(Boolean brIsActive) {
		this.brIsActive = brIsActive;
	}

	public Date getBrValidityStart() {
		return brValidityStart;
	}

	public void setBrValidityStart(Date brValidityStart) {
		this.brValidityStart = brValidityStart;
	}

	public Date getBrValidityEnd() {
		return brValidityEnd;
	}

	public void setBrValidityEnd(Date brValidityEnd) {
		this.brValidityEnd = brValidityEnd;
	}

	public Date getBrCreatedDate() {
		return brCreatedDate;
	}

	public void setBrCreatedDate(Date brCreatedDate) {
		this.brCreatedDate = brCreatedDate;
	}
	
}
