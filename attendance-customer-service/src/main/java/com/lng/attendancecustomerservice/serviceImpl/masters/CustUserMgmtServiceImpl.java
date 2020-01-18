package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.authentication.Login;
import com.lng.attendancecustomerservice.entity.masters.Branch;
import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.masters.EmployeeBlock;
import com.lng.attendancecustomerservice.entity.masters.LoginDataRight;
import com.lng.attendancecustomerservice.entity.masters.UserRight;
import com.lng.attendancecustomerservice.entity.userModule.Module;
import com.lng.attendancecustomerservice.repositories.authentication.ILoginRepository;
import com.lng.attendancecustomerservice.repositories.masters.BranchRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustEmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.repositories.masters.LoginDataRightRepository;
import com.lng.attendancecustomerservice.repositories.masters.UserRightRepository;
import com.lng.attendancecustomerservice.repositories.userModule.IModuleRepository;
import com.lng.attendancecustomerservice.service.masters.CustUserMgmtService;
import com.lng.attendancecustomerservice.utils.Encoder;
import com.lng.attendancecustomerservice.utils.MessageUtil;
import com.lng.dto.masters.custEmployee.CustEmployeeDtoTwo;
import com.lng.dto.masters.custUserMgmt.CustEmployeeDto;
import com.lng.dto.masters.custUserMgmt.CustEmployeeResponseDto;
import com.lng.dto.masters.custUserMgmt.CustLoginDataRightResponseDto;
import com.lng.dto.masters.custUserMgmt.CustLoginDto;
import com.lng.dto.masters.custUserMgmt.CustUserBranchDto;
import com.lng.dto.masters.custUserMgmt.CustUserBranchLoginMapDto;
import com.lng.dto.masters.custUserMgmt.CustUserBranchResDto;
import com.lng.dto.masters.custUserMgmt.CustUserBranchesDto;
import com.lng.dto.masters.custUserMgmt.CustUserLoginDto;
import com.lng.dto.masters.custUserMgmt.CustUserLoginModuleBranchDto;
import com.lng.dto.masters.custUserMgmt.CustUserLoginModuleBranchMapResponseDto;
import com.lng.dto.masters.custUserMgmt.CustUserMgmtDto;
import com.lng.dto.masters.custUserMgmt.CustUserModuleDto;
import com.lng.dto.masters.custUserMgmt.CustUserModuleMapDto;
import com.lng.dto.masters.custUserMgmt.CustUserModulesDto;
import com.lng.dto.masters.custUserMgmt.CustUserResponseDto;
import com.lng.dto.masters.custUserMgmt.CustUserRightResponseDto;
import com.lng.dto.masters.custUserMgmt.UserModuleDto;
import com.lng.dto.masters.custUserMgmt.UserModuleResDto;
import com.lng.dto.masters.custUserMgmt.UserModuleResponseDto;

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
	CustEmployeeRepository custEmployeeRepository;

	@Autowired
	LoginDataRightRepository loginDataRightRepository;


	MessageUtil messageUtil = new MessageUtil();

	ModelMapper modelMapper = new ModelMapper();

	Encoder encoder = new Encoder();


	@Override
	public CustUserResponseDto save(CustUserMgmtDto custUserMgmtDto) {
		CustUserResponseDto custUserResponseDto = new CustUserResponseDto();

		Customer customer = customerRepository.findCustomerByCustId(custUserMgmtDto.getCustomerId());
		// Login login3 = iLoginRepository.findByLoginMobileAndRefCustId(custUserMgmtDto.getuMobileNumber(), custUserMgmtDto.getCustomerId());
		List<Login> loginData = iLoginRepository.findAllByLoginIsActiveAndRefEmpId(custUserMgmtDto.getEmpId());
		try {
			if(customer != null) {

				if(loginData.isEmpty() || custUserMgmtDto.getEmpId() == 0) {

					/*if(login3 != null && customer.getCustMobile().equals(login3.getLoginMobile())) {
						login3.setRefEmpId(custUserMgmtDto.getEmpId());
						iLoginRepository.save(login3);
						custUserResponseDto.status = new Status(false, 200, "Admin has been linked to the selected employee");
						custUserResponseDto.setLoginId(login3.getLoginId());
				} else {*/
					String userName = custUserMgmtDto.getUserName();
					String custCode = customer.getCustCode();

					String loginUserName = userName+"@"+custCode;

					Login login1 = iLoginRepository.findByLoginNameAndRefCustId(loginUserName, custUserMgmtDto.getCustomerId());
					//	Login login2 = iLoginRepository.findByLoginMobileAndRefCustId(custUserMgmtDto.getuMobileNumber(), custUserMgmtDto.getCustomerId());
					Employee employee = custEmployeeRepository.findEmployeeByEmpIdAndEmpInService(custUserMgmtDto.getEmpId(), true);
					if(login1 == null) {
						// if(login2 == null) {
						if(employee != null) {


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

							String mobileNo = employee.getEmpMobile();
							String mobileSmS = "User Id has been successfully created to access the Attendance System Web application."
									+ "The login details are User Id: "+loginUserName+" and Password is : "+ newPassword;	
							String s = messageUtil.sms(mobileNo, mobileSmS);

							custUserResponseDto.status = new Status(false, 200, "created");
							custUserResponseDto.setLoginId(login.getLoginId());
						}else {
						}
						/*}else {
								custUserResponseDto.status = new Status(true, 400, "Mobile number already exist");
							}*/
					}else {
						custUserResponseDto.status = new Status(true, 400, "User name already exist");
					}
				} else {
					custUserResponseDto.status = new Status(true, 400, "The user has already been created for the selected Employee");
				}


			}else {
				custUserResponseDto.status = new Status(true, 400, "Customer is not exist");
			}
		} catch (Exception e) {

			custUserResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return custUserResponseDto;
	}

	/*@Override
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
	}*/


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

							status = new Status(false, 200, "updated");

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
				status = new Status(false, 200, "Modules added");
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
				status = new Status(false, 200, "Branches added");
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
			status = new Status(false, 200, "Modules updated");

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

				status = new Status(false, 200, "Branches updated");

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
				status = new Status(false, 200, "deleted");
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

				status = new Status(false, 200, "Password reset and sent");
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

	@Override
	public CustEmployeeResponseDto getEmployeeByCustId(Integer custId) {
		CustEmployeeResponseDto custEmployeeResponseDto = new CustEmployeeResponseDto();
		try {
			List<Employee> empList = custEmployeeRepository.findByCustomer_CustId(custId);
			if(!empList.isEmpty()) {
				custEmployeeResponseDto.setEmpData(empList.stream().map(employee -> convertToUserCustEmployeeDto(employee)).collect(Collectors.toList()));
				custEmployeeResponseDto.status = new Status(false, 200, "Success");
			} else {
				custEmployeeResponseDto.status = new Status(false, 400, "No records found");
			}
		} catch (Exception e) {
			custEmployeeResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return custEmployeeResponseDto;
	}


	public CustEmployeeDto convertToUserCustEmployeeDto(Employee employee) {
		CustEmployeeDto  custEmployeeDto = modelMapper.map(employee, CustEmployeeDto.class);
		return custEmployeeDto;
	}


	@Override
	@Transactional(rollbackOn={Exception.class})
	public Status saveAllDetails(CustUserLoginModuleBranchDto custUserLoginModuleBranchDto) {
		Status status = null;
		String mobileNo = null;
		Customer customer = customerRepository.findCustomerByCustId(custUserLoginModuleBranchDto.getUserDetails().getCustomerId());

		List<Login> loginData = iLoginRepository.findAllByLoginIsActiveAndRefEmpId(custUserLoginModuleBranchDto.getUserDetails().getEmpId());
		try {
			if(customer != null) {

				if(loginData.isEmpty() || custUserLoginModuleBranchDto.getUserDetails().getEmpId() == 0) {

					String userName = custUserLoginModuleBranchDto.getUserDetails().getUserName();
					String custCode = customer.getCustCode();

					String loginUserName = userName+"@"+custCode;

					Login login1 = iLoginRepository.findByLoginNameAndRefCustId(loginUserName, custUserLoginModuleBranchDto.getUserDetails().getCustomerId());


					if(login1 == null) {




						String newPassword = iLoginRepository.generatePassword();
						Login login = new Login();
						login.setLoginName(loginUserName);
						login.setLoginMobile(custUserLoginModuleBranchDto.getUserDetails().getuMobileNumber());
						login.setLoginPassword(encoder.getEncoder().encode(newPassword));
						login.setLoginCreatedDate(new Date());
						login.setLoginIsActive(true);
						login.setRefCustId(customer.getCustId());
						if(custUserLoginModuleBranchDto.getUserDetails().getEmpId() != null) {
							login.setRefEmpId(custUserLoginModuleBranchDto.getUserDetails().getEmpId());
						}else {
							login.setRefEmpId(0);
						}

						iLoginRepository.save(login);

						if(login.getLoginMobile() == null) {
							Employee employee = custEmployeeRepository.findEmployeeByEmpIdAndEmpInService(custUserLoginModuleBranchDto.getUserDetails().getEmpId(), true);
							if(employee != null) {
								mobileNo = employee.getEmpMobile();
							}else {
								status = new Status(true, 400, "Employee not found");
							}
						} else {
							mobileNo = login.getLoginMobile();
						}

						String mobileSmS = "User Id has been successfully created to access the Attendance System Web application."
								+ "The login details are User Id: "+loginUserName+" and Password is : "+ newPassword;	
						String s = messageUtil.sms(mobileNo, mobileSmS);

						status = new Status(false, 200, "created");
						try {
							if(!custUserLoginModuleBranchDto.getModules().isEmpty()) {
								for(CustUserModuleDto custUserModuleDto : custUserLoginModuleBranchDto.getModules()) {
									UserRight userRight = new UserRight();
									userRight.setRefLoginId(login.getLoginId());
									userRight.setRefModuleId(custUserModuleDto.getModuleId());
									userRightRepository.save(userRight);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							if(!custUserLoginModuleBranchDto.getBranches().isEmpty()) {
								for(CustUserBranchDto custUserBranchDto : custUserLoginModuleBranchDto.getBranches()) {
									LoginDataRight loginDataRight = new LoginDataRight();
									loginDataRight.setRefLoginId(login.getLoginId());
									loginDataRight.setRefBrId(custUserBranchDto.getBrId());
									loginDataRightRepository.save(loginDataRight);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else {
						status = new Status(true, 400, "User name already exist");
					}
				} else {
					status = new Status(true, 400, "The user has already been created for the selected Employee");
				}


			}else {
				status = new Status(true, 400, "Customer is not exist");
			}
		} catch (Exception e) {

			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}

	@Override
	public Status checkUserName(CustUserMgmtDto custUserMgmtDto) {
		Status status = null;
		try {
			Customer customer = customerRepository.findCustomerByCustId(custUserMgmtDto.getCustomerId());
			String userName = custUserMgmtDto.getUserName();
			String custCode = customer.getCustCode();

			String loginUserName = userName+"@"+custCode;
			Login login = iLoginRepository.findByLoginNameAndRefCustId(loginUserName, custUserMgmtDto.getCustomerId());
			if(login == null) {
				status = new Status(false, 200, "Not Exist");
			} else {
				status = new Status(true, 400, "Exist");
			}
		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}

	@Override
	public UserModuleResDto findAllModules() {

		UserModuleResDto userModuleResDto = new UserModuleResDto();
		List<UserModuleResponseDto> userModule = new ArrayList<UserModuleResponseDto>();
		try {
			List<Object[]> modules = iModuleRepository.findAllModules();
			if(!modules.isEmpty()) {
				for (Object[] p: modules) {
					UserModuleResponseDto userModuleResponseDto = new UserModuleResponseDto();
					userModuleResponseDto.setModuleId(Integer.valueOf(p[0].toString()));
					userModuleResponseDto.setModuleName(p[1].toString());
					userModuleResponseDto.setModuleURL(p[2].toString());
					userModuleResponseDto.setParentId(Integer.valueOf(p[3].toString()));
					userModule.add(userModuleResponseDto);
					userModuleResDto.setModules(userModule);
				}

				userModuleResDto.status = new Status(false, 200, "Success");
			} else {
				userModuleResDto.status = new Status(false, 400, "Not found");
			}


		} catch (Exception e) {
			userModuleResDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return userModuleResDto;
	}

	@Override
	public CustUserBranchResDto findAllBranchesByCustId(Integer custId) {
		CustUserBranchResDto custUserBranchResDto = new CustUserBranchResDto();
		List<CustUserBranchDto> branchList = new ArrayList<CustUserBranchDto>();
		try {
			List<Object[]> braches = branchRepository.getAllBranchesByCustId(custId);
			if(!braches.isEmpty()) {
				for(Object[] p: braches) {
					CustUserBranchDto custUserBranchDto = new CustUserBranchDto();
					custUserBranchDto.setBrId(Integer.valueOf(p[0].toString()));
					custUserBranchDto.setBrName(p[1].toString());
					branchList.add(custUserBranchDto);
				}

				custUserBranchResDto.setBranches(branchList);
				custUserBranchResDto.status = new Status(false, 200, "Success");
			} else {
				custUserBranchResDto.status = new Status(true, 400, "Not found");
			}
		} catch (Exception e) {
			custUserBranchResDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return custUserBranchResDto;
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
