package com.lng.attendancecustomerservice.service.masters;
import com.lng.dto.masters.shift.ShiftDto;
import com.lng.dto.masters.shift.ShiftResponse;

import status.Status;

public interface ShiftService {
	ShiftResponse saveShift(ShiftDto shiftDto);
	ShiftResponse getAll();
	Status updateShiftByShiftId(ShiftDto shiftDto);
	ShiftResponse deleteByShiftId(Integer shiftId);
	ShiftResponse getBlockDetailsByRefBrId(Integer refBrId);
	ShiftResponse getShiftDetailsByShiftId(Integer shiftId);
}
