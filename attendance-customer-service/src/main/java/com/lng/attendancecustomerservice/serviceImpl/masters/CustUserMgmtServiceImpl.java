package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.authentication.Login;
import com.lng.attendancecustomerservice.entity.masters.Branch;
import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.entity.masters.EmployeeBlock;
import com.lng.attendancecustomerservice.entity.masters.LoginDataRight;
import com.lng.attendancecustomerservice.entity.masters.UserRight;
import com.lng.attendancecustomerservice.entity.userModule.Module;
import com.lng.attendancecustomerservice.repositories.authentication.ILoginRepository;
import com.lng.attendancecustomerservice.repositories.masters.BranchRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.repositories.masters.LoginDataRightRepository;
import com.lng.attendancecustomerservice.repositories.masters.UserRightRepository;
import com.lng.attendancecustomerservice.repositories.userModule.IModuleRepository;
import com.lng.attendancecustomerservice.service.masters.CustUserMgmtService;
import com.lng.attendancecustomerservice.utils.Encoder;
import com.lng.attendancecustomerservice.utils.MessageUtil;
import com.lng.dto.masters.custEmployee.CustEmployeeDtoTwo;
import com.lng.dto.masters.custUserMgmt.CustLoginDataRightResponseDto;
import com.lng.dto.masters.custUserMgmt.CustLoginDto;
import com.lng.dto.masters.custUserMgmt.CustUserBranchDto;
import com.lng.dto.masters.custUserMgmt.CustUserBranchLoginMapDto;
import com.lng.dto.masters.custUserMgmt.CustUserBranchesDto;
import com.lng.dto.masters.custUserMgmt.CustUserLoginDto;
import com.lng.dto.masters.custUserMgmt.CustUserLoginModuleBranchMapResponseDto;
import com.lng.dto.masters.custUserMgmt.CustUserMgmtDto;
import com.lng.dto.masters.custUserMgmt.CustUserModuleDto;
import com.lng.dto.masters.custUserMgmt.CustUserModuleMapDto;
import com.lng.dto.masters.custUserMgmt.CustUserModulesDto;
import com.lng.dto.masters.custUserMgmt.CustUserRightResponseDto;
import com.lng.dto.masters.custUserMgmt.UserModuleDto;

import status.Status;

@Service
public class CustUserMgmtServiceImpl implements CustUserMgmtService {

	@Autowired
	ILoginRepository iLoginRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	IModuleRepository iModuleRepository;

	@Autowired
	BranchRepository branchRepository;

	@Autowired
	UserRightRepository userRightRepository;

	@Autowired
	LoginDataRightRepository loginDataRightRepository;

	MessageUtil messageUtil = new MessageUtil();

	ModelMapper modelMapper = new ModelMapper();

	Encoder encoder = new Encoder();

	@Override
	public Status save(CustUserMgmtDto custUserMgmtDto) {
		Status status = null;

		Customer customer = customerRepository.findCustomerByCustId(custUserMgmtDto.getCustomerId());

		try {
			if(customer != null) {

				String userName = custUserMgmtDto.getUserName();
				String custCode = customer.getCustCode();

				String loginUserName = userName+"@"+custCode;

				Login login1 = iLoginRepository.findByLoginNameAndRefCustId(loginUserName, custUserMgmtDto.getCustomerId());
				Login login2 = iLoginRepository.findByLoginMobileAndRefCustId(custUserMgmtDto.getuMobileNumber(), custUserMgmtDto.getCustomerId());

				if(login1 == null) {
					if(login2 == null) {

						String newPassword = iLoginRepository.generatePassword();
						Login login = new Login();
						login.setLoginName(loginUserName);
						login.setLoginMobile(custUserMgmtDto.getuMobileNumber());
						login.setLoginPassword(encoder.getEncoder().encode(newPassword));
						login.setLoginCreatedDate(new Date());
						login.setLoginIsActive(true);
						login.setRefCustId(customer.getCustId());
						if(custUserMgmtDto.getEmpId() != null) {
							login.setRefEmpId(custUserMgmtDto.getEmpId());
						}else {
							login.setRefEmpId(0);
						}

						iLoginRepository.save(login);

						String mobileNo = login.getLoginMobile();
						String mobileSmS = "User Id has been successfully created to access the Attendance System Web application."
								+ "The login details are User Id: "+loginUserName+" and Password is : "+ newPassword;	
						String s = messageUtil.sms(mobileNo, mobileSmS);

						status = new Status(false, 200, "successfully created");

					}else {
						status = new Status(true, 400, "Mobile number already exist");
					}
				}else {
					status = new Status(true, 400, "User name already exist");
				}
			} else {
				status = new Status(true, 400, "Customer is not exist");
			}

		} catch (Exception e) {
			status = new Status(true, 400, "Oops..! Something went wrong..");
		}
		return status;
	}


