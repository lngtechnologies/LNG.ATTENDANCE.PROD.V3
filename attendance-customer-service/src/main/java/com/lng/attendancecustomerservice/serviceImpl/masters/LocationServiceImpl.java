package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Contractor;
import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.entity.masters.Department;
import com.lng.attendancecustomerservice.entity.masters.Location;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.repositories.masters.LocationRepository;
import com.lng.attendancecustomerservice.service.masters.LocationService;
import com.lng.dto.masters.department.DepartmentDto;
import com.lng.dto.masters.location.LocationDto;
import com.lng.dto.masters.location.LocationResponse;

import status.Status;
@Service
public class LocationServiceImpl implements LocationService {

	ModelMapper modelMapper=new ModelMapper();

	@Autowired
	LocationRepository locationRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Override
	public LocationResponse save(LocationDto locationDto) {
		LocationResponse locationResponse = new LocationResponse();
		try {
			Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(locationDto.getRefCustId(), true);
			if(customer != null) {
				Location location1 = locationRepository.findLocationByCustomer_CustIdAndLocation(locationDto.getRefCustId(), locationDto.getLocation());
				if(location1 == null) {
					Location location = new Location();
					location.setCustomer(customer);
					location.setLocation(locationDto.getLocation());
					location.setCreatedDate(new Date());
					locationRepository.save(location);
					locationResponse.status = new Status(false, 200, "created");
				}else {
					locationResponse.status = new Status(true, 400, "Location already exists");	
				}
			}else {
				locationResponse.status = new Status(true, 400, "Customer not found");
			}

		}catch(Exception e) {
			locationResponse.status = new Status(true, 500, "Oops..! Something went wrong");
		}
		return locationResponse;
	}

	@Override
	public LocationResponse updateByLocationId(LocationDto locationDto) {
		LocationResponse locationResponse = new LocationResponse();
		try {
			Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(locationDto.getRefCustId(), true);
			if(customer != null) {
				Location location = locationRepository.findLocationByLocationId(locationDto.getLocationId());
				if(location != null) {
					Location location1 = locationRepository.findLocationByCustomer_CustIdAndLocation(locationDto.getRefCustId(), locationDto.getLocation());
					if(location1 == null || location1.getLocationId() == locationDto.getLocationId()  ) {
						location = modelMapper.map(locationDto,Location.class);
						location.setCustomer(customer);
						location.setCreatedDate(new Date());
						locationRepository.save(location);
						locationResponse.status = new Status(false, 200, "updated");
					}else {
						locationResponse.status = new Status(true, 400, "Location already exists");
					}
				}else {
					locationResponse.status = new Status(true, 400, "Location id not found");
				}
			}else {
				locationResponse.status = new Status(true, 400, "Customer not found");
			}

		}catch(Exception e) {
			locationResponse.status = new Status(true, 500, "Oops..! Something went wrong");
		}
		return locationResponse;
	}

	@Override
	public LocationResponse deleteByLocationId(Integer locationId) {
		LocationResponse locationResponse = new LocationResponse();
		try {
			Location location = locationRepository.findLocationByLocationId(locationId);
			if(location != null) {
				locationRepository.delete(location);
				locationResponse.status = new Status(false,200, "deleted");

			}else {
				locationResponse.status = new Status(true, 400, "Location id not found");
			}
		}catch(Exception e) {
			locationResponse.status = new Status(true, 500, "Oops..! Something went wrong");
		}
		return locationResponse;
	}

	@Override
	public LocationResponse getAllByCustId(Integer refCustId) {
		LocationResponse locationResponse = new LocationResponse();
		try {
			List<Location> locations = locationRepository.findAllByCustomer_CustId(refCustId);
			locationResponse.setLocationDetails(locations.stream().map(location -> convertToLocationDto(location)).collect(Collectors.toList()));
			if(locationResponse.getLocationDetails().isEmpty()) {
				locationResponse.status = new Status(false,400, "Not found");
			}else {
				locationResponse.status = new Status(false,200, "success");
			}
		}catch(Exception e) {
			locationResponse.status = new Status(true, 500, "Oops..! Something went wrong");
		}
		return locationResponse;
	}

	public LocationDto convertToLocationDto(Location location) {
		LocationDto locationDto = modelMapper.map(location,LocationDto.class);
		locationDto.setRefCustId(location.getCustomer().getCustId());
		locationDto.setCustName(location.getCustomer().getCustName());
		return locationDto;
	}
}
