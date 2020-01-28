package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.masters.Login;
import com.lng.attendancecompanyservice.entity.masters.UserRight;
import com.lng.attendancecompanyservice.entity.userModule.Module;
import com.lng.attendancecompanyservice.repositories.masters.LoginRepository;
import com.lng.attendancecompanyservice.repositories.masters.UserRightRepository;
import com.lng.attendancecompanyservice.repositories.userModule.IModuleRepository;
import com.lng.attendancecompanyservice.service.masters.CompanyUserMgmtService;
import com.lng.attendancecompanyservice.utils.Encoder;
import com.lng.attendancecompanyservice.utils.MessageUtil;
import com.lng.dto.masters.custUserMgmt.CompanyUserLoginDto;
import com.lng.dto.masters.custUserMgmt.CompanyUserLoginModuleDto;
import com.lng.dto.masters.custUserMgmt.CompanyUserLoginModuleMapResponseDto;
import com.lng.dto.masters.custUserMgmt.CustUserBranchesDto;
import com.lng.dto.masters.custUserMgmt.CustUserLoginDto;
import com.lng.dto.masters.custUserMgmt.CustUserLoginModuleBranchMapResponseDto;
import com.lng.dto.masters.custUserMgmt.CustUserMgmtDto;
import com.lng.dto.masters.custUserMgmt.CustUserModuleDto;
import com.lng.dto.masters.custUserMgmt.CustUserModuleMapDto;
import com.lng.dto.masters.custUserMgmt.CustUserModulesDto;
import com.lng.dto.masters.custUserMgmt.UserModuleResDto;
import com.lng.dto.masters.custUserMgmt.UserModuleResponseDto;

import status.Status;
@Service
public class CompanyUserMgmtServiceImpl implements CompanyUserMgmtService {

	@Autowired
	LoginRepository iLoginRepository;

	@Autowired
	IModuleRepository iModuleRepository;

	@Autowired
	UserRightRepository userRightRepository;


	MessageUtil messageUtil = new MessageUtil();

	ModelMapper modelMapper = new ModelMapper();

	Encoder encoder = new Encoder();

	private final Lock displayQueueLock = new ReentrantLock();