	@Override
	public Status updateUserDetails(CustUserMgmtDto custUserMgmtDto) {
		Status status = null;
		try {
			Customer customer = customerRepository.findCustomerByCustId(custUserMgmtDto.getCustomerId());
			Login login = iLoginRepository.findByLoginId(custUserMgmtDto.getLoginId());

			if(customer != null) {

				String userName = custUserMgmtDto.getUserName();
				String custCode = customer.getCustCode();

				String loginUserName = userName+"@"+custCode;

				Login login1 = iLoginRepository.findByLoginNameAndRefCustId(loginUserName, custUserMgmtDto.getCustomerId());
				Login login2 = iLoginRepository.findByLoginMobileAndRefCustId(custUserMgmtDto.getuMobileNumber(), custUserMgmtDto.getCustomerId());
				if(login != null) {
					if(login1 == null ||(login.getLoginId() == custUserMgmtDto.getLoginId() && login.getLoginName().equals(loginUserName))) {
						if(login2 == null || (login.getLoginId() == custUserMgmtDto.getLoginId() && login.getLoginMobile().equals(custUserMgmtDto.getuMobileNumber()))) {

							String newPassword = iLoginRepository.generatePassword();
							//Login login = new Login();
							login.setLoginName(loginUserName);
							login.setLoginMobile(custUserMgmtDto.getuMobileNumber());
							// login.setLoginPassword(encoder.getEncoder().encode(newPassword));
							login.setLoginCreatedDate(new Date());
							login.setLoginIsActive(true);
							login.setRefCustId(customer.getCustId());
							iLoginRepository.save(login);

							/*String mobileNo = login.getLoginMobile();
							String mobileSmS = "User Id has been successfully created to access the Attendance System Web application."
									+ "The login details are User Id: "+loginUserName+" and Password is : "+ newPassword;	
							String s = messageUtil.sms(mobileNo, mobileSmS);*/

							status = new Status(false, 200, "successfully updated");

						}else {
							status = new Status(true, 400, "Mobile number already exist");
						}
					}else {
						status = new Status(true, 400, "User name already exist");
					}
				} else {
					status = new Status(true, 400, "Login id not found");
				}

			} else {
				status = new Status(true, 400, "Customer is not exist");
			}

		} catch (Exception e) {
			status = new Status(true, 400, "Oops..! Something went wrong..");
		}
		return status;
	}

