package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;
import com.lng.attendancecompanyservice.entity.masters.Block;
import com.lng.attendancecompanyservice.entity.masters.Branch;
import com.lng.attendancecompanyservice.entity.masters.Country;
import com.lng.attendancecompanyservice.entity.masters.Employee;
import com.lng.attendancecompanyservice.entity.masters.EmployeeBranch;
import com.lng.attendancecompanyservice.entity.masters.Login;
import com.lng.attendancecompanyservice.entity.masters.LoginDataRight;
import com.lng.attendancecompanyservice.entity.masters.Shift;
import com.lng.attendancecompanyservice.entity.masters.State;
import com.lng.attendancecompanyservice.repositories.custOnboarding.CustomerRepository;
import com.lng.attendancecompanyservice.repositories.masters.BlockRepository;
import com.lng.attendancecompanyservice.repositories.masters.BranchRepository;
import com.lng.attendancecompanyservice.repositories.masters.CountryRepository;
import com.lng.attendancecompanyservice.repositories.masters.CustEmployeeRepository;
import com.lng.attendancecompanyservice.repositories.masters.EmployeeBranchRepositories;
import com.lng.attendancecompanyservice.repositories.masters.LoginDataRightRepository;
import com.lng.attendancecompanyservice.repositories.masters.LoginRepository;
import com.lng.attendancecompanyservice.repositories.masters.ShiftRepository;
import com.lng.attendancecompanyservice.repositories.masters.StateRepository;
import com.lng.attendancecompanyservice.service.masters.BranchService;
import com.lng.dto.customer.CustomerDto;
import com.lng.dto.masters.branch.BranchDto;
import com.lng.dto.masters.branch.BranchResponse;
import com.lng.dto.masters.country.CountryDto;
import com.lng.dto.masters.state.StateDto;

import status.Status;
@Service
public class BranchServiceImpl implements BranchService {


	ModelMapper modelMapper=new ModelMapper();
	@Autowired
	BranchRepository branchRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	StateRepository stateRepository;
	@Autowired
	CountryRepository countryRepository;
	@Autowired
	LoginRepository loginRepository;
	@Autowired
	LoginDataRightRepository loginDataRightRepository;
	@Autowired
	BlockRepository blockRepository;
	@Autowired
	CustEmployeeRepository custEmployeeRepository;
	@Autowired
	ShiftRepository shiftRepository;
	@Autowired
	EmployeeBranchRepositories employeeBranchRepositories;

	@Override
	public BranchResponse saveBranch(BranchDto branchDto) {
		BranchResponse response = new BranchResponse();

		// BranchDto branchDto2 = new BranchDto();

		try{
			if(branchDto.getBrName() == null || branchDto.getBrName().isEmpty()) throw new Exception("Please enter Branch name");
			int b = branchRepository.findByRefCustomerIdAndBrName(branchDto.getRefCustomerId(), branchDto.getBrName());
			if(b == 0) {
				Customer customer = customerRepository.findCustomerByCustId(branchDto.getRefCustomerId());
				if(customer != null) {
					Country country = countryRepository.findCountryByCountryId(branchDto.getRefCountryId());
					if(country != null) {

						State state = stateRepository.findStateByStateId(branchDto.getRefStateId());
						if(state != null) {
							String brnchCode = branchRepository.generateBranchForCustomer(customer.getCustId());
							Branch branch = new Branch();
							branch.setState(state);
							branch.setCustomer(customer);
							branch.setCountry(country);
							branch.setBrAddress(branchDto.getBrAddress());
							branch.setBrCity(branchDto.getBrCity());
							branch.setBrCode(customer.getCustCode() + brnchCode);
							branch.setBrCreatedDate(new Date());
							branch.setBrEmail(branchDto.getBrEmail());
							branch.setBrLatLong(branchDto.getBrLatLong());
							branch.setBrIsActive(true);
							branch.setBrIsBillable(branchDto.getBrIsBillable());
							branch.setBrLandline(branchDto.getBrLandline());
							branch.setBrMobile(branchDto.getBrMobile());
							branch.setBrName(branchDto.getBrName());
							branch.setBrPincode(branchDto.getBrPincode());
							branch.setBrValidityEnd(branchDto.getBrValidityEnd());
							branch.setBrValidityStart(branchDto.getBrValidityStart());
							branchRepository.save(branch);
							// Save to login data right table
							try {
								Login login = loginRepository.findByRefCustId(branch.getCustomer().getCustId());
								LoginDataRight loginDataRight = new LoginDataRight();
								loginDataRight.setBranch(branch);
								loginDataRight.setLogin(login);
								loginDataRightRepository.save(loginDataRight);
							}catch (Exception e) {
								e.printStackTrace();
							}

							response.status = new Status(false,200, "Successfully Created");
						}
						else{ 
							response.status = new Status(true,400, "State Not Found");
						}
					}
					else{ 
						response.status = new Status(true,400, "Country Not Found");
					}
				}
				else{ 
					response.status = new Status(true,400, "Customer Not Found");
				}
			}
			else{ 
				response.status = new Status(true,400,"BranchName already exist");
			}
		}catch(Exception ex){
			response.status = new Status(true,3000, ex.getMessage()); 
		}

		return response;
	}


