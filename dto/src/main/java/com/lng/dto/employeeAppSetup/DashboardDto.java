package com.lng.dto.employeeAppSetup;

import com.lng.dto.employeeAttendance.ShiftResponseDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapListResponse;
import com.lng.dto.masters.customerConfig.DashboardCustConfigDto;
import com.lng.dto.masters.customerConfig.DashboardCustConfigResponse;
import com.lng.dto.masters.employeeLeave.EmpLeaveResponseDto;

import status.Status;

public class DashboardDto {
	
//	private CustomerValidityDto customerValidity;

	private Boolean isValidCustomer;
	
	private Boolean isValidBranch;
	
	private Boolean isEmployeeInService;
	
	private Boolean isFaceregistered;
	
	private Boolean isShiftAllotted;
	
	private DashboardCustConfigResponse config;
		
	private EmpAttndStatusDto empAttendanceStatus;
	
	private ShiftResponseDto empShiftDetails;
	
	private BlockBeaconMapListResponse empBeacons;
	
	private EmpLeaveResponseDto empLeaveData;
	
	public Status status;
	
	public Boolean getIsValidBranch() {
		return isValidBranch;
	}

	public void setIsValidBranch(Boolean isValidBranch) {
		this.isValidBranch = isValidBranch;
	}

	public DashboardCustConfigResponse getConfig() {
		return config;
	}

	public void setConfig(DashboardCustConfigResponse config) {
		this.config = config;
	}

	public Boolean getIsShiftAllotted() {
		return isShiftAllotted;
	}

	public void setIsShiftAllotted(Boolean isShiftAllotted) {
		this.isShiftAllotted = isShiftAllotted;
	}

	public Boolean getIsValidCustomer() {
		return isValidCustomer;
	}

	public void setIsValidCustomer(Boolean isValidCustomer) {
		this.isValidCustomer = isValidCustomer;
	}

	public Boolean getIsEmployeeInService() {
		return isEmployeeInService;
	}

	public void setIsEmployeeInService(Boolean isEmployeeInService) {
		this.isEmployeeInService = isEmployeeInService;
	}

	public Boolean getIsFaceregistered() {
		return isFaceregistered;
	}

	public void setIsFaceregistered(Boolean isFaceregistered) {
		this.isFaceregistered = isFaceregistered;
	}

	public EmpAttndStatusDto getEmpAttendanceStatus() {
		return empAttendanceStatus;
	}

	public void setEmpAttendanceStatus(EmpAttndStatusDto empAttendanceStatus) {
		this.empAttendanceStatus = empAttendanceStatus;
	}

	public BlockBeaconMapListResponse getEmpBeacons() {
		return empBeacons;
	}

	public void setEmpBeacons(BlockBeaconMapListResponse empBeacons) {
		this.empBeacons = empBeacons;
	}
	
	public EmpLeaveResponseDto getEmpLeaveData() {
		return empLeaveData;
	}

	public void setEmpLeaveData(EmpLeaveResponseDto empLeaveData) {
		this.empLeaveData = empLeaveData;
	}

	public ShiftResponseDto getEmpShiftDetails() {
		return empShiftDetails;
	}

	public void setEmpShiftDetails(ShiftResponseDto empShiftDetails) {
		this.empShiftDetails = empShiftDetails;
	}
}