	@Override
	public CustUserRightResponseDto getAssignedAndUnAssignedUserRights(Integer loginId, Integer custId) {
		CustUserRightResponseDto custUserRightResponseDto = new CustUserRightResponseDto();
		List<UserModuleDto> userModuleDtos1 = new ArrayList<>();
		List<UserModuleDto> userModuleDtos2 = new ArrayList<>();
		List<UserModuleDto> userModuleDtos3 = new ArrayList<>();
		try {

			List<Object[]> assignedModules = iModuleRepository.getAssignedModuleByLogin_LoginIdAndCustomer_CustId(loginId, custId);

			for(Object[] p: assignedModules) {
				UserModuleDto userModuleDto = new UserModuleDto();
				userModuleDto.setModuleId(Integer.valueOf(p[0].toString()));
				userModuleDto.setModuleName(p[1].toString());
				userModuleDto.setModuleURL(p[2].toString());
				userModuleDto.setParentId(Integer.valueOf(p[3].toString()));
				userModuleDto.setUserRightId(Integer.valueOf(p[4].toString()));

				userModuleDtos1.add(userModuleDto);
				custUserRightResponseDto.setAssignedModules(userModuleDtos1);
			}

			List<Object[]> unAssignedModules = iModuleRepository.getUnAssignedModuleByLogin_LoginId(loginId);
			for(Object[] p: unAssignedModules) {
				UserModuleDto userModuleDto = new UserModuleDto();
				userModuleDto.setModuleId(Integer.valueOf(p[0].toString()));
				userModuleDto.setModuleName(p[1].toString());
				userModuleDto.setModuleURL(p[2].toString());
				userModuleDto.setParentId(Integer.valueOf(p[3].toString()));
				userModuleDto.setUserRightId(0);

				userModuleDtos2.add(userModuleDto);
				custUserRightResponseDto.setUnAssignedModules(userModuleDtos2);
			}

			List<Object[]> allModules = iModuleRepository.getAllModules();

			for(Object[] p: allModules) {
				UserModuleDto userModuleDto = new UserModuleDto();
				userModuleDto.setModuleId(Integer.valueOf(p[0].toString()));
				userModuleDto.setModuleName(p[1].toString());
				userModuleDto.setModuleURL(p[2].toString());
				userModuleDto.setParentId(Integer.valueOf(p[3].toString()));
				userModuleDto.setUserRightId(0);

				userModuleDtos3.add(userModuleDto);
				custUserRightResponseDto.setAllModules(userModuleDtos3);
			}

			if(assignedModules.isEmpty()) {
				custUserRightResponseDto.status = new Status(false, 200, "Modules are not assigned to this customer");

			} else if(unAssignedModules.isEmpty()) {
				custUserRightResponseDto.status = new Status(false, 200, "All modules are assigned");
			} else {
				custUserRightResponseDto.status = new Status(false, 200, "Success");
			}

		} catch (Exception e) {
			custUserRightResponseDto.status = new Status(true, 500, "Opps..! Sometging went wrong..");

		}
		return custUserRightResponseDto;
	}

	@Override
	public CustLoginDataRightResponseDto getAssignedAndUnAssignedLoginDataRights(Integer loginId, Integer custId) {
		CustLoginDataRightResponseDto custLoginDataRightResponseDto = new CustLoginDataRightResponseDto();
		try {

			List<Branch> assignedDataRights = branchRepository.getAssignedDataRights(loginId, custId);
			custLoginDataRightResponseDto.setAssignedBranch(assignedDataRights.stream().map(branch -> convertToCustUserBranchDto(branch)).collect(Collectors.toList()));

			List<Branch> unAssignedDataRights = branchRepository.getUnAssignedDataRights(loginId, custId);
			custLoginDataRightResponseDto.setUnAssignedBranch(unAssignedDataRights.stream().map(branch -> convertToCustUserBranchDto(branch)).collect(Collectors.toList()));

			if(assignedDataRights.isEmpty()) {
				custLoginDataRightResponseDto.status = new Status(false, 200, "There is no branches of this customer assigned in Login Data Right");

			} else if(unAssignedDataRights.isEmpty()) {
				custLoginDataRightResponseDto.status = new Status(false, 200, "All Branches are assigned in Login Data Right");
			} else {
				custLoginDataRightResponseDto.status = new Status(false, 200, "Success");
			}

		} catch (Exception e) {
			custLoginDataRightResponseDto.status = new Status(true, 500, "Opps..! Sometging went wrong..");
		}
		return custLoginDataRightResponseDto;
	}

	@Override
	public Status addModules(CustUserModuleMapDto custUserModuleMapDto) {
		Status status = null;
		try {

			for(CustUserModuleDto custUserModuleDto : custUserModuleMapDto.getModuleIds()) {
				UserRight userRight = new UserRight();
				userRight.setRefLoginId(custUserModuleMapDto.getLoginId());
				userRight.setRefModuleId(custUserModuleDto.getModuleId());
				userRightRepository.save(userRight);
				status = new Status(false, 200, "Modules added successfully");
			}

		} catch (Exception e) {
			status = new Status(true, 400, "Opps...! Something went wrong..");
		}
		return status;
	}