	@Override
	public BranchResponse getAll() {
		BranchResponse response = new BranchResponse();
		try {
			List<Branch> branchList=branchRepository.findAllBranchByBrIsActive(true);
			response.setData1(branchList.stream().map(branch -> convertToBranchDto(branch)).collect(Collectors.toList()));
			response.status = new Status(false,200, "success");
		}catch(Exception e) {
			response.status = new Status(true,3000, e.getMessage()); 

		}
		return response;
	}

	@Override
	public Status updateBranchByBrId(BranchDto branchDto) {
		Status status = null;
		try {
			if(branchDto.getBrName() == null || branchDto.getBrName().isEmpty()) throw new Exception("Please enter Branch name");
			if(branchDto.getBrId() == null || branchDto.getBrId() == 0) throw new Exception("Branch id is null or zero");
			if(branchDto.getRefCustomerId() == null || branchDto.getRefCustomerId() == 0) throw new Exception("RefCoustomerId id is null or zero");
			if(branchDto.getRefCountryId() == null || branchDto.getRefCountryId() == 0) throw new Exception("RefCountryId id is null or zero");
			if(branchDto.getRefStateId() == null || branchDto.getRefStateId() == 0) throw new Exception("RefStateId id is null or zero");

			Branch branch = branchRepository.findBranchByBrId(branchDto.getBrId())	;	

			Customer customer = customerRepository.findCustomerByCustId(branchDto.getRefCustomerId());
			if(customer != null) {

				Country country = countryRepository.findCountryByCountryId(branchDto.getRefCountryId());
				if(country != null) {

					State state = stateRepository.findStateByStateId(branchDto.getRefStateId());
					if(state != null) {
						// int b = branchRepository.findByRefCustomerIdAndBrName(branchDto.getRefCustomerId(), branchDto.getBrName());
						Branch br = branchRepository.findBranchBybrNameAndCustomer_custId(branchDto.getBrName(), branchDto.getRefCustomerId());
						if(br == null) {
							branch = modelMapper.map(branchDto,Branch.class);
							branch.setCustomer(customer);
							branch.setCountry(country);
							branch.setState(state);
							branch.setBrIsActive(true);
							branch.setBrIsBillable(true);
							branch.setBrCreatedDate(new Date());
							branchRepository.save(branch);
							status = new Status(false, 200, "Updated successfully");
						} else if (br.getBrId() == branchDto.getBrId()) { 
							branch = modelMapper.map(branchDto,Branch.class);
							branch.setCustomer(customer);
							branch.setCountry(country);
							branch.setState(state);
							branch.setBrIsActive(true);
							branch.setBrIsBillable(true);
							branch.setBrCreatedDate(new Date());
							branchRepository.save(branch);
							status = new Status(false, 200, "Updated successfully");
						} else {
							status = new Status(true,400,"Branch Name already exist"); 
						}
					}

					else{ 
						status = new Status(true,400, "State Not Found");
					}
				}
				else{ 
					status = new Status(true,400, "Country Not Found");
				}
			}
			else{ 
				status = new Status(true,400, "Customer Not Found");
			}

		}catch(Exception e) {
			status = new Status(true, 4000, e.getMessage());
		}
		return status;
	}

