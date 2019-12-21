package com.lng.attendancecompanyservice.serviceImpl.azureEmpDetails;


import java.net.URI;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.masters.Branch;
import com.lng.attendancecompanyservice.repositories.masters.BranchRepository;
import com.lng.attendancecompanyservice.repositories.masters.CustEmployeeRepository;
import com.lng.attendancecompanyservice.service.azureEmpDetails.RegisteredEmployeeService;
import com.lng.dto.empAzureDetails.AzureFacelistResponseDto;
import status.Status;

@Service
public class RegisteredEmployeeServiceImpl implements RegisteredEmployeeService {

	@Autowired
	BranchRepository branchRepository;
	
	@Autowired
	CustEmployeeRepository custEmployeeRepository;
	
	@Override
	public AzureFacelistResponseDto getRegisteredEmpDetailsByBranchId(Integer brId) {
		AzureFacelistResponseDto azureFacelistResponseDto = new AzureFacelistResponseDto();
		try {
			Branch branch = branchRepository.findBranchByBrId(brId);	
			if(branch != null) {
				String brCode = branch.getBrCode();
				
				//azureFacelistResponseDto.setFacelist();
				azureFacelistResponseDto.status = new Status(false, 200, "Success");
			}else {
				azureFacelistResponseDto.status = new Status(false, 400, "Branch not found");
			}
		} catch (Exception e) {
			azureFacelistResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return azureFacelistResponseDto;
	}


	// Get list of persisted face id's
	@Override
	public void getPersistedFaceIds(String branchCode) throws Exception {
		
		HttpClient httpclient = HttpClients.createDefault();

		try
		{
			String brCode = branchCode.toLowerCase();

			URIBuilder builder = new URIBuilder("https://centralindia.api.cognitive.microsoft.com/face/v1.0/largefacelists/"+brCode+"/persistedfaces");
			
			URI uri = builder.build();
			HttpGet request = new HttpGet(uri);
			request.setHeader("Ocp-Apim-Subscription-Key", "935ac35bce0149d8bf2818b936e25e1c");


			// Request body
			
			 //StringEntity reqEntity = new StringEntity(""); 
			 //request.setEntity(reqEntity);
			 

			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) 
			{
				System.out.println(EntityUtils.toString(entity));
			}
		}
		catch (Exception e)
		{
			throw e;
			// System.out.println(e.getMessage());
		}
	}
}
