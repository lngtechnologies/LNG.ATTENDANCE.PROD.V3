package com.lng.attendancecompanyservice.serviceImpl.azureEmpDetails;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Service;
import com.lng.attendancecompanyservice.entity.masters.Branch;
import com.lng.attendancecompanyservice.repositories.masters.BranchRepository;
import com.lng.attendancecompanyservice.repositories.masters.CustEmployeeRepository;
import com.lng.attendancecompanyservice.service.azureEmpDetails.RegisteredEmployeeService;
import com.lng.attendancecompanyservice.utils.AzureFaceListSubscriptionKey;
import com.lng.dto.empAzureDetails.AzureLargeFaceListDto;
import com.lng.dto.empAzureDetails.AzureLargeFaceListResponseDto;
import com.lng.dto.empAzureDetails.AzurePersistedFaceIdsDto;
import com.lng.dto.empAzureDetails.AzurePersistedFaceIdsResponseDto;
import status.Status;

@Service
public class RegisteredEmployeeServiceImpl implements RegisteredEmployeeService {

	@Autowired
	BranchRepository branchRepository;

	@Autowired
	CustEmployeeRepository custEmployeeRepository;

	ModelMapper mapper = new ModelMapper();
	
	AzureFaceListSubscriptionKey subscription = new AzureFaceListSubscriptionKey();

	@Override
	public AzurePersistedFaceIdsResponseDto getPersistedFaceIdByBranchId(Integer brId) {
		AzurePersistedFaceIdsResponseDto azureFacelistResponseDto = new AzurePersistedFaceIdsResponseDto();
		try {
			Branch branch = branchRepository.findBranchByBrId(brId);	
			if(branch != null) {
				String brCode = branch.getBrCode().toLowerCase();
				azureFacelistResponseDto.setFacelist(getPersistedFaceIds(brCode));
				azureFacelistResponseDto.status = new Status(false, 200, "Success");
			}else {
				azureFacelistResponseDto.status = new Status(false, 400, "Branch not found");
			}
		} catch (Exception e) {
			azureFacelistResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return azureFacelistResponseDto;
	}


	@Override
	public AzureLargeFaceListResponseDto getAllFaceList() {
		AzureLargeFaceListResponseDto azureLargeFaceListResponseDto = new AzureLargeFaceListResponseDto();
		try {
			azureLargeFaceListResponseDto.setLargeFaceList(getLargeFaceList());
			azureLargeFaceListResponseDto.status = new Status(false, 200, "Success");
		} catch (Exception e) {
			azureLargeFaceListResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return azureLargeFaceListResponseDto;
	}

	// Get All Largefacelist
	public List<AzureLargeFaceListDto> getLargeFaceList() throws Exception {

		HttpClient httpclient = HttpClients.createDefault();
		HttpEntity entity = null;
		List<AzureLargeFaceListDto> list = new ArrayList<AzureLargeFaceListDto>();
		try
		{
			URIBuilder builder = new URIBuilder("https://centralindia.api.cognitive.microsoft.com/face/v1.0/largefacelists");

			URI uri = builder.build();
			HttpGet request = new HttpGet(uri);
			request.setHeader("Ocp-Apim-Subscription-Key", subscription.getKey());

			HttpResponse response = httpclient.execute(request);
			entity = response.getEntity();
							
			if (entity != null) 
			{
				list.add(convertToAzureLargeFaceListDto(entity));
				System.out.println(EntityUtils.toString(entity));
			}
		}
		catch (Exception e)
		{
			throw e;
			// System.out.println(e.getMessage());
		}
		return list;
	}

	// Get list of persisted face id's
	public List<AzurePersistedFaceIdsDto> getPersistedFaceIds(String branchCode) throws Exception {

		HttpClient httpclient = HttpClients.createDefault();
		HttpEntity entity = null;
		List<AzurePersistedFaceIdsDto> list = new ArrayList<AzurePersistedFaceIdsDto>();
		try
		{
			String brCode = branchCode.toLowerCase();

			URIBuilder builder = new URIBuilder("https://centralindia.api.cognitive.microsoft.com/face/v1.0/largefacelists/"+brCode+"/persistedfaces");

			URI uri = builder.build();
			HttpGet request = new HttpGet(uri);
			request.setHeader("Ocp-Apim-Subscription-Key", subscription.getKey());

			// Request body

			//StringEntity reqEntity = new StringEntity(""); 
			//request.setEntity(reqEntity);

			HttpResponse response = httpclient.execute(request);
			entity = response.getEntity();
			
			String retSrc = EntityUtils.toString(entity);			

			if (entity != null) 
			{
				list.add(convertToAzureFacelistDto(entity));
				System.out.println(EntityUtils.toString(entity));
			}
		}
		catch (Exception e)
		{
			throw e;
			// System.out.println(e.getMessage());
		}
		return list;
	}

	// Conver to modelmapper
	public AzurePersistedFaceIdsDto convertToAzureFacelistDto(HttpEntity entity) {
		AzurePersistedFaceIdsDto  azureFacelistDto = mapper.map(entity, AzurePersistedFaceIdsDto.class);
		return azureFacelistDto;
	}

	public AzureLargeFaceListDto convertToAzureLargeFaceListDto(HttpEntity entity) {
		AzureLargeFaceListDto  azureLargeFaceListDto = mapper.map(entity, AzureLargeFaceListDto.class);
		return azureLargeFaceListDto;
	}
}
