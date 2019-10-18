package com.lng.attendancecompanyservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.masters.IndustryTypeService;
import com.lng.dto.masters.industryType.IndustryTypeDto;
import com.lng.dto.masters.industryType.IndustryTypeListResponse;
import com.lng.dto.masters.industryType.IndustryTypeResponse;

import status.StatusDto;


@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/industry/type")
public class IndustryTypeController {

	@Autowired
	IndustryTypeService industryTypeService;
	
	@PostMapping(value = "/create")
    public ResponseEntity<StatusDto> save(@RequestBody IndustryTypeDto industryTypeDto) {
		StatusDto statusDto = industryTypeService.saveIndustryType(industryTypeDto);
        if (statusDto !=null){
            return new ResponseEntity<StatusDto>(statusDto, HttpStatus.CREATED);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
	
	@GetMapping(value = "/findAll")
	public ResponseEntity<IndustryTypeListResponse> findAll() {
		IndustryTypeListResponse industryTypeListResponse = industryTypeService.findAllIndustryType(); 
       if(industryTypeListResponse.getIndustryTypeDtoList().isEmpty()) {
           return new ResponseEntity(HttpStatus.NO_CONTENT);
       }
       return new ResponseEntity<IndustryTypeListResponse>(industryTypeListResponse, HttpStatus.OK);
   }
	
	@PostMapping(value = "/findByIndustryId")
    public ResponseEntity<IndustryTypeResponse> findByIndustryId(@RequestBody IndustryTypeDto industryTypeDto) {
		IndustryTypeResponse industryTypeResponse = industryTypeService.findIndustryByIndustryid(industryTypeDto.getIndustryId());
        if (industryTypeResponse !=null){
            return new ResponseEntity<IndustryTypeResponse>(industryTypeResponse, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	
	@PostMapping(value = "/update")
	  public ResponseEntity<StatusDto> updateCustomer(@RequestBody IndustryTypeDto industryTypeDto) {
		StatusDto statusDto = industryTypeService.updateIndustryType(industryTypeDto);
	    if (statusDto != null) {
	      return new ResponseEntity<StatusDto>(statusDto, HttpStatus.OK);
	    }
	    return new ResponseEntity(HttpStatus.NO_CONTENT);
	  }
	
	@PostMapping(value = "/delete")
	  public ResponseEntity<StatusDto> deleteCustomer(@RequestBody IndustryTypeDto industryTypeDto) {
		StatusDto statusDto = industryTypeService.deleteIndustryByIndustryId(industryTypeDto.getIndustryId());
	    if (statusDto != null) {
	      return new ResponseEntity<StatusDto>(statusDto, HttpStatus.OK);
	    }
	    return new ResponseEntity(HttpStatus.NO_CONTENT);
	  }
}