	@Override
	public Status addBranchLoginDataRight(CustUserBranchLoginMapDto custUserBranchLoginMapDto) {
		Status status = null;
		try {
			for(CustUserBranchDto custUserBranchDto : custUserBranchLoginMapDto.getBranchIds()) {
				LoginDataRight loginDataRight = new LoginDataRight();
				loginDataRight.setRefLoginId(custUserBranchLoginMapDto.getLoginId());
				loginDataRight.setRefBrId(custUserBranchDto.getBrId());
				loginDataRightRepository.save(loginDataRight);
				status = new Status(false, 200, "Branches successfully added");
			}
		} catch (Exception e) {
			status = new Status(true, 400, "Opps...! Something went wrong..");
		}
		return status;
	}

	@Override
	public Status updateModules(CustUserModuleMapDto custUserModuleMapDto) {
		Status status = null;
		try {

			List<UserRight> alreadyMapped = userRightRepository.getByRefLoginId(custUserModuleMapDto.getLoginId());

			List<CustUserModuleDto> nonNullUserRightIds = custUserModuleMapDto.getModuleIds().stream().filter(e -> e.getUserRightId() != null).collect(Collectors.toList()); 

			List<UserRight> removed = alreadyMapped.stream().filter(o1 -> nonNullUserRightIds.stream().noneMatch(o2 -> o2.getUserRightId().equals(o1.getUserRightId())))
					.collect(Collectors.toList());

			for(UserRight CustUserRightDto : removed) {
				UserRight userRight3 = userRightRepository.findByUserRightId(CustUserRightDto.getUserRightId());
				userRightRepository.delete(userRight3);
			}

			for(CustUserModuleDto custUserModuleDto : custUserModuleMapDto.getModuleIds()) {
				
				if(custUserModuleDto.getUserRightId() == null && custUserModuleDto.getModuleId() != null) {
					UserRight userRight2 = new UserRight();
					userRight2.setRefLoginId(custUserModuleMapDto.getLoginId());
					userRight2.setRefModuleId(custUserModuleDto.getModuleId());
					userRightRepository.save(userRight2);
				}

				Module module = iModuleRepository.findByModuleId(custUserModuleDto.getModuleId());
				List<UserRight> userRights = userRightRepository.findByRefLoginId(custUserModuleMapDto.getLoginId());
				//UserRight userRight2 = userRightRepository.findByRefModuleId(custUserModuleDto.getModuleId());
				for(UserRight userRight: userRights) {
					if(custUserModuleDto.getUserRightId() != null && custUserModuleDto.getUserRightId().equals(userRight.getUserRightId())) {
						userRight.setRefModuleId(module.getModuleId());
						userRightRepository.save(userRight);
					} 
					/*
					 * else if(module != null && custUserModuleDto.getUserRightId() == null){
					 * 
					 * UserRight userRight2 = new UserRight();
					 * userRight2.setRefLoginId(custUserModuleMapDto.getLoginId());
					 * userRight2.setRefModuleId(custUserModuleDto.getModuleId());
					 * userRightRepository.save(userRight2);
					 * 
					 * }
					 */
					else if(module == null){
						userRightRepository.delete(userRight);
					}
				}
			}
			status = new Status(false, 200, "Modules successfully updated");

		} catch (Exception e) {
			status = new Status(true, 400, "Opps...! Something went wrong..");
		}
		return status;
	}

