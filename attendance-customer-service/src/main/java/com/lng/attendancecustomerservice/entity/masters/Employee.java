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
import javax.validation.constraints.Size;

@Entity
@Table(name = "tmemployee")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "empId")
	private Integer empId;

	@ManyToOne
    @JoinColumn(name = "refBrId")
	private Branch branch;

	@ManyToOne
    @JoinColumn(name = "refEmpType")
	private EmployeeType employeeType;

	@ManyToOne
    @JoinColumn(name = "refContractorId")
	private Contractor contractor;

	@ManyToOne
    @JoinColumn(name = "refShiftId")
	private Shift shift;

	@ManyToOne
    @JoinColumn(name = "refCustId")
	private Customer customer;

	@Column(name = "empName")
	@Size(max = 50)
	// @NotNull(message = "This field should not be an empty")
	private String empName;

	@Column(name = "empMobile")
	@Size(max = 10)
	// @NotNull(message = "This field should not be an empty")
	private String empMobile;

	@Column(name = "empPassword")
	@Size(max = 50)
	// @NotNull(message = "This field should not be an empty")
	private String empPassword;

	@Column(name = "empGender")
	@Size(max = 1)
	// @NotNull(message = "This field should not be an empty")
	private String empGender;

	@Column(name = "empInService")
	private Boolean empInService;

	@Column(name = "empReportingTo")
	// @NotNull(message = "This field should not be an empty")
	private Integer empReportingTo;

	@Column(name = "empJoiningDate")
	private Date empJoiningDate;

	@Column(name = "empPicBlobPath")
	@Size(max = 100)
	private String empPicBlobPath;

	@Column(name = "empPresistedFaceId")
	@Size(max = 100)
	private String empPresistedFaceId;

	@Column(name = "empDeviceName")
	@Size(max = 50)
	private String empDeviceName;

	@Column(name = "empModelNumber")
	@Size(max = 50)
	private String empModelNumber;

	@Column(name = "empAndriodVersion")
	@Size(max = 10)
	private String empAndriodVersion;
	
	@Column(name = "empAppSetupStatus")
	private Boolean empAppSetupStatus;
	
	public Integer getEmpId() {
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}
	
	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}
	public EmployeeType getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(EmployeeType employeeType) {
		this.employeeType = employeeType;
	}

	public Contractor getContractor() {
		return contractor;
	}

	public void setContractor(Contractor contractor) {
		this.contractor = contractor;
	}

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEmpMobile() {
		return empMobile;
	}

	public void setEmpMobile(String empMobile) {
		this.empMobile = empMobile;
	}

	public String getEmpPassword() {
		return empPassword;
	}

	public void setEmpPassword(String empPassword) {
		this.empPassword = empPassword;
	}

	public String getEmpGender() {
		return empGender;
	}

	public void setEmpGender(String empGender) {
		this.empGender = empGender;
	}

	public Boolean getEmpInService() {
		return empInService;
	}

	public void setEmpInService(Boolean empInService) {
		this.empInService = empInService;
	}

	public Integer getEmpReportingTo() {
		return empReportingTo;
	}

	public void setEmpReportingTo(Integer empReportingTo) {
		this.empReportingTo = empReportingTo;
	}

	public Date getEmpJoiningDate() {
		return empJoiningDate;
	}

	public void setEmpJoiningDate(Date empJoiningDate) {
		this.empJoiningDate = empJoiningDate;
	}

	public String getEmpPicBlobPath() {
		return empPicBlobPath;
	}

	public void setEmpPicBlobPath(String empPicBlobPath) {
		this.empPicBlobPath = empPicBlobPath;
	}

	public String getEmpPresistedFaceId() {
		return empPresistedFaceId;
	}

	public void setEmpPresistedFaceId(String empPresistedFaceId) {
		this.empPresistedFaceId = empPresistedFaceId;
	}

	public String getEmpDeviceName() {
		return empDeviceName;
	}

	public void setEmpDeviceName(String empDeviceName) {
		this.empDeviceName = empDeviceName;
	}

	public String getEmpModelNumber() {
		return empModelNumber;
	}

	public void setEmpModelNumber(String empModelNumber) {
		this.empModelNumber = empModelNumber;
	}

	public String getEmpAndriodVersion() {
		return empAndriodVersion;
	}

	public void setEmpAndriodVersion(String empAndriodVersion) {
		this.empAndriodVersion = empAndriodVersion;
	}

	public Boolean getEmpAppSetupStatus() {
		return empAppSetupStatus;
	}

	public void setEmpAppSetupStatus(Boolean empAppSetupStatus) {
		this.empAppSetupStatus = empAppSetupStatus;
	}

}