	@Override
	public BranchResponse getBranchByBrId(int brId) {
		BranchResponse response = new BranchResponse();
		try {
			Branch branch=branchRepository.findBranchByBrId(brId);
			if(branch != null) {
				BranchDto branchDto = convertToBranchDto(branch);
				response.data = branchDto;
				response.status = new Status(false,200, "success");
			}
			else {
				response.status = new Status(true, 4000, "Not found");
			}
		}catch(Exception e) {
			response.status = new Status(true,3000, e.getMessage()); 

		}
		return response;
	}



	@Override
	public BranchResponse deleteByBrId(Integer brId) {
		BranchResponse response=new BranchResponse();
		Branch branch = new Branch();
		try {
			branch = branchRepository.findBranchByBrId(brId);
			if(branch != null) {
				
				// Check weather the branch is used in any transaction or no
				List<LoginDataRight> loginDataRight = loginDataRightRepository.findByBranch_BrId(brId);
				List<Block> block = blockRepository.findByBranch_BrId(brId);
				List<Employee> employee = custEmployeeRepository.findByBranch_BrId(brId);
				List<Shift> shift = shiftRepository.findByBranch_BrId(brId);
				List<EmployeeBranch> employeeBranch = employeeBranchRepositories.findByBranch_BrId(brId);
				
				if(loginDataRight.isEmpty() && block.isEmpty() && employee.isEmpty() && shift.isEmpty() && employeeBranch.isEmpty()) {
					branchRepository.delete(branch);
					response.status = new Status(false, 200, "Deleted successfully");
				}else {
					branch.setBrIsActive(false);
					branchRepository.save(branch);
					response.status = new Status(false, 200, "The record has been just disabled as it has been used in another transaction");
				}
			}else {
				response.status = new Status(true, 400, "Branch not found");
			}
		
		}catch(Exception e) {
			response.status = new Status(true,400, e.getMessage());
		}
		return response;
	}

	public BranchDto convertToBranchDto(Branch branch) {
		BranchDto branchDto = modelMapper.map(branch,BranchDto.class);
		branchDto.setBrId(branch.getBrId());
		branchDto.setRefCustomerId(branch.getCustomer().getCustId());
		branchDto.setRefCountryId(branch.getCountry().getCountryId());
		branchDto.setRefStateId(branch.getState().getStateId());
		branchDto.setCustName(branch.getCustomer().getCustName());
		branchDto.setStateName(branch.getState().getStateName());
		branchDto.setCountryName(branch.getCountry().getCountryName());
		CustomerDto customerDto = modelMapper.map(branch.getCustomer(),CustomerDto.class);
		StateDto stateDto = modelMapper.map(branch.getState(),StateDto.class);
		CountryDto countryDto = modelMapper.map(branch.getCountry(),CountryDto.class);
		return branchDto;
	}


	@Override
	public BranchResponse getAllByCustId(Integer custId) {
		BranchResponse response = new BranchResponse();
		try {
			List<Branch> branchList=branchRepository.findAllByCustomer_CustIdAndBrIsActive(custId, true);
			response.setData1(branchList.stream().map(branch -> convertToBranchDto(branch)).collect(Collectors.toList()));
			if(response != null && response.getData1() != null) {
				response.status = new Status(false,200, "success");
			}else {
				response.status = new Status(true,400, "Branch Not Found"); 
			}
			
		}catch(Exception e) {
			response.status = new Status(true,500, "Somenthing Went Wrong.."); 
		}
		return response;
	}
}