	@Override
	public Status updateBranchLoginDataRight(CustUserBranchLoginMapDto custUserBranchLoginMapDto) {
		Status status = null;
		try {
			List<LoginDataRight> alreadyMapped = loginDataRightRepository.getByRefLoginId(custUserBranchLoginMapDto.getLoginId());

			List<CustUserBranchDto> nonNullUserRightIds = custUserBranchLoginMapDto.getBranchIds().stream().filter(e -> e.getLoginDataRightId() != null).collect(Collectors.toList()); 

			List<LoginDataRight> removed = alreadyMapped.stream().filter(o1 -> nonNullUserRightIds.stream().noneMatch(o2 -> o2.getLoginDataRightId().equals(o1.getLoginDataRightId())))
					.collect(Collectors.toList());

			for(LoginDataRight custUserBranchDto : removed) {
				LoginDataRight loginDataRight = loginDataRightRepository.findByLoginDataRightId(custUserBranchDto.getLoginDataRightId());
				loginDataRightRepository.delete(loginDataRight);
			}

			for(CustUserBranchDto custUserBranchDto : custUserBranchLoginMapDto.getBranchIds()) {

				Branch branch = branchRepository.findBranchByBrId(custUserBranchDto.getBrId());
				LoginDataRight loginDataRight = loginDataRightRepository.findByRefLoginId(custUserBranchLoginMapDto.getLoginId());
				//UserRight userRight2 = userRightRepository.findByRefModuleId(custUserModuleDto.getModuleId());

				if(branch != null && custUserBranchDto.getLoginDataRightId() != null) {
					loginDataRight.setRefBrId(branch.getBrId());
					loginDataRightRepository.save(loginDataRight);

				} else if(branch != null && custUserBranchDto.getLoginDataRightId() == null){
					LoginDataRight loginDataRight2 = new LoginDataRight();
					loginDataRight2.setRefLoginId(custUserBranchLoginMapDto.getLoginId());
					loginDataRight2.setRefBrId(custUserBranchDto.getBrId());
					loginDataRightRepository.save(loginDataRight2);

				} else if(branch == null){
					loginDataRightRepository.delete(loginDataRight);
				}

				status = new Status(false, 200, "Branches successfully updated");

			}

		} catch (Exception e) {
			status = new Status(true, 400, "Opps...! Something went wrong..");

		}
		return status;
	}

