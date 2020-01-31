package com.lng.attendancecompanyservice.serviceImpl.azureEmpDetails;


import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lng.attendancecompanyservice.entity.masters.Branch;
import com.lng.attendancecompanyservice.entity.masters.Employee;
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
		AzurePersistedFaceIdsDto azurePersistedFaceIdsDto = new AzurePersistedFaceIdsDto();
		List<AzurePersistedFaceIdsDto> faceList = new ArrayList<AzurePersistedFaceIdsDto>();

		try {
			Branch branch = branchRepository.findBranchByBrId(brId);	
			if(branch != null) {
				String brCode = branch.getBrCode().toLowerCase();

				String json = getPersistedFaceIds(brCode).toJSONString();

				ObjectMapper mapper = new ObjectMapper();

				List<AzurePersistedFaceIdsDto> list = Arrays.asList(mapper.readValue(json, AzurePersistedFaceIdsDto[].class));
				for(AzurePersistedFaceIdsDto p: list) {
					Employee employee = custEmployeeRepository.getEmployeeByEmpPresistedFaceId(p.getPersistedFaceId());
					if(employee != null) {
						azurePersistedFaceIdsDto.setEmpName(employee.getEmpName());
						azurePersistedFaceIdsDto.setMobileNo(employee.getEmpMobile());
						azurePersistedFaceIdsDto.setEmpInService(employee.getEmpInService());
						azurePersistedFaceIdsDto.setPersistedFaceId(p.getPersistedFaceId());
						azurePersistedFaceIdsDto.setUserData(p.getUserData());
						faceList.add(azurePersistedFaceIdsDto);
					}
				}

				azureFacelistResponseDto.setFacelist(faceList);
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
		AzureLargeFaceListDto azureLargeFaceListDto = new AzureLargeFaceListDto();
		List<AzureLargeFaceListDto> list1 = new ArrayList<AzureLargeFaceListDto>();

		try {
			// azureLargeFaceListResponseDto.setLargeFaceList(getLargeFaceList());

			String json = getLargeFaceList().toJSONString();

			ObjectMapper mapper = new ObjectMapper();

			List<AzureLargeFaceListDto> list = Arrays.asList(mapper.readValue(json, AzureLargeFaceListDto[].class));

			for(AzureLargeFaceListDto p : list) {

				Branch branch = branchRepository.getBranchByBrCode(p.getLargeFaceListId());
				if(branch != null) {
					azureLargeFaceListDto.setBrId(branch.getBrId());
					azureLargeFaceListDto.setBrName(branch.getBrName());
					azureLargeFaceListDto.setBrIsActive(branch.getBrIsActive());
					azureLargeFaceListDto.setLargeFaceListId(p.getLargeFaceListId());
					azureLargeFaceListDto.setName(p.getName());
					azureLargeFaceListDto.setUserData(p.getUserData());
					list1.add(azureLargeFaceListDto);
				}

			}
			azureLargeFaceListResponseDto.setLargeFaceList(list1);
			azureLargeFaceListResponseDto.status = new Status(false, 200, "Success");
		} catch (Exception e) {
			azureLargeFaceListResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return azureLargeFaceListResponseDto;
	}
	
	
	@Override
	public Status deleteLargeFacelist(String largeFacelist) {
		Status status = null;
		try {
			deleteLargeFaceId(largeFacelist);
			status = new Status(false, 200, "success");
		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}
	
	@Override
	public Status deletePersistedFaceId(String faceId) {
		Status status = null;
		try {
			Employee employee = custEmployeeRepository.getEmployeeByEmpPresistedFaceId(faceId);
			if(employee != null) {
				Branch branch = branchRepository.findBranchByBrId(employee.getBranch().getBrId());
				if(branch != null){
					String brCode = branch.getBrCode().toLowerCase();
					deletePersistedFaceId(brCode, faceId);
					status = new Status(false, 200, "success");
				} else {
					status = new Status(false, 400, "Branch not found");
				}
			} else {
				status = new Status(false, 400, "Employee not found");
			}
		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}


	// Get All Largefacelist
	public JSONArray getLargeFaceList() throws Exception {

		HttpClient httpclient = HttpClients.createDefault();
		HttpEntity entity = null;
		JSONArray jsonArr = new JSONArray();
		try
		{
			URIBuilder builder = new URIBuilder("https://centralindia.api.cognitive.microsoft.com/face/v1.0/largefacelists");

			URI uri = builder.build();
			HttpGet request = new HttpGet(uri);
			request.setHeader("Ocp-Apim-Subscription-Key", subscription.getKey());

			HttpResponse response = httpclient.execute(request);
			entity = response.getEntity();

			String content = EntityUtils.toString(entity);

			// JSONArray jsonArr = new JSONArray();


			JSONParser parser = new JSONParser();
			jsonArr = (JSONArray) parser.parse(content);

			System.out.println(jsonArr);

		}
		catch (Exception e)
		{
			throw e;
		}
		return jsonArr;
	}

	// Get list of persisted face id's
	public JSONArray getPersistedFaceIds(String branchCode) throws Exception {

		HttpClient httpclient = HttpClients.createDefault();
		HttpEntity entity = null;
		JSONArray jsonArr = new JSONArray();
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

			JSONParser parser = new JSONParser();
			jsonArr = (JSONArray) parser.parse(retSrc);

			System.out.println(jsonArr);
		}
		catch (Exception e)
		{
			throw e;
			// System.out.println(e.getMessage());
		}
		return jsonArr;
	}

	//Delete LargerFaceList
	public void deleteLargeFaceId(String largeFacelist) {
		HttpClient httpclient = HttpClients.createDefault();

		try
		{
			URIBuilder builder = new URIBuilder("https://centralindia.api.cognitive.microsoft.com/face/v1.0/largefacelists/"+largeFacelist);

			URI uri = builder.build();
			HttpDelete request = new HttpDelete(uri);
			request.setHeader("Ocp-Apim-Subscription-Key", subscription.getKey());


			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) 
			{
				System.out.println(EntityUtils.toString(entity));
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	
	//Delete Persisted face id
	public void deletePersistedFaceId(String brCode, String persistedFaceId) {
		HttpClient httpclient = HttpClients.createDefault();

        try
        {
            URIBuilder builder = new URIBuilder("https://centralindia.api.cognitive.microsoft.com/face/v1.0/largefacelists/"+brCode+"/persistedfaces/"+persistedFaceId);


            URI uri = builder.build();
            HttpDelete request = new HttpDelete(uri);
            request.setHeader("Ocp-Apim-Subscription-Key", subscription.getKey());


            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) 
            {
                System.out.println(EntityUtils.toString(entity));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

	}

	// Conver to modelmapper
	public AzurePersistedFaceIdsDto convertToAzureFacelistDto(HttpEntity entity) {
		AzurePersistedFaceIdsDto  azureFacelistDto = mapper.map(entity, AzurePersistedFaceIdsDto.class);
		return azureFacelistDto;
	}

	public AzureLargeFaceListDto convertToAzureLargeFaceListDto(String entity) {
		AzureLargeFaceListDto  azureLargeFaceListDto = mapper.map(entity, AzureLargeFaceListDto.class);
		return azureLargeFaceListDto;
	}	
}
