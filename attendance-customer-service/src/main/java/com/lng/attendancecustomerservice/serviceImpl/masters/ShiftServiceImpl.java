package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Branch;
import com.lng.attendancecustomerservice.entity.masters.Shift;
import com.lng.attendancecustomerservice.repositories.masters.BranchRepository;
import com.lng.attendancecustomerservice.repositories.masters.ShiftRepository;
import com.lng.attendancecustomerservice.service.masters.ShiftService;
import com.lng.dto.masters.shift.ShiftDto;
import com.lng.dto.masters.shift.ShiftResponse;

import status.Status;

@Service
public class ShiftServiceImpl implements ShiftService {

	ModelMapper modelMapper=new ModelMapper();

	@Autowired
	ShiftRepository shiftRepository;
	@Autowired
	BranchRepository branchRepository;



	@Override
	public ShiftResponse saveShift(ShiftDto shiftDto) { 

		ShiftResponse shiftResponse = new ShiftResponse();
		Shift shift = new Shift();
		try {
			if(shiftDto.getShiftName() == null || shiftDto.getShiftName().isEmpty()) throw new Exception("Plz Enter Shift Name");

			int a = shiftRepository.findByRefBrIdAndShiftName(shiftDto.getRefBrId(), shiftDto.getShiftName());
			if(a == 0) {
				Branch branch = branchRepository.findBranchByBrId(shiftDto.getRefBrId());
				if(branch != null) {
					shift.setBranch(branch);
					shift.setShiftName(shiftDto.getShiftName());
					shift.setShiftStart(shiftDto.getShiftStart());
					shift.setShiftEnd(shiftDto.getShiftEnd());
					shift.setDefaultOutInhrs(shiftDto.getDefaultOutInhrs());
					shift.setShiftIsActive(true);
					shiftRepository.save(shift);
					shiftResponse.status = new Status(false,200, "successfully created");
				}
				else{ 
					shiftResponse.status = new Status(true,400, "Branch not found");
				}
			}
			else{ 

				shiftResponse.status = new Status(true,400,"Shift name already exists");
			}
		} catch (Exception e) {
			shiftResponse.status = new Status(true, 500,"Oops..! Something went wrong..");
		}

		return shiftResponse;
	}

	@Override
	public ShiftResponse getAll() {
		ShiftResponse response = new ShiftResponse();
		try {
			List<Shift> shiftList = shiftRepository.findAllByShiftIsActive(true);
			if(!shiftList.isEmpty()) {
				response.setData1(shiftList.stream().map(shift -> convertToShiftDto(shift)).collect(Collectors.toList()));
				response.status = new Status(false,200, "success");
			} else {
				response.status = new Status(false,400, "Not found");
			}

		}catch(Exception e) {
			response.status = new Status(true,500, "Oops..! Something went wrong.."); 

		}
		return response;
	}

