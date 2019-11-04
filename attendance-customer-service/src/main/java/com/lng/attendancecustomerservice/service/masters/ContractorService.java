package com.lng.attendancecustomerservice.service.masters;

import com.lng.dto.masters.contractor.ContractorDto;
import com.lng.dto.masters.contractor.ContractorResponse;

import status.Status;

public interface ContractorService {


	ContractorResponse saveContractor(ContractorDto contractorDto);

	ContractorResponse getAll();
	Status updateContractorByContractorId(ContractorDto contractorDto); 
	ContractorResponse deleteByContractorId(Integer contractorId);
	ContractorResponse getContractorByContractorId(Integer contractorId);
	ContractorResponse getAllByCustId(Integer custId);
	

}