	@Override
	public CustUserLoginModuleBranchMapResponseDto getAllByCustId(Integer custId) {
		CustUserLoginModuleBranchMapResponseDto custUserLoginModuleBranchMapResponseDto = new CustUserLoginModuleBranchMapResponseDto();

		List<CustUserLoginDto> custUserLoginDtoList = new ArrayList<>();

		try {

			List<Object[]> loginList = iLoginRepository.findByCustId(custId);
			for(Object[] p : loginList) {
				CustUserLoginDto custUserLoginDto = new CustUserLoginDto();

				List<CustUserModulesDto> custUserModulesDtoList = new ArrayList<>();
				List<CustUserBranchesDto> custUserBranchesDtolist = new ArrayList<>();

				custUserLoginDto.setLoginId(Integer.valueOf(p[0].toString()));
				custUserLoginDto.setLoginName(p[1].toString());
				custUserLoginDto.setLoginMobile(p[2].toString());
				custUserLoginDto.setCustId(Integer.valueOf(p[3].toString()));
				custUserLoginDto.setCustName(p[4].toString());
				custUserLoginDto.setCustCode(p[5].toString());

				custUserLoginDtoList.add(custUserLoginDto);
				custUserLoginModuleBranchMapResponseDto.setLoginDetails(custUserLoginDtoList);

				List<Object[]> moduleList = iModuleRepository.findByCustomer_CustIdAndLogin_LoginId(custId, custUserLoginDto.getLoginId());
				// custUserLoginModuleBranchMapResponseDto.setModules(moduleList.stream().map(module -> convertToCustUserModulesDto(module)).collect(Collectors.toList()));
				for(Object[] m : moduleList) {
					if(custUserLoginDto.getLoginId() == Integer.valueOf(m[0].toString())) {
						CustUserModulesDto custUserModulesDto = new CustUserModulesDto();

						custUserModulesDto.setLoginId(Integer.valueOf(m[0].toString()));
						custUserModulesDto.setModuleId(Integer.valueOf(m[1].toString()));
						custUserModulesDto.setModuleName(m[2].toString());

						custUserModulesDtoList.add(custUserModulesDto);

					}
				}	
				custUserLoginDto.setModules(custUserModulesDtoList);

				List<Object[]> branchList = branchRepository.findByCustId(custId, custUserLoginDto.getLoginId());
				//custUserLoginModuleBranchMapResponseDto.setBranches(branchList.stream().map(branch -> convertToCustUserBranchesDto(branch)).collect(Collectors.toList()));
				for(Object[] b : branchList) {
					if(custUserLoginDto.getLoginId() == Integer.valueOf(b[0].toString())) {
						CustUserBranchesDto custUserBranchesDto = new CustUserBranchesDto();

						custUserBranchesDto.setLoginId(Integer.valueOf(b[0].toString()));
						custUserBranchesDto.setBrId(Integer.valueOf(b[1].toString()));
						custUserBranchesDto.setBrName(b[2].toString());

						custUserBranchesDtolist.add(custUserBranchesDto);
					}
				}
				custUserLoginDto.setBranches(custUserBranchesDtolist);

			}

			if(loginList.isEmpty()) {
				custUserLoginModuleBranchMapResponseDto.status = new Status(false, 200, "Success and there is no login details exist for this customer");
			}else {
				custUserLoginModuleBranchMapResponseDto.status = new Status(false, 200, "Success");
			}

		} catch (Exception e) {
			custUserLoginModuleBranchMapResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return custUserLoginModuleBranchMapResponseDto;
	}


	@Override
	public CustUserLoginModuleBranchMapResponseDto findAll() {
		CustUserLoginModuleBranchMapResponseDto custUserLoginModuleBranchMapResponseDto = new CustUserLoginModuleBranchMapResponseDto();

		List<CustUserLoginDto> custUserLoginDtoList = new ArrayList<>();

		try {

			List<Object[]> loginList = iLoginRepository.findAllDetails();
			for(Object[] p : loginList) {
				CustUserLoginDto custUserLoginDto = new CustUserLoginDto();

				List<CustUserModulesDto> custUserModulesDtoList = new ArrayList<>();
				List<CustUserBranchesDto> custUserBranchesDtolist = new ArrayList<>();

				custUserLoginDto.setLoginId(Integer.valueOf(p[0].toString()));
				custUserLoginDto.setLoginName(p[1].toString());
				custUserLoginDto.setLoginMobile(p[2].toString());
				custUserLoginDto.setCustId(Integer.valueOf(p[3].toString()));
				custUserLoginDto.setCustName(p[4].toString());
				custUserLoginDto.setCustCode(p[5].toString());

				custUserLoginDtoList.add(custUserLoginDto);
				custUserLoginModuleBranchMapResponseDto.setLoginDetails(custUserLoginDtoList);

				List<Object[]> moduleList = iModuleRepository.findAllModules(custUserLoginDto.getLoginId());
				// custUserLoginModuleBranchMapResponseDto.setModules(moduleList.stream().map(module -> convertToCustUserModulesDto(module)).collect(Collectors.toList()));

				for(Object[] m : moduleList ) {			
					if(custUserLoginDto.getLoginId() == Integer.valueOf(m[0].toString())) {
						CustUserModulesDto custUserModulesDto = new CustUserModulesDto();

						custUserModulesDto.setLoginId(Integer.valueOf(m[0].toString()));
						custUserModulesDto.setModuleId(Integer.valueOf(m[1].toString()));
						custUserModulesDto.setModuleName(m[2].toString());
						custUserModulesDtoList.add(custUserModulesDto);
					}
				}
				custUserLoginDto.setModules(custUserModulesDtoList);

				List<Object[]> branchList = branchRepository.findAllBranches(custUserLoginDto.getLoginId());
				//custUserLoginModuleBranchMapResponseDto.setBranches(branchList.stream().map(branch -> convertToCustUserBranchesDto(branch)).collect(Collectors.toList()));
				for(Object[] b : branchList) {
					if(custUserLoginDto.getLoginId() == Integer.valueOf(b[0].toString())) {
						CustUserBranchesDto custUserBranchesDto = new CustUserBranchesDto();

						custUserBranchesDto.setLoginId(Integer.valueOf(b[0].toString()));
						custUserBranchesDto.setBrId(Integer.valueOf(b[1].toString()));
						custUserBranchesDto.setBrName(b[2].toString());

						custUserBranchesDtolist.add(custUserBranchesDto);
					}
				}
				custUserLoginDto.setBranches(custUserBranchesDtolist);
			}

			if(loginList.isEmpty()) {
				custUserLoginModuleBranchMapResponseDto.status = new Status(false, 200, "Success and there is no login details exist");
			}else {
				custUserLoginModuleBranchMapResponseDto.status = new Status(false, 200, "Success");
			}

		} catch (Exception e) {
			custUserLoginModuleBranchMapResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return custUserLoginModuleBranchMapResponseDto;
	}


	@Override
	public CustLoginDto getLoginDetailsByLoginId(Integer loginId) {
		CustLoginDto custLoginDto = new CustLoginDto();

		try {
			List<Object[]> loginDetails = iLoginRepository.findLogindDetailsByLoginId(loginId);
			if(loginDetails != null) {
				for(Object[] p: loginDetails) {

					custLoginDto.setLoginId(Integer.valueOf(p[0].toString()));
					custLoginDto.setLoginName(p[1].toString());
					custLoginDto.setLoginMobile(p[2].toString());
					custLoginDto.setCustId(Integer.valueOf(p[3].toString()));
					custLoginDto.setCustName(p[4].toString());
					custLoginDto.setCustCode(p[5].toString());
				}
				custLoginDto.status = new Status(false, 200, "Success");
			} else {
				custLoginDto.status = new Status(true, 400, "Not found");
			}

		} catch (Exception e) {
			custLoginDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return custLoginDto;
	}



	@Override
	public Status deleteByLoginId(Integer loginId) {
		Status status = null;
		try {
			Login login = iLoginRepository.findByLoginId(loginId);
			if(login != null) {
				login.setLoginIsActive(false);
				iLoginRepository.save(login);
				status = new Status(false, 200, "successfully deleted");
			}else {
				status = new Status(true, 400, "Login id not found");
			}
		} catch (Exception e) {
			status = new Status(true, 400, "Oops..! Something went wrong..");
		}
		return status;
	}

	@Override
	public Status resetPasswordByLoginId(Integer loginId) {
		Status status = null;
		try {
			Login login = iLoginRepository.findByLoginId(loginId);
			if(login != null) {
				String newPassword = iLoginRepository.generatePassword();
				login.setLoginPassword(encoder.getEncoder().encode(newPassword));
				iLoginRepository.save(login);

				String mobileNo = login.getLoginMobile();
				String mobileSmS = "Password to access the Attendance System Web Application has been reset to : "+ newPassword;	
				String s = messageUtil.sms(mobileNo, mobileSmS);

				status = new Status(false, 200, "Password reset and sent successfully");
			} else {
				status = new Status(true, 400, "Login id not found");
			}

		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}





	public UserModuleDto convertToUserModuleDto(Module module) {
		UserModuleDto  userModuleDto = modelMapper.map(module, UserModuleDto.class);
		return userModuleDto;
	}

	public CustUserBranchDto convertToCustUserBranchDto(Branch branch) {
		CustUserBranchDto  custUserBranchDto = modelMapper.map(branch, CustUserBranchDto.class);
		return custUserBranchDto;
	}

	public CustUserLoginDto convertToCustUserLoginDto(Login login) {
		CustUserLoginDto  custUserLoginDto = modelMapper.map(login, CustUserLoginDto.class);
		custUserLoginDto.setCustId(login.getRefCustId());
		return custUserLoginDto;
	}





	/*public CustUserModulesDto convertToCustUserModulesDto(Module module) {
		CustUserModulesDto  custUserModulesDto = modelMapper.map(module, CustUserModulesDto.class);
		UserRight userRight = userRightRepository.findByRefModuleId(module.getModuleId());
		custUserModulesDto.setLoginId(userRight.getRefLoginId());
		return custUserModulesDto;
	}

	public CustUserBranchesDto convertToCustUserBranchesDto(Branch branch) {
		CustUserBranchesDto  custUserBranchesDto = modelMapper.map(branch, CustUserBranchesDto.class);
		Login login = iLoginRepository.getByRefCustId(branch.getCustomer().getCustId());
		custUserBranchesDto.setLoginId(login.getLoginId());
		return custUserBranchesDto;
	}*/

}
