package com.lng.dto.empMovement;

import java.util.List;

import status.Status;

public class EmpMovementParamResponse {


	public  List<EmpMovementParam> empPlaceVisitList;


	public Status  status;


	public List<EmpMovementParam> getEmpPlaceVisitList() {
		return empPlaceVisitList;
	}


	public void setEmpPlaceVisitList(List<EmpMovementParam> empPlaceVisitList) {
		this.empPlaceVisitList = empPlaceVisitList;
	}


}