	@Override
	@Transactional(rollbackOn={Exception.class})
	public Status saveAllDetails(CompanyUserLoginModuleDto companyUserLoginModuleDto) {
		final Lock displayLock = this.displayQueueLock;
		Status status = null;
		String mobileNo = null;
		String userName = null;
		try {
			displayLock.lock();
			userName = companyUserLoginModuleDto.getUserDetails().getUserName();
			String custCode ="LNG";
			String loginUserName = userName+"@"+custCode;
			Login login1 = iLoginRepository.findByLoginNameAndRefCustIdAndLoginIsActive(loginUserName,0, true);
			if(login1 == null ) {
				if(!isNullOrEmpty(companyUserLoginModuleDto.getUserDetails().getuMobileNumber())){
					String newPassword = iLoginRepository.generatePassword();
					Login login = new Login();
					login.setLoginName(loginUserName);
					login.setLoginMobile(companyUserLoginModuleDto.getUserDetails().getuMobileNumber());
					login.setLoginPassword(encoder.getEncoder().encode(newPassword));
					login.setLoginCreatedDate(new Date());
					login.setLoginIsActive(true);
					login.setRefCustId(0);
					login.setRefEmpId(0);
					iLoginRepository.save(login);
					mobileNo = login.getLoginMobile(); 


					String mobileSmS = "User Id has been successfully created to access the Attendance System Web application."
							+ "The login details are User Id: "+loginUserName+" and Password is : "+ newPassword;	
					String s = messageUtil.sms(mobileNo, mobileSmS);

					status = new Status(false, 200, "created");
					try {
						if(!companyUserLoginModuleDto.getModules().isEmpty()) {
							for(CustUserModuleDto custUserModuleDto : companyUserLoginModuleDto.getModules()) {
								UserRight userRight = new UserRight();
								userRight.setRefLoginId(login.getLoginId());
								userRight.setRefModuleId(custUserModuleDto.getModuleId());
								userRightRepository.save(userRight);
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					status = new Status(true, 400, "Please enter mobile number");
				}
			}else {
				status = new Status(true, 400, "User name already exist");
			}
		}catch(Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");

		}
		finally {
			displayLock.unlock();
		}
		return status;
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
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}

	@Override
	public Status updateUserDetails(CustUserMgmtDto custUserMgmtDto) {
		Status status = null;
		final Lock displayLock = this.displayQueueLock; 
		try {
			displayLock.lock();
			String userName = custUserMgmtDto.getUserName();
			String custCode ="LNG";

			String loginUserName = userName+"@"+custCode;
			Login login = iLoginRepository.findByLoginId(custUserMgmtDto.getLoginId());
			if(login != null) {
				Login login1 = iLoginRepository.findByLoginNameAndRefCustIdAndLoginIsActive(loginUserName,0, true);
				if(login1 == null ||(login.getLoginId() == custUserMgmtDto.getLoginId() && login.getLoginName().equals(loginUserName))) {
					//Login login2 = iLoginRepository.findByLoginMobileAndRefCustId(custUserMgmtDto.getuMobileNumber(),0);
					//if(login2 == null || (login.getLoginId() == custUserMgmtDto.getLoginId() && login.getLoginMobile().equals(custUserMgmtDto.getuMobileNumber()))) {
					login.setLoginName(loginUserName);
					login.setLoginMobile(custUserMgmtDto.getuMobileNumber());
					login.setLoginCreatedDate(new Date());
					login.setLoginIsActive(true);
					login.setRefCustId(0);
					login.setRefEmpId(0);
					iLoginRepository.save(login);

					status = new Status(false, 200, "updated");


					/*  }else { status = new Status(true, 400, "Mobile number already exist"); 
					  } */

				}else {
					status = new Status(true, 400, "User name already exist");

				}
			} else {
				status = new Status(true, 400, "Login id not found");

			}


		} catch (Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");

		}
		finally {
			displayLock.unlock();
		}
		return status;
	}
	public static boolean isNullOrEmpty(String str) {
		if(str != null && !str.trim().isEmpty())
			return false;
		return true;
	}

	@Override
	public Status updateModules(CustUserModuleMapDto custUserModuleMapDto) {
		final Lock displayLock = this.displayQueueLock; 
		Status status = null;
		try {
			displayLock.lock();
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

				Module module = iModuleRepository.findModuleByModuleId(custUserModuleDto.getModuleId());
				List<UserRight> userRights = userRightRepository.findByRefLoginId(custUserModuleMapDto.getLoginId());
				//UserRight userRight2 = userRightRepository.findByRefModuleId(custUserModuleDto.getModuleId());
				for(UserRight userRight: userRights) {
					if(custUserModuleDto.getUserRightId() != null && custUserModuleDto.getUserRightId().equals(userRight.getUserRightId())) {
						userRight.setRefModuleId(module.getModuleId());
						userRightRepository.save(userRight);
					} 
					else if(module == null){
						userRightRepository.delete(userRight);
					}
				}
			}
			status = new Status(false, 200, "Modules updated");


		} catch (Exception e) {
			status = new Status(true, 500, "Opps...! Something went wrong..");

		}
		finally {
			displayLock.unlock();
		}
		return status;
	}

	@Override
	public CompanyUserLoginModuleMapResponseDto getAllUserByLoginId(Integer loginId) {
		CompanyUserLoginModuleMapResponseDto companyUserLoginModuleMapResponseDto = new CompanyUserLoginModuleMapResponseDto();

		List<CompanyUserLoginDto> companyUserLoginDtoList = new ArrayList<>();

		try {

			List<Object[]> loginList = iLoginRepository.findAllUsersByloginId(loginId);
			for(Object[] p : loginList) {
				CompanyUserLoginDto companyUserLoginDto = new CompanyUserLoginDto();

				List<CustUserModulesDto> custUserModulesDtoList = new ArrayList<>();
				companyUserLoginDto.setLoginId(Integer.valueOf(p[0].toString()));
				companyUserLoginDto.setLoginName(p[1].toString());
				companyUserLoginDto.setLoginMobile(p[2].toString());
				companyUserLoginDtoList.add(companyUserLoginDto);
				companyUserLoginModuleMapResponseDto.setLoginId(Integer.valueOf(p[0].toString()));
				companyUserLoginModuleMapResponseDto.setLoginDetails(companyUserLoginDtoList);

				List<Object[]> moduleList = iModuleRepository.findByLogin_LoginId(loginId);
				for(Object[] m : moduleList) {
					CustUserModulesDto custUserModulesDto = new CustUserModulesDto();

					custUserModulesDto.setLoginId(Integer.valueOf(m[0].toString()));
					custUserModulesDto.setModuleId(Integer.valueOf(m[1].toString()));
					custUserModulesDto.setModuleName(m[2].toString());

					custUserModulesDtoList.add(custUserModulesDto);

				}	
				companyUserLoginDto.setModules(custUserModulesDtoList);
			}
			if(loginList.isEmpty()) {
				companyUserLoginModuleMapResponseDto.status = new Status(false, 200, "Success and there is no login details exist for this admin");
			}else {
				companyUserLoginModuleMapResponseDto.status = new Status(false, 200, "Success");
			}

		} catch (Exception e) {
			companyUserLoginModuleMapResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return companyUserLoginModuleMapResponseDto;
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
	public CompanyUserLoginModuleMapResponseDto getAllUserByCustId(Integer custId) {
		CompanyUserLoginModuleMapResponseDto companyUserLoginModuleMapResponseDto = new CompanyUserLoginModuleMapResponseDto();

		List<CompanyUserLoginDto> companyUserLoginDtoList = new ArrayList<>();

		try {

			List<Object[]> loginList = iLoginRepository.findAllUsersByCustId(0);
			for(Object[] p : loginList) {
				CompanyUserLoginDto companyUserLoginDto = new CompanyUserLoginDto();

				List<CustUserModulesDto> custUserModulesDtoList = new ArrayList<>();

				companyUserLoginDto.setLoginId(Integer.valueOf(p[0].toString()));
				companyUserLoginDto.setLoginName(p[1].toString());
				companyUserLoginDto.setLoginMobile(p[2].toString());

				companyUserLoginDtoList.add(companyUserLoginDto);
				companyUserLoginModuleMapResponseDto.setLoginDetails(companyUserLoginDtoList);

				List<Object[]> moduleList = iModuleRepository.findModulesByLogin_LoginId(companyUserLoginDto.getLoginId());
				// custUserLoginModuleBranchMapResponseDto.setModules(moduleList.stream().map(module -> convertToCustUserModulesDto(module)).collect(Collectors.toList()));
				for(Object[] m : moduleList) {
					CustUserModulesDto custUserModulesDto = new CustUserModulesDto();

					custUserModulesDto.setLoginId(Integer.valueOf(m[0].toString()));
					custUserModulesDto.setModuleId(Integer.valueOf(m[1].toString()));
					custUserModulesDto.setModuleName(m[2].toString());

					custUserModulesDtoList.add(custUserModulesDto);

				}	
				companyUserLoginDto.setModules(custUserModulesDtoList);
			}
			if(loginList.isEmpty()) {
				companyUserLoginModuleMapResponseDto.status = new Status(false, 200, "Success and there is no login details exist for this customer");
			}else {
				companyUserLoginModuleMapResponseDto.status = new Status(false, 200, "Success");
			}
		} catch (Exception e) {
			companyUserLoginModuleMapResponseDto.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return companyUserLoginModuleMapResponseDto;
	}
}