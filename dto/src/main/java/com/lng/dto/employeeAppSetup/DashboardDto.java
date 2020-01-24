package com.lng.dto.employeeAppSetup;

import com.lng.dto.employeeAttendance.ShiftResponseDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapListResponse;
import com.lng.dto.masters.employeeLeave.EmpLeaveResponseDto;

import status.Status;

public class DashboardDto {
	
	private CustomerValidityDto customerValidity;

	private EmpAttndStatusDto empAttendanceStatus;
	
	private ShiftResponseDto empShiftDetails;
	
	private BlockBeaconMapListResponse empBeacons;
	
	private EmpLeaveResponseDto empLeaveData;
	
	public CustomerValidityDto getCustomerValidity() {
		return customerValidity;
	}

	public void setCustomerValidity(CustomerValidityDto customerValidity) {
		this.customerValidity = customerValidity;
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