	@Override
	public Status updateShiftByShiftId(ShiftDto shiftDto) {
		Status status = null;
		try {
			if(shiftDto.getShiftName() == null || shiftDto.getShiftName().isEmpty()) throw new Exception("Please enter Shift name");
			if(shiftDto.getShiftId() == null || shiftDto.getShiftId() == 0) throw new Exception("Shift id is null or zero");
			if(shiftDto.getRefBrId() == null || shiftDto.getRefBrId() == 0) throw new Exception("RefBranchId id is null or zero");
			Shift shift = shiftRepository.findShiftByShiftId(shiftDto.getShiftId());

			Branch branch = branchRepository.findBranchByBrId(shiftDto.getRefBrId());
			if(branch != null) {
				Shift sh = shiftRepository.findShiftByshiftNameAndBranch_brId(shiftDto.getShiftName(), shiftDto.getRefBrId());
				if(sh == null) {	

					shift = modelMapper.map(shiftDto,Shift.class);
					shift.setBranch(branch);
					shift.setShiftIsActive(true);
					shiftRepository.save(shift);
					status = new Status(false, 200, "successfully updated");
				} else if (sh.getShiftId() == shiftDto.getShiftId()) { 

					shift = modelMapper.map(shiftDto,Shift.class);
					shift.setBranch(branch);
					shift.setShiftIsActive(true);
					shiftRepository.save(shift);
					status = new Status(false, 200, "successfully updated");
				}
				else{ 

					status = new Status(true,400,"Shift name already exists");
				}
			}

			else {
				status = new Status(false, 400, "Branch not found");
			}
		}
		catch(Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}

	@Override
	public ShiftResponse deleteByShiftId(Integer shiftId) {
		ShiftResponse shiftResponse=new ShiftResponse(); 
		try {
			Shift shift = shiftRepository.findShiftByShiftId(shiftId);
			int b = shiftRepository.findEmployeeByShiftShiftId(shiftId);
			if(shift!= null) {
				if(b == 0) {

					shiftRepository.delete(shift);	
					shiftResponse.status = new Status(false,200, "successfully deleted");
				} else {
					shift.setShiftIsActive(false);
					shiftRepository.save(shift);
					shiftResponse.status = new Status(false,200, "The record has been disabled since it has been used in other transactions");
				}

			} else {
				shiftResponse.status = new Status(true,400, "Shift not found");
			}

		}catch(Exception e) { 
			shiftResponse.status = new Status(true, 500, "Oops..! Something went wrong..");
		}

		return shiftResponse;
	}

	@Override
	public ShiftResponse getShiftDetailsByRefBrId(Integer refBrId) {
		ShiftResponse response = new ShiftResponse();
		List<ShiftDto> shiftDtoList = new ArrayList<>();
		try {

			List<Object[]> shiftList = shiftRepository.findShiftDetailsByBranch_RefBrIdAndShiftIsActive(refBrId, true);
			if(shiftList.isEmpty()) {
				response.status = new Status(false,400, "Shift not found");
			}else {
				for (Object[] p : shiftList) {	

					ShiftDto shiftDto1 = new ShiftDto();
					shiftDto1.setShiftId(Integer.valueOf(p[0].toString()));
					shiftDto1.setShiftName(p[1].toString());
					shiftDto1.setShiftStart((p[2].toString()));
					shiftDto1.setShiftEnd((p[3].toString()));
					shiftDto1.setRefBrId(Integer.valueOf(p[4].toString()));
					shiftDto1.setDefaultOutInhrs(Integer.valueOf(p[5].toString()));
					shiftDtoList.add(shiftDto1);
					response.status = new Status(false,200, "Success");
				}
			}
		}catch (Exception e){
			response.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		response.setData1(shiftDtoList);
		return response;
	}


	@Override
	public ShiftResponse getShiftDetailsByShiftId(Integer shiftId) {
		ShiftResponse response = new ShiftResponse();
		try {
			Shift shift=shiftRepository.findShiftByShiftId(shiftId);
			if(shift != null) {
				ShiftDto shiftDto = convertToShiftDto(shift);
				response.data = shiftDto;
				response.status = new Status(false,200, "Success");
			}
			else {
				response.status = new Status(false, 400, "Not found");
			}
		}catch(Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong.."); 

		}
		return response;
	}


	@Override
	public ShiftResponse getAllByCustId(Integer custId) {
		ShiftResponse response = new ShiftResponse();
		try {
			List<Shift> shiftList=shiftRepository.findByCustomer_CustIdAndShiftIsActive(custId);
			response.setData1(shiftList.stream().map(shift -> convertToShiftDto(shift)).collect(Collectors.toList()));
			if(response.getData1().isEmpty()) {
				response.status = new Status(false,400, "Not found"); 
			}else {
				response.status = new Status(false,200, "Success");
			}
		}catch(Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong.."); 

		}
		return response;
	}

	public ShiftDto convertToShiftDto(Shift shift) {
		ShiftDto shiftDto = modelMapper.map(shift,ShiftDto.class);
		shiftDto.setRefBrId(shift.getBranch().getBrId());
		shiftDto.setBrName(shift.getBranch().getBrName());
		return shiftDto;
	}

	// Convert 24 hours time to 12 hours
	/*public String TimeHourConvert(String time) {
		String pattern = "h:mm:ss a";
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		//dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String date = dateFormat.format(time);
		return date;
	}*/
}