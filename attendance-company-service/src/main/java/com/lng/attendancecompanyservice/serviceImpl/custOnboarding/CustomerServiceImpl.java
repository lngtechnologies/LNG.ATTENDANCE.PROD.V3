package com.lng.attendancecompanyservice.serviceImpl.custOnboarding;

import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;
import com.lng.attendancecompanyservice.entity.masters.Branch;
import com.lng.attendancecompanyservice.entity.masters.Country;
import com.lng.attendancecompanyservice.entity.masters.CustLeave;
import com.lng.attendancecompanyservice.entity.masters.CustomerConfig;
import com.lng.attendancecompanyservice.entity.masters.IndustryType;
import com.lng.attendancecompanyservice.entity.masters.LeaveType;
import com.lng.attendancecompanyservice.entity.masters.Login;
import com.lng.attendancecompanyservice.entity.masters.LoginDataRight;
import com.lng.attendancecompanyservice.entity.masters.State;
import com.lng.attendancecompanyservice.entity.masters.UserRight;
import com.lng.attendancecompanyservice.repositories.custOnboarding.CustomerRepository;
import com.lng.attendancecompanyservice.repositories.masters.BranchRepository;
import com.lng.attendancecompanyservice.repositories.masters.CountryRepository;
import com.lng.attendancecompanyservice.repositories.masters.CustLeaveRepository;
import com.lng.attendancecompanyservice.repositories.masters.CustomerConfigRepository;
import com.lng.attendancecompanyservice.repositories.masters.IndustryTypeRepository;
import com.lng.attendancecompanyservice.repositories.masters.LeaveTypeRepository;
import com.lng.attendancecompanyservice.repositories.masters.LoginDataRightRepository;
import com.lng.attendancecompanyservice.repositories.masters.LoginRepository;
import com.lng.attendancecompanyservice.repositories.masters.StateRepository;
import com.lng.attendancecompanyservice.repositories.masters.UserRightRepository;
import com.lng.attendancecompanyservice.service.custOnboarding.CustomerService;
import com.lng.attendancecompanyservice.utils.AzureFaceListSubscriptionKey;
import com.lng.attendancecompanyservice.utils.Encoder;
import com.lng.attendancecompanyservice.utils.MessageUtil;
import com.lng.dto.customer.CustomerDto;
import com.lng.dto.customer.CustomerDtoTwo;
import com.lng.dto.customer.CustomerListResponse;
import com.lng.dto.customer.CustomerResponse;
import com.lng.dto.customer.StatusDto;

import status.Status;


@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BranchRepository branchRepository;

	@Autowired
	LoginRepository loginRepository;

	@Autowired
	CountryRepository countryRepository;

	@Autowired
	StateRepository stateRepository;

	@Autowired
	IndustryTypeRepository industryTypeRepository;

	@Autowired
	UserRightRepository userRightRepository;

	@Autowired
	CustLeaveRepository custLeaveRepository;

	@Autowired
	LeaveTypeRepository leaveTypeRepository;

	@Autowired
	LoginDataRightRepository loginDataRightRepository;

	@Autowired
	MailProperties mailProperties;
	
	@Autowired
	CustomerConfigRepository customerConfigRepository;


	ModelMapper modelMapper = new ModelMapper();

	MessageUtil messageUtil = new MessageUtil();

	Encoder Encoder = new Encoder();

	AzureFaceListSubscriptionKey subscription = new AzureFaceListSubscriptionKey();

	//Sms sms = new Sms();

	//Email email = new Email();


	/*
	 * @Bean public BCryptPasswordEncoder getEncoder() { return new
	 * BCryptPasswordEncoder(); }
	 */
	private final Lock displayQueueLock = new ReentrantLock();

	@Override
	@Transactional(rollbackOn={Exception.class})
	public StatusDto saveCustomer(CustomerDto customerDto) {

		final Lock displayLock = this.displayQueueLock; 
		StatusDto statusDto = new StatusDto();
		Login login = new Login();

		try {
			displayLock.lock();
			List<Customer> customerList1 = customerRepository.findCustomerByCustEmail(customerDto.getCustEmail());
			List<Customer> customerList2 = customerRepository.findCustomerByCustMobile(customerDto.getCustMobile());
			//List<Customer> customerList3 = customerRepository.findCustomerByCustName(customerDto.getCustName());

			
			
			//Thread.sleep(3000L);

			if(customerList1.isEmpty() && customerList2.isEmpty()) {

				Customer customer = saveCustomerData(customerDto);

				if(customer != null) {

					List<LeaveType> leaveTypes = leaveTypeRepository.findAll();

					if(leaveTypes.isEmpty()) {

						statusDto.setCode(400);
						statusDto.setError(true);
						statusDto.setMessage("Leave type not found");

					}else {
						List<CustLeave> custLeaves = custLeaveRepository.assignCustLeaveToCustomer(customer.getCustId());
					}


					Branch branch = setCustomerDetailsToBranch(customer);

					if(branch != null) {
						int custId = saveBranch(branch);

						// Create faceList in Azure
						createBranchFaceListId(branch.getBrCode());

						
						List<CustomerConfig> customerConfig1 = customerConfigRepository.assignConfigToBranch(branch.getCustomer().getCustId(), branch.getBrId(), "FACIAL_Recognition");
						List<CustomerConfig> customerConfig2 = customerConfigRepository.assignConfigToBranch(branch.getCustomer().getCustId(), branch.getBrId(), "QR_Code");
						List<CustomerConfig> customerConfig3 = customerConfigRepository.assignConfigToBranch(branch.getCustomer().getCustId(), branch.getBrId(), "GEO_Fencing");
						List<CustomerConfig> customerConfig4 = customerConfigRepository.assignConfigToBranch(branch.getCustomer().getCustId(), branch.getBrId(), "Proximity");
						
						
						/*List<CustomerConfig> customerConfig = setCustAndBrToConfig(branch);
						if(!customerConfig.isEmpty()) {
							List<CustomerConfig> customerConfigs =saveCustConfig(customerConfig);
						}*/
						
						login = setCustomerToLogin(customer);

						if(login != null) {
							int loginId = saveLogin(login);
							if(loginId != 0) {
								List<UserRight> userRights = userRightRepository.assignDefaultModulesToDefaultCustomerAdmin(loginId);
							}
						}

						// saves to LoginDataRight table
						if(login != null) {
							Login login1 = loginRepository.findByRefCustId(branch.getCustomer().getCustId());
							LoginDataRight loginDataRight = new LoginDataRight();
							loginDataRight.setBranch(branch);
							loginDataRight.setLogin(login1);
							loginDataRightRepository.save(loginDataRight);
						}
					}
				}
				//send mail
				customerDto.setCustCode(customer.getCustCode());				
				sendMailWithoutAttachments(customerDto, login.getLoginName());
				statusDto.setCode(200);
				statusDto.setError(false);
				statusDto.setMessage("created");
				
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Customer mobile number or email already exist");		
				
			}
			
		} catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage("Oops..! Something went wrong..");
			
		}
		finally {
			displayLock.unlock();
		}
		return statusDto;
	}

	//Send email without Attachments
	public void sendMailWithoutAttachments(CustomerDto customerDto, String lName) {

		/*
		 * String lngLogoUrl = "C:/Users/Admin/Desktop/Welcome/images/lng_logo.png";
		 * String welcomeImageUrl =
		 * "C:/Users/Admin/Desktop/Welcome/images/welcome_img.png"; String
		 * socialMediaIconUrl =
		 * "C:/Users/Admin/Desktop/Welcome/images/social-media-icon.png";
		 */
		String subject = "Smart Attendance System Welcome Kit";
		String mailFrom = mailProperties.getUsername();
		String password = mailProperties.getPassword();
		String port = mailProperties.getPort().toString();
		String host = mailProperties.getHost();
		//String template = email-template.html;


		String mailSmS = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\r\n" + 
				"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n" + 
				"<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\" style=\"min-height: 100%; background: #f0f0f0;\">\r\n" + 
				"    <head>\r\n" + 
				"        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n" + 
				"        <meta name=\"viewport\" content=\"width=device-width\">\r\n" + 
				"    </head>\r\n" + 
				"    <body style=\"min-width: 100%; background: #f0f0f0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; -moz-box-sizing: border-box; -webkit-box-sizing: border-box; box-sizing: border-box; color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; width: 100%;\">\r\n" + 
				"        <table class=\"body\" style=\"border-spacing: 0; border-collapse: collapse; vertical-align: top; background: #f0f0f0; height: 100%; width: 100%; color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px;\" width=\"100%\" height=\"100%\" valign=\"top\" align=\"left\">\r\n" + 
				"            <tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"                <td class=\"center\" align=\"left\" valign=\"top\" style=\"word-wrap: break-word; -webkit-hyphens: auto; -moz-hyphens: auto; hyphens: auto; vertical-align: top; color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; border-collapse: collapse;\">\r\n" + 
				"                    <center data-parsed=\"\" style=\"width: 100%; min-width: 580px;\">\r\n" + 
				"                        <table class=\"container text-center\" style=\"border-spacing: 0; border-collapse: collapse; padding: 0; vertical-align: top; background: #fefefe; width: 580px; margin: 0 auto; Margin: 0 auto; text-align: center;\" width=\"580\" valign=\"top\" align=\"center\"><tbody><tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\"><td style=\"word-wrap: break-word; -webkit-hyphens: auto; -moz-hyphens: auto; hyphens: auto; vertical-align: top; color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; border-collapse: collapse;\" valign=\"top\" align=\"left\"> <!-- This container adds the grey gap at the top of the email -->\r\n" + 
				"                                        <table class=\"row grey\" style=\"border-spacing: 0; border-collapse: collapse; vertical-align: top; text-align: left; background: #f0f0f0; padding: 0; width: 100%; position: relative; display: table;\" width=\"100%\" valign=\"top\" align=\"left\"><tbody><tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                    <th class=\"small-12 large-12 columns first last\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; text-align: left; font-size: 13px; line-height: 21px; margin: 0 auto; Margin: 0 auto; padding-bottom: 16px; width: 564px; padding-left: 16px; padding-right: 16px;\" align=\"left\">\r\n" + 
				"                                                        <table style=\"border-spacing: 0; border-collapse: collapse; padding: 0; vertical-align: top; text-align: left; width: 100%;\" width=\"100%\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                            <tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                                <th style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px;\" align=\"left\">\r\n" + 
				"                                                                    &#xA0; \r\n" + 
				"                                                                </th>\r\n" + 
				"                                                                <th class=\"expander\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; visibility: hidden; width: 0; padding: 0;\" align=\"left\"></th>\r\n" + 
				"                                                            </tr>\r\n" + 
				"                                                        </table>\r\n" + 
				"                                                    </th>\r\n" + 
				"                                                </tr></tbody></table>\r\n" + 
				"                                    </td></tr></tbody></table>\r\n" + 
				"\r\n" + 
				"                                    <table class=\"container text-center\" style=\"border-spacing: 0; border-collapse: collapse; padding: 0; vertical-align: top; background: #fefefe; width: 580px; margin: 0 auto; Margin: 0 auto; text-align: center;\" width=\"580\" valign=\"top\" align=\"center\"><tbody><tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\"><td style=\"word-wrap: break-word; -webkit-hyphens: auto; -moz-hyphens: auto; hyphens: auto; vertical-align: top; color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; border-collapse: collapse;\" valign=\"top\" align=\"left\"> <!-- This container is the main email content -->\r\n" + 
				"                                                    <table class=\"row\" style=\"border-spacing: 0; border-collapse: collapse; vertical-align: top; text-align: left; padding: 0; width: 100%; position: relative; display: table;\" width=\"100%\" valign=\"top\" align=\"left\"><tbody><tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\"> <!-- Logo -->\r\n" + 
				"                                                                <th class=\"small-12 large-12 columns first last\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; text-align: left; font-size: 13px; line-height: 21px; margin: 0 auto; Margin: 0 auto; padding-bottom: 16px; width: 564px; padding-left: 16px; padding-right: 16px;\" align=\"left\">\r\n" + 
				"                                                                    <table style=\"border-spacing: 0; border-collapse: collapse; padding: 0; vertical-align: top; text-align: left; width: 100%;\" width=\"100%\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                                        <tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                                            <th style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px;\" align=\"left\">\r\n" + 
				"                                                                                <center data-parsed=\"\" style=\"width: 100%; min-width: 532px;\">\r\n" + 
				"                                                                                    <a href=\"http://www.lngtechnologies.in\" align=\"center\" class=\"text-center\" target=\"new\" style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; line-height: 1.3; color: #f7931d; text-decoration: none;\">\r\n" + 
				"                                                                                        <img src=\"http://52.183.143.13/welcomekit/images/lng_logo.png\" class=\"swu-logo\" style=\"outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; max-width: 100%; clear: both; display: block; border: none; width: 230px; height: auto; padding: 15px 0px 0px 0px;\" width=\"230\">\r\n" + 
				"                                                                                    </a>\r\n" + 
				"                                                                                </center>\r\n" + 
				"                                                                            </th>\r\n" + 
				"                                                                            <th class=\"expander\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; visibility: hidden; width: 0; padding: 0;\" align=\"left\"></th>\r\n" + 
				"                                                                        </tr>\r\n" + 
				"                                                                    </table>\r\n" + 
				"                                                                </th>\r\n" + 
				"                                                            </tr></tbody></table>\r\n" + 
				"                                                            <table class=\"row masthead\" style=\"border-spacing: 0; border-collapse: collapse; vertical-align: top; text-align: left; background: #009899; padding: 0; width: 100%; position: relative; display: table;\" width=\"100%\" valign=\"top\" align=\"left\"><tbody><tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\"> <!-- Masthead -->\r\n" + 
				"                                                                        <th class=\"small-12 large-12 columns first last\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; text-align: left; font-size: 13px; line-height: 21px; margin: 0 auto; Margin: 0 auto; padding-bottom: 16px; width: 564px; padding-left: 16px; padding-right: 16px;\" align=\"left\">\r\n" + 
				"                                                                            <table style=\"border-spacing: 0; border-collapse: collapse; padding: 0; vertical-align: top; text-align: left; width: 100%;\" width=\"100%\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                                                <tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                                                    <th style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px;\" align=\"left\">\r\n" + 
				"                                                                                        <h1 class=\"text-center\" style=\"margin: 0; Margin: 0; line-height: 1.3; word-wrap: normal; font-family: Helvetica, Arial, sans-serif; font-weight: normal; margin-bottom: 10px; Margin-bottom: 10px; font-size: 34px; text-align: center; color: #f7931d; padding: 35px 0px 15px 0px;\">Welcome to LNG Attendance System!</h1>\r\n" + 
				"                                                                                        <center data-parsed=\"\" style=\"width: 100%; min-width: 532px;\">\r\n" + 
				"                                                                                            <img src=\"http://52.183.143.13/welcomekit/images/welcome_img.png\" valign=\"bottom\" align=\"center\" class=\"text-center\" style=\"outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; width: auto; max-width: 100%; clear: both; display: block; margin: 0 auto; Margin: 0 auto; float: none; text-align: center;\">\r\n" + 
				"                                                                                        </center>\r\n" + 
				"                                                                                    </th>\r\n" + 
				"                                                                                    <th class=\"expander\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; visibility: hidden; width: 0; padding: 0;\" align=\"left\"></th>\r\n" + 
				"                                                                                </tr>\r\n" + 
				"                                                                            </table>\r\n" + 
				"                                                                        </th>\r\n" + 
				"                                                                    </tr></tbody></table>\r\n" + 
				"                                                                    <table class=\"row\" style=\"border-spacing: 0; border-collapse: collapse; vertical-align: top; text-align: left; padding: 0; width: 100%; position: relative; display: table;\" width=\"100%\" valign=\"top\" align=\"left\"><tbody><tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\"> <!--This container adds the gap between masthead and digest content -->\r\n" + 
				"                                                                                <th class=\"small-12 large-12 columns first last\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; text-align: left; font-size: 13px; line-height: 21px; margin: 0 auto; Margin: 0 auto; padding-bottom: 16px; width: 564px; padding-left: 16px; padding-right: 16px;\" align=\"left\">\r\n" + 
				"                                                                                    <table style=\"border-spacing: 0; border-collapse: collapse; padding: 0; vertical-align: top; text-align: left; width: 100%;\" width=\"100%\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                                                        <tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                                                            <th style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px;\" align=\"left\">\r\n" + 
				"                                                                                                &#xA0; \r\n" + 
				"                                                                                            </th>\r\n" + 
				"                                                                                            <th class=\"expander\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; visibility: hidden; width: 0; padding: 0;\" align=\"left\"></th>\r\n" + 
				"                                                                                        </tr>\r\n" + 
				"                                                                                    </table>\r\n" + 
				"                                                                                </th>\r\n" + 
				"                                                                            </tr></tbody></table>\r\n" + 
				"                                                                            <table class=\"row\" style=\"border-spacing: 0; border-collapse: collapse; vertical-align: top; text-align: left; padding: 0; width: 100%; position: relative; display: table;\" width=\"100%\" valign=\"top\" align=\"left\"><tbody><tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\"> <!-- main Email content -->\r\n" + 
				"                                                                                        <th class=\"small-12 large-12 columns first last\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; text-align: left; font-size: 13px; line-height: 21px; margin: 0 auto; Margin: 0 auto; padding-bottom: 16px; width: 564px; padding-left: 16px; padding-right: 16px;\" align=\"left\">\r\n" + 
				"                                                                                            <table style=\"border-spacing: 0; border-collapse: collapse; padding: 0; vertical-align: top; text-align: left; width: 100%;\" width=\"100%\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                                                                <tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                                                                    <th style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px;\" align=\"left\">\r\n" + 
				"                                                                                                        <b><h5 style=\"padding: 0; margin: 0; Margin: 0; text-align: left; line-height: 1.3; color: inherit; word-wrap: normal; font-family: Helvetica, Arial, sans-serif; font-weight: normal; margin-bottom: 10px; Margin-bottom: 10px; font-size: 20px;\">Welcome "+ customerDto.getCustName() + "!</h5></b>\r\n" + 
				"																											<p style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; font-size: 13px; line-height: 21px; margin-bottom: 5px; Margin-bottom: 5px; text-align: justify; color: #777777;\"> We are so glad that you have registered to LNG Attendance System. Your new Attendance account has been created Successfully. </p>\r\n" + 
				"																											\r\n" + 
				"																											 <table style=\"border-spacing: 0; border-collapse: collapse; padding: 0; vertical-align: top; text-align: left; width: 100%;\" width=\"100%\" valign=\"top\" align=\"left\">\r\n" + 
				"																												<tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"																													<td class=\"large-offset-1\" style=\"word-wrap: break-word; -webkit-hyphens: auto; -moz-hyphens: auto; hyphens: auto; vertical-align: top; color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; padding-left: 64.33333px; border-collapse: collapse;\" valign=\"top\" align=\"left\">\r\n" + 
				"																														<p style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; font-size: 13px; line-height: 21px; margin-bottom: 5px; Margin-bottom: 5px; text-align: justify; color: #777777;\"> Please find details of the account and the admin user.</p>\r\n" + 
				"																													</td>\r\n" + 
				"																												</tr>\r\n" + 
				"																											</table>\r\n" + 
				"																											 <table style=\"border-spacing: 0; border-collapse: collapse; padding: 0; vertical-align: top; text-align: left; width: 100%;\" width=\"100%\" valign=\"top\" align=\"left\">\r\n" + 
				"																												<tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"																													<td class=\"large-offset-1\" style=\"word-wrap: break-word; -webkit-hyphens: auto; -moz-hyphens: auto; hyphens: auto; vertical-align: top; color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; padding-left: 64.33333px; border-collapse: collapse;\" valign=\"top\" align=\"left\">\r\n" + 
				"																														<p style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; font-size: 13px; line-height: 21px; margin-bottom: 5px; Margin-bottom: 5px; text-align: justify; color: #777777;\"> Weblink</p>\r\n" + 
				"																													</td>\r\n" + 
				"																													<td style=\"word-wrap: break-word; -webkit-hyphens: auto; -moz-hyphens: auto; hyphens: auto; vertical-align: top; color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; border-collapse: collapse;\" valign=\"top\" align=\"left\">\r\n" + 
				"																														<p style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; font-size: 13px; line-height: 21px; margin-bottom: 5px; Margin-bottom: 5px; text-align: justify; color: #777777;\">: <a href=\"http://52.183.143.13/lngattendancesystemv5\" style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; line-height: 1.3; color: #f7931d; text-decoration: none;\" target = \"_blank\">https://www.lngattendancesystem.com</a> </p>\r\n" + 
				"																													</td>\r\n" + 
				"																												</tr>\r\n" + 
				"																												<tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"																													<td class=\"large-offset-1\" style=\"word-wrap: break-word; -webkit-hyphens: auto; -moz-hyphens: auto; hyphens: auto; vertical-align: top; color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; padding-left: 64.33333px; border-collapse: collapse;\" valign=\"top\" align=\"left\">\r\n" + 
				"																														<p style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; font-size: 13px; line-height: 21px; margin-bottom: 5px; Margin-bottom: 5px; text-align: justify; color: #777777;\"> Customer Code</p>\r\n" + 
				"																													</td>\r\n" + 
				"																													<td style=\"word-wrap: break-word; -webkit-hyphens: auto; -moz-hyphens: auto; hyphens: auto; vertical-align: top; color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; border-collapse: collapse;\" valign=\"top\" align=\"left\">\r\n" + 
				"																														<p style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; font-size: 13px; line-height: 21px; margin-bottom: 5px; Margin-bottom: 5px; text-align: justify; color: #777777;\">: "+ customerDto.getCustCode() +" </p>\r\n" + 
				"																													</td>\r\n" + 
				"																												</tr>\r\n" + 
				"																												<tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"																													<td class=\"large-offset-1\" style=\"word-wrap: break-word; -webkit-hyphens: auto; -moz-hyphens: auto; hyphens: auto; vertical-align: top; color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; padding-left: 64.33333px; border-collapse: collapse;\" valign=\"top\" align=\"left\">\r\n" + 
				"																														<p style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; font-size: 13px; line-height: 21px; margin-bottom: 5px; Margin-bottom: 5px; text-align: justify; color: #777777;\"> Admin User Id</p>\r\n" + 
				"																													</td>\r\n" + 
				"																													<td style=\"word-wrap: break-word; -webkit-hyphens: auto; -moz-hyphens: auto; hyphens: auto; vertical-align: top; color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; border-collapse: collapse;\" valign=\"top\" align=\"left\">\r\n" + 
				"																														<p style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; font-size: 13px; line-height: 21px; margin-bottom: 5px; Margin-bottom: 5px; text-align: justify; color: #777777;\">: "+ lName +" </p>\r\n" + 
				"																													</td>\r\n" + 
				"																												</tr>	\r\n" + 
				"																											</table>	\r\n" + 
				"																											\r\n" + 
				"																											<p style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; font-size: 13px; line-height: 21px; margin-bottom: 5px; Margin-bottom: 5px; text-align: justify; color: #777777;\"> From now on, please log in to your account using the User Id mentioned above. The password to access the Web Application is sent to the Admin Mobile number provided during the On-boarding. </p>\r\n" + 
				"																											<br>\r\n" + 
				"																											<b>Note:</b> <i>Make sure you don't share the Customer Code and User Id mentioned in this mail, because it's unique for you!</i>\r\n" + 
				"																										<br>\r\n" + 
				"																										\r\n" + 
				"                                                                                                        <div class=\"button\">\r\n" + 
				"                                                                                                            <!--[if mso]>\r\n" + 
				"                                                                                                                <v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" href=\"#\" style=\"height:35px;v-text-anchor:middle;width:150px;\" arcsize=\"8%\" strokecolor=\"#f7931d\" fillcolor=\"#f7931d\">\r\n" + 
				"                                                                                                                  <w:anchorlock/>\r\n" + 
				"                                                                                                                  <center style=\"color:#ffffff;font-family:sans-serif;font-size:16px;font-weight:bold;\">Click here Button</center>\r\n" + 
				"                                                                                                                </v:roundrect>\r\n" + 
				"                                                                                                            <![endif]-->\r\n" + 
				"                                                                                                            <!--<a href=\"#\" style=\"background-color:#f7931d;border:0px solid #f7931d;border-radius:3px;color:#ffffff;display:inline-block;font-family:sans-serif;font-size:16px;font-weight:bold;line-height:35px;text-align:center;text-decoration:none;width:150px;-webkit-text-size-adjust:none;mso-hide:all;\">Click le Button</a>\r\n" + 
				"                                                                                                        </div> -->\r\n" + 
				"                                                                                                    </div></th>\r\n" + 
				"                                                                                                    <th class=\"expander\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; visibility: hidden; width: 0; padding: 0;\" align=\"left\"></th>\r\n" + 
				"                                                                                                </tr>\r\n" + 
				"                                                                                            </table>\r\n" + 
				"																							\r\n" + 
				"                                                                                        </th>\r\n" + 
				"                                                                                    </tr></tbody></table>\r\n" + 
				"																				\r\n" + 
				"																					<table class=\"row\" style=\"border-spacing: 0; border-collapse: collapse; vertical-align: top; text-align: left; padding: 0; width: 100%; position: relative; display: table;\" width=\"100%\" valign=\"top\" align=\"left\"><tbody><tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"																						<th class=\"small-12 large-12 columns first last\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; text-align: left; font-size: 13px; line-height: 21px; margin: 0 auto; Margin: 0 auto; padding-bottom: 16px; width: 564px; padding-left: 16px; padding-right: 16px;\" align=\"left\">\r\n" + 
				"																							<table style=\"border-spacing: 0; border-collapse: collapse; padding: 0; vertical-align: top; text-align: left; width: 100%;\" width=\"100%\" valign=\"top\" align=\"left\">\r\n" + 
				"																								<tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"																									<th style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px;\" align=\"left\">\r\n" + 
				"																										<center data-parsed=\"\" style=\"width: 100%; min-width: 532px;\">\r\n" + 
				"																											Stay Updated On Our Product Features\r\n" + 
				"																										</center>\r\n" + 
				"																									</th>\r\n" + 
				"																								</tr>\r\n" + 
				"																								<th class=\"expander\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; visibility: hidden; width: 0; padding: 0;\" align=\"left\"></th>\r\n" + 
				"																								<tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"																									<th style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px;\" align=\"left\">\r\n" + 
				"																										<center data-parsed=\"\" style=\"width: 100%; min-width: 532px;\">\r\n" + 
				"																											<img src=\"http://52.183.143.13/welcomekit/images/social-media-icon.png\" alt=\"\" style=\"outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; clear: both; width: 124px; max-width: 600px; height: auto; display: block; padding-top: 6px;\" width=\"124\">																											\r\n" + 
				"																										</center>\r\n" + 
				"																									</th>\r\n" + 
				"																								</tr>\r\n" + 
				"																							</table>\r\n" + 
				"																						</th>\r\n" + 
				"																					</tr></tbody></table>\r\n" + 
				"                                                                                    <table class=\"row\" style=\"border-spacing: 0; border-collapse: collapse; vertical-align: top; text-align: left; padding: 0; width: 100%; position: relative; display: table;\" width=\"100%\" valign=\"top\" align=\"left\"><tbody><tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\"> <!-- This container adds whitespace gap at the bottom of main content  -->\r\n" + 
				"                                                                                                <th class=\"small-2 large-2 columns first last\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; text-align: left; font-size: 13px; line-height: 21px; margin: 0 auto; Margin: 0 auto; padding-bottom: 16px; width: 80.66667px; padding-left: 16px; padding-right: 16px;\" align=\"left\">\r\n" + 
				"                                                                                                    <table style=\"border-spacing: 0; border-collapse: collapse; padding: 0; vertical-align: top; text-align: left; width: 100%;\" width=\"100%\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                                                                        <tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                                                                            <th style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px;\" align=\"left\">\r\n" + 
				"                                                                                                                &#xA0; \r\n" + 
				"                                                                                                            </th>\r\n" + 
				"                                                                                                            <th class=\"expander\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; visibility: hidden; width: 0; padding: 0;\" align=\"left\"></th>\r\n" + 
				"                                                                                                        </tr>\r\n" + 
				"                                                                                                    </table>\r\n" + 
				"                                                                                                </th>\r\n" + 
				"                                                                                            </tr></tbody></table>\r\n" + 
				"                                                </td></tr></tbody></table>  <!-- end main email content --> \r\n" + 
				"\r\n" + 
				"                                                <table class=\"container text-center\" style=\"border-spacing: 0; border-collapse: collapse; padding: 0; vertical-align: top; background: #fefefe; width: 580px; margin: 0 auto; Margin: 0 auto; text-align: center;\" width=\"580\" valign=\"top\" align=\"center\"><tbody><tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\"><td style=\"word-wrap: break-word; -webkit-hyphens: auto; -moz-hyphens: auto; hyphens: auto; vertical-align: top; color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; border-collapse: collapse;\" valign=\"top\" align=\"left\"> <!-- footer -->\r\n" + 
				"                                                                <table class=\"row grey\" style=\"border-spacing: 0; border-collapse: collapse; vertical-align: top; text-align: left; background: #f0f0f0; padding: 0; width: 100%; position: relative; display: table;\" width=\"100%\" valign=\"top\" align=\"left\"><tbody><tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                                            <th class=\"small-12 large-12 columns first last\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; text-align: left; font-size: 13px; line-height: 21px; margin: 0 auto; Margin: 0 auto; padding-bottom: 16px; width: 564px; padding-left: 16px; padding-right: 16px;\" align=\"left\">\r\n" + 
				"                                                                                <table style=\"border-spacing: 0; border-collapse: collapse; padding: 0; vertical-align: top; text-align: left; width: 100%;\" width=\"100%\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                                                    <tr style=\"padding: 0; vertical-align: top; text-align: left;\" valign=\"top\" align=\"left\">\r\n" + 
				"                                                                                        <th style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px;\" align=\"left\">\r\n" + 
				"                                                                                            <p class=\"text-center footercopy\" style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; margin: 0; Margin: 0; line-height: 21px; margin-bottom: 5px; Margin-bottom: 5px; padding: 20px 0px; font-size: 12px; text-align: center; color: #777777;\">&#xA9; Copyright 2019 LNG Technologies. All Rights Reserved.</p>\r\n" + 
				"                                                                                        </th>\r\n" + 
				"                                                                                        <th class=\"expander\" style=\"color: #0a0a0a; font-family: Helvetica, Arial, sans-serif; font-weight: normal; margin: 0; Margin: 0; text-align: left; font-size: 13px; line-height: 21px; visibility: hidden; width: 0; padding: 0;\" align=\"left\"></th>\r\n" + 
				"                                                                                    </tr>\r\n" + 
				"                                                                                </table>\r\n" + 
				"                                                                            </th>\r\n" + 
				"                                                                        </tr></tbody></table>\r\n" + 
				"                                                            </td></tr></tbody></table>  \r\n" + 
				"\r\n" + 
				"\r\n" + 
				"                    </center>\r\n" + 
				"                </td>\r\n" + 
				"            </tr>\r\n" + 
				"        </table>\r\n" + 
				"    </body>\r\n" + 
				"</html>\r\n";		

		String message = mailSmS;
		String toAddress = customerDto.getCustEmail();
		try {
			messageUtil.sendOnlyEmail(host, port, mailFrom, password, toAddress, subject, message);
			System.out.println("Email sent.");
		} catch (Exception ex) {
			System.out.println("Could not send email.");
			StringBuffer exception = new StringBuffer(ex.getMessage().toString());
			if (exception.indexOf("SendFailedException") >= 0)      // Wrong To Address 
			{
				System.out.println("Wrong To Mail address");
			}
			ex.printStackTrace();
		}
	}

	// Save to customer table
	private Customer saveCustomerData(CustomerDto customerDto) {
		CustomerResponse customerResponse = new CustomerResponse();
		Customer customer = modelMapper.map(customerDto, Customer.class);
		String custCode = "";
		try {


			custCode = customerRepository.generateCustCode();

			if(customerDto.getCustNoOfBranch() == 0) {
				customer.setCustNoOfBranch(1);
			}else {
				customer.setCustNoOfBranch(customerDto.getCustNoOfBranch());
			}
			customer.setCustIsActive(true);
			customer.setCustCreatedDate(new Date());
			customer.setCustCode(customerDto.getCustCode() + custCode);
			if(customerDto.getCustLogoFile() == null) {
				customer.setCustLogoFile(base64ToByte(Logo));
			}else {
				customer.setCustLogoFile(base64ToByte(customerDto.getCustLogoFile()));	
			}

			try {

				customer = customerRepository.save(customer);
				customerResponse.data = customerDto;

			}catch (Exception e) {
				e.printStackTrace();
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		return customer;
	}

	// Convert base64 to byte
	public  byte[] base64ToByte(String base64) {
		byte[] decodedByte = Base64.getDecoder().decode(base64);
		return decodedByte;
	}

	// convert byte to base64
	public  String byteTobase64(byte[] custLogoFile) {
		String base64 = Base64.getEncoder().encodeToString(custLogoFile);
		return base64;
	}
	// Set Customer Details to Branch
	private Branch setCustomerDetailsToBranch(Customer customer){
		Branch branch = new Branch();
		try {

			String brnchCode = branchRepository.generateBranchForCustomer(customer.getCustId());
			//Customer customer = new Customer();
			branch.setBrAddress(customer.getCustAddress());
			branch.setBrCity(customer.getCustCity());
			branch.setBrCode(customer.getCustCode() + brnchCode);
			branch.setBrCreatedDate(new Date());
			branch.setBrEmail(customer.getCustEmail());
			branch.setBrIsActive(true);
			branch.setBrIsBillable(true);
			branch.setBrLandline(customer.getCustLandline());
			branch.setBrMobile(customer.getCustMobile());
			branch.setBrName(customer.getCustName());
			branch.setBrPincode(customer.getCustPincode());
			branch.setBrValidityEnd(customer.getCustValidityEnd());
			branch.setBrValidityStart(customer.getCustValidityStart());
			branch.setCountry(customer.getCountry());
			branch.setCustomer(customer);
			branch.setState(customer.getState());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return branch;
	}
	
	/*public List<CustomerConfig> setCustAndBrToConfig(Branch branch) {
		CustomerConfig customerConfig = new CustomerConfig();
		List<CustomerConfig> configList = new ArrayList<CustomerConfig>();
		
		try {
			
			for(CustomerConfig custConfig: configList) {
				custConfig.setCustomer(branch.getCustomer());
				custConfig.setBranch(branch);
				custConfig.setConfig("FACIAL_Recognition");
				custConfig.setConfig("QR_Code");
				custConfig.setConfig("GEO_Fencing");
				custConfig.setConfig("Proximity");
				custConfig.setStatusFlag(true);
				
				configList.add(custConfig);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return configList;
	}*/

	// Call to Azure to create faceListId
	/*public void callAzure() { 

		HttpClient httpclient = HttpClients.createDefault();

		try { 
			URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/face/v1.0/facelists/{faceListId}");


			URI uri = builder.build(); 
			HttpPut request = new HttpPut(uri);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", "{subscription key}");


			// Request body 
			StringEntity reqEntity = new StringEntity("{body}");
			request.setEntity(reqEntity);

			HttpResponse response = httpclient.execute(request); HttpEntity entity =
					response.getEntity();

			if (entity != null) { 
				System.out.println(EntityUtils.toString(entity)); 
			} 
		} catch (Exception e) { 
			System.out.println(e.getMessage()); 
			} 
		}*/



	//Save to Branch Table
	private int saveBranch(Branch branch) {

		try {
			branchRepository.save(branch);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return branch.getCustomer().getCustId();
	}

	//Save to custConfigTable
/*	private List<CustomerConfig> saveCustConfig(List<CustomerConfig> custConfig){
		try {
			for(CustomerConfig customerConfig: custConfig) {
				customerConfigRepository.save(customerConfig);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return custConfig;
	}*/
	
	//Set Customer to Login 
	private Login setCustomerToLogin(Customer customer){
		Login login = new Login();
		try {

			login.setRefCustId(customer.getCustId());
			login.setLoginName("admin@"+customer.getCustCode());
			login.setLoginMobile(customer.getCustMobile());
			login.setLoginIsActive(true);
			login.setLoginCreatedDate(new Date());
			login.setRefEmpId(0);
		} catch (Exception e) {
			e.printStackTrace();
		}		

		return login;
	}

	//save to Login Table
	private int saveLogin(Login login){
		try {
			String randomPassword = loginRepository.generatePassword();
			login.setLoginPassword(Encoder.getEncoder().encode(randomPassword));
			loginRepository.save(login);
			String mobileNo = login.getLoginMobile();
			String mobileSmS = "Greetings from LNG! Your account has been created successfully. "
					+ "The login details has been sent to your E-Mail and password is : "+ randomPassword;	
			String s = messageUtil.sms(mobileNo, mobileSmS);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return login.getLoginId();
	}

	//Finds All Customers which are having isActive is 1
	@Override
	public CustomerListResponse findAll() {
		CustomerListResponse customerListResponse = new CustomerListResponse();
		try {
			List<Customer> customerDtoList = customerRepository.findAllCustomerByCustIsActive();

			customerListResponse.setDataList(customerDtoList.stream().map(customer -> convertToCustomerDtoTwo(customer)).collect(Collectors.toList()));

			if(!customerDtoList.isEmpty() && customerListResponse.getDataList() != null) {
				customerListResponse.status = new Status(false, 200, "Success");
			}else {
				customerListResponse.status = new Status(false, 400, "Not found");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			customerListResponse.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return customerListResponse;
	}


	//Finds the Customer by custId
	@Override
	public CustomerListResponse getCustomerByCustomerId(int custId) {
		CustomerListResponse customerResponse = new CustomerListResponse();
		try {
			Customer cust = customerRepository.findCustomerByCustId(custId);
			if(cust != null) {
				CustomerDtoTwo custDto = convertToCustomerDtoTwo(cust);
				custDto.setCustLogoFile(byteTobase64(cust.getCustLogoFile()));
				customerResponse.data = custDto;
				customerResponse.status = new Status(false, 200, "Success");
			}
			else {
				customerResponse.status = new Status(true, 400, "Not found");
			}
		} catch (Exception e) {
			customerResponse.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return customerResponse;
	}


	//Updates the Customer details
	@SuppressWarnings("unused")
	@Override
	public CustomerResponse updateCustomerByCustomerId(CustomerDto customerDto) {
		CustomerResponse customerResponse = new CustomerResponse();
		final Lock displayLock = this.displayQueueLock; 
		try {
			displayLock.lock();
			Customer customer = customerRepository.findCustomerByCustId(customerDto.getCustId());
			// Login login = loginRepository.findByRefCustIdAndLoginMobileAndLoginIsActiveAndEmployee_EmpId(customer.getCustId(), customer.getCustMobile(), true, 0);
			Login login = loginRepository.findByRefCustIdAndLoginName(customer.getCustId(), "admin@"+customer.getCustCode());
			Country country = countryRepository.findCountryByCountryId(customerDto.getRefCountryId());
			State state = stateRepository.findByStateId(customerDto.getRefStateId());
			IndustryType industryType = industryTypeRepository.findIndustryTypeByIndustryId(customerDto.getRefIndustryTypeId());
			Customer customer1 = customerRepository.getCustomerByCustMobile(customerDto.getCustMobile());
			Customer customer2 = customerRepository.getCustomerByCustEmail(customerDto.getCustEmail());
			if(customer1 == null || (customer.getCustId() == customerDto.getCustId() && customer.getCustMobile().equals(customerDto.getCustMobile()))) {
				if(customer2 == null || (customer.getCustId() == customerDto.getCustId() && customer.getCustEmail().equals(customerDto.getCustEmail()))) {
					if(customer != null) {
						customer.setCountry(country);
						customer.setState(state);
						customer.setIndustryType(industryType);
						customer.setCustAddress(customerDto.getCustAddress());
						customer.setCustCity(customerDto.getCustCity());
						customer.setCustCode(customerDto.getCustCode());
						customer.setCustCreatedDate(new Date());
						customer.setCustEmail(customerDto.getCustEmail());
						customer.setCustIsActive(true);
						customer.setCustLandline(customerDto.getCustLandline());
						customer.setCustMobile(customerDto.getCustMobile());
						customer.setCustName(customerDto.getCustName());
						customer.setCustNoOfBranch(customerDto.getCustNoOfBranch());
						customer.setCustPincode(customerDto.getCustPincode());
						customer.setCustValidityEnd(customerDto.getCustValidityEnd());
						customer.setCustValidityStart(customerDto.getCustValidityStart());
						if(customerDto.getCustLogoFile() == null) {
							customer.setCustLogoFile(customer.getCustLogoFile());
						} else {
							customer.setCustLogoFile(base64ToByte(customerDto.getCustLogoFile()));
						}

						customer.setCustGSTIN(customerDto.getCustGSTIN());
						customerRepository.save(customer);

						if(login != null) {
							login.setLoginMobile(customer.getCustMobile());
							loginRepository.save(login);
						}

						customerResponse.status = new Status(false, 200, "updated");
						
					} else {
						customerResponse.status = new Status(false, 400, "Customer not found");
						
					}
				} else {
					customerResponse.status = new Status(true, 400, "Customer email id already exist");
					
				}
				
			} else {
				customerResponse.status = new Status(true, 400, "Customer mobile number already exist");
				
			}
		} catch (Exception e) {
			customerResponse.status = new Status(true, 500, "Oops..! Something went wrong..");
			
		}
		finally {
			displayLock.unlock();
		}
		return customerResponse;
	}

	//Deletes the customer which means the custIsActive will set to zero(0)
	@Override
	public CustomerResponse deleteCustomerByCustomerId(int custId) {
		CustomerResponse customerResponse = new CustomerResponse();

		Customer customer = customerRepository.findCustomerByCustId(custId);
		List<Branch> branches = branchRepository.findAllByCustomer_CustId(custId);
		
		try {
			if(customer != null) {
				if(!branches.isEmpty()) {
					customer.setCustIsActive(false);
					customerRepository.save(customer);
					for(Branch branch: branches) {
						branch.setBrIsActive(false);
						branchRepository.save(branch);
					}
					customerResponse.status = new Status(false, 200, "deleted");
				} else {
					customerResponse.status = new Status(true, 400, "Branch not found");
				}	
			} else {
				customerResponse.status = new Status(true, 400, "Customer not found");
			}
		} catch (Exception e) {
			e.printStackTrace(); 
			customerResponse.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return customerResponse;
	}

	// Search customer by name or code 
	@Override
	public CustomerListResponse searchCustByNameOrCode(String cust) {
		CustomerListResponse customerListResponse = new CustomerListResponse();
		try {
			if(cust.length() >= 3 && cust.length() <= 10) {

				List<Customer> customerDtoList = customerRepository.searchAllCustomerByNameOrCode(cust);

				customerListResponse.setDataList(customerDtoList.stream().map(customer -> convertToCustomerDtoTwo(customer)).collect(Collectors.toList()));

				if(customerListResponse != null && customerListResponse.getDataList() != null) {
					customerListResponse.status = new Status(false, 200, "Success");
				}else {
					customerListResponse.status = new Status(true, 400, "Not found");
				}
			}else {
				customerListResponse.status = new Status(true, 400, "Data too long or too less");
			}
		}
		catch (Exception e) {

			customerListResponse.status = new Status(true, 500, "Something went wrong");
		}
		return customerListResponse;
	}


	public CustomerDto convertToCustomerDto(Customer customer) {
		CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
		customerDto.setRefCountryId(customer.getCountry().getCountryId());
		customerDto.setRefStateId(customer.getState().getStateId());
		customerDto.setRefIndustryTypeId(customer.getIndustryType().getIndustryId());
		return customerDto;
	}

	//String Logo = "iVBORw0KGgoAAAANSUhEUgAAAdYAAAHoCAYAAAD0as6HAAAABGdBTUEAALGOfPtRkwAAACBjSFJNAACHDwAAjA8AAP1SAACBQAAAfXkAAOmLAAA85QAAGcxzPIV3AAAACXBIWXMAABJ0AAASdAHeZh94AAAAIXRFWHRDcmVhdGlvbiBUaW1lADIwMTk6MDQ6MjQgMTA6Mjc6NTbFaTbFAABYEElEQVR4Xu3dB3hUVeIF8DPpvUxC70gHqdKsWBCR3rvuqn8bghRFBAVFQERQQEXXdd2VGnpvInalSi/SeyeT3iaTmf+7j8suKpkkZObNvDfn931+eo+riyGZ88otJocCROQVslf/DlN0CILvqywTItIbP/lnIvKw7A1HkTRgMSzd5sG6+YxMiUhvWKxEXiDnuxNI6rsQjhwbHOlWWLoo5brjvPy7RKQnLFYiD8v58RQsPebDkZUrE8Cemg1Lxzmw7r4oEyLSCxYrkQeJR76W7vPgyPxfqd5gT85CYofZyD1wWSZEpAcsViIPsW4/pz7yFY9+8+NIzETi47OQ+/tVmRCRt2OxEnmAeMRr6TRXfeRbEPvVDFiUcrUdS5QJEXkzFiuRxnL3XYKl/Wz1UW9h5V1KU+9cbaeSZEJE3orFSqQh8Ug3UZSqJVMmhZd3LgWJj30F2+lkmRCRN2KxEmlEPMoVj3TFo93blXcmWf135J1PlQkReRsWK5EGxCNc8ShXPNItLttJC661c82/i4hcj8VK5Gbi0a14hCse5bpK3pFrSGw3G3nFuPslIvdgsRK5kXhkqz66PeP696K2Q1dgUe5c7YlFf19LRO7DYiVyE/GoVjyyFY9u3SV3/2UkdpxTpBnGROReLFYiNxCPaNVHtUeuycR9cnddgKVz4dbEEpH7sViJXEw8mhWPaMWjWq1Yt51DYpd5sKfnyISIPIXFSuRC6v6+Heeoj2i1lrv5DJK6z4c9M/8tEonI/VisRC5iT8tRH8mKR7OeIk7KSeqVAEf2Xzf1JyJtsFiJXMCeYYWl6zz1kayn5Ww6AUvv62e7EpH2WKxExSQevSZ1mw/rL6dl4nk5Xx+FZcBiOHLzZEJEWmGxEhWDeOQqHr3m/HhSJt4jZ/XvSP77UjhsLFciLbFYiW6Tw2pDUt9F6qNXb5W19ACSn10BR55dJkTkbixWotsgHrEmDViE7PVHZOK9shL2IvnFlXDYWa5EWmCxEhWReLSa9NRSZK86LBPvlzV7N1IGr4HD4ZAJEbkLi5WoCMRdX/JzK5C95IBM9CPzy9+Q+sp6liuRm7FYiQpJLdUXVyFr/l6Z6E/Gp1uR+vrXckRE7sBiJSoEcZeX+vJaZM3aJRP9ypixGaljN8kREbkai5WoEMQj1Ix/7ZAj/Ut//yekTvhejojIlVisRAVIGfW1+gjVaNKVYk1TCpaIXIvFSuRE2tvfImPar3JkPGljNyF9hnH/+4g8gcVKlI+0d39A2ns/ypFxpb6+EekzjXdHTuQpLFaiW0ib8hPS3vlOjgxOTMx6dT0yvjTOO2QiT2KxEv1J+kebkTbGx2bNKuUqNpDInK3/Wc9EnsZiJbpJxufbkDrSR9d52h3qOt1MHa/TJfIGLFYiKeM/O5EydJ169+az8sTOUsuRpcOdpYi8BYuVSJE5dzdSXlrl26V6g00p16eWImvV7zIgoqJgsZLPy1y4D8nPr1QfhdJ14vSeZHF6zzrvP72HyNuwWMmnZa04iORnlqmPQOmPHNY8JPVbiOxvjsmEiAqDxUo+K3vtYSQ/uUR99Em35si2Ian3AuT8eFImRFQQFiv5pOyNR5W7sUXqXRk558jMhaX7fOT8ekYmROQMi5V8Ts53J5S7sIVw5NhkQgVxpFth6ToX1q1nZUJE+WGxkk/J+ekULD0T4MjKlQkVliM1B5bOSrnuvCATIroVFiv5DOuWs+ojTUeGVSZUVPaUbFg6zkHuvksyIaI/Y7GST7DuOK/ebTnScmRCt8tuyURiu9nIPXhFJkR0MxYrGV7unovqXZY9NVsmVFz2axlKuc6C7cg1mRDRDSxWMrTcA5dxrf1s2JOzZEKuYr+cjsTHlXI9YZEJEQksVjKs3N+vqh/8jsRMmZCr5V1IvV6up5NlQkQsVjIk27FEWNrNgv1qhkzIXfLOJCPxsa+QdzZFJkS+jcVKhmM7laTeReVdTJMJuVveafk1V+5giXwdi5UMRdw1JbZVPuDP8e5Ja7bjiUh6YrEcEfkuFisZRt751OuPJJW7J9KeX3QIoiY/JkdEvovFSoaQdykN18Tyj5OcoeoJflEhMK/sj6DGZWVC5LtYrKR7eVczkNh+NvK4ptIjTJHBMC/vh6Cm5WVC5NtYrKRrYhcgS4fZsHEXII8whQfBvLgPglpUkAkRsVhJt8SmD4kd5iB3L/et9QRTWKBaqsH3VZYJEQksVtIle9r1k1Zyd/GkFU8whQQgNqEXgh+oIhMiuoHFSrpjzxBng86Ddds5mZCWTMFKqc7vhZBHqsmEiG7GYiVdEeeoJnVXSvWX0zIhLZmC/BE7pwdC2lSXCRH9GYuVdMORnQtLz/nI+eGUTEhTAX6I+aobQtrVlAER3QqLlXTBYbUhqd9C5Gw6IRPSlFKqsV92RWinOjIgovywWMnrOWx5SHpyMbLXHZUJacpfuVP9RyeEdq8nAyJyhsVKXs2RZ0fS08uQveJ3mZCm/EyImdkBYX0ayICICsJiJa/lsNuR/OxyZC/aLxPSlMmE6I/bI2xAIxkQUWGwWMkrORwOJL+4Clnz98qENCVK9cO2CP9bExkQUWGxWMnriFJNGbIWWbN2yYS0FjW5DcKfbSZHRFQULFbyOqmvrkfmP7fLEWktamJrRAxsIUdEVFQsVvIqKaM3ImPmVjkirUW+/TAihtwjR0R0O1is5DVSx32LjA9/kSPSWuQbrRD56n1yRES3i8VKXiFt0g9In/SjHJHWIkbej8hRreSIiIqDxUoel67cpaaN+06OSGvhw+5B1JiH5IiIisvkEFMwiTwk/ePNSB2xQY5IaxGDWiLqvTZyRESuwDtW8piMf25H6mtfyxFpLey5Zoic9KgcEZGr8I6VPCLjPzuRMnCVWLQqE9JS+NN3IWpGO5hMJpkQkavwjpU0lzl/D1JeYql6SugTjRA1/XGWKpGbsFhJU5kL9yH52RWAnaXqCaF9G6ib6pv8+KNP5C786SLNZK04iORnlgF5dpmQlkJ73qke/8ZSJXIv/oSRJrLXHkbyk0sAG0vVE0K71kXMF51h8uePPJG78aeM3C77m2NI6r8IDmueTEhLwe1rIebfXWEK8JcJEbkTi5XcKuf7E0jqtQCObJtMSEshbavDPKc7TIEsVSKtsFjJbXJ+Pg1LjwQ4snJlQloKfqQaYuf2hCkoQCZEpAUWK7mFdetZWLrNgyPDKhPSUlCrqjAv6AVTSKBMiEgrLFZyOevOC7B0ngtHWo5MSEvB91eGeXFvmEJZqkSewGIll8rdcxGWDrNhT8mWCWkpsGVFxC7uA7+wIJkQkdZYrOQyuQcuI7HDHNiTsmRCWgpqVh5xy/rCLyJYJkTkCSxWconcw1eR2F65U72WIRPSUmDjsjAv7we/qBCZEJGnsFip2GzHE2Fpp5Tq5XSZkJYCGpRB3Mr+8IsJlQkReRKLlYrFdioJiW1nIe9CqkxIS4H1SiF+9QD4mcNkQkSexmKl25Z3NuV6qZ5LkQlpKaB2CZhFqcaxVIm8CYuVbou4Q018XCnV00kyIS3514hH3Jon4F8yQibkzE8/HEVWFtdUkzZYrFRkeZfTca3dLPXdKmkvoKoZ8aJUS0fKhJxZs2ofXh++GK8NXYzsbO4CRu7HYqUiybuWgUSlVPMOX5MJacm/Uizi1j0J/3JRMiFnNqzbj3ffXg273YEd205h1CtLYLVy32pyLxYrFZpYn2ppPxu2g1dkQlryrxiDuPXKnWqFaJmQM5s2HsT4MddL9YYtv57Am68tgy2XJy2R+7BYqVDETkqJHecgd+8lmZCWxB1q3NonEKDcsVLBvt/0O94evRJ5tzhUX7xvHfP6cth4NjC5CYuVCmRPy1H3/s397bxMSEviXapaqlXNMiFnRHGOHbXCaXF+/+1hjHtzxS2Ll6i4WKzklD3Dqp5SI06rIe35lYyAWZRq9XiZkDObfz6GN15bitxCPOr9ZsMhTJTvX4lcicVK+RLnqIrzVK0/n5YJackvPhxxawYgsFYJmZAz27acxKhXlVK1Fv796brV+/He+LVwOFiu5DosVrolR44Nlt5KqX5/QiakJbGTUtxqpVTrlpIJOfPb9lMYOWwxcpTv26JatXwPpr73NcuVXIbFSn/hsNqQ1HcBcjYelwlpyS86BOaV/RFYv7RMyJndO89gxJBFxVqjunThb5gxdZMcERUPi5X+wGHLQ9KTi5G97qhMSEvidBpRqkGNy8qEnNm35xxeeXkhsrKKv/HDgnnb8Ml0lisVH4uV/suRZ0fS08uQveJ3mZCWTBFBMC/ri6Cm5WVCzhzYfwHDBy1AZobrtiqc+9VW/HPmD3JEdHtYrKRy2O1IeX4FshftlwlpyRSulOoSpVRbVpQJOXP490sYNjAB6ek5MnGdf3/xC/6j/EF0u1ispE7aSBm4Gplz98iEtGQKC4R5cR8E31dZJuTMsSOXMeSF+UhLy5aJ632u3LXO/WqLHBEVDYvVx6mlOnQtMr/aKRPSkikkALEJvRD8QBWZkDPHj13BYKVUU1KyZOI+n0z/Fovmb5cjosJjsfq41Nc2IPNzfnh4gilYKdX5vRDySDWZkDOnTl7Dy0qpJidlysT9pk3ZqM4YJioKFqsPS33zG2R8zMddnmAK8kfsnB4IaVNdJuTM2dMWDH5+HiyJGTLRhljaOvW9DVi9fLdMiArGYvVRqe98h/SpP8sRaSrADzFfdUNIu5oyIGfOn0tSS/Xa1XSZaEuU66Tx67BhzT6ZEDnHYvVBaZN+QPq7XFLgEUqpxn7ZFaGd6siAnLl4IQWDnpuHy5dTZeIZYj/hd8auxsYNB2RClD8Wq49Jn/YL0sZ9J0ekKX/lTvUfnRDavZ4MyJnLl1KVUp2LSxdTZOJZolzHvbFKPZKOyBkWqw9J/2QLUkdtlCPSlJ8JMTM7IKxPAxmQM1evpKmPfy+cT5aJdxDHzIkj6X7+kTuTUf5YrD4i45/bkTpigxyRpkwmRH/cHmEDGsmAnEm8lq6W6tkzFpl4F3Ek3RsjlmHrZh5QQbfGYvUBGV/tVNeqqrMwSFuiVD9si/C/NZEBOZNkyVCX1Jw+lSgT72S12tTTdLZvPSkTov9hsRpc5vw9SBm4CuBhzh4RNbkNwp9tJkfkTEpyplqqJ45flYl3E0fUiXIVp+sQ3YzFamBZi/cj+bkVLFUPiZrYGhEDW8gROZOamqWW6rGjV2SiD+JUHXG6zv6952VCxGI1rKyVh5D89DLAZpcJaSny7YcRMeQeOSJn0tOyMXRgAo4cviwTfRGn6wx7KQEHD1yQCfk6FqsBZa87guQnFsORmycT0lLkG60Q+ep9ckTOZGTkYNigBBw6cFEm+iRO2Rn20gLl4uCSTMiXsVgNJvubY0jqtxAOK0vVEyJG3o/IUa3kiJzJyrLilcELsH+vMe70UlOyMOSFBPWgAPJtLFYDyfnhJJJ6L4Aj2yYT0lL40HsQNeYhOSJnsrNy8erLi7Bn1zmZGEOynIAlDgwg38ViNYicn0/D0n0+HJm5MiEtRQxqiegJreWInMnOzsVrwxZj547TMjEWcVCAug73tHeuwyX3Y7EagHXrWVi6zYMjwyoT0lLYc80QOelROSJnxPrP0a8uNfz6T3FggDfuHEXaYLHqnHXnBVg6z4UjLUcmpKXwp+9C9AdtYTKZZEL5ETsWiVLd/MtxmRibODhAPUDgkmcPECDtsVh1LHfvJVg6zoE9JVsmpKXQJxohavrjLNVCsCmlOvb15fjlp2My8Q0XLyTjpWfn4sqVNJmQL2Cx6lTuwStIbD8bdkumTEhLoX0bqJvqm/z4I1QQm82Ot99Yge+/PSwT33LjPFmxBzL5Bn4q6JDtyDUktpsF+7UMmZCWQnveqR7/xlItmDgNZvzYldi00bePWjtzKlGdLSz2Qibj4yeDzthOWJD4uFKql3n16wkhnesg5ovOMPnzR6cg4vzSd8etwdfrDsrEt4k9kIcMTEBKSpZMyKj46aAjttPJaqnmXeBkCE8Ibl8LsV91gynAXyaUH4fDgffGr8XaVftkQsLRw5cx5MX5SEvjvAgjY7HqRN7ZFCQ+9hXyznD6vieEtK0O85zuMAWyVAsiSnXqpA1YtXyPTOhmhw9dUrc/FNs5kjGxWHUg72La9TvV00kyIS0FP1INsXN7whQUIBNyZtqUb7B00U45ols5sO+8up2j2NaRjIfF6uXyrqSrE5Vsx7374GejCmpVFeYFvWAKCZQJOfPRB5uwaP52OSJnxHaOYltHsb0jGQuL1YvZEzNhaT8btt/1cfCz0QTdWwnmRb1hCmWpFsanH3+H+XO2yhEVhtjWceTwxeqh6WQcLFYvZU/KwjWlVHP36/OMSr0LbFkR5qV94RceJBNy5ovPfsTsLzfLERXFti0n8caIperOVGQMLFYvJHZSSuw4B7Y9+j6jUq+CmpVH3DKlVCOCZULO/OeLX/Dl5z/LEd0OsSPVmJHL1R2qSP9YrF7Gnp6DxC5zkfvbeZmQlgIbl4V5eT/4RYXIhJyZ89UWfD7zBzmi4vjhu8PqDlVipyrSNxarF7FnWGHpOg+5W87KhLQU0KAM4lb2h19MqEzImflzt2Lm9G/liFxB7FA14a1V6uYapF8sVi/hyMqFpUcCrD8b84xKbxdYrxTiVw+AnzlMJuTMooTt+PiDTXJErrRh7QFMemcty1XHWKxewJFjg6XPQli/PyET0lJA7RIwi1KNY6kWxvLFOzHt/Y1w8HPfbVav2IMp765XN9sg/WGxepgjNw+W/guR8/VRmZCW/GvEI27NE/AvGSETcmbl8t2YMmkDS1UDy5fswrTJG+WI9ITF6kEOWx6SnlyCnDVHZEJaCqhqRrwo1dKRMiFn1q3Zj8nj1/ERpYYWLdiBj6fxkbvesFg9xJFnR/Izy5G9nCd/eIJ/pVjErXsS/uWiZELObFx3ABPGclKNJ8ybtRWff/K9HJEesFg9wGG3I+WFlchayJM/PMG/Ygzi1it3qhWiZULObNp4COPGsFQ96T//+pVrhXWExaoxMRkhZdAaZM7ZLRPSkrhDjVv7BAKUO1Yq2I/fH8Hbo1eoB5aTZ6m7W/2bu1vpAYtVQ2qpDl2LzH//JhPSkniXqpZqVbNMyJmffzyKN19bxg0LvMinH32nrh8m78Zi1VDayK+R+TlP/vAEv5IRMItSrR4vE3Jmy6/H8caIZdy/1guJ9cNLF/Li3JuxWDWSOuYbpH/Exzie4Bcfjrg1AxBYq4RMyJntW0/i9eFLYLXyxBVvJJY6TX1vA1Yu3SUT8jYsVg2kjv8O6VM48cATxE5KcauVUq1bSibkjDjG7LWhPMbM24lynTxxPdas4gRIb8RidbO0939C+kRuUu4JftEhMK/sj8D6pWVCzuzdfRavDlmE7GwevK0HYpb2u2+vVpdCkXdhsbpR+rRfkDaWi7s9QZxOI0o1qHFZmZAz+/eex/BBC5GVaZUJ6YEoV7EU6tuNh2RC3oDF6ibpM7cidRS3I/MEU0QQzMv6IqhpeZmQM4cOXMSwQQuQkZEjE9ITsRTqrdEr1KVR5B1YrG6Q8eUOpL66Xo5IS6ZwpVSXKKXasqJMyJnDv1/C0JcSkJ6WLRPSI7Ek6s2Ry7D552MyIU9isbpY5uxdSBm85vrsAtKUKSwQ5sV9EHxfZZmQM8eOXsHQFxOQmpIlE9KzXGseRr26FNu2nJQJeQqL1YUy5+9F8ourAG79pjlTSABiE3oh+IEqMiFnTh6/ipdfmI/k5EyZkBGI2dwjhy1WZ3eT57BYXSRryQEkP7cc4NZvmjMFK6U6vxdCHqkmE3Lm9KlEDHp+HpIsGTIhIxGzusXsbjHLmzyDxeoCWat+R/JTSwFu/aY5U6A/YmZ1R0ib6jIhZ86esWCwUqqWRJaqkYnZ3a8MXogD+y/IhLTEYi2m7HVHkDxgkXpgOWkswE8p1W4I7VBLBuTMhfPJGPzcPFy9kiYTMrL09BwMHZiAw4cuyoS0wmIthuxvjiGp30I4rCxVzSmlGvtlV4R2qiMDcubSxRQMUkr18uVUmZAvELO9h7yYgGNHLsuEtMBivU05P55EUu8FcGRz6zfN+St3qv/ohNDu9WRAzlxR7lBFqV68kCwT8iUpKVl4WSlXMWGNtMFivQ05v56Bpft8ODK59Zvm/EyImdkBYX0ayICcuXY1DYOfm4vz55JkQr5ITFQb/MJ8nDmdKBNyJxZrEVm3nYOl61w40rn1m+ZMJkTPaIewAY1kQM5YxIfp8+LD1CIT8mWJ19LVd+znzvIiy91YrEVg3XkBlk5z4Ejl1m+aE6X6YVuEP3WXDMiZ5KRM9UP01MlrMiG6/lpAzAq/eCFFJuQOLNZCyt13CZaOc2BP4dZvnhA1uQ3Cn20mR+TM9Xdq83GC79ToFsRENlGuVziRzW1YrIWQe+gKEtvPht3CXWo8IWpCa0QMbCFH5EyaOgt0Po4e5ixQyp945y7KVbyDJ9djsRbAduQaEh+fBftVLqj3hMi3H0bE0HvkiJwRSyvE3r+HD12SCVH+xLt3MaFJvIsn12KxOmE7YbleqpfTZUJaihzdCpGv3idH5Eym2Gnn5QU4eIA77VDhnTpxDUOUck3hntEuxWLNh+10slqqeRf4HsITIkberxYrFSwr6/r2dXt3n5cJUeGJU47EJhLiNQK5Bov1FvLOpcAiSvUMF9R7QvjQexA15iE5ImfEhuuvDV2M3TvPyISo6NRzeZVyzUjnigdXYLH+Sd7FNCS2nQXbSa7984SIQS0RPaG1HJEzN44I27HtlEyIbp94jTB8cIL6WoGKh8V6k7wr6Uhsr5Tqce5O4glhzzVD5KRH5YicsVptGPXqEh5qTS4lXieMGLII2VncVa44WKySPTETlvazYTvEtX+eEPZUE0R/0BYmk0kmlB9bbh7eHLkcm38+LhMi1xGHpL82bLH6moFuD4tVYU/OQmKH2cjdz7V/nhD6RCN1q0KWasFsNjvGjFqBn74/IhMi19u+9SRGv7pUfTJCRefzxWpPzVZ3VMrdzTMLPSG0T311U32TH6/xCpKXZ8e4N1fg+02/y4TIfTb/clx9MiKekFDR+PSnmT09B4ld5sG6g8sUPCGkRz3EfN6ZpVoIdrsD48euwjcbDsmEyP3Ek5Gxo1eoT0qo8Hz2E82eaUVS9/nI3cxlCp4Q0rkOYv/VBSZ/lmpBRKm+O241Nqw9IBMi7Xz3ze94Z8wq9YkJFY5Pfqo5snJh6TEfOT9ymYInBLevhdivusEU4C8Tyo/D4cD7E9djzcp9MiHS3sb1B5SLuzXqRR4VzOeK1ZFjg6XPQli/4zIFTwhpWx3mOd1hCmSpFkSU6tT3vsaKpbtkQuQ5a1ftw+SJ69TvS3LOp4rVkZsHy4DFyPn6qExIS8GPVEPs3J4wBQXIhJz56INNWLrwNzki8ryVS3erF3ssV+d8plgdtjwk/30pclZzRqUnBLWqCvOCXjCFBMqEnPlk+iYkzN0mR0TeQ1zsiYs+yp9PFKsjz47kZ5Yjayknf3hC0L2VYF7UG6ZQlmph/OOT7zH3q61yROR9xEXfpx99J0f0Z4YvVofdjpQXViJrISd/eEJgy4owL+0Lv/AgmZAz//rHT/jqX7/KEZH3mv3vzfjisx/liG5m6GIV7wFSBq9B5pzdMiEtBTUrj7hlSqlGBMuEnJn15a9qsRLpxZef/6x+39IfGbZY1VIdvg6ZX3LyhycENi4L8/J+8IsKkQk5M/erLfjs4+/liEg/xPctX138kWGLNfX1r5H5GSd/eEJAgzKIW9kffjGhMiFnFs3fjk+mfytHRPojJtstStguR2TIYk0duwkZMzbLEWkpsG4pxK1SStUcJhNyRsywnDZloxwR6de09zdi+eKdcuTbDFesaRO+R/r7fE/lCQG1S8C8ZgD848NlQs4sX7oLU9/bAC4JJCMQ38dTJm3A6uWc02KoYk1TClUUK2nPv0Y84tY8Af+SETIhZ9as3IMpE9ezVMlQxJaHk8avw4Y1vr0KwzDFmv7RZqSN5aJlTwioaka8KNXSkTIhZ8SHzrvj1nLfVTIk9SSmt9Zg00bfPYnJEMWa/tlWpI78Wo5IS/6VYhG37kn4l4uSCTmzaeNB9UOHpUpGJk7CeXv0Cvzw3WGZ+BbdF2vGlzuQOnz99Qf8pCn/ijGIW6/cqVaIlgk5Iw4of2vUSh6/RT5BnOE6ZuRy/PKT7+3NrutiFRs/iA0gWKraE3eocWufQIByx0oF+/H7Ixjz+nKWKvmU3Nw8jH51GbZuPiET36DbYs1csA/JL6wE+EhNc+JdqlqqVc0yIWc2/3wMb45cpl7BE/kaq9WG14cvwW/bfef8a10Wa9aSA0j+v2UAr/4151ciHGZRqtXjZULOiCv1119ZilxrnkyIfE92di5GDFmE3TvPyMTYdFesWat+R/JTSwFe/WvOLz5cvVMNrFVCJuSMuEIXV+riip3I12Vl5eKVlxdi/97zMjEuXRVr9oajSH5isXpgOWlL7KQUt3qAurMSFWzXb2fUK3RxpU5E12VmWDFs0AL8fvCiTIxJN8Wa8+1xJPVdCEcOr/615hcdAvPK/gisX1om5Mze3efUK3NxhU5Ef5Selo0hAxNw5PAlmRiPLoo158eTsPRMgIMfVJoTp9OIUg1qXFYm5MyB/RfwyuAFyMq0yoSI/iw1JQtDXkzA8WNXZGIsXl+sOb+egaX7fDgyWapaM0UEwbysL4KalpcJOSMebw1VrsTT03NkQkT5SU7KxMsvzMfpU4kyMQ6vLlbr9nOwdJ0LRzqv/rVmCldKdYlSqi0ryoScOXr48vVSTcuWCREVxJKYgcHPz8O5s0kyMQavLVbrrguwdJwDRyqv/rVmCguEeXEfBN9XWSbkjHic9fKL85GSkiUTIiqsq1fSMOjZubhwPlkm+ueVxZq77xIsHebAnsKrf62ZQgIQm9ALwQ9UkQk5c+rENfVxlnisRUS35/LlVAx6bh4uX0qVib55XbHmHrqCxPazYbfwg0prpmClVOf3Qsgj1WRCzpw9bcGg5+epj7OIqHguXkhWHwuLO1i986pitR1LhKWdUqpX+UGlNVOgP2JmdUdIm+oyIWfEO6FBz81F4rV0mRBRcZ09Y1HL1ZKo758rrylW2wkLEtt+hbxL+r9a0Z0AP6VUuyG0Qy0ZkDM3rqyvGODKmsjbiFnCg5/X9+sVryhW2+lkJD4+C3nnjfF8XVeUUo39sitCO9WRATkj3gG99Ow8XLqYIhMicrUTx6/qekKgx4tVlKlFlOoZ48wI0w1/5U71H50Q2r2eDMgZcYcqHv+KO1Yici+xhG3YS/pcwubRYhWPfa+1mwXbSYtMSDN+JsTM7ICwPg1kQM6Id6lGXG9H5M0OHbiIoS8tQEaGvpZdeqxY866kI1Ep1bwj12RCmjGZED2jHcIGNJIBOWOxZKizf88YcIcYIm93YN/569uEZulnoyCPFKs9MROW9rNhO3RVJqQZUaoftkX4U3fJgJxJSc7EkBfmq+tVicgz9uw6h9eGLtbNaVGaF6s9OQuJHecgd/9lmZCWoia3QfizzeSInElNzVI3fzh21JgbhRPpyY5tpzBy2GLk6OCEM02L1Z6aDUunucjddUEmpKWoCa0RMbCFHJEzaeJoqxfF0Va8ACTyFtu2nMQbI5Yi18vP5NasWO3pOUjsMk/dWJ+0F/n2w4gYeo8ckTMZyvfqsJeMfxgzkR798tMxjH19OWw2u0y8jybFas+0Iqn7fORuPiMT0lLk6FaIfPU+OSJnMpXv1eGDE9QJE0Tknb7/9jDGvbkCeXneWa5uL1ZHdi6SeiUg58dTMiEtRYy4Ty1WKlh2Vi5GDFmEvbtZqkTe7psNhzB+7CrY7Q6ZeA+3FqvDakNS30XI2XRCJqSl8KH3IOqth+WInBGzDUcMXYSdO07LhIi83Ya1BzDpnbVwOLyrXN1WrI7cPFj6L0b2+iMyIS1FDGqJ6Amt5YicEbMMXx++RJ11SET6snrFHkydtMGrytUtxeqw5SH570uRs/p3mZCWwp5rhshJj8oROSNmF4pZhls386kKkV4tXbQT06Z8I0ee5/JideTZkfzsCmQtPSAT0lLYU00Q/UFbmEwmmVB+bEqpvjlymTrLkIwjOjpU/hX5kkXzt+OT6ZvkyLNcWqwOu1KqL65EVsJemZCWQp9opG5VyFItmJiq/9YbK/Hjd3xVYSS16pTBghXPo217Hizhi+Z+tRWfz/xBjjzHZcUqnm+nDF6DrNm7ZUJaCu1TX91U3+TnttfmhiGm6L8zZhW+3XhIJmQE1aqXxIcf90JUVCheH9MeDz7C84V90X+++AVf/vNnOfIMl30Kp76yHplf/iZHpKWQHvUQ83lnlmohiKn5745bg43r+arCSCpXice0T/sgOiZMHQcE+OHtCZ1w7/3V1TH5li8+/VG5e90iR9pzySdxyqivkfHpVjkiLYV0roPYf3WByZ+lWhBRqu+NX4u1q/bJhIygXPlYtVTN5nCZXBcQ6I933uuCZi2qyIR8ySfTv8WCedvkSFvF/jROHbsJGdN+lSPSUnD7Woj9qhtMAf4yofyIVxVT3l2PVcv3yISMoHSZaMz4rC9KloyUyR8FBwfg3and0KBReZmQL5kx9RssXaj9k9RiFWvaxO+R/v5PckRaCnmsBsxzusOkXJWTc6JUp03eiOVLdsmEjCC+RASmK3eqZcpGy+TWQkODMGVGL9StV1Ym5CvE0tap723AyuXazv257WJNm/IT0sZ/L0ekpeCHqyJ2Xg+YggJkQs58/OG3WLRghxyREcSawzHj076oUNEsE+fCw4Mx9eNeqF6zlEzIV4hynTx+Hdat0m61ym0Va/pHm5E2xjvWC/maoFZVYV7YB6aQQJmQM59+9B3mz+H7fyOJig7F9Jm9UblqvEwKR8wWnvaJ8s9VKdo/R/on5ldMeHsNNm7QZtJikYs14/NtSB35tRyRloLurQTzot4whbJUC+OfM3/A7H9vliMygoiIYHyg3HlWq3F7d57qne5nfVC+QqxMyFeIch33xip8v8n9OwIWqVgz/v0bUoauu35vTZoKbFkR5qV94RceJBNyRqxj+/cXv8gRGUFomHhX2hN16hbvXWl8iUh1wpOY+ES+RaxhHztqBX764ahM3KPQxZo5ZzdSBq1mqXpAULPyiFumlKpytU4FE3epYh0bGYeY3fveB91Rv2EFmRTPjdnEYgIU+RZ1f/DXlmLzL8dl4nqFKtbMBfuQ/MJK5V6apaq1wEZlYV7eD35RITIhZ+bP3qq+VyXjCAzyx8Qp3XBXs8oycQ3xOFhMgBKPh8m35FrzMOqVJdi+9aRMXKvAYs1afhDJ/7dMuYf2zpPajS60XwP4xXBT8cJYuXQXPp7GSXVG4u/vh7ff7YyW99whE9cSE6A+/KQXIiN54eprxHGRI4ctxq7fzsjEdZwWa/bq35H85BLAxlL1lNTXNiBzIXcKKowGjSvy7sNA/PxMGDO+A1o9WFMm7lGjZml8OLO3uiSHfEtWVi5eeXkh9u4+JxPXyLdYszccRdKAxeqB5eRB4hi+Z5YhayU3jC9Ipcpx+OizvjDHsVz1TpTqyDGPo3WbujJxLzEh6v3pPRDKGfc+JyvTilcGL8DBAxdkUny3LNac704gqe9COJRbZfICNqVcn1yiXuyQc1XuKKHuxhMTe30zdtIfcerhsJFt0L5jA5loo2Hjinh3ancEceMVn5OenoOhLybg8O+XZFI8fynWnJ9OwdIzAQ7lFpm8h7jIERc7Od+fkAnl545qJTFDnHTCA691afDwR9C1e2M50pbYsH/C+10QyK1CfU5aWrZarsePXZHJ7ftDsVo3n4Gl2zw4MqwyIW8iLnYsPRKQ8/NpmVB+xAYC4sQTsUsP6cdzA1uhV99mcuQZ99xXHWPHd1QnTpFvSU7OxODn5+PUiWsyuT3//c6x7jgPSxelVNNZqt5MXPRYus+DdbtrX7YbUc1apdUt7CI441MX/vbMPXjy6bvlyLMeal0bo95qr77rJd+SZMnAoOfn4expi0yKTi1W6+6LsHScA3tqthqSd3Ok5sDSaS5y91yUCeWnVp0y+FCUKzfX8Gp9BjTDsy8+IEfeoW27ehgx6jH1nS/5lsRr6cqd6zxcOJ8sk6LxsydnXS9V5c+kH+L361r72cg9WPz3AUYnjgv74GMup/BWXXs0xktDHpYj79KxayMMHvaIHJEvuXw5FS89OxcXL6TIpPD8xOYDEa/cI4ekJ47ETCS2mwXb0eK9D/AF9eqXw9SPeqr7zZL3aNexPoaPbKPcFXrvbWGvfs3Ud7/key5dTFHvXK9cSZNJ4fi/pQhqXkH5KxOsP5ySMemFeOeavfowQjrU4g5NBShVOhr1G5THd98cgo2bnnjcI21q4423OyhX994/Sahh4wrqBu67d56VCfmKtNRs/PrzcTz4cC2EFfLCXC1W8RfB91aGw5oH66+u396J3Eu8c81efQShnWrDL5oTdZwpUzZavXv97pvfWa4e9MCDNfH2xM4ICNDPzNsmTSsjMzMH+/eelwn5ipTkLHXT/gcfqYXQ0ILL9b/FKgQ/WBX2pCzkbuc3jt44UrKRs+4oQrrUgV8k3yU6U7ZcDGrXLauWq7gLIW21vPcOTHi/qy7Xiop1rkmWTPx+kBMHfU1yUia2bT6Jh1rXQkiI8x26/nK5GPX+Ywj7m2cWZ1Px2I4nIrH9bORdzZAJ5Ud8QL47tRt32dGYuOubMFmfpSqId8HinXC7jnfKhHzJsaNXMGRggrqZhDN/KVbxjRP9cXuE9uI3jh7ZDl2BpcNs9ckDOdfi7jswUdw5BXGXHS2Is1QnT+te4NW+t1P3MX6zHR55tI5MyJccPnQJw15agIz0HJn81S1fcJj8/BDzz84I6VhbJqQnuXsvIZHrkgvl7vuqYfx7+r2D0ovadctg6oyehXo/pQdiV6Yx73TAfQ9Ulwn5kgP7zmP44ARkZt56Q6V8Zw6YAvwR+1U3BLd2zzmI5F65v52Hpes82Lk9ZYHEh+O4SfqaSKMn1WuWUjfpCDfYJh0BysXYO+91QfOWVWVCvmTv7vMYMWQRsm+xr77TTxJTcADMCb0RfL9rT+4nbYgZ3knd5/FAhUIQs1THTezM/WFd7PpB4r0RFWXMpWDiHf27U7qhUZMKMiFfsnPHaYwcvlg9NP1mBX6KmEIDEbuoD4KalZcJ6UnOD6dg6Z3AIwALodUjtfDWRG6+7ioVKprVU4bMBj98PkT5jJw8rSfq3llOJuRLtm05iVGvLIHV+r/P2EJ9gojlG+bl/RDYsIxMSE9yNh6HhYfWF8rDrevgjXHcfL24ypSNwozP+iK+RKRMjE1sl/nBx73Ugx/I94g1rm+OXA6b/Iwt9KW52NXHvLI/AmqXlAnpSc7q35H09DI4uG6zQG3a1sNodUcgluvtKFEyEtM/7YdSpaNk4hsiI0PUx97isH3yPT99fwSLFu5Q/7pIz7z848MRt3oAAqqaZUJ6kr14P1KeXwGHneVaEHGyycgxj7Nci8gcF67eqZavECsT3xITG4bpM3urj8HJt4hJbN173qX+dZFfJvmXiYR57RPwrxgjE9KTzLl7kDJ4DRwOh0woP+07NuCxYUUQHR2KaTP7oFLlOJn4JvH4W1xciMfh5BvqNyyHiVP+t2zvtmZpBCilGrdmAPxL+8b7E6PJ/PI3pI7YIEfkjDg2bPjrLNeCiMPkxWPQatX5qkgQj8FnfNZPfSxOxlajZim8P73XH9Zo31axCgF3xMG8egBMcWEyIT3J+GQLUt/8Ro7Ima7dG2PoiEdZrvkQR/FNmd5TPVSe/qdc+VhM/7QPYg0+K9qXVaxkVietiffrN7vtYhUC65RE3Mr+PFFFp9Kn/oy0Cd/LETnTvdddeGmYdx7G7Ulie8L3p/VA/YZcjncrlavEq+9cxWNyMpZSpSPVVx/muAiZ/E+xilUIalRWXYpj4okquiSKNU0pWCpYn37NMfDlh+SIxB7L4iCDxndVkgndSrUapTBVuavhIfvGISbpTf+0L0qXiZbJHxW7WAVxULp5UR+YwvS9ubavSnvzG6TP3CpH5Ey/J1vg+ZdayZHvEts/jp/E7fwK6+KFFORkcwc0IxDzCaZ+1AsVK+U/Sc8lxSqIbQ9j5/dUt0Ek/Ul9dT0y/v2bHJEzTzx1N5554X458j1iZ6qxEzrhvlY1ZELObP75GMa9sRJ2O2fi65149TFleo8CNwJxWbEKIa2rI+arbsrlrEv/taQFhwMpg1Yjc+5uGZAzT/3fvXj6uXvlyHeIdb2jxjyOh1vz5KvC2PXbGYx6dSlyueuZ7omlNGJJjTj+sCAub8DQjrUR83ln5d/MKZS6o1xRJz+/EllLDsiAnHn6ufvx5NN3y5HxiVnRYl1v2w71ZULOHDpwUT395M8btJP+iAvKMeM7qmc4F4Zbbi3DetdXD0vn+gQdyrMj+amlyF79uwzImecGtkL/J1vIkbENHvaIuq6XCnb82BUMeykBGRn5H4ZN+iBqbOQbbYv0lMZtz2zD/9YEUZPbyBHpidisP2nAYmRvPCoTcubFlx9Cn/7N5ciYXhz0IHr1ayZH5MzZMxYMeTEBKSlZMiE9E8vs2nduKEeF49aXoREDWyDynUfkiPREHDOX1Hshcr4/IRNyZpDyw9ezb1M5Mpannr0X/f/eUo7ImSuXU5VSnYfEa+kyIT37+zP3qMvsisrts4wih9+LiJG+O4NSz8QB6ZaeCcj59YxMyJmXhz+Cbj2byJEx9H2iOZ55nj+/hWGxZODlF+bj4oVUmZCe9eh1F/7vxQfkqGjcXqxC1JiHED6YV7x65Ei3IqnrPFh3nJcJ5cdkMmHYa4+iczdjvIdUd5sawt2mCiM1NQtDBybg9KlEmZCetW1fD0NGtJajotOkWIWodx9F+NPXj9QhfbGnZsPSaQ5y91yUCeVHlOurox5Dxy5FeyfjbcSvf2gxPlh8SWamFa8MXoCjhy/LhPTsgQdr4vUx7dWf5dulWbGKX2TU9McR2reBTEhP7ElZSOyolOuhKzKh/Ijv9RGj26Jdxztloi/ioHfx6y/OB4uvEEtpXh++BPv3XpAJ6dldzSrj7Xc7qTuLFYdmxSqY/PwQ81lHhHbh4nI9sl/NgKXdbNiO8XFXQcS6N3HV+1i7ejLRhwcfqYXRb7fnAe+FYLPZMeb15di+9aRMSM/q3lkOkz7ojqCg4u8eqGmxCqYAf8T8uxtCHuN2aHqUdykNiY/Pgu10kkwoP6KcRr/VHq0fqysT73b3fdXw1oTiX637ArE94fixK/HT90dkQnp2R7WSmDKjJ8JcdFCCR36CTMoVQey8Hgh+oLJMSE/yzqUgse0s5J3n7MeCiH113xzXAQ+3riUT79SsRRVMmNxV3baNnHM4HJjy7np8ve6gTEjPyleIxTQXH+3nsUtTU0ggYhf3RdDdFWVCepJ3KgnX2inlqtzBknPiDnDshM5o9bB3lmuDRuXV49+CeYBGocyc8S2WL9klR6RnJUtGqofRx8X/9UzV4vDoMx+/8CCYl/RFYOOyMiE9yTtyDYntZyPvWoZMKD+iXMdN9L4TYerWK4spM3ohNJRnhRbGf774BXO/4hGLRhATE4YPP+mNMmVjZOI6Hn+Z4hcdgrgV/RFYt5RMSE9sB68gscMcddYwORcQ6I/x73XBPfdVk4lniaOvPlA+WMLDg2VCziyavx2fz/xBjkjPxPf8Bx/3QpU7SsjEtbxiloJfXBjMqwfAv0a8TEhPbHsuwtJ5Luxp3HC8IOId5oT3u6LlPYU7JcNdxAeKuFqPjAyRCTmzevluTJuyUY5Iz8Qrj8nTeqBWnTIycT2vKFbBv1QE4lcp5VopViakJ9bt52DpOg/2DKtMKD9iOv/EKd3UCUOeULFyHGZ82gcxsWEyIWc2bTyESePXiSOLSefEK5nxk7ugURP3zu3xmmIV/CtEI26tUq5lo2RCemL95TQsPRLgyM6VCeVHXDWLNXNiQbqWxPuk6TNdP1nDqH796RjGvbFSXV5D+iaWv735Tgfcc191mbiPVxWrEFDFDPOaAfArES4T0hPr9yeQ1HcRHFYe7lyQkJBATP6wBxrfVUkm7lWqVBQ++kdflCrNC9fC2LnjNEaPWIrc3DyZkF6JTcReGfUYWrfRZk251xWrEFizBOJWKeUa67p1RaSd7PVHkPTEEjhs/EAqSEhoIN6f3kNd8uJO5rhwTP+sD8qWc/0MSCM6sP8CRgxdpG5ZSPr3wksPorOGh/R7ZbEKgfVLw7yiP0xRnLGoR9krDyHp6WVw5NllQvkRS13Ekpf6DcvJxLXEsoIZn/VFxUpxMiFnjh+7glcGLUAm5wsYwoCnWmp+nrDXFqsQdFc5mBf3hSmca+z0KHvRfiS/uAoOO8u1IGL6/9QZvdX9Sl1JzPr9cGZvVHXTsgKjOXvGgiEvJiAlhcvHjKBrj8bq3arWvLpYheB7KyE2oRdMIdwVRo+yZu9C6pC16jZw5Fx4xPW1da5aBhCmXJCKf59Yr0oFu3I5VSnVeUi8li4T0jNxStOw19rIkba8vliFkIfvQOzsHjBxH1NdyvhiB1Jf2yBH5Iy4wxT7lha3DENDAzFlek+X3wEblcWSgZdfmI+LF7j/tRGITVhGv9XOY6c06aJYhZB2NRHzRRfAXze/ZLpJxsdbkPrWJjkiZ6KiQtVyrVajpEyKRqyTFXv/NmzMfbgLIzU1S7lTnY/Tp3gcohGIWfbjJ3dVdzrzFF21VGiPeoiZ2UH5VfOsSD1Kn/wT0t7llnCFES0mHH3aVz3Oqiiu7+zUBc1aVJUJOZOZacUrgxfg2BEe4G8EteuWwXsfdvf4gRK6u/0LG9AI0R+0vb4wiXQn7Z3vkD7tFzkiZ8TOSGKJTOWqhdvqUxxRN3Z8R00WwBuBWEozcthi7N97QSakZ+LnZOqMXl6x97Uun6uGP9sMURNbyxHpTeqojUj/jCeEFIbZHI6PPuuLSpWdL5UR75LeeLs9HmpdWybkjM1mx5jXl2PHtlMyIT0T67PFjmLesk2nbl9YRrx8NyJHt5Ij0pvU4euR8Z/f5IicEdsPXl+HapbJH4mHNyPfaIs2j9eTCTkjtid8Z8wq/PT9EZmQnsWXiFDPVC1RMlImnqfrmUCiWMOH3SNHpCsOB1JeWo3M+XtlQM6ID40Z/+iH8hX+ekjF0BGPon3nhnJEzohlX+9PXI+N6w/IhPQsKjpUPaWpXHnvOrxF18UqRI9vjbDnmskR6Ypy55D83HJkLeOHXGGUVMr1I6Vcb96WcODLD6N7r7vkiAryyfRvsWLpLjkiPRPrtKd+1KvIE/y0YFKu4HS/cl/8JyQ/v1LdjID0R6xPjp3bAyHta8mEnLl4IQUvPTsb7To2xFPP3itTKsiX//wZX3z6oxyRnoklZVM/6okmTbU9HaqwDFGsgtiTNunvS5G9eL9MSE/EzlqxC3sj5JFqMiFnxJZ70dE8pKKwFs3fjg/f50HlRiBmv094vyvub1VDJt5H94+CbzApX+zYf3VBcDvv/WJT/hzZNiT1XoCcH0/KhJxhqRbe6uW7MW0KS9UI1Nnvb7Xz6lIVDFOsgnikaJ7TE0EP3SET0hNHZi4s3efDuvmMTIiKZ9PGg5g0fp2YK0cGMOTV1mjT7k458l6GKlbBFBwA88JeCLpXm8OjybUc6VZYus6DdScX7VPx/PrTMYx7Y5W6vIb079mBD+hmop7hilXwCwuCeUlfBDV17+HR5B72lGxYOs5B7r5LMiEqmp07TmP0iKXIzeVh+0bQ94nm+NvT+llaachiFfwig2Fe3g+Bd/LILD2yWzKR2H42cn+/KhOiwjmw/wJGDF2kbllI+texa0MMfPkhOdIHwxar4BcbCvOq/gioxUOe9ch+NQOWdrNgO85TR6hwjh29guGDFiAzwyoT0rOHW9fCq68/BpPO9oY3dLEK/iUjELdqAAKq3Ho7OPJueRfTkNhuNmynk2VCdGtnz1gwdGACUlOyZEJ61vKeOzBmfCd1eY3eGL5YBf9yUTCvHQD/8tEyIT3JO5MMy+OzkHeeh1DTrV2+lIohL85D4rV0mZCeNWhUHhMmd1WPQdQjnyhWIaBSLOLWDIBfqQiZkJ7YTlpwrZ1Srpf5wUl/ZElMx8svzMPFC7zwMoIaNUvh/Wk9ERIaKBP98ZliFQKqxyNu9QCY4rzjaCEqmrwj12BpPxv2xEyZkK9LTc3CkIEJOHPaIhPSM3E8othUPyIyRCb65FPFKgTWLYW4lf3hF63v3zhflXvgMhI7KOWazPdovi4z04pXBi/AsSNXZEJ6VrpMtHr8W6w5XCb65XPFKgQ1Kgvz0r4wRQTJhPQkd/dFWDrPhT0tRybka8RSmpHDFmP/Xm4kYgTmuHD1oPKSpaJkom8+WaxCUMuKMC/sDZOOn+P7Muu2c+r2h3blroV8iy03D2++tgw7tp2SCelZZGQIPvy4Nyrkc5C/HvlssQrBraqqx5WZgvQ588zXWX86BUvPBXBk58qEjE5sT/jO2NX4+cejMiE9C1VubKbM6InqNUvJxBh8uliFkMdqIOY/3YAAn/9S6JL12+NI6rsIDit32TE6ccLl+xPXY+N6HoxvBIHKDc3EKd1wZwPjbT3LNlGEdq6DmM86KV8Nfe3uQddlrz+C5L8vgcPGfWGN7JPp32LF0l1yRHomNn14e0InNG9ZVSbGwmKVwvo2QPRH7QGdbZ1F12UtO4TkZ1fAYbfLhIzky89/xrxZW+WI9Ex8xI4c0w6tHq4lE+Nhsd4k/O9NEPXeo3JEepOVsBfJL65SHxmScSyYtw1ffPajHJHeDR7+CNp18P4zVYuDxfonES+1RORYfZ2kQP+TNWsXUoasZbkaxMrluzFj6jdyRHr39HP3oVffZnJkXCzWW4h87X5EjLhPjkhvMv+5HamjNsoR6dWmjQcxefw65SJJBqRrolBFsfoCFms+ot56GOEDW8gR6U3G9F+R9va3ckR68+tPxzDujVXq8hrSv3Yd62Pw8IflyPhYrE5ETW6DsL81liPSm7T3flT/IH3ZueM0Ro9YitxczvI2glYP1cTINx/X3ZmqxWFy8GWUU2KWafLTy5C1YJ9MSG+i3n0UES/fLUfkzQ7sv6CeVMODyo2hWYsqmDytB4KCAmTiG1ishSDWRyb1X4zslYdkQrqiXClHT3sc4f/XVAbkjY4dvYKXnp3Lg8oNQmz8MG1mb4SG+t6e7HwUXAimAH/EzuqG4NZ3yIR0Rbl2FDOFM77aKQPyNmfPWDDkxfksVYOoVqMk3p/ewydLVWCxFpIpKADmhN4Ivr+KTEhXRLkOXIXMhL0yIG9x6WIKBj8/F5bEDJmQnlWsZMa0T3ojKipUJr6HxVoE4iSc2MW9EdS8gkxIV+wOJD+7HFkrDsqAPM2SmK7eqV6+lCYT0rOSJSPx4Sd9YI6LkIlvYrEWkV9EMMzL+iKwYRmZkK7Y7Eh+cgmy1x2RAXlKamoWhgxMwJnTFpmQnsXEhmHap31Qpmy0THwXi/U2+MWEwryyPwJql5QJ6YnDmoekfguRvem4TEhrmZlWDB+0EMeOXJEJ6VmEcsPx4Se9UblKvEx8G4v1NvnHhyNu9QAE3BEnE9ITR7YNSb0SkPMjD8vWWk6ODSOGLMKBfedlQnoWEhKoLqmpWau0TIjFWgz+ZSJhXjMA/hVjZEJ64sjMhaXHfFi3npUJuZstNw9vvrZM3QSC9C8w0B8TJndBw8YVZUICi7WYApRSjRPlWjpSJqQnjrQcWDrPhXXXBZmQu4jtCd8Zuxo//3hUJqRnfn4mjBnfES3vrSYTuoHF6gLicbB57RPwiw+XCemJPSUblg5zkLv/skzI1cQ+NO9PXI+N6w/IhPRM7E44YtRjeLh1bZnQzVisLhJYqwTiVvVXJzaR/tgtmUhsPxu2I9dkQq708YffYsXSXXJEevfi4IfRsWsjOaI/Y7G6UGCDMjAv7wdTZLBMSE/sV9KR+Pgs2E5w+Ycrffn5z5g/Z6sckd797em70e/J5nJEt8JidbGgZuVhXtQHprBAmZCe5F1IvV6uZ5JlQsUxf+5WfPEZTxgyiu697sKzA1vJEeWHxeoGwfdXRmxCL5iCfetEB6PIU0rVopSrKFm6fSuX78bHH2ySI9K7No/XxZBXW8sROcNidZOQR6ohZlY3IIBfYj0Sj4PFO9e8q9y/9nZs2ngQk8evE1s0kwHc16oGRr/VQZ0JTAXjp74bhXaojZh/dgH8+WXWI/+SETCF85F+UYnlNG+PXqkuryH9u6tZZYx7tzMCeJNQaPxKuVlYrzsR/XH76/PTSTfUx/lL+sAvzDePvbpd2dm5mPTOWthsdpmQntWpWxbvTu2GYL7WKhIWqwbCn2yMqKmPyRF5u6D7RKn2hV84S7WoxPZ2E9/vilBekOhe1TtKYOrHvRAezlUORcVi1UjE880R+c4jckTeSpSqeSlLtTjqN6yAKdN7IjSUj9H1qlz5WEyb2RvR0VyXfztYrBqKHH4vIl5/QI7I27BUXadRk4rqxuziDpb0Jb5EBKZ/2kf5M7dpvV0sVo1Fvfkgwge3lCPyFixV12vStDImfdCd7+d0JCYmDNNn9kHZcjxYpDhYrB4Q9e6jCH/mLjkiT2Opuk+zFlXUd66BQf4yIW8l3qVO/agXqtxRQiZ0u1isHmAymRA17XGE9WsgE/KUoHsrsVTdTJx+MmGyUq6BLFdvJZ4qvPdhd9SuW0YmVBwsVg8x+fkh+rNOCO3C0yE8JegelqpW7r2/OsZN4lpIbyR+T955rwsa31VJJlRc/C73IJO/H2L+0x3Bj9eUCWlFLdVlSqlGcCmBVh54sCbGTugEf26Y4jXETkpvvt1BvfAh1+F3uIeZAv1hntMdQQ9WkQm5W9DdFVmqHiLO7xwzjlvjeYthIx5F67Z15YhchcXqBUwhgTAv7KN+4JN7qaW6vB9L1YPEB/lo5S6J5epZLwx6EF17NpEjciUWq5cQ7/nE+77AxmVlQq7GUvUebdvVw+tjHme5ekj/v7XAgL9z2Z+7sFi9iF9UCOJW9Edg3VIyIVe5/viXpepN2nVsgFdGPcZttDXWqWsj9W6V3IfF6mX84sJgXj0A/jXiZULF9d9SjWSpepvOyof88NfasFw18kib2nhVvZjhF9ydWKxeyL9UBOJFuVaKlQndrsAWFViqXk685xvyCg/Qdrd77quGMeM68vG7BlisXsq/fDTi1irlWjZKJlRUolTFo3WWqvfr0acpBr78kByRqzVsXFFdqxrATTo0wWL1YgFVzEq5PgG/khEyocJSS1VMVGKp6ka/J1vg+ZdayRG5Sq06ZfA+D0TQFIvVywXUiEfcKuWuK5bHNxVWUHNZqlEhMiG9eOKpu/HM8/fLERVX5arx+OCjXgjnpD1NsVh1IPDO0jCv6A9TFH84CiJK1byCpapnTz17L/7+zD1yRLerTNkoTPukN2Jiw2RCWmGx6kTQXeVgXtIXJu5rmy+WqnH834sPcJ1lMcTFR2DazL4oWYpzNDyBxaojwWJ/2wW9YArh+ZZ/xlI1HrHWsk//5nJEhRUVHYoPlTvVChXNMiGtsVh1JvihOxA7u4e6xzBdF9S0/PUdlViqhjNo2MPqjGEqnNCwIEyZ3hPVqpeUCXkCi1WHQtrVRMy/ugA8JeR6qa7sD79olqpRDXnlEe5pWwjiMPlJU7uhXv1yMiFP4SezToV2r4eYzzoqv4O+u9ibpeobxC5Bw197FB27NJQJ/Zk4iu+dSV3QtDlPyfIGLFYdC+vXENHTHhefPDLxHSxV3yLKdcTotmjX8U6Z0A3ix3/UmMdxf6saMiFPY7HqXPgzTRE10be2g2Op+iaxFd/IN9uhzeM8P/RmQ15tjbYd6ssReQMWqwFEvHw3It/wjR1r1GVHLFWfJR55vvF2B3UzeQKeeeF+9OjNyV3ehsVqEJGjWiF8mLEX1aulumoAS9XHiXId804ntHq4lkx8U58BzfDU/90rR+RNWKwGEj2+NcKebyZHxhLYhHeq9D8BAX4YN7ET7vPR94odOjfAS0MeliPyNixWg4me2hahAxrJkTGIUlX3S47hfsn0P+KklvHvdVGPQ/MlD7WurU7k4pmq3svkUMi/JoNw5NmR9NRSZC/aLxP9CmxcFnGrB7BUKV85OTaMHLYYWzefkIlxtbi7Kt77sAcCuUGMV2OxGpTDloekfguRveqwTPSHpUqFlZ2di9eGLsb2rSdlYjz1G5bDtE/6IiSUx795OxargTmUK3lLjwTkfHNMJvqhlqqYqMTj8qiQsrNy8crLC7Fzx2mZGEeNmqXw8ef9EBHJOQZ6wHesBmYKDkBsQk8E3VdZJvrAUnUuMytLuUPLliO6QdzJvT+9Bxo0Ki8TY6hYOQ4ffNKbpaojLFaD8wsLgnlxH3VTBT1gqTonCnXzli3YsnUrcnJyZEo3hIYGYcqMXqhXv6xM9K1U6Uj1TFWzOVwmpAcsVh/gFxmsnv4SWL+0TLxTYMMyiBNLaliqtySKdPPmzcjMyEBaWpparlarVf5duiE8PBgffNQbderqu1zNceGY/mlflC4TLRPSCxarjxBlJdaBBtQqIRPvopaqmKhkDpMJ3UwUqCjSdKVUb0hNTcXWbduQm5srE7pBPDb9cGZv1Kzt3ReT+RG//g8+7oWKleJkQnrCYvUh/iUj1PIKqOJdByCzVJ0Txbllyxa1SP8sOTkZ27Zvh81mkwndEKmUk3iMWq2Gvs4mDQ0NxJTpPVCjpj4vCojF6nP8y0bBvPYJ+FXwjsdLLFXnRGFuU+5KU25RqjdYLBb1f2PLy5MJ3RAdE4YZn/ZF1Tu880nNn4n1qROndEP9hhVkQnrEYvVBAZViEL/mCfiXjpSJZwQ0YKk6I4pyq3I3aklKkkn+EpVy/W3HDuSxXP8iJlYp13/0ReWq8TLxTuL0nrHjO6J5y6oyIb1isfqogGpxMCulZorzTKmJUo1fw1LNjyjIHUpRWhITZVKwK1evYufOnbDb7TKhG8Ss2o8+66suXfFGYnfCkW+0VbcrJP1jsfqwwDolr8/C1XhjezE7OZ53qvkSxfjbb7/hqlKURXXp8mXs2rWL5XoLcfERmKGUa/kKsTLxHoOHP4L2nRvKEekdi9XHBTUqC/PSvjBFBMnEvUSpxq15An4eulP2dqIQxV3n5StXZFJ0Fy5exO7du8FN1f6qZMlIfPSPfihTNkYmnvfUs/eiV19jnkrlq1ishKCWFWFe1AcmN+9BGngnS9UZUYS79+zBxUuXZHL7zl+4gD379rFcb6FU6Sh1e0BvWB/ao9ddeOb5++WIjILFSqrgB6ogdm4PmILcc2qGWqpiNjJL9ZZEAe5RSvX8+fMyKb6zZ87gwIEDLNdbKFM2Wrlz7YtSpaJkor3HO9yJISNayxEZCYuV/ivksRqImdUdCHDttwVL1TlRfPv378fZc+dk4jonT53CwUOH5IhuVq58rPrONb5EhEy088CDNTHyzXY8U9WgWKz0B6EdayPmH52V7wzX/MCLUjWL2b8s1XwdOHgQp06770SWEydO4PfD+j0+0J0qVDKr5Sq2D9RK0+ZV8Pa7nRDg4gtY8h78naW/COtTH9Eftb++BqAYbpSqfzw3EM/PIeVu8uRJ958hevToURxR/qC/qlwlXl2KE6vBRvd17yyHd6d2Q1BQgEzIiFisdEvhf2+CqPfayFHRBdYtpa6TZanm7/CRIzh2/Lgcud9h5a71uIb/f3pS5Y4SmD6zN6Kj3XcARLXqJTH1o54IC9NmBj55DouV8hXxUgtEvvWQHBWeWqprn4B/CZZqfkTBHVGKVWvifasWd8h6VK1GKUz7tA+i3FCuYu3sh5/0RlQUT27yBSxWcipyxP2IGHGfHBWMpVqwEx6eULT/wAGcOXNGjuhmNWuVxocfu/ZQcbF2drpS2GKDCvINLFYqUNRbDyN8YAs5yh9LtWCi0A7s3y9HnrN33z6cc8MsZCOoXbeMencpznUtLrFPsbgL9qYNKcj9WKxUKFGT2yDs703k6K9YqgU7e/asWmje4MZmFBcuXJAJ3axuvbLq+9DQYrwPvX7gei91chT5FhYrFYpYbxf9UTuE9q4vk/9hqRZMbPywZ+9er9qsQfxaxL7Cl1yw05MRiaPbpkxXyvU2diQLDg7A5Gk9UKtOGZmQL2GxUqGZ/PwQ83knhHSuIxMgoE5JlmoBRHF56969duXX9NvOnbhSjL2JjaxRk4pqQYaEFL5c1TNV3++q/rPkm0zKDzv3O6MicVhtSOqVANuZVMStU0q1JCdl5Edspi+Of/P202b8/f3RrGlTxMfzseWtbN18Aq8NXQyr8r3vjHqm6oSOaN2mrkzIF7FY6bY4snJhT7fyTtUJcezbdh0dPh4QEIDmzZrBbDbLhG62+edjGPnKEuRab/37KfZTeXV0W3Tu2kgm5Kv4KJhuizgJh6Wav8TERGzfvl03pSrYbDZs27YNScnJMqGbtby3Gsa/11V91HsrLw5+iKVKKhYrkYslJSVhmyhVHR42nquU69atW5GSkiITutl9D1THuEmd/7LP74CnWqLfkwUvSSPfwGIlciFRSFuVuz5x96dXubm52LJlC1LT0mRCNxMn04yd0An+/tc/Prv2aIwXXnpQ/Wsige9YiVwkNTUVm5VCslqtMtG3kOBgtGzZEhERnJx2KxvXHcCWzccx+q0O6qQlohtYrEQukJ6ejl82b4Y1J0cmxhAaGqqWa3gYj/0jKiw+CiYqpoyMjOt3qgYrVSErK0t9LCz+TESFw2IlKoZMpXA2K3eq2dnZMjGezMxM/KqUq5H/G4lcicVKdJtE0ah3cz5QOJnKXfmWrVuRY8C7ciJXY7ES3QZRMOJOVTwG9hVpaWlquRplchaRu7BYiYpIFIsomHQfKtUbxMxnsZxILMkholtjsRIVwX/XeCoF46uSk5PVHZr0vFaXyJ1YrESFJIpE3K2l+HCp3mARu0uJctXRlo1EWmGxEhWCKJCt27er2xXSdYkWi64OGSDSCouVqACiOMTRb5bERJnQDdeuXsXOnTu9/lg8Ii2xWImcEIXx22+/qUfA0a1dunyZ5Up0ExYrUT5EUYjCEIeVk3MXL13C7t27wR1SiVisRLckCkIUhSgMKpzzFy5g7969LFfyeSxWoj8RxbBnzx61KKhozpw9i30HDrBcyaexWIluIgph3759OHvunEyoqE6fOoWDhw7JEZHvYbES3eTAwYM4feaMHNHtOnHiBH4/fFiOiHwLi5VIOqTcZZ08eVKOqLiOHj2q/kHka1isRIrDR47g2PHjckSuIu5aj/PrSj6GxUo+TxTqEaVYyT3E+1Y+CSBfwmIln3bi1Cn1ETC51/4DB/jumnwGi5V81unTp3Fg/345IncTs63PcbY1+QAWK/mks2K9JUtVU+qmG3v24ALXB5PBsVjJ55w/fx57uEOQR4iv+a5du3CJO1qRgbFYyadcvHgRu7inrUfZla/9bzt34gr3YCaDYrGSzxCb6e9U7pZYqp4nDjjYIU4NunZNJkTGwWIlnyCOfRPHv/FoM+/x33NuLRaZEBkDi5UMLzExEdu3b1c/yMm72Gw2bN22DUnJyTIh0j8WKxlaUlIStolS5Z2q11LLdetWpKSkyIRI31isZFjJyge1uBsSH9zk3XJzc7FlyxakpqXJhEi/WKxkSKmpqepdkPjAJn2wKr9Xm5VyTU9PlwmRPrFYyXDEB/MWpVStVqtMSC+sOTnq711GZqZMiPSHxUqGkpGRod715Cgf0KRPWVlZ6mNh8WciPWKxkmFkKnc5mzdvRnZ2tkxIr8Tv5a9KufL3kvSIxUqG8N+7HH4QG0amePqgXCjx6QPpDYuVdE988IpS5Xs540lXypXvy0lvWKyka+IDV3zwig9gMiZ1hve2bZzhTbrBYiVdE+/ixB9kbGJSGiczkV6wWEnXYmJi0KxpU/j78VvZqAICAtC8WTNERUXJhMi78dOIdC8uLg5NlXL1Y7kajrhgEhdOsbGxMiHyfvwkIkMoUaIE7mrShOVqIP7+/miq3KmKCyciPeGnEBlGqVKl0LhRI5hMJpmQXokLJHGhVCI+XiZE+sFiJUMpU6YMGjVsyHLVMT/l965J48YoWbKkTIj0hcVKhlOuXDk0qF+f5apD4vesoXJhVLp0aZkQ6Q+LlQypQoUKqFe3rhyRHohSFRdE4sKISM9YrGRYlStXRt06deSIvF39O+9UL4iI9I7FSoZWtWpV1K5dW47IW9WtVw8VK1aUIyJ9Y7GS4VW74w7UqF5djsjb1FEufKpWrixHRPrHYiWfULNmTdyhFCx5l5o1avD3hQyHxUo+Q70zqlJFjsjT1CcJSrESGQ2LlXxKnTp1+C7PC1RRLnD47puMisVKPkUs6VBnn5YvLxPSWuVKlThbmwyNxUo+R10v2aABypUtKxPSirigqVevHjfvIENjsZJP+u8OP6VKyYTcTd0RS7mgYamS0bFYyWeJjd6bNGmCUtyT1u3KlC6NhixV8hEsVvJpN8o1nkeTuU1JcepQ48Y80o98Br/Tyeep5342bQozD9N2OfWcXJYq+Rh+txMpAgIC0Lx5c8SyXF3GHBeHu+66S71wIfIlLFYiSS3XZs0QFR0tE7pd4u6/edOmCGCpkg9isRLdJDAwEC2VO9eoyEiZUFFFR0WhmXKBIi5UiHwRi5XoT4KCgtCiRQtERETIhAorSilV8bUTFyhEvorFSnQLwcHBaKHcuYaHhcmEChIRHq5+zcSFCZEvY7ES5SM0NFS9+woNCZEJ5SdMKdWWLVuqFyREvo7FSuREmHLHKgojhOWaL/E1ulu5AOHXiOg6FitRAcLF3ZhSHLwb+ytRpuLxr7i7J6LrWKxEhSAmMvH94R8FKRca4oJDXHgQ0f+wWIkKScx4FZtIcMbr9ZnTolQ5c5ror1isREUQEx2tbiLhy2s0xYWFuHvnWl+iW2OxEhWR2PawWdOm8PfB/W9v7E4Vzd2piPLFYiW6DXFxcerG/b60uby4kBA7KnE/ZSLnWKxEt0k9uaVJE58oV/UEIKVU48xmmRBRflisRMVQSpw12qiRoQ/wFhcO4gKiRHy8TIjIGRYrUTGVKVMGjRo2NGS5+in/TU0aN0bJkiVlQkQFYbESuUC5cuXQoH59Q5Wr+G9ppNyNly5dWiZEVBgsViIXqVChAu6sV0+O9E2UqrhQKFu2rEyIqLBYrEQuVKlSJdStU0eO9Kv+nXeqFwpEVHQsViIXq1q1KmrXri1H+lOvbl1UrFhRjoioqFisRG5Q7Y47UKNGDTnSjzrKBUGVKlXkiIhuB4uVyE1qKsV6h1KweqG3Xy+Rt2KxErmRXu4Aq1evrss7bCJvxGIlcjMxmamSF7+zFO+Ea9WsKUdEVFwsViI3E0tX7hSzbMuXl4n3qFypknpXTUSuw2Il0oC6LrRBA5TzonWhFStUQL169Qy1qQWRN2CxEmlEFFjDhg1RulQpmXiOKPj6BtspishbsFiJNCQ2tG/SpAlKeXDv3TKlS6sFz1Ilcg8WK5HGbpSrOHZOayXFaTyNG/vUObJEWuNPF5EHiPNNxVFsZg0PDY8X58eyVIncjj9hRB4SEBCA5s2bI1aDchUHlDe96y610InIvVisRB6klmuzZoiOipKJ64m74mbK/0cAS5VIEyxWIg8LDAxEixYtEOWGco2JibleqkqBE5E2WKxEXiAoKAgtmjdHRHi4TIpPFLW4GxbFTUTaYbESeYng4GD1zjU8LEwmt08UtChqUdhEpC0WK5EXCQ0NVcs1NCREJkUXppRqy5Yt1aImIu2xWIm8TJhyxyqKMeQ2ylX8s3crxXw7/ywRuQaLlcgLhYu7TqUgi3LX+d+7XeXPROQ5LFYiLxUREVHo96RB4v2s8r91xftZIioeFiuRF1Nn9iqF6Wxmb5Dy98TdrShiIvI8FiuRl4uJjlaXzdxqLep/18BGRsqEiDyNxUqkA2Lbw2ZNm8L/pn1+b2yJGK0ULxF5D5NDIf+aiLzc1atXsX3HDvXIN3EXazab5d8hIm/BYiXSmctXrqgn1JSIj5cJEXkP4P8BE2RYOCxJ7UYAAAAASUVORK5CYII=";

	String Logo = "iVBORw0KGgoAAAANSUhEUgAAAi4AAAJiCAYAAADg0EnIAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAPbTSURBVHhe7N0HeFzF1Qbgb2xLbrLce+/dxgZ3gyvY9BZqaAm9QyAkgfCTAAECCQRC6CGhh95MMWDjBtjGvffe5F5kVVvzz5k7snZX9+7eu3t3tbs67/MIzYxlI8vS7tmZM+cIqYAxxhhjLAVUMe8ZY4wxxpIeBy6MMcYYSxkcuDDGGGMsZXDgwhhjjLGUwYELY4wxxlIGBy6MMcYYSxkcuDDGGGMsZXDgwhhjjLGUwYELY4wxxlIGBy6MMcYYSxkcuDDGGGMsZXDgwhhjjLGUwU0WGWPp6Ug+kJ+j3nYCBXvU+93AUbVG64X7zQeVkUVm7Wih+nj1sYd3qPf71Pyw+viD+veJkgLrY2xIkQlRrSZkZm31kjADyMxWq9UgqmerX6um5mq9ak2IKtXVx9RRY/XxGeq9+j2oWl39ej1rTL+WkaXe1Pvqao3G9GtV1J/BGOPAhTGWQvJ3WUEFBSN5Kigp2AV5eLsVlBSo+WEKUtTHqDVxNM/8pvQgq6lApmZjoEYD9VZfBTXqfa1mELWaqnX1psb612s1t8aCN9RZeuLAhTGWHGgX5NB64OAG9X6jCki2qgBkC5Cr3tPb4W3qAavIfDALR9LuTM0WQJ3WQFYr9b4NRG31PquNemtprVGww1gK4sCFMZYYFJjkblRBySYrMFFvOKSClAMqWFHvRdE+84HJRaIqBB3XuCSPFoY9UkoWko6nskoDm3YQ6g3Z7YG6nYF6XYDq9c1HMpZcOHBhjPnnqHrC3r9Kva1Wb8sh1VjsW6HGa4DiA+aD4kdWrWU94dJbzQb6vcioC1nDyhUR1Wqr9/SWXZZLUk39Hp1HUtf6tSqZVo4KjWN1JE99TcwuUfEhK3+mOFetH7bWi6zcGf1xhXshC/YC6o2COKnmyFfBXOFu/WuiWP2eBJI1G0PU7wHZoBdEw15Ag57qTb2nrxNjFYgDF8ZYdGjHZM9i9bYQUr9XbwdUoCJLzAf4R2aqIKROS6B2C/XWBiJLvdd5HZTP0cSMm1nJremqRAU6FNhQQEPvKcen0Eo6ljoJmfJ/aK7e51Guz271b1FsfrN/JO3SNOoL0e5soMM5Vq4NYwnEgQtjLDx6pb93iQpMFkHuXgTsWwKxa6FvOyh0FKOPLOp21G+iTlvr+KK2CVQoLyOdA5J4oh0dCmoOb1NvW/UxnczdBORuVm9b1HxzTEd0UmQA7c+A6HkT0Gq0WWUsvjhwYYyVoaOM3QuAXXMgc2YBOXP00Y8QsT1M6GTR7E4mOOkEkd3ByqWoq95TbgUdzbCKQYHpsWBmswpsrPcU1FjBzUZXOTuy/x8gBj1sZozFDwcujFVWdKSzbzmwUwUpO2fr99izQD1JHTEf4J0VoHQBGnaHqE85Eeqtfncr2ZODk9RFV9BLd2lKAxw6KqR53jYrZ6jVKIiBKnDh3TEWZxy4MFZZUECySwUn26ZCbp0C7PgRghJFoyRrNAEaH6fejodo2NsKUOiNAxTGWBxx4MJYuqJkzpzZVqCyTQUq22dGXZRN1mxeFqQ0OUG972/loDDGWIJx4MJYuqCjH8pP2fQV5JZJwM7ZEHTV1iN9pbjpAKDZEIhmw6wghW7sMMZYEuDAhbFURrkHmyZCbv4G2DwRgq7CeqSvt+ogZah+j4bHgfviMMaSFQcujKUSeRTY8ZMKVL6FoJ2VnfPg9caPrNsVaDUGosWJKlBRwQpdPWaMsRTBgQtjyY6qqtKuyvpPgA1feK67IbPaAa1Hq0BlpA5Y+NiHMZbKOHBhLBlRBdQNn0OuU8EK7a546H0jqYos7ai0HGUVBaM6KYwxliY4cGEsWVARMApU1n8CuX26+uF0VzpfiipAk0EQbU8F2qi3xv3UqrB+kTHG0gwHLoxVpINrgbUfQKo3sWueWYxMVm8EtB0PQYFK65OBGg3NrzDGWHrjwIWxRIs2WKnfSze1E21PA5oMUD+9VcyvMMZY5cGBC2OJkL9TBSvvQ656C4J6ALkgpQCaDYRof64KWNRb3U7mVxhjrPLiwIWxeKFy+pSvooIVbJ4EgaPmF5zpXj/NToToeD7Q/myrOzJjjLFjOHBhzFfqx2nbdMiVr+njIDe9gHRybcvREJ0vtoKV6g3MrzDGGAvFgQtjfqBOuStfh1zxOsShdWYxPNn4BBWsXAJ0vgio1dysMsYYC4cDF8aiVVIMbJgAuewVXSDOTQVbmd0R6HKpFbDU62pWGWOMucWBC2NeHVwHufzfwPL/QOTnmEVn+upyl4tVsHIp0HSQWWWMMRYNDlwYc4OKwVFvoMXPqvffRtxd0TeC2oyF6H4N0P4soEqm+RXGGGOx4MCFsXAK9+qdFbn0eYiD682iM+q0LLr9Cuh2FVCnrVlljDHmFw5cGLOzZzHkoqeBVe9E7BMkRQbQ/gyI7tdaVWy5MBxjjMUNBy6MHaN+FDZ+pQKWpyC2TDZrzqjrsuh9E9D1cqBmE7PKGGMsnjhwYexIvnWVeeHTEAdWmkVnsuUYFbDcArQ7g3dXGGMswThwYZVXwR5g6fOQi56BoHEYMqM20OVyK2Cp392sMsYYSzQOXFjlk7sJcsGTwIpXIYoPm0V7sm4niF43W8m2mdlmlTHGWEXhwIVVHvuWQc57DFj1v4h9g2SLURD97gJaj+PjIMYYSyIcuLD0t3s+oAIWuebDsPVXdO2VTuepgOUeoPEJZpUxxlgy4cCFpa+cmZDzHoXYMMEs2JNVauibQaLf3UDdTmaVMcZYMuLAhaUfClh+/jPE5m/MgoOMupC9b4TocxtQs6lZZIwxlsw4cGHpw2XAIms1gzjuLqDHdSp4yQJKioBDm1Tw0hjIrGs+ijHGWDLiwIWlPi8BC+WvUMBSrSZwcC3kgn8Aq14DivIgfrkcqNvZfDRjjLFkxIELS117FkHOug9i45dmwV65gCV3iwp0/g9Y8WbZ7aIhjwO0C8MYYyypceDCUs/BdZCzH7D6CIW7JRQasBTnQs5XAcrCJyGoWi59TIOeECf+E2gxQs8ZY4wlNw5cWOrI2wE55y/AspfVN26xWSyvXMAijwIr/gs5636I/BzrgzLqAgP+D6BKuFWqWWuMMcaSHgcuLPnpnZInAGp+GK7SLQUjdKW5z+1qXNta2/wt5E+/hdizWE9l1VpA39sgjvstUL2eXmOMMZY6OHBhyUuWACtfg5xJOyXbzWJ5UmQAdK35+PuAGo2sxX3LIX9UAcumr6y5IjtfCjHkMaB2S7PCGGMs1XDgwpLT1u8hf7gLYs9Cs1CernTb+WKIQQ8B2e2txYI9kHMeBBY/fyzxVjbsB3HS00CzYXrOGGMsdXHgwpLLgdWQP94dudptq9EQg/8KNO5vLZQUA0ue00GLKNyvl2T1RupjVFDT/Rr1nc79hhhjLB1w4MKSA+WuzH0YcsFT6psyTOJtw74QQx8HWo01K8rGLyFn/Abi4Go91Tsxva6HGPQwUL2+XmOMMZYeOHBhFW/Ne1YCbe4Ws1CerNEEYvBfgG5Xle2e5G5WAcvtEOs/teaKbHwCxIh/cZNExhhLUxy4sIqzbxnk9Fshtk4xC+VJuqrcW33MCfeXleMvOQIsfgby5z8du2Ukq9eDGKQCG7oCzcdCjDGWtjhwYYl3JE+X6Meif0BQEOJAtj4ZYvg/gHrdzIqyczbklOsh9iwyC+rjulwGMfQJoGYTs8IYYyxdceDCEmvTRMipN0HkbjAL5ck6HSCG/R1of5ZZUaiWy6w/Akv+pb5pS/SS/riRLwCtxug5Y4yx9MeBC0uM/BzIGXdCrHnXLJQnM2pD9L8X6HsnULW6WVU2fQ055UaIw5v0lI6PRN/fACf8n1UZlzHGWKXBgQuLM/XttezfkDN/d+yash3Z8XyIYU8FF4ejmizTb1fBzjtmQX1c44EQo14EGvYxK4zFUdFB4MhhfbyJokPWtXsak+JcK9+KdgDp4wIVqzmtU75VRrZZNEIrNmdkWW0nKIerSoZV9Zluw5XmdDHGgnDgwuLn4HrI76+G2DbVLJQns9tDnPgs0Ga8WTHWfQQ57ZZjvYVktZoQA/4M9L1DfddW1WuMuUbfR4e3AXk7gcK9VlCcr8bqvX4rpPdqvXCfFZiot7DtJRJEZqoApoYKdCiQoZYWpWP1JvT7BmpNvdF7yvGqpd50rpew/gDG0hAHLiwO1LfUkuchZ/7e8cFfl+nvd7dVpj/wuCd/F+T0myHWfmgW1Mc2OxFi9MtA3c5mhbFS6nstd6sKSrao9+pNBSfy2JjW1duhreqBrsh8fPqTUIF9rcZA7RbqfUugTiuIrNYAvdGOJq3TuGoN8zsYSy0cuDB/udllaToEYuSLQIOeZsVQwYpO3C3crac652XwY0CvG9WMX0FWWhT8Hlynv7fovdRjMz+wrlIFJX6SNVVwQy8G6naEqNvJjOm9euNjKpbEOHBhPnGxy1KtjgpEHlaByE3qOy+g1krhfquey+q3zYL62BYjIEb9u6wHEUtv8ihwaAOwdzmwfyXkvmXq/Wr9Jgp2mg9iiSJrNlcvLLoC9btD1KP3PYB6XaydGsYqGAcuLHa0Pf/9ryE2f2sWypNtToUY8Xz5Bz71e/TvpfwDReeyUCG5PrepGe+ypKXczQDV4dmzGFK/X6qDlXCtHliSyKgLSYnxjXpB0PsGvay3zJAEZMbiiAMXFhtKov3+eoiivWYhmN5lOfEpoNuvzIpxJN8q879EBTOGbDoIYvSrwQXnWApTDy37VwG75kLmzNZFA+WehWFvl7HUJOl4qXF/CGq10WSAGvdTQU4d86uM+YsDFxYduv45/XZg1etmoTzdwZmOe7LamBVj93zI7y6H2LdcT3VdlhMeAPr/Tn1H8o2hlEW3dHb8BLlzNpDzM8TOOUDxAfOLaYZ2HigPhHYaMtUTtHkTVWvrX5ZV1PcxrZVSHytsdhAlXZmmq9OljhRAqDcij+SqXzukftbUe7qKfeQQRMF+yOJc9Scd1R+TrHSjUzpaajoAotkQ9X4w0LA3/3wzX3Dgwrzb8QPkpCshKDnShmNSLT1IL3wKcuYf1TeelVAp1YObGPumeoV2vJ6zFEI3d7bPgKRE7K1TIQ6sNL+QWmhXELWbAtUbAbXorRlQoxEEXSumN33l2LqCfOz6cUUfYx7Jt4Ia2r0q2A3k5aj3u1TwuBvy8Ha1rtYokDxMV753J0WekKR6NU0H6iBGNBsKNFHjGg3NrzLmHgcuzD0KPOY/Djnr/xxf8ekbQ2P+a91MCER5MJNVsLNlsllQH9vjOqu0f7VaZoUlNUqe3TYdcvs0K1A5uNb8QnKSVH05uwOQ1UoFJupNvRf6fWsVkDRWAYoKVlSAElSlOV2VqBcKFGhSfpF5k4c2qX9T9Za7ETiwHqLE2ulJJHrhgmbqMaP5CKCleqvTzvwKY844cGHuqFdv+nhny3dmIRjVjhAD/w/o/wf1XRWyHUwl+ydfBZGvXhEqUr2yFaP/DbQ7Q89ZkspVT2pbvldByhQVrKhA5ZB6gksyuh4Q3XqhGzD1ulvXeukmGgUstZurj+AEb9fydqhAZj1wUAWoVNaAAtMDa4B9qxK2YyOzO+oARrQco96PtHa/GAvBgQuLbMtkFbRcdqyKbShKzBNj37C2fgOpV3ly5n0QC580C+pjW421dmRq0ZMKSyp0jX3bVMjNE1Ww+W1SHf3o45wG3SBUcELvKUhBA/VWRwUpVC6fxVfRARXE0PX0VfqKuty3FNi7zApq4phvI+v10AGMaDXa2pGhozpW6XHgwpzJo5A//wmY8yiEsP82kd2vhhimAhM6vw5Er9i+uRhi1xw91Tsygx4E+t2jvusCariwirV/BbBhAuSmr1TQ8uOx3KOKJGs2BRofBzTqD9FIvacbKrSDwrsnyYeOoPap76G9SyApkNm9ANg5G4LaKPhM0uMG9SprOx5oe6r+/uDHksqJAxdmL2875LeXQmybZhaCUQ8VfdzT/myzEmD9p1ZtFnPtVdZuAzHubaDpED1nFYiaAlJy9YbPdMAi6CigAtH3BprQNVr1JNRIBSgUqFBJepbaaHcmZzbkjpkQO2dZ1+Dpe89HuvJvm/EQ7dRjUOuTy794YmmLAxdWXqSjoWbDIE5+q3wxuZJiyJn3Bh8NdTgXYuTL1o0MVjHoBsqW7yDXvg9s/KLC6qjIqrUgmg6AbKpeNdP12KaD+MiwsqDvwV1zVTCjgpicmeq9CmhM0Uk/SJEJUPmF9mcB7c7k4DfNceDCytCtoTkPQc59WH1jqHEIvVXb/w8QAx5Q3zkhCbi5W6wdGvVqnlDSpBj6V6DP7XrOEozyVTZPLAtWHNowxJO+6t5EBSjNVIBCgQpVWOV8FFaKdvu2z9A31PxM/tY1ZFqoF1cdf2HtCIfWkWIpjwMXZinca+2ybJpoFoJR7xJx8htAy1FmJQDdOvnmkmM3DyRdOz3lf3w0lGhHC1Ww8g0k9Xxa91nCr7daN0IokXKMeuIYwTdCmDd0TXvLJEj1Ru+ddny90hW5O10EdLyAd2LSBAcuDNg1D/LrCyByN5iFYLL1KRBjXrdqX4SignI//u7YzQLZ+mSroBzVx2DxRztj26dbwcqaDyGK9plfiD9ZownQRn1vUKDScrRVL4Uxv+xdYh1bb/5avZ8acyCud4ybj4DocgnQ4Tw+vk5hHLhUdiv+Czn1ZtsHBfpBFyfcDxz/R/WdEpK9X5wL+f01EHQUodD2rK7jYvexzH/7V0GufE39+70BkbfVLMaXziNoPgyizTgrGbJRX7XKN31YAlCODF3Vp9tvG7+Kufih/l5uf7oKYq6wbihVyTC/wlIBBy6VFSXSzvgNxNLnzEIwXSTulLeAVmPNSoCD6yC/PBdin3pFpMjq9SDGvKEeAE7T84Shq7zbpkHunAdQXQkVTOkqqJRb0f0aoMVJ5gPTBNXSWPMe5IrXIHJ+MovxJWu1BNqpB/i2p+vkR65yzJLCvmXWNf71nwF0c8mhXIMbktoOdLwIotvl5WtRsaTEgUtllL8L8psLna86NxkMMe5d+63/rd9Dfq1+r+kGLRv0hBj/UfkS//FAxyIUqNAuz7pPIfK3m18oT+8WnT0ZaH6iWUlV6seTyuwvf8U6Copz3opObGxGtTJUENr2DN5VYcmPcmEoiFn7gXp8mhzTtWudUN71CqDzpUCdtmaVJRsOXCobymf56nyIw5vMQjDZ+1aIoU/Yb50u+Rfk9DvL8lk6qj9n1Kvxr59QqIKkpS9BLn0ZTnk4dmTfO9Tf5e9mlmKocd7K1yGXqb8zVSuNI+rOrc/+1b8nXyVlKY0eK9Z9Yr24iSGI0QF8m5Mhet6gAvjT+TZckuHApTJZ+6HV6JDOi0PIKjVUEPIi0OUysxKASvdPuwVi+b/1VO9mDHoE6He3msXx1TgVwZv/uApaXoE4mmcW3ZP9fgcxWH2eqYR2lJapf4c1H6kfzvhVsaV/b8pTsYIV9cDMpdRZuqEgZu0HkCvfhtgx3Sx6Rzcq0ePXEN2v5l2YJMGBS6Wg/onn/gWY/YCZB9OVbU/9AGh8vFkJQNekvz7/2LGSzmc5+X9Wcma8UOLv/L8C85+M7WjkjK/j+3n6hWqsrH4bcslzEHsWmUX/6Z2VVmMgaBucdlYy65pfYSzNUY2Y1e9ArnoTYt9ys+iNtQszDqLndbwLU8E4cEl3R/Ihv78aYs27ZiGYbDEC4hT1a3ZXnQ+sgfzyzGNHFTK7M8Tpn1rdeONFfZ7yh99AUKfaGOjGj5eoB6hkvuFEX18VrGDla3GrZqsfbFueZOpY/AKgRMRURruFBbt0t3Lk7wGK1NeNvnbmvQwY62RtCgpLjqrfd0j/dlGg1hVZdLBcQqfupxVw7CmrqK8dBXdVa+o3UT0bkpKTq6lxtSzIGvUhajSwrtXSjpV+q6e+xgFrfMMu+eycDbniDRXI/O9Yrp5XsnYL6xip5/Vc+qECcOCSzvJzIL84B2LXbLMQTPa6GWLY3+3zWag2CO20mGZpkgqLjXvfejCOh0MbIKfeCLH5G7MQo5PfATpdaCZJhgr2LXoKWP9FTLchwtFBZvcrgS6Xp0h9FfV1OLxd31hD7ib1tgXy8DYVwG6HPLQFoEC2IMf2mDOZ0Q4lMtXPDD251aK35vpFgtDvm1j/NtQ6g/KKQqtRs/iigo2U1Lvyv8DGicdy97zQR65dfwnR5zarMjRLCA5c0tW+5ZATzoBdMqsux3/Ss0CPa8xKiFVvQU6+Wn1zFOupVB8nTvynCnAy9dxXdFNo0dOQs+/37UlJtlN/71M/NbMkQV10V78LufApiD0LzaLPMupCdqZrnSpgoRL7yYZ2QQ6tV8GJ+p6kK/XmPQWttC7oiaSSot0eZKnghYKYOm10N2xBHbH1W3sV2KgAh3dv4ocCY50M/xLEQfU9GgXZcgzEcXfqxo/qH8taZHHBgUs62vo98NX5QPEBs1BGd3U+9UOrJLuduY8AKoggOgl3yONAX/XDGA+5m1WAdBXE1ilmIXb6iOj8n+K3M+RV0UFg6QuQKjiL9fjLEf1b9rgW6HAuUFW9AqxoUr1ypePF3QshKWdHvceexQkrlJeO6MUG6nYE6ncB6nWHqN9Njbtbx7aZ2eajWMzohRS1HVj6PLB+QnS7MHSlmnq0dVUvIKrVNKvMTxy4pBt61fD9dcd2SwLpH6jTPlMPgJ3NSgD1ZCNn3A6xRP3A0jQjC4KOW+JVVI5K1E+9xTa4ipYumnf+D4mpKROJClLkomfU1/MFX/+OpSgARbcrIOiMPZ45R5HQdVPaQdo5B3Lnzzq5WO5ZGvd6M6yMpF2ahr2BRsdBNOyrxn2snwHeoYkNda9e8SrkslcgqI+SR5KOBOlFH+XCcCK8rzhwSSfzHgNm3WcmwSS1fD/lPStpMNSRPMjvLodY/4meylrNVIDzOdC4v577qugA5NQbINaoz8VHUr3qFGdNis/n7EXuJsi5j6oHvNfj8uStG8b1uslKtK2I3RXaRqfkRvWGHPW2cx4HKUlIZtRWAYwKYpoMgKBqsE0HANQEk3lHwfn6j60XIjt+NIse0BFun5utXRhO5PUFBy7pIGS3JJTsfjXESf+yT8It2AP51TnHfiBl/e4Qp38Rn3oFObNUgPTLqM+QnUi65XHaBN2ZuMLQDSEKHFe+abvbFQt9TKACFdH3tsSWJKdtczrqoR4x22fohG2Rv8v8Iks1urR9YxXINBtsVZRuqr6XuIWDNzt/hlz4tK4P4/XnXAeT9Fjc964USZhPXhy4pDq67vzdZcd2S8oZ+Ger8aEdurnxxWkQe5fqqWxxEsT4j6GvdPpKfYvN/xvk7D9GXcnSiRW0fK6CllFmJcFUwII5f4Fc9VZU5+Hh0FYzelwP0esG6zZKvNG/za65KkCZAbltCsS2H+JyzMWSgw6Im/TXQYygQKb5MPsdWVbe4W1WHszi5z13ZNdf925XQZxwrwpg2phV5gUHLqmMmu59da5+RRxK3xwa+aL6AbnSrISgHYLPT4GgwkyK7HQRxJj/+n9zKH8n5KQrIDZ/axb8I6vWgjj9s4oJWuhI6OcHrSMhvwMW6v9E28pUxZiaRsYTBV6bv4HcPFFf0xZU+4RVSpSMr4tQthoL0WoM0GxIciR7JzP6eVn2MuSCpzwnn+sApse1VgCTiBcmaYQDl1RFjRI/PxViz3yzUEZWqwMx/n3nqrG7F0BOGF+27d/vHkCXxvf5Ct+OHyEnXhyX2yQ6EfeMzxPfzZXqisz7K7DkBf+PhPR1yt/o6py+/1uUoltO1CiTAhUVsPh9bMfSB+1motkwCLre2/ZUoF438yusHCp3QGUk5j0BcWClWXRH14Lpe6v62b+bc2Bc4sAlFVGxts9OUU86a81CGeqroZ/QG/UzKyGosNyXZ0GoJzCqqiqGPwlQ8SS/UW2Wn+7x/WiIyDodIM780v52VLxQVdYFTwALn4mqb5IT/SqX8lcoeHT6N4sV7aps+Axyg/q+2PaD7ztErHKgnzu0Ow2Cyt3TFfx47wamIsoLW/8p5NyHIdQLRC/0C06qA0O7rb4f16cXDlxSzf6VkJ+ebLuLIbM7qid09UqaClbZoSOBr8/Thd70UdLY1/2vLlt8yGoxsPZDs+Avfavm1E+sqqOJQK+kljyvHoj+cqyKsB+kyNTHeIIaVfp+fVv9SNP1ZBWs0INoaQ4TY36hY1q0HQfR4Tyrbw9f9w2hfgbXqxcLcx70HsDUaKgCmN+qAOZWPqpzwIFLKtmzWAUtYyEKd5uFMrJhbytoqdnUrIRQr7bl1xfo4w1do2W8CixajTW/6JP9q6zAKMomZpHIrpdDjHgxQa/01I/FmvchZ94HcWidWYud3hbudZ21w+L3uTYl1lI33NXvwa5iMmPxoIPwViNVEHM+0P6sxL2oSAkxBDB12kIMftS8uORKvIE4cEkVObOAz0+1veUhmw2FOH2C86se6oo66VdW0EK5IXTMYtcJOhabvrauOlNZd5/p5ndD/xq/Cr6hcmZajR7pa+4Tugopet2s/g53OAeX0aAHQ2pMueYDXwOsVKKL8VFjQ/UmqtXSgTl17hVUP6NqBkDXUKlJIgW8VaqqeR3rN9J76g9EXX4DjzSPUGNGk79EW/90rEqNG4sOqZ+/g1aeUKEaU+PG/L1AwW79s8XUl4uOPluMguh8MdBRBTK8E2OYAObnP3nuAC+bDIYY/jeg6RCzwjhwSQWUTEk7GfSAGUJSm/VxHzjXY1j5BuT3v1b/0CWQ2e0hzvja56MJ9e0z7zHI2f+n/x9+k+pJXpzyjnOLAj8d3go5816IVW+ahdjpm099bgGOu8u/xLvcLbrysG7Rn67HQBR01G4J1FFv1ICwVnMIqhBLu1S1mlnXdnUXZvWWDBViC1UAQ8nuKojBYWoIqd5Tk9PcbWq+CTi0VV+hjbYbcSrSOzHtxkN0UkFMu7O4/D2hx0h6ITlLPV563BWVnS6EGPSIcypAJcKBS7KjoOXLM20bEOpv5DGvOV9hXvoCME29yldk/V4qaPnC38JHlCtD+SzqFX886J2kU9SfTU9c8XS0AFjwpK5461fire9HQsWHgXUfQq74r/qemIZ4dZVOJEn/rvW66EaCVkPBjtaDMvXkSZZeU36jn2Pd/XqzVZLgwFqAkuzpdpd6L+jfOQ3p4mvt1ONYp0usW3N2xTArE8qdW/oi5M8P2x79O5G0a9jnNoj+91bqHlUcuCSzMEELulwBjH5F/Qs6tMIPDVrO/g7UTt831IuHdoF8PE4JJHvfCjH0ifg/wG38EnL67b4ds+guv91/7V9xKao2vOJVYNW76vvgkFlMLbqXDjUEbNADon5PNe6q3tR7vjlRXt52YN8K9bYMknLF9i7Tc5GfYz4g9dFxNbpdDqF+TlC/h1mtpIpzIRf8DVj4pKegVbdlocfHzpealcqFA5dkFW6npef1Vgl/p4StwKClYT+IM7/yN2jZsxDyi3MgaAvcZzoXZNS/gY4XmJU4ObQR8oc7INZ/ZhZiJzucCzHoL7E3PaTCgiteg1z+SkodBemgraF6Imp8PATlUDXuZwUo3L04dnQUtXuR1dBy1wLIvTReph7A1Sv3FKZ3Vbtfbf28065MZUUvBKnP3PLXPO2mypYjIU78Z6ULADlwSUbhgpbj7oYY8pgauQhaGg+EOOtrfxPkaIfi20viUmFV1utmFc6L5w8hJV3OfwJy3iP2O1lR0K0SBqt/k6aDzEqUqDDgkueAVe/4WismHqgGkN5FadIfoskJKkjpDzRSb5zHkDj0vUy7MtQ/p7Tp5d4l6kHd/1yzeKMaJuhykQpirtGNISutXfOAH++2rYbuRJe2oPov1NqlkgR/HLgkm3DHQ7rC7aNmYiPeQQuVtp52U1weGGXH862dltIbH/FAlXy/vx5i/zKzEBtZr4sKIh/XZ/dRoycfusK86FmInTPNYvKRdPOGdlIoSbr5cOuNb4wkHzpu2DXHOmKkYGb7jyl3zCQb9oHoeYPV8qKy7sKs+xjyp996qmwta7exCop2ONespC8OXJKJX0ELbR9S40HfOr9K69bQXGoL4C+6Pql3K6jUfRzL3MtZ9wKLX/AlqZWu34oB/wf0ujH6HBza+l/6EuTi5+LSEiFWutx7ExX8lgYq1LeGOwmnpv0r1GPLVN04k96nTCBDN8u6XwXRWz2uUeJ2ZXO0EFj0DyuB18MOrGxzKsSI59K6gSMHLsmCXiFR/yCbK88Rg5ZVbwGTrtBD34OWkiLIKddBrHzDLPhH15QZ97/4Nklc/wnktNt8CQ50Dkfvm1TQ8oB1HTcadJNEPRjps+wkOw6S9XoAbU+BaHOaFaxwSff0VBrIbPkOYvMk29pQyUQfS7Y/HaKvenGTiLIIyYYaus64E0I9lrml2wcMUc8Z1Fk+DYvXceCSDPYsgvxkhH3QMvDP1tmlE6rnMelKfXzje9ByJM+6ORSPzs6NT4AY/4F6VdDarPhs33LIH9QPu0+fu/7aDn8aaNDLrHi0e77VnJEKxSXJVWadV9B6jApUxgHUSI9b7Fc+VHiPjlA3fa2LSApK/k1mlPRNRRw7XmgVDqxMqMjntFu93YCkQG/ki4nt65YAHLhUNPXqR36kgha7u/yRdlroHPSbC62gRee0TPTvBkfhXsgvzojLdWfZ5TKIkS/F5xU9HQvNeRBY/E9fGjzqc+NhT+hGiFHZpl7ZzntMBVDfmIWKpa8mU1NHystpNpTrabBgh7cBmydCbvzC6h6epHVl6PtYd1OmG0mVKSGcak7Nf9x6TKGjJBfo2FcMfMhqputUPiPFcOBSkQ6uhfx4lP0xRqSgRT2wyK/OV/+Axf4n4uZuUUHLab5fxbXyWdTfiR5w4mHt+5DT74TI324WoqeTUfuqP+uE/4tuB4tuBfz8Z0+3A+JFd9PupAIvKsGuWz2k39YxiwPKtdv8LeS6D4ENX0AU7TO/kDz0cXPf24FeN1WuukBH8oC8HCB/p1WxOY+qNe+CpPd6Tb3Rr+ftOvaiWDeoHfky0KCnnqcyDlwqCpUD/+hECKqaGUL2vAnipH+amY2AJF5dXO5c9eTo1w8t5WB8fgrEoY1mwR/6zPWUt4G2p5kVH1EAOO1m/46Fmql/l5HPRXctO0kCFp2v0vFcCKqP0bC3WWUsSnT7jXYPqev7+s99eXHgqwz1oo0CmD7qjQsbBqOdZ9OCAtRLjvLXUnznhQOXilB0APKTUbbnybL7tepJ83k1cnhVHJDE63tF3L1LVNBymu+3XGRWO4gzJlh1P/xEP5CLnob8+QFfarLIzAYQw/8OdL1czTzuSmyfAcz+vwoNWKiaJl0hFfT5R5uLw1gk8qh68TQFcvVb+rjaNjevotBNpD7qRUyfO4AaDc0iSzccuCQa7ZLQMcy2aWahjOx8McSY152j4T2LVcAzWj1Q7LVqiJyj/gy/ghb6sz8d66lvhhv6GOv0T9Xn6XOre+qKPOU6YNdcsxAb/bUf9g/vX0/6us26D4JyAiqA7onU4WwVrFwJtB7r/L3DWDxQzsWGCSqIeVu9/0o9oSRJJV/agTlOBS90E4m6hbO0woFLIqlXKnLiL2zLzMt2Z0CMex+ODRPpOISOluiIiRJGz5vuX8NEvYtzuu9n2LoEPgViftb/oC3ruY9YlW/9Sr6lHS66VePFoQ2Qsx8AVr5VIbeEZPPhKli5wkoa5kJwLBkUqscPKqa47FWIXbPNYsWSNRpC9P8DQAXtuKpz2uDAJYF01dmlL5pZGesa8wTnH6z8XZAfqycqyj+hZDQKWqirrh/C1Y+JgaTEVqoqK6qYFR/QUdZ3V0HsmW8WYiN73WgVv/PyiqxgN+Sch4ElL6gfHhVEJZBuTkcFuXpcC9TtZFYZS0K0E6mbg6rAvmCPWaw4Ur3IEwMetI6B/XxMYhWCA5dEoaqzs+83kzK6CeI5k5xfNVMtlU/G6FcwMjMb4qzvzM0QH8QhaNE3h6jeCWX5+4VaDCz4O+Ss//NlK1rn3Iz+N6ACRteoDf3ifwE/P5Twgl26F1KP64AO53FROJZa6MouFYFc/gqw+fsKr2Gk2wkM+SvQ+hSzwlIRBy6JsPJ1YPKvzKSMzO4Icd4PznkVdLT01TkQ1NhQZEKc+aV/VWbjEbRQvYBT3gXanm5WfJC7GZh0pW9Jr1Htsmz4HPIH6huy2izEn85d6XIJRJ9bgYZ9zSpjKezgOquJ6PL/+n4s7ZVsfTLE0L+nxdXgyogDl3jbMlkFCKeVO1agGyDi3OlAdgezUp4utb/839Yuxinv+dc8Kx5BC+0GnT4BaDbMrPhgjfo7T7nBlx0OWbsFxOj/AK3GmhUXqKLxjN9AbPveLMSfrNlcBSu3AHQcxLciWDqiGiSr3lZBzL8g1M9YRbFaeKgXMrqFRwOzylIBBy7xRC3nP1RP5CFPvPpJ/pwp4V9JU/VXqgdCqMosVYj0QzyCFgrCzvjav3ohdPPqhzsglr1iFmIjO10EcdK/3PcXKtyvbwph2UvqB8T/Tth29NX2fncBnS92TtBmLN3Q7UoVwMi1H0PgqFlMLN00dZB6vO15vXpG5Ft5qYADl3jJ3wn5wRCI3A1mwSJFhnqS/0K98h9jVmxQQ8PJV1njQX8B+v/eGscq4Dq1X3RV1jO/8i9ZdO9SyIkXQ+xfZhaiJ6vXUwHLc4AKXNxRPwr0SvCHuyEKdpq1+NL5K/1+B1C/IK5oyyoraiS46Bn1YuFliOJcs5hYsnF/iBOfBZoOMissWXHgEg+0Y0A1UXbONAsBRv/XFDhzsH26+r0n66Ml2fMGa6fAD3Go06J3CShoqd3CrMRIBWxy6k2+dE3W14XpKnadtmYlgn3LIKfdApGgAnJSBSqi/71WFUvGmIUquy57EXLhPyukOq/uRN3j11ZrEj6qTVocuPhOQn77S4g175p5AGp0dbx6snJC151pl4YKzLU5FeK0T9W/kA9bl/TnfjzS1wcCXViOghY/ymsfLYCccbsvR0M6H+iE+9XX+T53Xzu6tUXXmxc8qYPFeJPtz7L6HzXqZ1YYY+XQLb6Vb0IueMqX3VevdNkJqqLd5TKzwpIJBy5+c7r23P1qqyOyk8J9kB8NVT+kq6wr0udO8XbzxQk1TPzkJF97D+m6M6d+oj6/OmYlBurz0kX5ds0zC9HTHWNPfst9gjCVLf/+Wm9t4qPEAQtj0VBPT+s/0x3fBVXLTjDZcoxVoDK7o1lhyYADFz+t/xT4+jwzKSNbjLKuMjslXapXF/Lz8fqYQj/5nv8TUKu5+cUYUOG6T1SQsX+FWYidVSzvc3+q4dKNq28v9qVAlWx7OsSY/7q7HVB0EPLHeyCWv2wW4kf/2w/+C5+bMxYT9TS16RsVwPwZImeWWUsMXeZh4INWA0dO3k0KHLj4ha7OUnXb4sNmwSLrdlWByI9hj1TkFPWqf/mr1m0juiLtR4M8auT42VhfdjJK+Rq0LHwS8qffqW/A2G7tyCrVIAY9Ahz3GzVzkdxKNXGogjHVh4kj2WSA+rxUwBIuCZsx5t2miZBzH4bYoR5XE0iqFx9i9KtAvW5mhVUUDlz8ULgX8oOBEAfXmwWL7pNx/sywtVpAmfQ/3OnutpFblDMy4VTbRo7R8i1oOVoIOfV6CLo5FSNZqyXE+HeBpkPMShh0FDfjDohVb5qF+JD1u6tXZw8BHc5RM74lxFjcbP1evfi5N6F9kWTV6tbuS9871Y83775UFA5cYkXVbanA3JbvzIJFByJnfQO0OMms2KCjEjoiovoFo14BupWvrusZfT7fXASx7mOzEDvZVv39qCJurEELNYj86nyInJ/MQvR0IHXyO+66TtPXedJVEHlbzYL/9BEfFbLqcjlQpZpZZYzFl3r6ohwY6tBOdbMSRDYbah1Nc+5LheDAJUb6uGPB38wswIgXrOqnTqj89fuDrBtEfe+wyk/7QE65wdfcjYj5OW7RUdoXZ0Mc3mQWoiePuxti8CORX/HQzhM9oC38h1nw37Hus71uBKrWMKuMsYSiI2cqp0A5MD5eRAhHZtS2HrfDPc6zuODAJRZUkv7bS8ykjOx1M8SJz5iZjeJcKx+GaqtQz4zTv/Bn25Eq7VLFXZ/IZsNU0PJ17DstdCY98SKII4fMQnRkRhYE1cFx0/pg9wLIby+P21VKStgDBVB9fwNkZptVxliFomvUS1+ybiElqCu1vhhAuS81GpkVFm8cuESLKrx+NKR8Mq7eoVBP9o7HBVI9iV+gj3JkdmeIX/zkvhR9OCv+A3x/jZnETjY+wTrqcupa7dYy9SAy9ZaYy3nrhpSnfuyiKZr6dp7/N986STuRrUarf+dvzYwxllSobcfcvwCL/qkeBxJQn4l6jI19jZPxE6SKec+8KD5k1R4JDVrqtIUY9274HAf1w6TzTzLqQpz+iT9By+ZvVHBwg5nETlfEpYaJMQUtKkCjfj9Tb4w9aGk1VgV4MyMHLQW7Ib84HZj5+7gGLVqTgWbAGEs61O5j6BMQl6oXmO0pUT6+qLin/GwcpHrsQUn8A6XKjndcPCvbMQmk7/qfOwNodJxZsUEBBj2xKuK0z4A2p+pxTOhIhKrixngMU0rvbJw7NbY6MrRd+/31wKrXzUL0dP7PkMcjH6VRDZzvLoM4vM0sxI9OvFYPiJyYx1iK2DYN8se7fC0P4UQ2GaxewL4DZLUxK8xvvOPi1cKnbG/siBEvhg9aqELsN79UkWKJ1QfDj6Dl8FbIL8/0L2ih68VnT4otaKES+l+eHXPQQsEBdcXWyW/hghZKypv7F6tmTQKCFiIG/ZmDFsZSCTUzPX8WoB6nKaE+nqhHnXz3eGDT12aF+Y13XLzYPkM9QY6BKDliFiyyz20Qw54yMxtUu+STEeob+mfIzpdCjI29hokOED5SP4x75puFGGXUBc6j4neRckjCKNgDOeGMmOsq6Dbzp36oHmxGmBUH1IH7u8vLXUWPJ9nhPPVq6j014hotjKUkepya+Qdg2asQIr5Pf7Lf79QLnYfCv/hinnHg4hblT7zbv1wtENl0CMQ53wNVMsxKeZLyPChJlXJHKBk35sqzEvLr8yGoxYAPpMiEOGti+JozkVBPpAmnQ+xbYhaiI+t1sY7R6nY2Kw4oCKQ8I/X/TRQ6K9e9kPjaM2Opj1oHTL8V2DXXLMSHpN2esepxw68u+oyPitxRgcKkK8oHLTUbQ5zyv7BBC1a+bgUt1epAnPqBL+Xy5cz7fAxaqqgn4zdjC1p09+kRsQctzYdDnPdD5KBlxX+tHaxEBi29b7F2WjhoYSw9UP8wqmx+0nPWjnOcUAVz+d7xQIJbFKQzDlzcmP84xKaJZmLRT/gURWe1Mis2qE7LtJv0UIz5T+QnZDdWvQUx/69mEjudQ9LxfDOLwr7lKogYrYKIDWYhOrLTRRBnfRu+SSI1o5x2M/D91RBHC81inNED2invQQx/Wn2xeLuXsbSiHsfR83rg0mWQbupDRUkU7NSPk/Sii8WOj4oiyZlpvboPyWvBwD8Dx//RTGwUH7b6F+1fAdn3ThUg2FTX9YqOR+hz8elJWx53l3VjJ1oUmH06FqJwt1mIjuz/e4hBD6tRmLyRvB2QEy+E2PGDWYg/qQI6MfwZoFYzs8IYS2vrP4WcfktcE/1ln9v1VW1+IRQ9DlzCoQ7L7x9fvnkiFZmj4mwUrTuZ9Ct9s0Y2OxHi7O8Qc/8aeuL+cBD8Oh6RnVQQcPLbahRlkmnOLMgvTlNBy36z4J3etRr+D6DXzWbFwe75VruAOPYaCqTzloY8CjQ/0awwxiqNooNWK5dlL5kF/+mK6Sf/T9ebYd5x4BKG/PZSiDXvmplF1mwKceG88K/CV72lApcrzMfOje16MaEjEtrZ8Gm3QTYZoIKp7wEqWx8NClomjIdQP+DR0l1Wx7wR+ZiKGqh9+0uIo3lmIX50Z+dBfwHan21WGGOV1vbpkN9fA3FgjVnwl76IcOqngHrPvOEcFycr/ls+aJECYuzr4YOW/asgp91o7Sac8k7sQYsif/iNf0EL1Wqh0vkVGbRkZkOc8XXkoGXB3yEnnh/3oIU6O2PUqxAXLeKghTFmaX6ieuE5H7KnlafoN0HPFR+dqB5TY++WX9nwjosd6tz8Xj+I4lyzYLFyMdQrcifUjfjDoRB7FgID/gSccL/5hRisfAOYfJWZxEZX9z1nGtC4v1nxyI+gpXojiLNU0NKon1mxUVJsnTMve8UsxIcOoKizc5/b+LYQY8zZ1imQ6nFY5G42C/7Rj8tj31Yvms4yKywSDlxCyaOQn4yECLm6JpsOsp70w+SqyGm3Qix9zsprOWeS+urGmHxFya8UCPm143DKe9HfIPIhEZd2NvTNoXC3qyiviOqzbJlsFvwnof5del4DQcFlzSZmlTHGwqDcF2obsPxVs+AfvUN/4j/V45J/PefSGR8VhZr3WPmgJSPLqnYbLsF201dW0EJVX6kuSqxBC/2Q0BO4X0EL3YKqyKCFOmFTL6dwQQu1MKCgMZ5BS4sREBfNg6DaDRy0MMbcoh3akS8Dp30OWcPfxw5qBYNpN0POopuqvJcQCe+4BNo1B/KjYeWvPo/+D9D1CjOxkb8L8n999F19jP8Q8KEbqdXI8SMzi42+1nsK5etEcYOIisvRFey8HWbBO10xmG5W1WxsVmzsWwY54TTEYyuWyKxW1tXvTheZFcYYixK1G6Gjo5D6Xn6QXS+HGPXv2F/8pjHecSlF+Snf/ap8H6JOF4YPWqiqLhVEowJDVMjIjxbqS/7lX9BCN2VG0dZmFEELlfH//JTYgpbGJ0QOWqgH1McnxiVokVWqQR53N8TFyzhoYYz5o2YTiNO/AIY9pW9I+kmsfAPym4utLvvMFu+4GPKneyAW/N3MLLJ2C+umSfX6ZsXG0heBaTdBNuhpdR+N9rZOqd0LID+ivJbYi8zpIy4qaa2CF89oF+mTk3Tme7Rk44FWIm5mmHLa6z+xumaXFJgF/+h6LCOeBxr2NiuMMeazPYusx7D96sWRj2Tr8RDj3/elTUy64R0XQleNF5bv7qy368IFLftX6mQtXZOEyv/HGrQU56ofgIt8CVqI/vyjCVqKD1nF5eIdtKx83ToS8zloob5Q1H9EnDuNgxbGWHw17ANxwWzIHteZBX+IzV9DfnmmvrDAgnHgciQPcvKvreSoAPrufutTzMwGXdn97jKII/lW+WYfniB1F2mfih3JvncAHX9hZh5QsbuvfwGxa55Z8M5V0LL4n8DkX5X7usdKtjkV4tKlVv+RcJWNGWPML3SlmXZ3T35b73T7RdA17M/GA4V7zQojlf6RXc5+oFywoCsaDo3QyHDuI/rJXbYcCfTyoUDR8lchVlMJ/tjpNuqDo2nESF2wVRC35Tsz985V0DL3YWCGCqx8JDMbAGNehzh9AlC7pVlljLEEomaxF8yB9HGnV+yaDfnxKH18zyyVO8dlp/qGoFtEAa/69X36c6cDTQebFRuUh/LhIBX2qSj7kkVAVhvzC1Gimzvv94coPmwWoueqJYEDXaNg4T/MzLvIQYsKjH78rfp/lD+Wi4XeZRn1SlR/Z8YY892RfMgZt/la88XV7cxKovLuuBwttPpQhB5V9L0zfNBCv4+OOEqOQJyonuRjDVrUnyO/u9KXoIWIMa9F9wSugomYghb6oTrzq/BBy7SbfQ1aZEZtYMSL1i4LBy2MsWRBR0dU80U9Hsuq/iTXin1L9C1PznmpxIGLnPcoxN6lZmaRdbtCDHzQzOzJOQ9BUBZ529OBbj6U4qfPY+dMM4kNXftF65PNzIPV7wA/qt8bpWOvBBw7nZqghW5g+UQ2Gwpx4UKgxzVmhTHGkkyXyyDO/wEyu71ZiI1+7qGcl0oevFTOo6J9yyHf7a/+8mX35HUDxfPpiGiIWbFBBeo+GKp3FcQli2N/lU9HVR8Oh8BRsxA9XS/lvBkqFM0wKy7t+FH9IIyN+iaTzO6ogpbJQFYrsxLK36CFjvLQ/14I6gMVrpIxY4wli8K9kN9cElP+YCBXuYRprBLuuKgn0qk3BAUtWp9bwgctVKBu0lU6yBAjn4s9aKGrz99d7k/QQvVaTnnbe9ByaAPkV9R9OcqghTpNn/Fl4oIW6nWkgiRB7Qs4aGGMpYrqDfRjpTzuLrMQG52w+8UZ+lZsZVT5Apdl/4bYPsNMLLJ2G4hBD5uZPTn3LxC0U0OVdDteYFajJ2f+3rerz/oaXnZHM3OJeiF9eZbVpiAKdItHnPEFULeTWSnP16Cl7elW0nHzE80KY4ylEFHVajsy9g1fqu1STz3qZ1cZK+xWrsAlP0cFDL8zkzJi5AtAuLv3exYB8x+HrN4IYvgzZjEGW6dALFHBhg9kl18CnS81M5eoA/a3F5fL8XFLt2E/47PwtWtm/sGXoEV3clY/7OK0T/WrFsYYS2nq8Zp2jqUPFwqoV5L87gr9mF6ZVKrARf54D0ThfjOz6Cf+NuPMzAY9yX9/rXWLaPjfY7+KRkdEk682k9jonaITnzUz9+T0O6JuDkaBhDjlvfDHalSnRQV6sZI1m0OcMxnQ26tR9FpijLFk1HSwbhEjG/YzC9ETa9+HnHqzGlWedNXKE7hsnw6x6k0zscjM+hBDg/sTlbPoGQhKyqUqul0uM4vR00dEuRvMLHo6mXjMf0Ct1j1Z/m+Ipc+ZiXdi1EtA29PMzAb1e5r9gJlETzYZoMtoo/lws8IYY2mEOtafNw3Sh8a8YvnLkLPuN7P0VzkCF6qVMo0i0mC6Om64HZSD6yFn36/rheg8klhtm+rbERH63gZQ1V4vclSEP/UWM4kCXRUPdwVcBUX46R4ziZ7seAHEOVOA2i3MCmOMpaFqtSDGve9L0q6Y9yiw9AUzS2+VI3BZ/M/yNVuaDQO6/9rM7EjIKddbvYgGPgTUaWfWo0Q9kab404RL1usGMfgRM3OJ8nsm/qL8bSqXJAUsx99nZjbWf6Zva8WKGpWJk98CqtYwK4wxlsaoWjsl7aoXxzqnLwZy+q3Axi/NLH2lf+BCT9iz/2wmFlmlmtlBCZM3septiK2T9JEFesewS2HY9USKhv7cx77m7YmdGkJ+czHE4W1mwRvZ+mT19QoTyW+fAfndpeWrEHtErzrEiOfUP0tsP7yMMZZy6EXbGROs7vZRosdgOfEiIIYmuakg7QMXSbdbjhwyM6PPHUCDnmZio3Af5A93W0EClW2O9Yl09wJg4dNmEhtBux6NTzAzd+SPd0Nsm2Zm3uiquJSM61QjZu9SyK/O1jtTsZB9f2O96uAkXMZYZdX6FIhzv4/pxpE4mgf55ZnAoY1mJf2kd+Cyczaw/HUzscjaLSBO+KOZ2ZOz7rPqm1AJ/Vi7fFIETAXvfKqOS1VjPVn1JsRi7zePiKzRBOL0T50TgHM3Q35+armbWl7JntdDDI39FhJjjKW8Rv1U8DIDsl4Xs+CdyNsB+cXpadsaII0DFwk54w4IEXxFTAz9G5ARZiuOgp0lL1lXjcPldLi19HmInT+bSfSkyIAY/ar6F/NQMZZ2Q6bcaCbeUIEkceqHzrk9xYesAnZ5W81CdHTQctK/1EgAexYC1OhxxX8qbUVIxhhDdnsVvEyHDFd2IgJdMPWbS9WDbPrVeEnfwGX1OxA5s8zEIukWTqeLzMwG1WyZcpMOdsTwJ3XGd0wOb4P8yYfgh/T/XfjjrVDUVl1909K2YTTEqFeAZkPNLAR9nSZerBt+xeJY0JK/G/Jb9QP2Xn/gx7uA769Rn3uYfyfGGEt3NRpBnPWNVYojSmLz15AzPe7Sp4D0DFzoSTskYND5Kif+08wcLHlOPRnPVwHOGKDDuWYxenLG7eXza6KgbxEd7+2bT864TbdBj4a+mhemGq+ccaf+gYiF7HSJVTxv9buQb/eAWPOu+RUjpC0DY4xVOnRd+rRPdYmIaIkFf1OPs2+bWXpIz8Bl0T8gDm8yE6PP7UD9HmZiQ7cDuN86kjnJh7L+m7+FWPeRmcRGjHwR8NLbYtVbEMtfNRNvZJtxEIMfNTMbFNwtoaOd6EkVFIoTn4b87jLgu19CFO01vxKgjsfeS4wxlo6qZOoSEbL7tWbBOzlZ/d5dc8ws9aVf4EIByNy/monFSsgNX1Ww9PaROO5OoF43sxqlkiLI6SpQ8gEdp3iqHrt/BeS0KPNasjtDjFWRudMtKhWM0S5SLHSzxOEqaPlkdPldlkBtx5sBY4xVctSgkXrq9YuuwKcoKYD88jwgb4dZSW1pF7jIOQ+VO54Rg/4SPiGXcmGWvw6Z1Qo4PvyNI1eoTcCBlWYSPR1wDX7MzFw4qr45qV5L8WGz4J6uDnzqB0D1emYlxMF16s++JKZaLZRjRMnR8pNRYY+xdDuDbleaGWOMlTlSIvHzhjxMXZ1rViqRwY9CRngR7oQuUshJ9Lia+j2N0itwoQJvy142E4ts3D9CjyFpNR2khFyqI6KewGOSt10HT37QOSAeehHpa9x7FpuZN7peTYNeZhaCGkN+eS5E0T6z4B39O1AirvziDIiDa82qPdHpfKBuZzNjjDHgUEEJHpuYg94PLsNZz69FZtXKWfNJDPiTCmA8Vk43xJbv9AvrVJdegQs9cZccMROLvv4swvw1V7wOsWs2ZIuTwt84ckl3oFZP9LGS7c8G6M2tLZMh6CpxFCTl/zj+3VVgN/lXUSf6EpnVDmL0fyC/Oj9i0ELXsOFll4kxlvZW7CjAqH+swtOTd2J/fgnG9cjGkA4xvshMZf1+B0RqEOyA0iJwYLWZpab0CVwo8WjtB2Zike3PAlqMMDMbRQetf0RFDH1Cv4/Jjh8gfMjellVqQAx70sxcKNyrgovojlaoZ5NVsdbBvMdiSjKWmQ0gTvsIcuqNEPtXmFVngo7qstubGWOsslu/pwjnvbQeW/cV63nVKgL3nxZ9Zdm00fcOwMvzhCGOFkJOv83MUlPaBC7yx9+bkUXfDhocnKQbSs57BIKSeenqr8cy+uVJyB9i7/BJxPHq7+KhqaOcelNUfYhkjYYQJ6tAy6mo3ZbJkLP/z0y80wEYBS1z/wKx40ezGgYFmVSvhjHGlLyiElz52gbsO1y2k37FoAbo2NjDLct0RrvlURwbic3fqIjwEzNLPekRuGybCrHtezMxet8IhCuZfHA9sOBpq0LsoIfNYgxW/8+fCrlZKmChVgNuUUn/te+biXs6AXbMGwAlJNuhktHfXRZTMi41g5TrPlKf34dmxZms1RKgrtDcYJExZvz+421YnVNoZur1XI0quOvkJmbGNDo2iiJ4kbP+qP4TW2PcipIWgYv+Bwggq9eLfP151r3qSblIBTi3qp+GtmY1SlTwbpY/FXKtir01zSyC3E2QU6PsXE27Om3GmUkIqoz7rQpa8nPMQhQGPKA+vy0QLhLBpPr7itM+Bmo1NyuMscru7Z/34f15wRcCbh7RGA1re2h7UllQ8DLQ26UQagmADZ+ZWWpJ/cBl08RyxxCC7rpXb2BmNnJmQax5zzoq6W/luMRk8TMQPnTi1KWdXSfkSsjvr42qMi/1v9CZ6U7mPFx+B8uLjr/QN5Sow7YbYtSrQOPjzYwxVtkt31GAP3y8xcwszbIzcP2JjcyMlXP8vZBedusVuThCNfkkleKBi3rynh2y20LtwHuHTzySP/5Wvxcn/J9z3RK38ndCzov9FozOyRn+lJm5sOzf1tU2rzLqQpz8Zvi8lrnRH53Jhv3U1/8WyElXlGtwaYv+DTpdaCaMscrucFEJrnlzE4pCegPed2oz1MhIm7TMuBBDHvNWYXfLVL0znmpS+7tg0zcQu+aZiUXfSgl31EI5Fzt+0FViQVVpYyTnPAhRdNDMYnDcHe4r9h7eCvmTFXx5NupF58RfCsImXR51Xous2RTipKetInhH8s2qM91/Y0D0yb+MsfRzz0dbsG5XWV4L6d2yJs7rF+OLzEpBQIz4F2Tni808PP3icuMEM0sdKR24yDl/NiOLrNMB6HG1mdmgUvyl158Hqd9bJUOPo0YF75a+YibRk9UbQfR330RRTrk+qmBJdr0ccGzWJVXQ8iuIKEtCS1TVHaXltFvd/RmNj4cYTf2UKmcRKcZYeW/M2ouP5h8wszIPn9UcVfihwh1qD0B1s9qdYRbCk9Hs3Few1A1c6CYRleoPIAY+oP5GmWZmY9krECrYkI2OC/ME7p78+c8Q0qotEAsx4H73FXJXvAax6SszcU9mtYYYHiZRltoUxNDxWQx5FHLp8xB7FpoVZ/o4b/xHuvMpY4yRpdsKcN9nW82szJl96mJgu0pcbC4a1Jhx3PuQboqq7ky95ospG7hQ0BBINugJUD0WJ8WHdT0RonNbwlXTdWPPImDVO2YSPZndEeh5nZlFkLcdmHGnmXgjRv/XOTjaPb9cHRxPOv4CMnczxMYvzYIzXdvl1I+dr2Ezxiqd3MISXPvGBhQHFz5HRjXgj6dysbmoUPAy9k1IujkbhlCP3akmNQOXnJkQ26aaiUXQVbBwwQjd/KHaJNS7iCrqxkjOvt9d8mkEgu7fh9slCiApaCkuv40aiexzG9BypJmFoIDu20utq+FRkPW76+J9wmV2uhj9b6DJQDNjjDHg7g+3YP3e8rvXt41sgjYN3D0+MhvqOVEM/wdAR0cOffjodm2qScnAhRJiA+mjn3DBSOE+YJ5V0l8MpBszMR6W7vgRYkPsCU2yyQCg4/lmFgFd+46m0Fy9bio4etTMypMzbofYv8rMvNH1V3pcq2viuCGPvw9wmTTGGKscXv1xDz5dWP4FWYdG1XHrKC4254uuV0BcvNQqVRHK6UVtEku9wGX3fAj1JB5IH/2ECUbk/L/qnQrZdJBz0TUvQgreRcvqEeQiiDqSBzn9ZjNxT1appqvXomoNsxJi5esQK/5jJlGgI6Kf/+TqFpLscJ4KGoOP9xhjlduirfl44HP7diWPntMC1atxRq5vsloDp7wLXDAXUC9m6eaR7HsHxEnPmQ9IHUIqZpwS5NfnQwT0WKDcFnERJYQ6fIPnbYd8oxNESQFw1ncquhxlfiFKdET16WgziZ5sezrEaS6rFtJNqPlhGiE6oeq1OqizsWch5AdDra9LlHS7hKPB1xbt0I6YOHc6J+Myxo45WHAUp/xjNTaa5omBzj2uHp67RD3RMmYjtXZcqETxuk/NxCLo+CHcbsvPD1pPztTAL9aghYQkBUdD9wkKc3wTZO9SyIXeO4Dq4zOnK9YFe6wAMIaghbgKWqi2CyXjctDCGDPo5fJvPthqG7RQP6I/ncHtP5izlApc5MKngxJidWJouGvNuZuA5eYoxGnnwQvabQlJCo5KZ/U50y2oiCTk1BtUgBGSah9BaU0V2+q41Ido4kUQ1GQyzvSOzKkfAVltzApjjAGv/LAbXyy2v2jwh/HN0KQO9yNizlIncCnYDax8w0wsupFimJtEcu6jus6Kzm3xIwHJr92WCA0gj1F/39A+TK70vwdo1M9MgsmffhdbHyIPdPDUdLCZMcYYMH9zHh760r5IZZ9WNXDl4NS75cISK3UCl6UvBh1t6PondhnSpQJ2W0S/GGqUlPJrt6XLRUD9HmYSRtFBFWR4/7xl3a4qMHJIHl71FsRCD/2QYiD73hm+rg5jrNI5kH8U17+5GcVHy6dWVoHEE+e14gq5LKLUCFyoVP+S4Mxn0e+36j9Vzay8Y7stdJzU/kyzGr3QK9jR0Ec4A9zt2sg5D0Hk55iZO3o3Z/TL9reIds2F/N5lobsY0Q6X6xwexlilQHktd7y3BZv329eMumpIQ/RpGabPHGNGagQuq98N6n8jazQBulxmZjaCdlt+R//V46jtmgOxdYqZRE90+SVQt5OZhbF/hS6Y51mfm4Fmw8wkADVP/OoXMSfjuiGr14MY+5b6zoqxDxRjLK28MH03vl5m32OtSZ2q+P14Tshl7qRE4CJDjjdEn1vCdoA+tttC99Y7X2JWoyfn/82Moke7LTiBbkBFRhVyPSfk1m5hVQ8ORX8OdWs+rIK5BBAjXgKy25sZY4wBczbm4RGHvBby4Jkt9W0ixtxI/u+UrVOCGvfpssU9bzAzG4e3lu22HHe3+hvGmJ1Ot2/WfmQm0XO927L+M4jN35iJe2Lo3217EcnZf/QnN8cF2e0q95WAGWOVwv68o7ju7Y044lAybESn2ji7b10zYyyypA9c5KKQZNKu6skxTG8FueBv1m4LfUz3q81q9Gi3R+ComUWHck9wgouy+JTL86MKtjySLccAnS40swAbJkDMt1odxJvMbm/1xGCMMYNilVvf3Yzt++13kKmJ4qPnccNV5k1yBy4H1gDrvzAT9UNAya10W8UJXZle+ooeCuqIGeY4yZWCPWV1YGLR8VygbmczCWPpSxAH15qJO1JkQpz0rJkFOLgecvKVZhJfkhp5jXlNPQrVMSuMMQY8O3UXvltxyMzKu31UU7RvyE0UmTdJHbjIZS8Hd2DudH7Y/Am9O3I0Tz+Zo+f1ZjUGS1/Qf16sxHF3mVEYdP35Z5sclUj63w3U62ImBu3cfHsJROF+sxBnFEzaJQUzxiqt2RsO468TnW9G6iaKIxubGWPuJW/gop58sVK9ig+gd1GcqCdpsfh5a0y1UmrG2FX0SD7k4n+aSfRk8+GuirDJ+Y+rQGO3mbkjs9pB2JT1lz/cDbHzZzOLL103ZmDsV8UZY+lj7+EjuP7tzThaYp/XQqiJYiY3UWRRSN7AZf2nEPm7zEQ9QTbur17VDzUzG0tf0B2gSdgAx61VbwT9/6MljvutGYVBCcULvBeGE0MfL38ctvZDiCX/MpP4so6IXnXuPs0Yq3QoVrn5nS3YcaB8H6JS1ETxpM5ZZsaYN8kbuCwxuyeG6HWzGdk4WqD7GBG9w9H4eD2OnoRcFEUdlRCyXjeg3elm5kzOfsBzjRXZ7MTyN3gOroOcco2ZJECf27mkP2MsyDOTd2LKaue8Fm6iyGKVnIHL/pVBV3j1DaHOF5uZjZVvQhTs1EPR+xb9PiZbp0JQJ+oYieN+Q/+1Jk4oAXnF62bijq6QO/zvZmaUHIH87gqIIvsCT36TddryERFjLMhP6w7jiW+c67UQbqLIYpWUgQsl5QYSdK3Z6ThClkAueNIa1moJtD9Xj2Mhl9jc0vFI1moGdL3czJzJ2f/n+bq1oD83ZFdJzn0YIucnM4s/cdJzQLVaZsYYq+x25x7BjW9vQkmYF2vcRJH5IfkCl6OFwMqyHQjKowhbcG7jlxAHVuqh6H2T+hvFGMnnbgHWfWYm0dM7P1UiXPPbuxRY/Z6ZuKML8A3+i5kZO34A5oasxZGkmjFtxpsZY6yyoyTcm97ZjJxDzhW/uYki80vyBS7rPoag+iml2p0J1GlrJuXRbRz9vmp1oMe1ehwLfQU71oJzdB27e+RcE6pqG3Td2wXR7x6gdgszU4oOQH53OYQsMQvxJavVgRhm7XAxxhh5atJOTF+Ta2b2uIki80vSBS5y5X/NyCLC7bbkzISg3QbS/rywFXVdoSvYIcdUUel8AVAzQn2C3fMh1nvb2dHHT33uMDNj+h0QhzaaSfyJAfcDtTixjjFmoYDlqe/Cd7LnJorMT8kVuNC14C2TzEQ9UdduA7Qaa2Y2Apovih4+3Kah3Z788D+AboheN5qRMznHe2Kr6P8HICPgCuHGL4FV3hJ7Y6FvSfW5zcwYY5XdzkNHcHOEvBbCTRSZn5LrO2nV20FHHqL7Veo/Dp9i7ibItR/roczuCLQcocexkEueM6PoyUbHAU2HmJmDaHZbstoBPa8zM6VgD+SU2I/GvNC9iKpkmBljrDKjvBZKxt11OPzROjdRZH5LqsBFBlTK1Y0Ju//KzGwsef5YLoro/mv6rx5Hbc9iiO0zzCR6otdNZuSsNC/HCzHwT+pfqyzZV06/HSIv/LVDP8n25wCtTzYzxlhl98S3O/HjusNmZo+bKLJ4SJ7AZefPwbVT2qgnyaw2ZhLiSB6kaaYo6RZRt6v0OBZyxatmFD1ZvR7Q+RIzc7BvGbD6fTNxR9brAXT5pZkpVFV4zTtmEn+ySg2IoX8zM8ZYZTd1VS6e+T7ysTo3UWTxkDSBiwwpwibC3RBa9RZE0V5r3PY0gJJWY1FSDKx820xi0PWKiLVN5LzHvN8kGvyw+o/5p6JbRNN9KLLnRf/fhm1uyRirPHYcLNZXn/WueBjcRJHFS3IELnSbZ/X/zEQ9udONnHZnmFkoKsdfViBOuLh2HNHGLzw3OLQjekZIyj24XgVdZX9PN3TOTPuzzEzNf/o9xOFtZhZ/MqsVRL/fmRljrDI7UiJx/ZubsTfPuV5LKW6iyOIlOQIXKiJXuoNCOl8alM8RZMv3EPuW6CE9qfpRCE2u+I8ZRU/3DqrXxczsyYVPea4RI074P/qvNdk+A2LZS9Y4QcSAB8s3cmSMVUp/nZiD2RvD57UQbqLI4ikpAhe55l0zsogul5lReTKg87GgAEdUNbMo0fXnTV+bSfREtyvMyEGhCsxCatREIut3L9ttOVoIOeV6a5wgsmEfV20LGGPpb/LKQ3h2SuSO+dxEkcVbxQcuR/L1UU0pXSukcX8zC0F1XtZ/biZKYMJqtOgKdknkbc9wJO1IdPyFmTlY8gJEceRXKoHECffTf63Jgr9D7F9hjRNEDPmr+k9ybMoxxirO9gPFuPmdTWYW3u/HcRNFFl8V/6xE+SUBT+giXDCy7JVjRy2yYW+gQS89joUfx0S6sWNmtpnYOFoAueifZuKO3m3peIE1Obgect4j1jhBZKvRQOtTzIwxVlkVH5W49o1N2J8fua1I75Y1ceXgBmbGWHxUeOAi14QkqzpdJy45Arm0rBy/iHTt2I1dcyCo0WGMRKTr2CvfhCjYaSbu6IRYs9shf7gDgnamEoRuC4ghT5gZY6wye+TrHZi7Oc/MnFlNFFuiKndRZHFWsYFLcS6w4SszUU+YzYY6X7vd8BlE/nYzUXwIXOTKN8woejKrNdBylJnZk0vKbkG5oVsdlP79KHF5wwRrnChdLwXoNhNjrFL7ZvlBvDDN3Y3LK4c0Qt9WnMjP4q9iAxc6JiopMJPwx0TUtbmUvsHjVJzOLXkUWP2emcSAklfD5YFsmwqxZ7GZuCP63qr+ZapZR0zTbzeriaGLzQ162MwYY5XVln3FuPV/m80sPKuJYlMzYyy+KjRwkavLir7pCrilOR2hcjcBm741EwpwfDgm2jrF8/GNHUFF58KQi73ttiCjLlBafG/RMxCH1lnjROl1XexBIWMspVFeyzVvbsTBgsh5LeRPZ7RAdo0Yb3gy5lLFBS5FB4GN35iJ0nI0UKOhmYRY8dqxarM6wOnkEOB4IFfHXjJfNhkM1O1sZjZytwDrPjUTd2TPa1TwUkdf05ZzEpyQS7st/e4xM8ZYZfXQlzuwcIu7vLqTOtfWdVsYS5SKC1w2T4SQRWYCCKfdFlkSfPOn9TigeoxZ60cLgfVWZ+lYRNr5kUvLGkG6IUUGRO/brPGsByCOHNLjhKHdllpcf4GxyuyrJQfx8gx3eS26ieI5Lc2MscSosMBFBuxESJEJdDjPzEJsnQxxaKOZqGCh04VmFAM6JircbybRkZTXEu5zoTYGy/9tJi51Vn8eVQPeswhY4fH3xoh3Wxhjm/YW4Y4PtphZZLeNbKJ7EjGWSBUTuFDBt8BqtdQJmjor25DLy6rN6mOitqebWfTkWm/dmW21GgvUbGImNjZMgMiPXGUykOhzh34vf7wLQro7W/YN77YwVqkVHZG4+s1NOJjvbpe4fYMM3DoqzGMgY3FSMYEL9dwp2mcmYXZR6Lr0+oAcEcqDqV7fTKJEt4nWfWIm0dPtBsKQHnsKyaaDrIrBW76D2DLZrCYG77Ywxv40YTuWbHVfL+rR81qhOjdRZBWgQgIXuSHgmEjvojh0gl73EcTRssJHjnkwXmydGhQ0RUMfbQV0bC6HjrY2fWcm7lidpSXkzD9YC4lECcG828JYpTVh8QH856c9ZhbZOX3rYQQ3UWQVpGJ2XNZ9ZgZKi5HOx0QrXjcjChbUp9rOIcDxQK7/yIxiQEdbmXXNpDxKJi69BeWGrN7IypdZ+wHErnlmNTEkqkIcd5eZMcYqm/V7ivAbD3ktuonimfxCh1WcxAcuexZB5G4wE0B0ONeMQtBV4q1TzERpOix8TokrKpgIDJqi5Pg5EzqK8tr/qMev1B9aFXLWH81CAnW+iOu2MFZJFR6RuPb1DTjksl4LoSaKTbmJIqtAiQ9cNgR0dybtHI5c1r4XtGsh2se+24KdcyDytppJdPTRVvuzzczG1u9VYOb+1YvuC9TjehXsvApxYI1ZTRzR77dmxBirbO7/bBuW7ig0s8i4iSJLBgkPXOTmsqJzOiG1dgszCyZXv2tGhlOA44EMTPSNlk4Qdv7BlaveNCOX2o4HslpCzk1ssTkiW6v/d8M+ZsYYq0w+Wbgfb8zaa2aRcRNFliwSG7hQtdztP5mJerXf7kwzCnFwLcSuOWainmCzOwP1uphZDDxWsbUjwgVQR/LU/8NbYTvR/dfA8v9A5LrrCeIn0Z9vEjFWGa3dVYi733e/M0y4iSJLFokNXKiYXGAl2banmUGINSF1Vto7BDheHFwHsX+ZmcTAKdgi6z+FoCvcLklqcdD6FMh5j5qVxJGNBwItRpgZY6yyKCguwTVvbsLhYvcXCLiJIksmCQ1cgo6JqEJsw75mFkyuCu4jJJwCHC82fWUG0ZONjrMq2zqQKz0eE1E37FVvVdBuC+e2MFYZ3ffpNqzYUdaV3w1uosiSSWJ3XAICF7RxCEYOrIbYt8RMVDCQkQU0H2Zm0ZOBlXqjFW63JX+n+vuVdbB2Q6jApUJ2W+q0DZ9gzBhLSx/O34+3f/ZWx4qbKLJkk7jAhfJWDq43E9pFcSjdH5oj0nKk+iwzzSRKR/L1bZ9YOebkEPV5e2qoSFVy9yypmN0WKnYn+NUTY5XJypwC3PORt1uV3ESRJaPEBS6bJpqBetKuWh1oNdrMgsn1weX4RZtTzSgG26ZCUPASA1mzqVWS34Fc562wneh6BeT8J8wscWS1mkCPq82MMVYZ5BWV4Nq3Nun3XnATRZaMEha4yE0Bx0RNhwLVaplJgMNbIXJmmYnRepwZRE9uLguaotb6ZPUfh2uAhXtVcBRQLM+NogMQ+1eYSQJRXk2Y69yMsfTz+4+3YXWO+3othJsosmSVmMBFlkBsm2YmtIviEIyE7LbI7I5Adnszi8GWSWYQPdFmvBnZWPcJBHW89kAueNqMEkv0usWMGGOVwbtz9uH9ed77s3ETRZasEhO47FkEFB8wE6X1WDMIJkOr6lKxt1jl7YDYu9RMokPVba0dF3tybcj1bRdEkfvCT36RdP25YW8zY4ylu+U7CvRui1fcRJEls8QELtummoF68qzZGKBrxaGo/smWso8jovUYM4qBH7stTfoDNRqZWYgiFZBtnWwmyU30uc2MGGPp7nCRVa+l4Ii3vBZuosiSXUICF7l9hhkpLWm3xWb7UQUYQhaZidnloM7RMZJbYw9cZOtTzMjG5m88HxNVBFlTPRC19aHfE2MsJdzz0Ras2+Utr4VwE0WW7BIQuEhga0B+S6tRZhSs3DFRoz4A7c7EKrDDdJRES/vPmciNX5hRkuvxa/WvzQ9GjFUGb83ei4/mBxzPu8RNFFkqiH/gsm8FROFuM1Fsy8yr4Ca0sm3LMOXoD21Q/3FRrlp9nDi00Uyio69uNxtiZiHkUWDjl2aSvHQH6u58BZqxymDptgLc+4n3LvjcRJGlivgHLgG3iWStlkDdTmYWYM8iiLwdZmIRzU80oxBrPwDe7OiumeH2H8wgeqLpYPur2yRnJkTBHjNJYnSLi6rlMsbSWm5hCa59YwOK3NfCPIabKLJUEffARQbWN3HaRbErle8QuMiF/7DeB9aFcSADkoKjRpV7HciAonrJTPS4xowYY+ns7g+3YP3eYjNzj5soslQS/x2X7TPNgHJF7IOA0CBE1u1qn9+yewFEzk/WuNDFTse2gKTgaIVLEA7svZSkdMXfdpyUy1i6e/XHPfh0ofe8FvIAN1FkKSS+gQvVUDm8yUwUu10UKsW/IyTAcNiZkUteMCOlSoYZOMjfCXFgpZlERwr1/2g60MxC0BFRzhwzSWLdfxX5a8UYS2mLtubjgc+912shJ3bKwnncRJGlkPgGLjt/NgN65d8YqNfFzAJsnwFxNPjKnmg21IwClBRBrHnPTJRazczAQWjrgGg0OQGoWsNMQtD1beEiQbiCia5XmhFjLB0dLDiK697YCI/lWrRq6hngsXNbmBljqSGugYsMDB7sghFiV7yt6SAzCLBjZlD1XdGglxnZkzvMkVIMRAuHBGFFbrHJy0kysukQ+2CRMZYWpHrtdPt7m7Fxn/e8FnLbaG6iyFJPwnZcbHdRlNAEWlmjof2TbWluSynaDQlnpw87Ls2Hm4GNrT4k/saZ6Hq5GTHG0tErP+zG10sPmZk37Rpm4DZuoshSUBwDFwmRUxa4oPkwMwhAZf5D80TsdlsUGXjslJEFNOhpZjaovkrAx0eNrkLbObQR4uBaM0lOuv5Mp4vMjDGWbuZvzsNDXwaXkfDisXO4iSJLTfELXPavOna0I0Um0Ph4PQ6y/QcIBBcccNqZCQpEGvdXHxgmA37vUojiw2YSHZndGaDdHzs+VOONN9HuTKA6J9wxlo725x3F1W9uRPHR6PLszupTFyO6cBNFlpriF7jsDNhJoaaKVVTwEiqwxkupJgPMIADdTsrdYiaK3ccE2jXXDGLQzOE2EUmBwAXdrjIDxlg6obyW297bjO37o+uRllVd4MGzOCGXpa64BS5y52wzUhzyUeS26WYUgHZTQoUEIsJu9yaA9CFw0RVzHQQ1jUxCunZLq5PNjDGWTp6ftgvfLo8ur4X8flxzbqLIUlr8dlz2LDIDFQQ0s8lbKSkCds8zE4ukdgDVyzf4kjkBQRCxC24C7Qr+c6PiVL+Fdn8OrTOTJNXpAvUvyw9MjKWb2RsO45Gvc8zMO2qieNUQbqLIUltCAhfbHZddc8vVb3EMSHYFJOZmZtv3OypVckR9/EIziY7OyWnY18xC7PjRDJKX6HyJGTHG0sXew0dw/dubcbQkurwWbqLI0kV8ApfczRCF+/XQCjRsrjdTXZYQjkdAOWVHP6JRP/qvNbGzbxlESYGZRKlRH/WVsa82K3fE3rgxnmRWO8ebWYyx1ESxyi3vbsGOA9HVayHcRJGli/gELoHHRBRoiPL/Gxlal4XYBS4H16sgaLeZqN/XyGEnpFTgTk+0wh1FbfNvx0Vm1DYj/4jOF9J/rQljLC08M3knvl8ZfV4LN1Fk6SROgctiMwgTaNhVtm3YxwwChAQiouFxZmRP+hC4OO78lKhXO7sXmElsZGZ9oH4PM/MRHxMxllZ+WncYT3wTfb0W8uCZLbmJIksbcQlcAoMH62gnhG6+GNwQTNZqaV83ZW9ZEKQlZMfFITii7tSyyExi1PE8YH9sTSBDyXrd7IM/FhdHSiTyi6NoEMOYSzsPHcGNb29CSQy7qCM61cbZfeuaGWOpLz47LrsDgg27QMPu1k/D3mYQInD3hm7KNIiwSxHw8dGw/h8On4sf9WEM0WwIRNFBM/OH6MK7LYmyeV8Rfvnqegx/YiW+XOLvvyNjhJJwb35nM3JU8BKtDPVw9uh5rcyMsfTgf+BCN4XMToK+nWN3HGIXADjscsjAICi7i/qMbQrZlSrYDZEX25aq/n9QuXwbgW0HYqE7ZVd3qMobi46/MAMWb/lFEut3FWPbgSO4+o2NuOSVDVi3O+SWHGMxeGrSTsxYm2tm0bljVFO0bxjmMZOxFOR/4KKClmNl/Bt0V/+H8rdz5K6Q/kSKsDviOFoAHFxtJkpD9eeFs3epGcQg3P/Drx2X9ufq209+ktS7iY6KWEJ0aVodX97aUdfFIFNWH8LIJ1fjL1/tQF4RHx+x2ExbnYunvou+Xguhrs+3jFQvkhhLM3EJXI5xSj7dOd8MAtS3CRj2LoOQAU8CkZJZ960wg+iJ+g7NG6lg3r7lZhIb0fECX5KIg7Q/xwxYojTKqoYPrmuPgW2t22HUN+bZKbsw7IlV+HyR1aeLMa92HCzGjW9vjimvhTx2bgtkchNFlob8D1wCggdhl49SuBcib6uZWCSqqqDEZrcgJF9F2AU3AaQfgYVT12lq3EjF7WKkj4lajlB/3hKz4g8Khlji0U2N/13bDiM71zEr1hPPdW9twoUvr8eqHD4+Yu5RXssNKmjZmxfbY815/erixE7cRJGlJ98DF7k/YNfDLgiwS56t11F9JuXPYeXekF2JCIEL7dDEzOn/sdunHZK2p6m/2FH1uca+O1RKZquvn1NyM4u7mhlV8Pqv2uKiE+qbFcv0NbkY89RqPPjFduQW8vERi+zxiTmYtT62zvbZNaviT2dwE0WWvuK642J7A8gucLHbbSF7QnYl6tlU4A10YJUZREffKHL4f8g9/tRvEe3Oto7ASvOAfCA6nm9GrKJkVBX4xwWt8MDpzVFNlG3PH5ESz0/bjRP/tgofL9ivO/syZmfyykN4ZsouM4veveObonEW9ypj6cvnwEU9Ku+zggdJN3OyO+hxIBlal4U45ZXsX2MG6vfVVq8gqtYwMxtH8iByN5tJlOjzdSj178fRjqyiPv/WJ8d8ZbucDueZAatoN5zUCB/f2AHNsoO/j+j46KZ3NuPcF9dhxY4YW1KwtLP9QDFufmeTmUWvX6tauHxQHG4sMpZE/A1cDm2COJpnjet1BYRNpUabJ21Rt7MZBaBk2MMBP8g2QVCQA2vNIAZ11efsxIfEX7QeDVSr5WtirsxqZd/EklWYE9rWwqQ7OmFU17K8l1J0DDD26TV4YMJ2HCrg4yNmJXVf99ZG7M+P7fuBmic+cX5LcA9Flu78DVwCbxQ5dHAWdrkddW2CkoPrgm8UZbc3AwcHfQhc6jsELoX7y1X6jYZoe7o18DMxt92Z6j/8SJVsGtSuhrd+1Q73ntqsXDdeSsB8afpuDH1iJd6fu4+Pjyq5R77egTkb880ser8e2hA9W4TZlWYsTfgbuATuetgluebnqJcXNtdE7XZTAo6JiIi44xL88dEQTrk2+/25Bo024633PibminZnmRFLNpTqcuvIxvjo+vZoVrf8EeTu3CO47b0tOPuFtVi6nY+PKqNvlh/EC9PKmshGi76/7jmFmyiyysHXwEUeXGdG6kHbLsnV5rhF533UbmlmAUIDkTrhd1zkgYBCddGyO7IiPhwTSfp61GkHHMmHCDwCi4GsVgdoMcLMWLIa2K42vru9k+4ZY+fnDXkY98wa3PvJNhzI9y9pmyW3TXuLcOv/YszLMx4+szmyqvucsshYkvI5x2WDGSh2uxf7bW79OOykBAZBWlZrM3AQ+vHRcDjekvt9CIpKd1tivPkUpO0pju0JWHJpWLsa3r6mA36nXhVXoST2EHR89J+f9mDo46vwzs98fJTurLyWTTjoQ57T6G5ZOL03N1FklYfPR0XrzUCx2b0IqvFSKrutGYQI3UHJstmVCXQotl0MvfNTq5mZhTjowzFUaxVkkJAjsFjoq9UsZVCqyx1jmuCD6zuiaR3766pUeOw3H2zBmf9ai0VbY897YMnpoS93YOGW2P99q2dUwaNnR3hsZCzN+Bu45FqBi8xsAGTavAKw2xWp08YMQoQ+wUfacYn1KnTdMEdRMd5Y0pWBm59oTeyCtyhIof7p2p5qZiyVDOlQG9/d0RkndbY/OiJzN+dh/D9X456PtmJ/Hh8fpZMJiw/g5Rmx57WQO0c3RpsG3ESRVS7+BS5080a9EeEUBBwM2JExRJZN4FJSDBzaaCbqSZrK5Ier4ZKfA0FdqWOR5bDzQ2JN/G02GMiwym9Lu+OyKAgKhKqrAJGlJOpz9Pav2+OusU1sj46IlAJvzNqLIY+vwFuz96KEj49S3oY9RXpHzQ+dGmfiphHcRJFVPv4FLofKghJZx+n4JyAHphTVIQmlgpagyrLx3m0h2e3MIET+Toji2FrLo+VIM1BMgb6YUesAltLomvTdJzfF/67tgMa1bWoeGVTf4+4Pt2L8P9dg3iZTJ4mlnKIjEte+tcm3+j1PnN9KV2xmrLLxL3A5GBCU0O2ZUAW7IY4cMpMAdkFJaCBSK8IZbm7sr2CEU7Bls0vklWg5yoyUgz4FLqXJvizlUTO87+7sguEdwzfFW7w1H2c8t0YHMXsPx97wkyXW/Z9vwxKf8pYu6F8fg9s7HzUyls78C1zyygq0CbvdC6cAwO4qdGiibaQdFx+Kw9l+HiTwplQUdH5Lk4HWpHDfseO0WOgdrQa9zIylgyZ1quF/17TTybtCOJ8J0fERHRsNeXylvoVEt5FY8vtk4X68PnOvmcWmbq0q+NMZDhcJGKsEfAtcZO5WM1Lsdlycbv3YBQy5wR8r6oQPXGTedjOKAeXR2Ilxx0U0Pg7IMK+MQv5eUePdlrRER0d0XfqdX3dAg1rhm+TRNVqq+zLun2swZyMfHyWzdbsLcff7/uS1kPtPba4rMzNWWfkWuIjDAcGDXRAQsCNzTEZdoFpNMykjQ5/g7RJ4A+XF3lHVKXCRAbk70ZDNhpiRcsifYlPHWgewtDSiSxYm3dkJg1wcBSzdVoAzn1uLO9QT465cPj5KNgXFJbj6jU04XOzPztiANjVx6QBOymeVm387Lvk7zEipWb70tDwcsCNjSKe6KaE5K5FquBTsNIMY2HzOWmDuThREs6FmpPiw46K7bgfmzLC0RN2lP7yuPW4f3cSshPfunH0Y9sRKvPLDHhzh46Okcd+n23zrBl5NCPz1/FagVhKMVWY+5rgE7rjYPNgestkqzWpuBiHyQwKRWg4fV+pwjhnEwOmoKNbE34Adl3I7SdGgG0rVapkJS2d0dPT7cU3x1q/boV7NyD+qdFvl/s+2Ydwzq3UXalaxPpy/H2//vM/MYnfdSY3QvRk3UWTMx8DF2nGRVK/E5vjH9qjIacclNBBxSpwtVRhbMSeZma2+EuWb4Gl50QdFkq56Bx5zxVjdl4jW48yIVRaju9bB5Du7YEA7dwHrsu2FOOeFdbj5nc3IOcTHRxVhZU4B7v6g/C5ztFrUraZr/jDG/ApcSoog8k2eiVMwYpdAW8Nhl6OwLGdFNxK0C4QC5e8xgyhVr28GIY4WQBTF8IqpaUB+C/Gj3kyrMWbAKpPmdTPw0fUdcMtIh58ZGx8t2I/hT6zAi9N38/FRAuUVleh6LQVH/KnXQv5yTkvUyvTvdSZjqcyfn4S8gKOd6o3MIIRNAq2o0dCMAqgASJQEvEqs3cIMwig6aAZRouDIjtlFipZoPsyMjBiPiiTl4TToaWassqlWReC+U5vhzV+1Q3YNdz+6uYUSf5qwHaP/sRoz1sZYSJG58vuPt2F1ToyVvAOM75Gt3xhjFn8Cl8KAHY+aNhnvJcX2Oxd2Ox0FIcc+TnkwpYoPB1fZjUamU+AS4zXrxv3NgKhXvLHWm2k5Wv2HM/MquzHdrKOj41u7z3WiJ9ILXlqPG97ehB0His0q8xvV2Hl/nn95LTWqVcHDZ7t48cZYJeJT4BJQVM0uMbfA4Sinhk2QUxhSpMnpOKlU0QEziEGmQ8XSWPJbpAowGvYxM0V9DYSMbetYtB5rRqyya1kvAx/f2AE3nOSww+ng04UHMPyJlXh2yi4UH+XjIz8t31GAez/xL6+F3DOuqf63ZoyV8T9wqWHzQBq6i1LKbsclNF+lZoQHZj8ClwyHHZf8GJJ+63YK/nOdgjcvWtGOC2MW6lPzwOnN8dpVbV0fHRGqKfKXr3Zg5FOrMXU1Hx/5IbewBNe8uQlFPjby7tG8Oq4dbnOczlgl53vgYpu3EpgDE8jFUZGIuOMSY34LcToqCt398YIq5gaKMXCRFAhFKsTHKqVTumfj29s7o2+rCEnsIdbtKsTFr6zHNW9swtb9fHwUi7s/3KK/nn6htg+Pn9dK5zUxxoL5FLgEnOnaBS7FDsGFXeBSFNLLx6m+SqliH+pVZNY1g2AyhsBFNPI3cLHyWxiz16ZBJj6/qaN6he7t6Ih8seQAhv1tFZ6evFN3MGbeUA8iOoLz02UDG+L4NlyviTE7vgQuMjDYsNshKXT4obY7oikK6SBtd/QUqNim47RXGQ4Z+wUx7Lg06mcGRmhRPY8EFZ5jLAw6OnrwzOZ49fK2yK5Z1ay6U1hcgscm5mDk31fi+5U+/ExVEku25eOPn/ub19Ioq5q+PcYYs+d/jktNmx2X0F2UUlT4LURQEETsEngD+ZDjIqhonp1Yjooa9TUDI9YdlxYnmQFj4Z3aKxvf3tYJvVt6Ozoi6/cW49JXN+Cq1zZi094is8rsHCw4imte34hin2v8/fmM5qjrMfBkrDLxJ3AJ2nGxC1wcjorsAobQHRen4nClin1ILqyaaQbBRJQ7LrreSkghPhlD4CKzO0Zue8BYADo6mnBTR/xqSHTJnROXHcSJf1uJJ7/biUI+PrL1mw+2YuM+f3ODTuyUhfP61TMzxpgdnwKXgF2P6uV3SKTdrgh1hrYhQo9+EhG4ZNo/UMhoK/I2CrgGXSqWHRfebWFRyKwm8Mg5LfDyZW1Qx8Oto1J0Q+aJb3MwQgUwFMiwMtTM8ovF/ua1ZFQDHjuXa7YwFolPgUvAg5pdoqtNAq2sbp9XIktCtqftdnACHck3gxg47LigKMqjonrdzCCAU4KyC6L5iWbEmHdn9K6Lb27rjF5RHB0R2lWgo6Nf/mc9Nuzh46P5m/Pw4BcxFqe0cfuopujQqLqZMcac+BS4WLseskoNFQSU/8ETR2zaujuV2S8s23HRRdwcbvyUkkd9uILo1G05P7rARdTrakYBSmLYUm45wgwYi067htbR0eWDIuSMhTF5RS5OenIlHv8mB/nF/vXhSSUH8o/i+jc3+168jwKWWz30oWKsMvMpcDHBRnWHIONonhkEqGpfDVIg4AHRrn1AqKM+7LjY5docyYcosQm43Khvs+NSFN21bVmrJVCnnZkxFr3q1QQeP68lnr+0NWpnRFcfhBJRn5q0U1ff/XJJ5To+kipWueO9Ldi83/9dJzoioqM9xlhk/gQuR0zg4nCtuNzxD3EIcoLyYZxu+/itik0QFVibxqu6nc2gjJBRXj1oycdEzF/n9K2Hb+7ojJ7Noj+W2HbgCK5+YyMueWUD1u32r/BaMnth+m58HYdcn/P71dNJuYwxd/wJXEqPdzIcjlxsdhuEU7PAwH4+TsdJAUSBw1VrL2yuZUe7kyMp2MpqZWYBokwiFs1COkwz5gM6mvji1s64dECE5PcIpqw+hJFPrtYtBPKK0vf4aM7GPDzyZWzd4u1Qq4Y/ncE3BhnzwpfARZTuuDiUzrfbbdBP8HYCd1yq1TCDOLP7/4Rey3aLri7bkCVR7rg0HWwGjPmLjo7+/otWePbi1qiVGf1DAeV7UNPGYU+swueL/L1pkwz2Hj6C697eiCN0VuSz+09rrgvOMcbciz1wCbxRVMV+61naVbet4lBgSZZ1KRMZtc0ozqr6GCBltzWD2En6+4cWsmPMZ3RU8dUtndC5aWw3WnYcLMZ1b23ChS+vx6qc9Dg+oljllne3YPt+n6vMKSe0rYlfDow+WZqxyir2wCXwaCfTIdAosWmZWs1+x0UE3ipKVOBi52iUibl+JtI2Hqi+IFxBk8VfFxW0fK2Clwv6x3Z0RKavycWYp1brK8PUNTmVPTt1V1xaIFStQonSrSA4H5cxz2IPXAKf4O16DzkQwsX2qJ87IeHYXbmO8pq18DNwac75LSxx6LjomYta6eOjzBjjZTpWeX7abpz4t1X4eMF+vXORamauP4y/TswxM39df2IjdG+WoMc3xtKMv4GLD4GGPFJ2dVo47MoEktHe1gni48sep8AliuRc0WyoGTGWOJSw+/VtndGhcezF0Oj46KZ3NuPcF9dhxY4odzErwO7cI7jhrU04WuJ/xNWibjXcfXITM2OMeeXvUZEPRztCeizUdsSHkv92nPorRVLHIcclmgCr6SAzYCyxaDdg4q2dcHbf8AUg3Zq1/jDGPr0GD0zYjkMFyX18RLHKLe9sQs4h//NayGPntkLNjNgfehmrrGL/6Ql8gvdwVJT0ot09qu3Qa4SqCnsgs1oD1bnZGqs4WdWr4IVL2+Cv57bUfXRiRbsXL03fjaFPrMT7c/cl7fERFdibuia6gpGRUOfuk7un0eMkYxXA17BfpFMiqVP/ojBkFfXoXtOhbLfXq91OOzeMJdgVgxtgwk2d0La+fbVrr+gY5rb3tuDsF9Zi6fbkOj6ixOInv/W/XguhasUPn8lNFBmLVeyBS2Duhk2fIk/sukgngh9tA0iNpuo/PuXLZMZ+u4Mxv/RpWVNX2z2tp3117Gj8vCEP455Zg3s/2aZ7AFW0nYeO4Oa3N6HEz5y3AL8b3wwt6vkT/DFWmcUeuATuJFRz331WBvYkKlVRe8d+NGoktVuaQXmCdmO8qML3JFlyya5RFa9c3hYPntkcGVX9+f6k46P//LQHQx9fhXd+rrjjI/o8blRBy67D8QmgerWoiV8NjdDpnjHmSuyBi5trzXaKk6hB21GfmqbVoh0XBx77LolCj7tPuxdAfn0usGehWWDMf1R35NrhjfDpjR307Ri/7M07gt98sAVn/mstFm31aQfUA7q6/eO6+OS1CCHx+HktUI1fjDDmC19zXBzZNVQ8Gvn2kIy2O7NXAVewY1K7gnqOHC2A/OYiiPWfQc64yywyFj/9WtfCpDu6+J5oOndzHsb/czXu+Wgr9ucl5vho+Y4C/HVifPJayBWDG+mvF2PMHwkJXOwaKgq7YCE0IdZN9Vq7zs5eBbQZOEZE8aWp4Vy+Wzp0w3YiCz00j9w1D+LAGmu8Z776T5Je12BppV6tqnjtyna4/7RmuhKsX6QUeGPWXgx5fAXemr1XX0+OF/qz7/5wK47E6YZ2kzpVce/4MDuxjDHPEhK42D1pS7ugpFrIqxIXRdtEVR/aAtj9f6K42i1qhDnDdlFML2p7l5mB+hwo4Nm3wswYiy86OrppRGN8dH17NKvrb+Lp/vwSHVSM/+cazNvk065oiLdVYBSvP5v8+cwWOjeIMeafxBwV2f1vjkTeTRF2PY5CyBpxqnUSzU5OjUZmYKOaxwCryP2Oi8zdaEZG7mYzYCwxBrarjUm3d8KILv4H6Iu35uOM59boIIY6NfvlcFEJHo/jERF9Lc7py7WYGPObv4FLiX3eisiwuULp4onZ1XFJ6C5NNOz+P9FUAQ6345Lp8QH9qIcH6LxdZmAU7jMDxhKnQe1qePvX7XHPKU3VA4u/5zt0fETHRkMeX6lvIflRiv819efE6xYR9Xp67BznW4aMsej5G7gU22fly6o2tw/c1GxxsyvjdSfDTrFN99doatKEyXERXo+eit3fKhKh17ldfN0YiwdKdblzTBP879oOaFDLv1tHpQ4WlOi6L+P+uQZzNkZ/xFN8VOLlGXvMzH93jmmKdg29F7FkjEUWe+CS6eIJ2e5J26kXUEZAPoybJ28f+iPZfi5V3dekOSZcEBVuN8aGoLybwD5QXtgFYowl0ImdsjDpzk4Y0C4+t2mWbivAmc+txR3vb8GuXO/HR9+vOqQbQMZDx8aZuHmkQwVtxljMYg9cXJT5F9XLV4EV9KRss0MjA3NLCl3Uesn04QzZLnDxWHdFywxTVdRj4KK5zHORoTVxSuLTHI4xL5plZ+Cj6zvghpPC5H7F6N05+zDsiZV45Yc9OOLh+OiTBfGr0v3Eea18K9DHGCvPhx0XF9d8bQIXza4IXWbZroU4mhf5SdiHHRdpF7hUqQZZ1cdXi+ESd524LkIXsjNTUa0TGAtBRdceOL05Xr28LbKrx+d2DXWbvv+zbRj3zGrdhToSyo+ZvMrFi6IoXHB8fQzp4MMuMGPMkQ87LmV/hDzi8KDhFLgU2Jwxh+5aRHoS9iM516mKr9cbS+F2acLkvziKMgCRJT5VAmbMJ9QVeeLtnXTp+3hZtr0Q57ywDje/sxk5h5xf8KzMKcSBPP8Lt1BC7r3juGYLY/Hm746L09GOU+CSt9MMAoQGIk65MKXileNCMjwGLuESems2MQMPot058asSMGM+omTVCTd3xC8HRhHEe/DRgv0Y/sQKvDh9t+3x0cIt8WkpcPWwRr7XsmGMlRd74KLI0l2XIw5JodUdAoCC3WYQoGpA00YS6cnbzVFVJAV7zSCEx2q3YYvWRXNUZLcjZadKSMDEgQtLUtWrCfzt/JZ45sJWqFHNl4cfW7mFEn+asB2j/7EaM9YGF5hcmeP/rTvabbn+JE7IZSwRfHnkENXME7bTzoXjUZFN4BIaLEQKXPzYcSl0CFy8BEWBt6HsVKsJmenwdXCSH1KfxYEIuQEljvBREUtulAvy1a0d0aFxFGUHPFidU4gLXlqPG97ehB0HinWJ/+U7/N9xuXZ4YzSt4//1b8ZYef7suJTmgjiV6HcKXOyemEOPiiIlqPqx45JvE0ARDzku0s3nUbuZGbiUn2MG3kgPNWAYqyjdmtXAxFs74aw+PvwMR/DpwgMY/sRKDH98Baat9rcLdP3a1XALX39mLGH82astvZLsFGTUbAKJ8jcKZL5NjktokOOUOFsqmmvGoZwChNqtzMAFu0aNoWp7q6Qp89yVI5ehScEOFYwZSzZZ1avgxV+2wcNntYj7FeLDxRLr9/r/s/HYOS10w0nGWGL4E7iUHu84BS6UA1PL5hWJXQ5H9ZDEvUhHRVRHJtIxTQS62Fto9VlFeAg0RN5WYH+E5oa1PO64uAxcEFKZWDhUMGYsWV09rCE+vbEDmtdLreOWa4c3SsiOEWOsjE87LuYKs1NyrmIbBBSUPyoq12HZRYKqrOlDgSu7/0/tFmbgjlzyvBnZEx7/PBx2e1QU/EpVcnIuS0H9WtfC5Nu7YFRXj+0xKkCGiq/uO7UZ/nxGc7PCGEsUn4+KnAMXaXcd2C7HJSRwsT1OChVNjZRQNkEUsjwcFZGVr9tWAz4mq40ZuOTm766I0Pyao5ycy1ITHbm89at2+F0cGjXGgqoAt2+QgWEda+OusU0w87fddF6L4AK5jCWcT4GL9cQppHrCtDly0bJsdlzskmJDj4rcXAl2Sv71Is9md8NjToqgW1UbPjMzG9ntzMAlOn6KhstWAYwlIwoG7hjTBO9e1wGNa1d87kjrepmYd283/Pi7bvhAfU53n9wULepxvRbGKoq/OS7EqcFfLZstVdrlCC3pXyMkCLG7Mh2qtg/btYdtggT1OVM7fS/khs/NyEZWazNwR3d9dpPnEtjfiYTZ+WIsVQzvmIVv7uiMge0rtoT+yK5ZvLPCWBLxJXARNQPKXDsk04o65XcbdKPF0OOQ0B0XN7VM7IIirw5vM4MAVaqpoMhbCW+x8Wvn/ko2X4OIDm0wgzDK3cTiHReWHuiI5sPr2uPGODZqjGRoR+49xFgy8WfHJTAvxeloJ7u9GYQ4vMUMjNBcmLzIOy7C406GHZkb8nmU8nIlmlANlT0LzSRERhak1+vbhzaaQRghOT5W523edWHpgRo1/t/pzfHsxa0rpOvyoHYcuDCWTHwKXAJeDRXsM4MQjoHLdjMwqteHFJlmorg5Kqrl8baOHbujIpIVxZ+dM9sMbNRpawYuudpxsUlOLuRdF5Zezu9XD89d0lo9aCUuaZdybJpz/yHGkopPgUvAE2ehw45L7VYqILF5AMgLCVxIQL0TIYudWwmU8iHHRTjlknhM0NXCBS51O5mBO/Kgm8DFpsKvm4CPsRRzRu+6eOTcKH4mo3RcGx+6zzPGfOVP4BJ4vJPv8IRJRehscjxk7mYzChBaqC3StWCv15ZtOB0VCa9HRYrcv9yMbNTtbAYuudlxsbsOHrqTxViauHJwQ1wx2OZ7Pg6Oa8WBC2PJxvejImlXD6VUvQ5mEODgejMIUDskzyXS7oEKdIKOl6IgqOy/3VXuaHZc9jpX0BUed1xwYLUZhFHTpiJvaO4QY2mEWgT0S0BQ0a91cANTxljF8ydwoc7H6k0LV3elXlczCHDAJnCpGXL0kxepEJvwnjti55BdEOU9cBFUQdgpZ6ZuRzNwiZJzj0Zow59RW339g6uNOiYbM5YGKEn35cvbILuGPw9hdiiXpj8fFTGWdPz7qa9ljlTCHOuI+j3NKEBu+WBB1Ak5nnGze5DtQ+Bit/tTx2O121J7l5lBiLpdzMAdfUNo/yozCyO0wJ/d9W7G0kjLehn41yVR/ny6MLBDbdStyc0TGUs2PgYu5rgiN0xuRf3yOy6C6rSElskPLY1vlwcTyo8dF9vApS0k5ed4tXeJGYSo2Riyhk37g3D2rzSDMEJ3hpx2fBhLI2O71cHtoz3+PLk0vgc3T2QsGfkXuGSZ451wibQNbHZcyMF1ZmCE1GWRhyIHLsKHwEXaBS5Ulba291d1cu9iM7LRqI8ZuLQvQtdpEnptmwMXVkncc0pTFWSYRq8+qSYEzuauz4wlJf8Cl9LOx+GOKKo3gAysslvqwBozMEILyrnp2RNNVdpQdjkuxKkGTTh7lpqBjUZ9zcAdud9N4BLyNcvloyJWOVQRwHOXtsbAtv4Viju1dzaacf0WxpKSb4GLMDdbRElB+Kqt9buZQYDQJ+bQJ2E31WP9OCo64HD1uK7NbahI6Eo05afYEA29BS6O+TIBQlsqiKJ95Y/gGEtTNTOq4J1r2mFU1+Ak9WhVZIsBxlh4Ph4VBSTUhjumaFj+mESG5nBUrR68M0M5LiXFZuLAjx2Xg/ZJsJ6vMCuCgganHRybr0FY+1TgUlJkJg7s/v4HQ3ayGEtjtTKr4PWr2uo6L7G4+Ph66NeabxMxlqz8C1wCGx2GqfYqGvUzowB7bZJPA64N65s1uZvMzEHtFvaVeT3QwYZdInC0Be52zjWDEPW7e6o7I6hp4x6HZN9SdjtO+zlwYZUL9TV67NwWeP7S1qid4b2vUY/m1fHQ2YmrzMsY886/wCXw2rBdkmspu/yOAzY5HKF5JeH+TKIr8/pwNXK/TcG3wF5MHshdc8woBHWdbtjDTFxyatxYSv3dy91+cnMbibE0dE7fephyV1ec3tt9gu3A9rXx3rUdkFXdv4dFxpj//PsJzWoNKa1XODJcmfoGPSHpiTuAoF5EIQXTRHZIobbQm0d2okmiDUXHMqG8dnQutdMhcCFeE3R3zTcjB1Uyy91+kgdc1H9hLE21qp+BVy5rg2l3ddFXpil5t1FW8GNPNfUISAHL0xe2wkfXtUfD2sG/zhhLPv4FLvrasLlZFC7IoI+zK0QXWvckpMJsuTwYO/U89gGyIe2KvdndhHJj11z1Bzok6DYZaEYuRdpxIQ1C6uTsc9EugLE017lJdfx+XFN8elMHLL6/O9Y+1BOzf98V8+/rhnUP98KnN3TAhcfXR1W6nsQYS3r+7olmWwmiIsItINtbNXtC6p5kh9zk2Rc5cBHZ3pNoy7FrkFgzuqMiUZwLOO16NBtqBi7tXuAYBB0T2lLBTcVdxioZSuJtXT8TzbIzdOsAxlhq8TdwMRVvpV3/oUBNy+82yD2LzMgI7aLsppaJ1z5AduwShavWgMyIskZEzs9mEIKOzDKyzCQyHQTtC9N1WhEh7QRE0d7wvaMYY4yxFOPzjot1s0U/YVLeipOmA8wgQOhREZXGzwyohkk1Vo7km4kDHwIXQcXuCveZWYAa0R0XyZyfzCiEqKq+DoPNxKUdP5qBA7saOZznwhhjLI34GriIOgHJsU41TEjD48q6SZfau7x8rZaAow8hZORbMnU7QcKHpmihuz8kyptFYYONZkPMwB2ZM9OMHDTsbQYBXBSvY4wxxlKFvzsugcmxYWq56OvAjfqbiUVIFbSE5rmUy9mIELjQzZrQ3Jho7LZJhK0Z5c2i3UuAwv1mEkx4zXPZESFwUcGVrBVcg0La/V0YY4yxFOVz4BKQY3Egwo2WZjbHJLvmmYFFBP55ioyQ46HZHZd4VC7fhtQOKLDngd4pctopaTro2BVyNwTl+dgdYwUKbeAYrtkjY4wxlmL8DVyoem6GVfBJ2tVDCSCaDDKjMpKuDweq390MLMJF4CIaBP+eaAibwEVEe1SkyO0zzChEpvpaOXXMdpIzywwchNSHEbt4x4Uxxlj68DdwUWTp8c6+CLeAXOy4UM5KIHe1XPzYcVmq/nPUzIyaTcwgCjt+MAMbrUaZgTty2zQzsleupULxAXfF+xhjjLEU4HvgcqwI2p4ISaG1W5bLx8DuRcHNBFUQFFRll64qhwYUoUKPSqKgO1yH1kAJ7MXkFe2SHFV/pg3RaqwZubRtihk4aGJzYyt0J4sxxhhLUb4HLsLsuIgjh8J3iSbNgo+LhFRBCxVaK1W1etBxkf71SLsHNi0FohL6ZB9ljgsRRwudj3hajvTWHDJnDlB0wExs1GkLWbOxmVjkTodaMowxxliK8X/HJfCoJlLBtKbl81zKPcGHVtmNdL2XbhaF5MZEQ+bMNiOjdowdY7c67JRQEToP9VwEjgJOOTOlGp9gBka4nkmMMcZYCvE/cKkfcIU5UpDRfLgZlJE7ggu2lcvZCC1UZ8eupYBXoZ2ds1qZQZS2TTWD8kQbb8dFcutkM7JXLiCkv0ukIzbGGGMsBfgfuNTtAlmlhh5GvL7c+ITypfR3hux0NDrODCzSReAiPHZetkVHVoEF8ajsf8gRjBe6eJxDngu85rls+d4MHIQ0cBTFh+1r0zDGGGMpxv/AhfJLGvayxlQNNxz62GbDzMQiDq4H8nPMTAkJXCIdP2kNQ35PFHReSui16NCCeB6EzXOhhFpzjdwVSmLO32kmNpoPhRQh/7SRqu4yxhhjKcD/wIWU3uzZH7ncvGg50owCbA8ok1+9HmQdqweStncFUHLETBz4seNCQnNDQhs/eiQ3f2tGIURVSA/XonVRu41fmZmNjDrld6oi5cUwxhhjKSAugYswT5qCOhPnbtJjRy1GmEEZuT2kVknAk7BuDRCpnkuNhpDZsTdclCHHVqU3pqK28WszKE+0P9uMXNr0pRk4CM0f2saBC2OMsdQXnx2XhgG1VHbNNwMHdnkuW4MTWUsDoWPc1CWx60DtVeguRYyBi9ijvhZORzztzvDWIHLTt2F3nkSz4MBFd70+sMbMGGOMsdQUp8Cl7KhGhlbDDWWT56JzOAJ78oTcLArdCbEjPFwxdiLoiT5vh5kpgTemorVpohmEqN4AaHGSmbhAFXHDVeRVf1a5PkhOV7IZY4yxFBGfwCUzGzK7vTV2sTsiWo0xI4vO4dgRkOfS+HgzMHJcFFQLuVkTte0BwUF2h5iL20mnwEURHc4xI3fkxi/MyAbdgGocnOsjt04yI8YYYyw1xSdwIY37W+8j7bgQmwRdGbg7ULtFcHsA2pGhWzrhNDoOUmSaSfTkjoDjoioZQJYJyKJFgYtTTRWveS7rJ5iBg9Br1lvoa6qCQsYYYyxFxS1wEWbHQ9DV5kil/xv1g8zMNhOLCCnYJpoPMSM1ptL/Nh2cg1C7gCYhxeuiIAJ3XEhpL6YoiaK95WvVlMpqXX53KQxxYGXY6+Gi9clmZBEFOyPnHDHGGGNJLH47LoHVWyPtuoiq5fI75G71BFuca2ZK6NGPm/47PuS5yF0Lgj8PP7pPh7ldhPbejouw7kMzsNF8OCQFcIE2f2MGjDHGWOqJX+DSqP+xWzLSRa8c0XqcGVmELAnOL2kWHIS4qUsiQpN+o2D1Bir7PHypyrvhC4Cuim+fDhzJN4tGx/PMwB25+n0zslG1Rrnr5nKzc44NY4wxluziF7jQFefSCrq7XeS5tAkOXLRtAXkujY8PzllxU5fEyy2dMOSW78xICbzqHSW6Fi1fbQp8MhLyveOB3C3mV5R63SDrm6+bC2LfEmD/CjMrT7Q704wMSnoO112aMcYYS2LxC1xI6fFOjovuxNkdyxWNk4F5LrR70LSs67GuS3Jog5k5qNkYskFPM4nBloDbOBRY+JD0q29O0fsDKyEnnA6UFOk5EZ0vNCOX1n1iBjbaqj87gKDaL+Gq7jLGGGNJLK6Bi2hqEnQpKTRStVsSclyk81gK95uJ+nPKVYOdbgZhNC9fmdcz3RtolzWm69B+BEMB9K7JvL+amdL5YjNwR64Nc1xUpy1kyC6R3Pi5GTHGGGOpJb47LoF5KaG3c2yItqeakUXnuQTudjQ/0QwsknJEIrDtheSR3h3ZGtCRuYm56u0jueAJoGC3NaHdp9Lr5C4I6mS9d6mZ2Qg5LhIbvgrufM0YY4yliPgGLnSskllfD4PqoThpOQqyWk0zschNAccazYYEV4NNaJ5LWYNE4eHKslui+DDk4mfNTM07edx1WfWGGZVXrg8SVd3dFtIPijHGGEsB8Q1chPrjS493XOy4gIKWlqPNxNgUcH23ugqCSjtPK7qOSWBJfjuU5+Ih2dXRxoDbOE186INkZ9nLZTshnS6w3ru18m0VvZSYSQhKbK7TwUwscu0HZsQYY4yljvgGLopoYR3vlOv74yD0FoxOwt2z2MyUViGBjZv+O21OMYPoWZ/HQmvSsLcvCbqhBH19tky2JlltIJuWFd2LRH9+W83vtSE6/cKMjPUfq+jFoYIvY4wxlqTiHrjABC6ai9oraBOc56IFFE0TLUeZkcWx/w4dhZhrzKLtafp9zEpv41Dpfx+q8tpa/T8zUJ9391+ZkTty1ZtmZKNjcOAiKNk4pAs3Y4wxluziH7hQITqq6aK4KRqHrFblyt4HNROkarB0BFVK998JUXQA8sszgc/HAStfV79nmPocsswvRk9u/NKMlGZDzcBfcsPn6j9mJ6TThZBVa1ljN9Z9BBQfNpMQdFwUet18TVmQxBhjjKWC+AcudH249Mhjh4s8F9LhXDMwKAm3cK81zqwblGMiDq0rX89FBTrClOmXU28A9q0sf8QUjZyfjn0eflTltSOK9pXthGTUATq7z3WhBF+sftvMyhNdfmlGBl2jPlpgJowxxljyi3/goojSmz10bbfooDUOJ6Rfjy67H1g0LbTrcWieS0BOjDhaqIKXGyFajzcr0dPXs6m7M2kenx0XIteXFZQT3TweFy17yYxsdL3cDCyC/i1oh4cxxhhLEQkJXEqvJOsn/pCuz7bqd4esG9yFOejJPCRwkYG1XghV2Q0gaKfEp0RUSUmtpGbTckcvvtnwqfqPVVlXH41ld7bGLghqaLlrrpmFyO4AGbJTJFe+ZUaMMcZY8ktM4NJ0UFmeS2Dfn3A6hHRJpuaARwutcbOhkNXqWGNCNVacrgIbctmrKhjqZGYx2PR1WWPElj5U5bUhqHdRzqzSGUQPj7suS180o/JEyK4LNn8F5O80E8YYYyy5JSZwqZIJtDAVbEN3RxyIjuebkUXnb5Re96W8mYCKuPqGTGAjx6zWZlCGGhvqK9kx0p+H+TuE3nDyU1Cdla5XQNLf2a1V7zgfyVHCb0CRP927aKVz8TrGGGMsmSQmcFFE65Ot9/uWA4e36nFYdkXT1tERikW0DclZKc09IfXcH61EQ67/yBqEFsvzEyXOlu4i1WoOdDjPGrsgjuap4MXhanRmXYgOwQm/cvkrZsQYY4wlt4QFLjCBi7bZ5XFRl0vMwNjwiXqWNbkqbYIDFxkYuDTqF3xl2m/rPrM+j1rNyuXi+EUfFwXcwhK9bzUjd+Tif9F/rUmoXteZgUXsXwW46PvEGGOMVbTEBS7Ut4hqtChu81xEx+CdgaCiaXXaBZfy11eVTSfpjCwIFbzEi3Vl2dxkahMQkPlMrnnXjBTK6/HSeHH/iuB2CYGaDoFs2NdMLJLaDTDGGGNJLnGBCym9DaRzRBx2AwJRaf3Q20V0hGKIdmUVcUM7SUs/6raEIVe/o98Lu0q/fqE8F8pBMUSvm83IHbnoH2ZUnuhxrRkZq9XXlZN0GWOMJbmEBi6itdUzSOTnAHsW6XEkomtI0bTAHjshpfzlprIKu3ENKMi6T6xbTi1GQFYJvn7tF73DRLepSnW+GLJ6IzOJTFCrhH3LzCxE18uCbmYJWQQs41wXxhhjyS3BOy5jIKWwxhsCyviH0/lSM7AEHRfR8UlmfWtMNkwoC2qoJH9GXWscB/q4aPO3VkfrVvG5Fq1Ry4JSVJ+m5zVm4o5c8KQZhaCqvD2vNhOLLl4XsMPDGGOMJZvEBi41Gh1rTig3qiDDjez25YumlR4XiapA+zOssSIK9gDbTUJrlQxIH7pChyPXlB4X+dTE0YakROBCFSQZoueNkCLDzFxY+SZweJuZBBO9bglKYha5m4GAQn+MMcZYskls4KKItibQ2DEboCMjF0TXy8zIWEO5H8V6KNoH9zWSuuqsRbQrC2rigsrlH8lTwdNZZsF/+ghnzXtmplCCc+jxWRhCFkMuetrMQqigMLS9gpz/dzNijDHGkk/CA5fSJ3khZHD/oXA6XgApMs1E/d6ivWW/t/UpQQXVsL4scKEr0/G8Fq2L0a37WAUTbTzd+PFKrnzNjCyi3z1lR25u0BFQ6Y2rEKLP7WZkEbtUQOmmizdjjDFWARIfuFCNldpt9FBu+Ey/j6h6faBdSLLtatNjh4IWk/RLxMH1ZYm/dDTVdLA1jhNpclBEyM6FnwSV/w9oHIl6XVUwF9JBOwzdTHHJc2YWgnohUT5QALmQd10YY4wlp8QHLqSDOVrZ9C1wtMAaRyC6XmVGFknHNEUH9FiEVpUN2HWJ+3HR5kkAFYuLY+BCQuusiP6/MyN39NXo4lwzCyb6/96MjHXqa+t0G4kxxhirQBUSuJTuTujS9Fu/1+OI2p4KWbOpmdDvLQTWfmhN2p0ZdJQkKQemVLv45Z8QfeRF5fUb9ISs392s+k9Qki3l05RqfAJkSJfscHTi8hKqpmuj7WmQDXubifV3knMfNTPGGGMseVTMjgsdT1Svp4dyvcvjoioZQLcrzMQiS/vxZNZVAUpAMbp9SwCqHEtUMCHrdbHGcSJXmOOizhfr93FRfABYU1Z8j5TbKYlALnqmrLN1EKH+rD+YsbH6XcCHppSMMcaYnyomcKEgpPQKcWBBuQhEt1+ZkbF1GpC7SQ9Fpwv1+1KSOiSXan+2GcSHOLAS2PEj0PEXZiU+5NKXzMhoOapcfko4Im8HsPifZhaCEqADdowEjkIueMLMGGOMseRQMYGLIkwwEVRQLpJ6XSGbDzcT9XvpmGaFuXHT9vTg20UBuxPxTJw9ZunLVj+mRseZBf+JnTOBXXPNzCIGPWxG7sgFf7PPdRFVIE6430yMFf/lXRfGGGNJpcICF+uqspWXEth/KJLQXRe5XD25Up+ijCyd61JK74LsXmBNmgyErNncGseJXKf+DoX7y+38+E2G7phQywEPfZl0rsvCp8wsROiuS8kRyDkPmRljjDFW8SoucNGBxnhr7OG4SD+5BvbYyd1wrFNzaDdplHZXplou7cuCmngQlDtCV7TjfFyki9HRLlUAMdBjcLFABS4B1XiPoV2XgQ+aibFS/Z34hhFjjLEkUXGBiyI6XWK918dFLm8XZdQGul5uJha5/FVrQLdjMrOtsWLt5FhdqMsFNXEgl/0byO4I2XSQWfGfvk21PKQZYtPBkF6ufRcfgJz7iJmE6HAuZOMTzET9/+iG0ayQIyTGGGOsglRo4KKvMVMgosjV/9Pv3RC9bjAjY/1HVmVYakIYcFSji9Ftm25NWo6ArNnYGseJ2LMQyJmlgqQ4J+kufh4oKTIzixjwJzNyafFzxxKbgwmIwcFBjaD+RbvmmRljjDFWcSo2cKFk2tLE2XXqydH0H4qIaqa0OMlM1BMr7UKsftsadw25Ml1aLl83ZHRfbTZakmqldLzQW0l+j0Te1uD+RYQqEnvYVRIlBc47KdTFOyRvRv54txkxxhhjFadiAxdFdDbHRUX7gC3f6bEbomfwrotcZo5Pmg2FrNvJGpM1Hx4r3JaI4yKsfh+omgnRsiywigd9O8gcg5USg/8Cb52j3ypLYA4hhv49KPgS26YCbmvuMMYYY3FS4YELWo2FrNHQGtP1W7coF6NGEzNRT6x0TEO1VOioo0tZN2lx5JDVCJEk4riIujlTU0MTkMWLoN5FWyaZmZHdEehzi5lEpq+T//AbMwvRsA/QPaTNwo/3uN8VY4wxxuKg4gMXKkbX4Xw91P2HHLoYl1MlE+hxtZlY5NIXrUFo8m5prRc6Lor3rR9Ffx7tzwlqQxAPcn75AnHi+D+WBYJu0E5KaWAXgm4YleYgEXFwNbDkeTNjjDHGEq/iAxdFdLnUeq/7D4XkboRBx0WySjUzU+gWUeFeoE47Xd/kmC2ToRshKqKz9f+KJ3F4mwoIppRd944TQUdroUmz1etBDHjATNyRP/0WoK99qNotVCB0n5lY5M8PWl9jxhhjrAIkReCCZsMgs1rpYWnfH1fo95jdGqIDn9Ljpu6/tt4r+khkpVlvNkT9v1RgE2dy4dMJCZLk3L+YUYAe10PW7WomkenbV9Q92k6fO4JyhigXSf50r5kxxhhjiZUcgQsViDO3gUTOT8CB1XrshuhzuxlZ5BLq56MClY6/gMysby0qcum/1X9K1EgAXeLYDNHQf4/qDYPqysTFuk/LF4irUg1iGCXvuid/fhjI3WxmAapWV39WSKVdSoTOmWUmjDHGWOIkR+CiBJbylys97Lo0HRRU8E3nYWz+zqrp0q0s10Uc3qTWJ1rjBOyEELn0OYh28e2TpAvEzX3UzAJQMb7Wp5hJZOJoHuSPd5lZCPqz2p9lJub/OfVm9Rd0We2YMcYY80nSBC7I7gDZYpQ1XvWW2R1xp9yui+nnI3pcq9+XkqWJpVQHpmFfaxxPaz8Bmg02kzha/a5tM0Rx0rOQVaubWWRi7YeOV9LF8GeCE3X3zAeWvmBmjDHGWGIkT+CiCHNLSBzaCGz+Vo9d6XA+ZK2WZqJs+BI4uBao3yOomzQ2fXXsOER0u1K/t5VR1wxio3cmdi+ArN3CrMSHwFFgjk2uS3ZHiP6/NxN35NSbAOq7FCqrNcSAP5uJRc76I0CJyIwxxliCJFXggg7nHctLkcs8vJqnnI6+t5mJCRgW/8saB+y6CNrFKe1r1PlSx2Jtsue1QY0cY7LyTcDDkU20JO1S2eUG9bsnuCBfBEIFfI4doXvfCjQ+3kzUxxYdhJx+s5kxxhhj8ZdcgQsda3T9pTVe/wVKrzC70uO64J2SFf8BinNNkm4Ds6ie4FeowKXkCECF6NqdblZDbJgA9FVP0j6g3BGsUsFLnNGui5z9f2YWoGoNiBOfNROXFv4doAJ3oejq+ahXgq6gC6qma9otMMYYY/GWXIGLIrpfY72n44/l/9ZjVzKzIXup4MWg3QBQnyJ64u5p/ZlEUDC03iq4JroFV4YtJfavgKjfXT1B1zArsREUKCUCtRsIvWFEWp/sqfAefb7ye/W1tEu+bdgHon/wdWg57XYgf6eZMcYYY/GTdIELGvaGbDJAD4/tjrgket8WdPwjFz+r/lMC9LoZElXNqlknbcYHtQ0IJFe/B/Qou+mUCvQRmd2uCxn2pKfjL7FrNrAw5Bp0qf6/h6SWAIYo2gs53X2rAcYYYyxayRe4KKK7SdKl3ZGNX+ixK5QE2zWgT9H+Vdbvz2oF0bGsM7TYPgPYNVf97VWQ0y24m/QxGyZAUNJvQMCTCgSV79+pgo5QtVt6r+0y6wH7vBmq7TLmtaCWBvpG0pp3zYwxxhiLj6QMXHTibPV6eiiXUkE598RxdwV1NZbz/24N+pQl75LAK9OBH19K715Q0NM5AR2lfSZ/crhJ1ONqyFZjzSQyUVIA+f016g+0uZpOR0aDgm8ZYcqNAN0IY4wxxuIkOQMXqhdSWpBu00TbGiWO6ncHOpxpJurJd8d0YOfPVluBxv3NqrLqf0B+DkA3blqPMYshVvwHolfq3ZoR1DiRrn6XIyBGvBBUjyUSvTvl1A6AgsRmJ5qJUnwActIVKtDhwnSMMcbiIzkDF0X0vgVSVDFXm63dEbfE8febkUUufFK/pxyYUkIWA6abtOh5vX4fSlCn6j2LIduMMyupQ868136nJLs9xODHzMQd+dN96uuwyMwCiKoQJ78R1FpBBzrzvP35jDHGmFtJG7joDs/tzM4JXW2mIMKtxv2Dy92v+dA6wuh8EWTN5mZRPSEvft4qtqb+P7JmU7MajIIm0e93ZpY6BAUadKvKTq8bIQO7Z0cgZBHkpCvtO0hTYbpRL5uJRc55kHsZMcYYi4vkDVyU0h0SUXwYWP6KHrslBjxgRmpMNU7ohkyVTIjj7jCrar1gp/XkTkm6PcquTAcS+5YD6v8f2A8pVciZ91u1bMoRECNfgqxay8wjo0BIzg7eyTqmw7mQAbtW+jr1xItVsLnXrDDGGGP+SOrABS1HHrt2Kxf9y9PVaDQdDATuKiz7N1CwG6An2IBCdXLBk+o/Jfq4yKmSLhb8LTV3XfK3Q85/3MxC1O3k+ZYR5quvlUMrBuogHZhDRE0t5XeX6a8tY4wx5pfkDlwUQWXm6T11d173kR67FrjrQhVsF/xdBS11IHvfaFbVOvU0Wvehvi6MTueb1RCU7Fq7OWS9HmYhhdCtqlz1tbNDwVrbskTmSHS+ER0Z2RWboyvSp7x37DYYEZRYPdemhxJjjDEWpaQPXND5EsgaDfVQOt1ucdJiBGSTgO7MS17UuTKiz23BVXHnP6HflQZJtlTQI064z0xSh77SPPMPZlYe5afIWs3MLDKRn2PdHIK0FgJR4u/o4Lwane/ipWEmY4wxFkbyBy7Valp9iBRBCZ87ftRjt8SQR8xIKT4A0A0lSsTtHtAdmorRbZmsj5dk44FmMZik3R5K+s3uaFZSh1j9P2vXyE7NxirY+I+ZuCMoEHE6gmp3RvBOlyyB/OZS4OB6s8IYY4xFL/kDF0VfjTY7JHKewxOmE8pzCch10Veri3OtQnWBbQDm/1W/D+wyHUg/AS+iG0a/NSupRU6/1TlHqPUpkH1/YybuyFl/dA6GTrgfsv3ZZqK+dtQS4IuzAOofxRhjjMUgJQIX0FFG18v1UGz83L6mSDiBOwAFe6z6LdkdIbqYTtSK2PIdkDMT6HgBZO02ZjXEiv8CbU+HzGplFlKH2LtU/b2fN7PyxOBHgo/VIrB2Ui4B8naYlUDCagkQkBMk9i+D/PZSFfFwcTrGGIurnT9DvnecVfk8DaVG4KKIfncfK80v5z2q37tGuS4ty6rj6oJ0VL/lhPuCd11+/rP6ilRT/y/73Qed4Et1XTzuTiQL3YCRqgXbqZIBMe6doGJykeh8l29V8GK3k5NRB+KMz4OaWIpNX0H+lHq3sxhjLGUcWAP5+WkQexarF/nqBWsaSpnARZfm73SeNV77gbc2AIoY9KAZqTHtEix7yboSHLjrsvkba9el26+PJQSHkktfUJ/HhY6/nsxE0UHIH+42MxtZbSDGvm4m7oht01Qw4nB8VqcdxGkfByVCC6qns8xbTR7GGGMu0GP8F2fp43lCXf7llBt0LbJ0kjqBiyL6W7dj9DGFyUlxjRJvW483ExWAUHIpFWcL3XWh3Rzq5dPrJrMSjJ78sfSllN11EavfBrZMMjMbbU+D9FizRix6Blj5hpmFUF93MTbkptG0G3X3bcYYY36RkJOvhDiw0syN9Z/qk4R0klKBCxr1g2xzqjVeoZ4oneqTOBCD/3LsuEnvuiz5V/ldF3pC3T3fSgimG012Fj4NdCnrYJ1q5FQVOBwtMLPyxKCHPHWRJnLq9VYzSzsdfwEMs/pFER14fnux5xtijDHGHKjnJbH+MzMpI/rcrOtspZPUClwUccIfrfey2Kp660Wj41TAcZGZqCdbKkhHOyihuy5Ue6RGI6DHtWYlBF2rXv0ORL97zEJqoaJ7MlxhOGqeeMo7kNntzUJk4mgh5FfnA3nbzUqIPrcH3VwSR/Ktm0b7lpkVxhhjUcndDPlz2SWUUpJOD3qWFVxNFykXuOgjn9LdgKUvOz9ROhAD/nystL++YUQ5F5Q/0/0qvUZ01Eq7Lsf91rENgA56ul3lqXhbUpn3BLB3iZnYqN4AYvxH3voZ5W0FvjwbOJJnVoKJoY9DdrnMzNS8aB/khFPVD90Ws8IYY8wrOeN2CLu+dN1+pV6Ep14+ZiSpF7go4vh7rfdUFXbOw3rsGgUpPQOuiNGxD1XTHfCnoCRSOe+vQO0WKqBR//A2dNCz4vWU7GFE9I7VlOvUXzTM9eSGfSDGvGomLu2a61xZl65Jj34Vsv1ZZq5WVNAiPx3NwQtjjEVjw+fqxfanZlKGThFSNRczkpQMXPT15tanWGNqnug11+X4+6wtNELHPpToS0FKQOdorPlAPQnPg+j/+6BjpEBywRNA18uc674kOV2JeJEK3MLpeAGggjovxLqPIWc6tEfQx1DvBl1P10dXX5wG5O8yK4wxxiIq2K1egJZ15g/S+SKgTlszSS+pGbgowhSVo50DzCp/thdWreY656KUpCTdvB3W0VBmA72mGwrO/J3+hw9M3g2kd10WPwsx2OOuTxLRtV0OrDYzByf8EbLzpWbijqBgkK6O26mSCXHaJyp4GWkW1MfvXQr56VgOXhhjzCW6aEH1tELRJRRxvHOPulSXsoGLdb3Z2nWRq96K/OQbgo54ZE0VwNC4+LCVkFu9HsQJZf/YgvoXzXkIaNDNrNhY8A+gzTjIhv3MQmrRSbI6Yrc72imlfghGvQLZbJiZu6PbDGz43MxCVKulgpfPg4OXfUsgJ4wHCveZFcYYY7ZWvQlBPfTsdL4AqF9WuTzdCKmYceqh67cfWmXqZacLIU5+R49dW/EfwJRE1ueBlyzWOyzy7W4QuZv1uit0u6jVycDn6i1VDVcBWLju2IS2JdXXW3homEhXysVZ36pAc4hZCXEkD/LLMyG2TjEL6vfU7wVx9ne6ASRjjLEQlB7xv+OsVAc7Fy1SL7h7mkn6Sd0dF9JkAGQHU0139fsAlTj2ouuVx3ZKBI5CzrwXqFoDYpC3ox+56Fn1TdI9qMBdqpEz/wDsX2VmDmo0gjjjK8jqjcxCZHpHh24aOd1gctp5+eQkTthljLFQUj1XTbrKMWjRlx/SOGghqR24KBRk6N0SykmZbdV4cU1UgRj+dzNR0/WfADt+ALr8ErLxQLMaGfUwkvMegxj2N8dE3mSnA4zJ6ochUhPEup1V8DLB2zXpgj26dwacdmooeDn9S8h2Z5gF9XtUEKVvG3ls7cAYY2lt3l8hnDrzK+KE/zOj9JXygQvqdQW6X6mHuuptmH9QW3RDqf05ZqKet3/6vfqvgDjxKWvBrSUvqifgmgBVKUxR+pYRtUKIpMkAiPEfONa4sUM1XuTn45ybPFatDjHufchOZQUC9W2jT1TwwkXqGGNMVxuXs51veUq6BdooNfMtvUj9wEURJzxQVoPlR2r45y1tRwx9oqwoHZWhX/exlfzr4SaNrosy6z4d7ZbeTEpFcvafdfG9iNqMgxj1kpm4owORT09xvjlEt43GvgHZ+xazoH4PFbX7cLgKSKeZFcYYq4QK90N+d5lOa7CjTx48pjmkqrQIXJDVqqwGy665wKq3rbFb2R2AvgHXo2fdC5QUQwx5zNuRyOr/6aMNMfghs5J6dAD27WWO1W+DdL0CGB6hDkwInb9CScxFDkllVOeF/syBKoAqVXwA8jMV8Kx51ywwxlglM/V6iEMbzcRGj6utAquVQHoELoq+3mySRuXMP6on3nw9dot6IJWW76f8Cix5DqjdEuJ4OjpyT/50j+5xJKkvUooS+1dA/nCXmUVAuyODHzETd8SexSoQGe8cvJDj1b/hyJchhfUtquv1fHspsOBves4YY5UG1Rpb+4GZlEcnDmJA+ue2lEqbwAWZ2RADrX84cXhT5IqwoTLq6COjY+jIhI40+v4G0kP1QUFHGhu+gDjpWbOSmsSyl5xrsISioNFj6wOxa3bk4KX7r60bR9XqmAXlJ/X/ok7UJUVmgTHG0thO9Vg5I8ILyePutAqrVhLpE7iQHtdD1uuih3Leo8DhbXrsWudLIFucZI3peIKqylIdEq/Xo6nibuMBkN3KGjemIjn5GtdfQzH4EchIdWBCuApe2oyHOH9GUPAolr0C+ekYXe2YMcbSVuFeyIkXWjvODqiQquifmj3zopVegUuVahBDrevN1ClTzvR2zKN+F8TwZ8quNC9/BdizyApoPNyL10dNy15UT+aPpnSirijcDXx3mfrJiHBF2hDDn4Ls6dA3wwEFL/js5PCl/hv0UsHLT5BNB5kF9fsou/79gVYRQsYYSzeyxErGjVAMVQx5RJ8YVCbpFbiQtqdBthmnh4JaAdAtIS8a9gZ636iHgr5xZlDSrwpoPHbZlLMfVF/dTIhhAcdPqYiul7vuwK2+Tif9y/POi+4oHalPUc2mEGdPBrpQ52mLvmL9yQhgxX/NCmOMpYk5D0Fsmmgm9mSTAUDXy82s8ki/wEURw56ErFJNj+WM23Xk6oUY+CBkjSbWmJ646Xp054shM+vrNTdot0L+/ADQ7UrIFqPMamqSc1Xg4ro+Du1a/cNqg+CBVS13ZPhquVVrAGP+A5z4bNn19aOFwPdXQ05SAU1xrl5jjLGUtv5TFbioF78R6Mda9Zhb2aRl4IJ63YBeN+mh2DUPWP6qHruWWRdiaFkhNvnD3UDJURXZ2neJdrT4eWDvUoiRL0BWrW4WU4/eefpG/d2disfZGfyo9+CFbjN9PDxytdxeN0Kc+/2xJpmEdtfkeycAuxeYFcYYS0H7llkvxCKh3eemVq++hCvcCyz4O7D0RTVJfLvD9AxcFDHgT+qJzWrSJ3/6g24Q6EnXy44l6orcDZDTboLobjVkdEv3P5p+m75bn+plmEX+dhW8XKzr27hGwcvAyK8aAtF5rvxwWOQApOkQiIvmQrYaaxbU7z24GvKDIdbVwQr4YWKMsZgU7oP86lydoxmOrF4PGPpXM0uwLZMg3+kDUOkP9bzouW6aD9I2cNG7JvTEqYiivaaUvxeUr/EcpMi0ZqveBOhJtcO5eu6WPmpa+z5w3F2QlD+Twuiqt+v6LqWOvw848RkzcUcfs306KvLxFOW9nPm1DpBKjwaFLAJUsCg/H291UGWMsVRAzRO/vRTCRX82MfAh9fhnpTMk1PJXIb84Tb+QPeZw4pvhpm/gQqj7s2mWKFb8B9g+Q49dq98d6F923CGn3Wo1dQysK+KC/JGOmoohRr50rKBaqhK0m7HiNTNzqdfNwJjXPP3dRdFByM/GuaiWK/SRlDhHBVXZ7c2aWt3ynfWqYNkrasa7L4yx5Eb5mGLzN2YWRuPjAY+3N31Bx0JTroUoOWIWjOYnmkHipHfgQt2fT3oaUlrJS3Tcg9AvegTi+D9AmjLK+sho0T8hhj+p526J3C1WXZkmKoiiJ/EUJ6fc4D0I7HIZxPiPVNBX0yxEdqxa7kIXX++mgyAumAfZ/WqzoH7/kUO6TDbvvjDGkpp6QSiWPG8mzvRz2Yn/VA9upmRHoqz/BHJ6WQ+5UvrFYrMhZpY46R24EAoWelpPZmLvUvUk6LHrc9Ua+siolFj6AtDoOMguHhN1KZFp/yprxyarnVlMTXQcI78+Dzi4zqy41O5MiDO+gszMNgsu/fhbczssQj0Zqp488iXg9C8ha7cwi+rzpd2Xt3sCcx8B6BYSY4wli03qMVGX3XChxzX6RVpCUeVeqidjdztXvSBVj7DWOIHSP3BRdCG40kTdOX9WT7hr9di1VmOCOkXLeY9AjHhRRZudzUpkdG1XTr0JyKgNMfrfZjV1iYI9kBPO0B1LPWl+onWsExBYuCEWPwv55dlA0UGzEgZ1rr5ocXDNl6N5wOz7Id/tox4owtdGYIyxhNizEHLiJfZBQQhdIXfIY2aWQHk71fNWlpmU0d2ou//azBKrUgQuqN5A/YNbheDEkXzI7+l80Fvegxj292NVcAXVddnxkwpAXtZzt8S274GVbwItR3ov0paExIGVkF+f630Xo2FviHOnQ9btahbcEfTK5KMTgXAdUktR1j3VfDlrUtD/Rye+fXGaCoLO8R7AMsaYX+iyx4QzrSNtF8SIZ63HtQST6oWesCsO2k29MMxqYyaJVTkCF6KvN4/QQx1AeE0wrdlEBS9lnYn17kmTAZC9rCq7bumaMAW7rd4+adCCXN80mnS5+ou5awtwTJ12EOf/UNYbyiVdqO6DwUDOT2YlAhUkiovmAwP/HFRLR2z8XB8f6XPbvIAMecYYize69jzhVF392w19m7W9erGVaPOfgFhalipRio77xeC/mFniVZ7Aha43jyi73qwDCK9N+qgKbuuT9VDXDJn7qHUMldVar7mh+//88FugWi0IjzdtkpVY+6H6enpriaBVrw9x5sSgYzg3RMFOyI9HActc7nhRwHL8HyEuXgwZ8MNPyb+UECff6gw5817vx16MMeYV7fp/ebZ6EbbcLESQUReCEnITbc17gEO/P6ouT+UoKkrVPylmnP5qNIKgPKKtUyCOFkDmboToeIH1ay6JFidBLn8Fggqx7ZgJ0eUStTYSoDovbu1ZCNDuT/PhQPFh3TAw1Ymds9V/VBBmdrVcE1Uh1KsJKUsgtk8zi5EJlAAbJ0DmbYOg3lRusuzpyLDTRUBLFfTsXXJsp4Wu94kdPwBLXlQT9Q3S5HgV0lt1YRhjzDe6Vot6ztjyrVlw4SQVtHjcmY7Z9umQX52vHmfL76TLhn2sSxAV+KK7Eu24GP1+B1m/lx7STgHWfqDHrmW1KStsR7dr6Ip167FB13DdkNNu1Lkhui+Sh87TSe1nFQMvetpMvBDq6/Bn4OR3PF2XJmLZK5CfjgEObzMrLtCDwPkzgdH/Dd4tKz6gXmH8AfL9QZz/whjzmYScegPE+k/MPDLZ+hQg0Qmw1Hrly3P185sdvftTwS/sKl/gUiVDJ9WWHtHIqTeH70psp+eNkLRbolCOB3Un1sm7Hq45i/2rIOc8pI8xxOj/HKv8mvLoyMhrb6hSnS60bhxltTIL7tCOlXy/P7Blsllxgf79u14O8ctV6hXNc0EBjM6j+eIMvaXLGGN+oEKkwstjIx0RjaICmgm8bpyfA/n56RBF+8xCMNntKuukoIJVvsCFUG2X3lYxHV1ennY/vFBPemLki8eSPeUP96gnuQLv15znPQ7smqMrIaZ6L6NAcur1wOoo+1c07g9x/izIZkPNgjuU9S4njAOok7WXbuBVMnUVSiuA+RdkrZZ6mQJLLHtJjxljLCZzHoRYSJ2cPaBbRLWtx6OEOJIHfHEmqNCqHeqPVHraUNEqV45LAJ2rsvZ9FbjsVa+wVwD1Outruq7pfBkV922dDHE0H1JFqqLvHZBF+yFyZpkPCk/Q1iFdq6atQPp8tk2FcHPVN8npv9f6TyHo1lQ0/ZkysiDoFljRASt3xiX6/1L+ksyZCUFbrBm1za+4QDkyTU6A6HOrDiQl1duhTH6P9WYYYyzIomf0EbQXdItI9yNKFMq9mXgBdG89B2L40+p5KvHl/e0IqZhx5bPjB8hPRuriP1SjRVyyCKjV3PyiCyVH1O8/qSxQOeNrvY0mPxjgPmNckf3/oCvq6nv97/Zz3KZLNXQcRzen4PHWUJBVb+kdHKq/44Ws0QRirPp/UwDDGGMVgSqtT/PW5kU/dl2snotM0dT4Uy80v78aIkyJEColIs6epEaJr5Jrp3IeFZVqNgzoe6ce6g7S6h+P/hFdq1INYvSrZUdGU661mimerJ5szbVrV0qPjLJaQ4xKn+MJHRBOujL6YyPS5ZcQ5/3oqUoxoSvTmHAq5I+/Vf8m9klmjDEWN1EELUSMeTWBQYt63lKPkWGDlmo1rVtESRK0kModuCj6Vk+9btaYSsG7aHQVRP1efaddEbRj8uM9QMO+EIMf1mtu0JUzOfnXVgXaDudBUj+KNHEseFn1llmJAl2/u3CO53ovRCx8EvKDoQD1qWKMsUSg/LgoghbZ9w6gzalmlgDzHlOPkeH794kBfwaSrFhq5T4qKrVrLuRHQ3U9Dx1dXjBXBSQeytHTk/OnoyBKOyafNQloOQLys3EQW2l7zZ1jR0ZUoOiDwfp2S1o56V9AzxvMJErL/w05/Q6r95AHtCumz4xph83cKGOMMd/9f3vnASZldb3x99J7770LSJcOCoKASLG32I0lmmhibGlqzN/ExJKoURNb7L2A0gVpgvQiSO+916W38z/n3ruwu7DLtG92Zvb8nmec+e43srtTvvvec895z7KP7GItlP5DGaEKrWx02RpmxoOFb9ru+TlS8TxAfqcEq3rVK7hgq3oesw9tL6MxN9stn5CRXI4L3zzpQUJjf+6M5Xq+fbK/UUikbxmJeLr4U5sgmlLICuTHf/qDCGnycxaW00DlW/qB0JAml5jyCGhwD2DvKj+qKIoSQxb8NzLRIsUAvT6Kn2gRt/OzVNNai44L30g40SLk2aqi06jSGbRuDMz+9TAHNoLoGEyNnv5kCBQpD1OoJLB2JMyR3a4ipsF1MGUbOevkEMhUZVSsCow0sFo1yJ9NEdaNBkkeUA0WEJFStCJM41tBkrAr7sVhbL2afWtBi9+CKcCisFI7HkicfVtFUZKYuc8Dkx9w1Y1hYrr9B6hxkT8KmPVjQSOvZHGVc385mwJR/yp/lFiocElHoiY1uvOk9jaMJHNu/gGm6gVAqdBN5VC5PWjTRJi01TBbZzr7+1oXgw5uh9k2wz8pZ6wfCat1O7GXbwHavwFm+xx/NjWQLTU6uBWGX5uIt20kMbpmb36PuoL4i2iOhtZhVbDtGtaNckK1amdb2q4oihIxM54Epv/JH4QHiRGm5JHEgy3TQMP725Y3OUGVOznzu6C21Zd9ApSuz9fxgn4gPHSrKCOl6sNIHgbjkkpvsp2cQ8fA9HjHOh4Kbston+0qTRXb2LGQkC0jX2Jtzn/J7TOmGEZCqqOvj77ihwWeLR1sdLMfCB2zZQrok9bOtE62khRFUcKCXOXiTFegES5UvjnPOWEWhETKjvmgoSxaeE7KCbtt1ZPnsVD6v0XC7iWgCXcB22f7gfBR4ZKVRjeCGl5nH5r9G1l8SIVPGKE/sY7v5sXPvtXuQy2Job1ZYXpBczZsldF3PBEf3c//bxGgz2egQmX92dTBdpUe0gc4ssePREjhMkDPt4FLhoDCNIyz/TimP2H9c7BhnB9VFEU5C2LaNv4uW7kYEWLp3+cLm9MYOCJavr7I2n6cDdP5ueCqiKS58bfXs3jiuW1LaLsQZ0KFyxkQBUyl6rrHa4aEXyLd8HpQg2vsQyNlcWuG2WgOpD4/RMye5Sx6HnIHJes4bxhKvXwM6fVEg7sB+zf4kSiofQnMtfMji77sWQJ8cxFo9I22X4eiKEq2HDvguidH2pdNkKhGPMqMZS75prdtb3M2qObFQNM7/VHssf2advzoHm8NzWH+TKhwOROFStkMbzJu/80KiO1z7eNQMdK4z/e9sVEbaeRY9zJQi/vtWChY0bN2pDuo1Qemfer0M8qIkdXAF53Cfo3PSHr0ZcBoFp8sFsPELP8Y9FFjV/2kxnWKomTl8E4bKbaL2khp/QjPBwP9QYCIaBncwxlyngXr2Gv77QW0QF7xJUzGIMCWmf5B+KhwyY5K7WE6/8M+lFJa+vZaIIwEUBQu6/YJGfnQ0HhnKmc6/QMk1SwhYkXPoR3uoO1joDr93eMUwxzY4CIvYgIYCyT35VpW9nyBIIS3V2uO7AV+eBj0aUv+fUb4UUVR8jxiMjroAtuRPlJsVEP8uoJGflcRLXxtPRsSzTcXvWerWQNh7yqX15KRg5siXhyqcMmJFveD6l5qH9qtG3G3DQeePNMjLGb1UEDCivkKwfT53KrbUDD85tpWAjbPRj5cH4DKNrHnUg1JGpOMdyx4zY9Eiewdd3yaBcxsULUL/GDo2A7Rw/qDhl1i94gVRcnD7JgH+rJLWH3osiIu7ab3R3xxCSjxNZ1960Ff9wxJtAimzcNAzV7+KMacOAoacwPM4d1+wGG6vGDnw0hQ4ZIjLBQufAtUwpVEm5VfAT+5xNtQkTbgkjku0OTfsPJc4XoS9fnEGfyEgFn1teswKhQsCdN3kG0xnopY46aJ94KmPMJHMTJ1LtcM5tKxwEXvgyJYUUgrCPq8DWjcHbHJxVEUJblYOwI06PyQhcCZkAILcwlfywuFVqQRMXyNoq97wMhcEwJUqSMQYCdqmvr7U42IPdToRiCK1jZq+R8K23jFPqir2zIyhWCu/B6o2NafDIGdC3jiaw9z4pCrj798Ar/yrLjnvwxM+rV/Us5Ivo25fBzA/79FJlOpxw/ToTGZoLqXwfR8j8VaDB2Ej+wFzeQv6bx/82sXhjuyh/IVAVo9ANPqIZdPoyhKasPXaRJjuSiutbJINf1HAtUv9CMBYSMtYYgWvoaZq2cDJWv7kRiz4gtA0iwyQOXOhbliSlTXdTWgC4XiVWGkzHb1N65UWdxxG17PL3wJ/4SzULQSTJFyLDaGW2dea6EsWxeV2wN7VwM+yzonDE6AVg/jn3udTR6WbHQjP3/daP+M1MPsXgxaM9QazUnOUEyQ0nQxruPXkfat5Z+xxJ8IDSOOytKT6qfX+BvIwkf8efJHFu5UFCWBkXLnSffD8EInEjfcjJiuLwENrvZHARGmaBGkWhWVO/qjGLNrIS+uBzjDTw/xnGUGfstzani2FVnRiEsYSK5JevmbjZxcNjasPToafpnNRLfq+7IJ7gMjde2DusFIj6JQEDO6y8YDBYrZQ5pwN4w0y0phpN+T6fOpzRmKORvG82pKSvQicycmafUg0Zdm94YuZBVFSWwO7waN+ZndJo4WavUgTKdn/FFASA7mkL4waSv9QAhIZVPHp/1BjJHI9pcdXJ5gRnp9zALOWYVEgwqXcMgiMqjJ7TDd37CPQ+LQdtBnrZ2xXcl6MNfMctETyf7+gt/kEP1D7BZKn8/53ctns7JpKH9geQJOZUhaMnR6FpC277FGQsDLPwVNezy8L34GrIBp+VvX/Vq3kBQleZFI7/ArnLdTlIiZqRRU8MXaDQRBeslzGPk3VO1CmIEsygJJEibXC0lyMzNAzX7pnOBjgAqXcBGRIfkq6XXxXV8Emv/KPQ4FaXAlZkCG31z7of7QjUsPCfnwnci5h0Q6xD/TyM8WxFfgqy6nq9sUhBrdwGLxdecoHGukNG/B66BZT9meURFRsDSoBb83Uk2mPZAUJblYPQQ0+iaYY2FYX2QDVe8O039ExJUzIRGJaJHikKtm2Ga1gTDnH8DUP/gDB1XkOfOKCTF7LTTHJVwKlYap0hG05EOXd7J2NEy1roB32j0r/DxzXLoaT4bZ+RMf1wMqtARK1IAp0xBY+aV/Ys6YrdNdxEUaORYoavM2aPkn/IXjfzuFsWZ1kmMknbsLl/OjMUJWH5X5CyZRk0KlQNvnhv96njgMs+l7kFSfSYPMMo1s53BFURIZXr9L0v6Ee1yT3SixPYgkGVe60AeFXAsHsTg6FLrTty0uGTAckOtSEKz/DjTu9kw5QVLJaS4dE9NItEZcImXBf4GJv7QPbQ7G1dOcCAkFqWuXD9zWqS5Z6epZp6yf5cszIwwt2Y1/j3SLZhZD9HXvkKM2yQyJu3H3N4H6V/qRAJBmZCxAaO7zfHHwJoBhYts01BsI0+pBoEoXP6ooSsJwZA9o7G2nbW1EChWvBXPF93YxGhjpvYdCsPHPRLfXoipDzpG01aAvZDfi1LXytGrYGKHCJQqIhYt0ObaPyzZzH1bJWQmFtDXWG8Sa8kjC7RWT4Vp8kwtVLv/YPe8s2NyP3p/y5HiFG5DGhaOutVtReQG7Zdb52WDDsVbAvAqa9yLMgc1+MHykQ7hpfp9LTgtiq0tRlPDYPhc08uqIc9uyIk1ezaU8UQfZg2jzD6Bh/ZzDdxjEMsfkNI6mgb7sCrPrJz/gCUgoqXCJhhPH+APUF2b9WHtI0k/okiFuyyEUVg0GRrqIAbV6yLYDsIhfzDespkO0lbaq9pJBQK2+buDHfwHpDRrzANJCwUi2eqjbdZEi20ZL3gXNeYYvdGv8YPhQ4QrAuXe4LSnpJq4oSvxZ+Dpo0m+sP1cskO+13RLxhqOBIP5do64MewubavB80n946HNTOEjZ+IjLYaSZcAbENd504bkoAFS4RMvhXaCvOp9MjKVm97Cqfdk+DgWaeB/MglfdwYBvAcndEA5ucwm3oRoJSZ6LiCZvcGQNk9LddvMA1khJKrzSI09BwoJVjJWIBWLIZexnQKJlqHUxTBNekdS+xEfcFEUJFCnVncjX6WWf+IHosVvXA1m0SPQ8KJZ/BhpzM0/a4RlnUulzYK7kRXBA1Y405VGYuc/5I0fYi/gwUeESC1i00JcsXo7scsdd/gm0CM0R10ZXpP/Fjjku2/vaeae2myRjXBx7Q6xwoYLFYQaMBip3kCP+kN8Y0y9nMkCNb4Pp+kL8PFU2TmQB8zywalhU23NUtDJM41sA/v0DS5xTlLzO1hmgb38Ws60hwS4aZdFZpbMfCQCJDklqQpjuvbbNwFVTgNIN/UiMWfwuMC5zDz/bj0mEUoCtDVS4xAqpJPm6l1XDLu/kM179X+5PnoU9y0Cft3VNBpvcCdPd5c1YeEVPgy6EOX7AD+SMVf6SzS7iRTxehg+ESWF33TNhPXJ6vRfzhLAc2bsCJC3bF71zSsBGiN36avgzoOG1AAsaRVGiRCb8H/8JmvZHGImYxoiske5AmPkXYMaT/iB0Am8zsGmSSxDOEAGyCzARLSVdf7+gUOESSyS6MeYG+zBsFb7yK14JXOsU9YVv8cr7Vn+CWTsSNIwFCI77gZzJJF6OHQAN7gmzbbo/mzcg5AfO+z1M28dgWyzEC369sexj0PxXYEJo5ZATdiupBr93Da4D6g6Mffm3ouQFxHtr7K0xN+kMXLRI7sjEeyN3Rs86j8QSKS75ouMpPzPGvh7i6h5OH78IUeESa2Y/DUz7k31o3VQv/x4oc449PiuL/mct/KWPg7kpS/Ln4rcB6U4cIrZEW1oSSKKY5MuIMs6a8Z0HsMZHEn0JKlSaE9tmgRa+bgWtRNOiwXYSr9oNRsq/RcQUq+rPKIqSLeKILZO/VG/GEOuH0u9rQPqoBYEsOGVLaw0Lo0ho9wTQ9nF/EGOkfHwwX4t2zPcD8nrkg+nzpbs2xQEVLgGQsX8QieGciJdQJ5r1Y0CL33UlvsWq+EHPrKeA6fyBDJFMWe4RNOBKFexKoP1fXN5RQMliOXJ0v7uALuH3VRo0Ron1hqncAaZOX6DWJbzCac2jAVqKK0qycWiHa5AYQI5f4JEW+d2HXwqzZYofCA+b53dhhFGas2FbzPSH2fCdH/Bc8CpwLi+644QKlyCQEN+oq08aGlkXRWmqGINkJZr8W5h53uo/BDKJl70rnfFdGPbQqQTJZC+VR+XO9SO5wN5VwNL3QUs/hNmz3A9Gh+wro/bFMDX78MW0u+bFKHmbNcNA4+4MufdbOAQuWiRXbhgLgwjbt9hqnr7fBLQ9TqDvboHha1dGqO1jMO3ia8CvwiUoJNQ3hCeTzZPtIVU531kt+67OkcMfnnF3wCx+xx+fnUziRRqIfdUtfMfFFMGGeNs9BrR+OPfLj7dMBa34wpZWm33r/GD0kAgz2Vaq2ZPvu2rPJCVvINtBkx7ghcF7fiC22NzBvoNdm5UgEOdzae4Y4bXZGlyK+V1AFZU2sXn23/2Rg5ryXCQmc3FGhUuQiMeL7AXuXGAPqQ4r6Yu/4lc9yu0Kieh8ex3MSv63QsTm28hKQRJ2I7WLTiGofEuYHm8BFWSbJbfhr6A02VzxJbBqCMzeZX48NlCZRjCVOrKI6cTvP9+Xa8afwXz+rKKkACsHuXLhAKIsgl389R8KVGrnR2KMbCVL24EIzfCsV8vlE4JrnLiAxcnEe/2Bp/5VQK+Pop/PIkCFS9DY3JLuPBmtsofU4HqYi3hFEO3EEUGpc6ZqIxUvrvKoBV/s2j0ZequGeLB7ietSu4ovlJt/CLmaLFTE7wcs3FC+FQuaNv5xczXAU5KPA5tB3/+KF3GD/EDsoWLVXbRcBH8QZCjoiATr/3X5pOB6I60eChp5OYuFUx4ybktqMF8zAmy1kgMqXOKBNZK74ORqgM69G+aCV/hRlAmVdjuKP0AhtgYQrEld36/dHq2KFwtJ91IxDWxwrR9JII7sAdaPBa37lu9HnxTAscaKuFJ1gLKNgTINYaQSTm6l6tsqN43QKAmFTKIL3wBN+wNfv2JbMZQRKt3ALfaCaCci5qNSyLHkfT8QPjYSdOXk4HojbZ3uijoytBiIXdpD5KhwiRdSOjaoG3CUJyKGWj8M0zHzfmFESGnaNxeH5dNiE8wkgatGDxUvGSB+Pcz5/+YJmyfvREWEy8aJwIbxoE2TYuoAmh22FFt6KpWsDVO8lhM4ImaKV3eh6fR7jdgo8WDHPND4e2x3/SChim1h+g0NZvtFIkUjr4q4ckiwEXTxTZGIaRCcIR/S2ksMZCEXoCtuKKhwiSeSxzCURUZ6V8/WjwAdn3aPoyES8SKNGS/+HKgzQMVLBiR5F60fhGnze0C2VBKdfev5czUFtIXf+y18Id86G+bEIX8yvpBc4CURWIzyipbne74VKQcjxyyWLYXO0i9FDPxOHLGrUWlqSRJxkpusqg/zfT5j/10jJeBSCi5bXEreQHoMzXwS+PHfMd8+zQrV7OV8SYK4Bmyf45oSRpGQbxefA0YBVbr4kRgjBnNfnZ+pAjVRRIugwiXeyEp5eP9TobcOTwEySUZLBCZzVrxIgmqjG1S8ZMG2p+/wV35tbkyubRKxM985ny+OP4K2z+X39Ufbuj/cFvjJgPWzafenuJdiKvGGp6ilH7pmfgc2+7HgoCa3w4gvSRARxBVfgsbeEnZ354wEXpJ9cIsTLRk8v6h8CxfdSQDRIqhwyQ02jAMN63cqg7zj33iV/6h7HA2ROuRKU8Lm9/GEtwA0pG+e9Xk5I9LttfNzQLUL/ECSIqs7SfrduRC0axEMP6Y9y2D2b/RPSD6scGn7R5j24fdxUZIEcZ+e9MBJW4nAidVCMitSCTrjzzCz+FofBXax2Y9FS81efiTGSPT+qwsyzSFUtpmz0wiqYikCVLjkFlI1MvJqfgN8g6pcFi8nTYQkkVjESxxyJ5IJqnsZTOdnXLJqKiGuvnuW8o1XV9LsM22tEzlpfDuwPtDEx1Cx23dl+HUvVQ8oXR9G3gN5XKGVy7VRUg8R1FP/CFryPqLpuh4qlK8ITM//BZOgL7YYY26AWTvKD0RGpu39IJBij2947tgyzQ/wz0xA0SKocMlNsooX2Zpo8zv3OBoiFS/n3gtz/ov2omFzcXhlrpxCLhxozq/ReX/IO6Zu0mPpwCbg0Hb+XPFNQvX8mA7ttKszIys0m4eyly98h/j5B/iFOgKcIHsuHZIKhALppZP5gUIlYQqXcaXZYphVsKTLh5HXtQhfJG2+TDmX+GvLPLWlQZ5APm/zXgTN+QeMiOo4YKsK+w4CKrX3IzFEEolHXBn1QjBw0SL2GrILsH6sH+CfGbQ3TBSocMltsoqXWCXsingZ0huGvzjhQLziMD3fsYmQVrxInoSSCTvZtngApuUDAE++iqJEiSRkL3wTNOuvccljSce6zV7MoiUIDxRrKncHzHEW81EQuGixLWqugVk12A/wUKn6MJeyiAnKGyZKVLgkAlnEC/GEaJssRrvKlNVwmNVGAlXvzl8U58pLQy4JvOwwWaFCZWHaPOzyg3LR00BRkhbxY1n+CWjaE3HfnqZGNzi7+vSKt1gh0YtJD8IseNUPRE5cRMuYW2CWf+wHeCjBRYugwiVRWDvCNWb02ea2w2d3/lJFa6cs4mVov7D9AqhMU5gBw+yWCI24NFMIUcmMDTVLQt+5d+Wak6SiJB1rhrv+N2FGhaNFzBZNF14YSrf4WJO22rVj2TrDD0SOipbsUeGSSEi10fABp8RL/SthLvog+slQkq7k390w3g+EBhWtCtN/iO2mTGNuhlnBXyIlW6yAafUQ0PTOwBqdKUrSs3GCi7Bs/t4PxA/ppG5680QdRKNEscb/7laYI7v8QORkMgkNgjOJljKNnTdMgosWQYVLoiEdQqWtuffdsEZIsm0T7VaEiJdRV4Wd2U48AZvenwG1eoMm/xZm3kv+jJIdtqFl818Bze/XHBhFSYevPTTr6VwRLBYRK9IUkBcYMeXEUWD648CcZ/xAdFhHXPFpkc7uQXAm0ZKg1UPZocIlEdk6AzSUxYs3g6PKnWD6D4ve/Ef2XmVFsPxTPxAaNrTa7RUXSZjzLDA1BpVPeQCbxNvsXhYxLGC0bFfJk/D0suobFix/g9k204/FF+v3c96jMO3/Ev3We1b2rmQRcGOmEuJosL2HBowAKrbxIzEmBUSLoMIlURGDMKkKEkt3hso3h+k3PAYTIIEm/QZm/sv+OHSo+X0wXZ53Lpbj7uIPj6+EUnLEllE3ug6m5W+B8i38qKKkMJJ0u+Jz0EwWLOEaYsYQuzUk3fhrXORHYsiyT0AT74mZK7XtQi2W+mWb+pEYcybRIjb+/b5JKtEiqHBJZFi0ZPRTse3L+7N4icUHe+b/ATPCt0qn6j1h+nwKbJnumoRFWeqX17CvX+uHvPOlepMoKYb0mlryPmjuv2D2LvODuYPdZu/JoqVoJT8SI47u58Xfr2EWv+0HoofKNOJr+wigZB0/EmOyEy0J0nsoXFS4JDpi9jV0wKmS5oL8IbtkcGws6Be9BZrwC/4Q8OooDKhUQ1bp/Dsc3QcaNhDm4BZ/RgkVKncuTLNful5IydDMUVFyQiLDP70CWvBGTJJTo4HyF3ad91vcx0cxXhzINv53N8PsXuoHoifwqIekCEjF6uqhfoB/plhe9OVruBg/JiEqXJIBUfgjr4BZP8YeigW66cUrifpX2+OoWDvSeciEGzkRAdX7I6BsE1extHOBP6GEgyTiofGtLGLuBUo39KOKkiRsmQqa9yKw/MvAOzaHApVvCXPR+7YSMqZI89LZT4NmPQUjj2ME1eoD0+eL4HygbEXpZTAbvvMD8jP78s/8LKm9p1S4JAs2sfb2TKE+iEmd5E1EizQyk2TgQ1v9QGiQyedWNk3vAH17Lcy60f6MEi42gbBWLxeFqd2Xv5kxTiJUlFghLrcrB1nBEquk1GiRAgK0eQSm7WNA/sJ+NEZID6/vbon939roZuDC14PpQi2Ih9fwgTCbJvkBfp14sWtzfpLcb0qFS1JBoOlPwMz6qz/mEZ7oTNd/RT/R7V3lyrB3L/YDoWO/DN1fA/3wKMyiN/yoEimSpIcmt8I0vg0oVdePKkouIwUDC/n7vfQDmIPb/GDuY7dde/wPqNjWj8QKnhoXvA6a8nDs+ya1fRxox7eg8tyk5YtEwjMY4VETXvh2+29KLIpUuCQji98Fjb+b3zzfIkDCjb0+jj7J6vBu17MiQ1gxVOzF4+IvXenj1N/x7xZe3oxyOjYKU7MnTNM7gLqXqiuvEn8k2XbFl6BFb2ZauScClK+A7ahvzvtj7KMsspAbdyfMxnF+IDZYN9web7rctqA4sMm1eslQzUUtfu0qQlOkIECFS7KyfiyLjCtPGdWJ66EkeEnL/2g4cQw08VeRRU4k70XCkJLBPvpGrTiKIWJqJzlN5hy+4FXu5EcVJSC2z2Gx8hbMko+Ao6e6fCcKVKkdTHe+RpVv7kdihCy4FvyHF1+/j3mUhQqXcWaiQbj2prN3BWhIXxi+P0mHpwBpSZJCqHBJZnYtdNs7aWvsIRUqB9P3i9h8MX78F2jKIxFFTui8P8HUu9wl/ca5cVpegEo3gGn4M1613QDwY0WJCWmrXUdjFiu56b2SE1SgJExHnoib3RP7LQ/JZRl3RyCRpZOVmLzADIwsuYo2B1G2hpr83B6nEipckp0Dm0EjrjzZwdmGIi/4t3O5jZa1I4FveYKMYMVFNS/m3+Ml0MT7Ydbxv6MEAlXuAFP/KkBuJWr5UUUJkYM8yclW0DIWK5t/8IOJCTW41m13FKvqR2IEHeeF2gugGU+c7BMXS056XxUu60cCYN23ruT56D57aOcB6cnEC8hURIVLKiAVRxPuzWSIZF1upeoo2ox1ScgbfjnMniV+IHSsYV6vD0FrR2VKKFaCQUWMEhL7NwIrvwKtGgRsnBhRVDWe2E71578A1OjpR2LIjnnOyyqg6ihq/isWW/+MfXQoI2L4J/k46TmP0uvoYn5vq3e3x6mICpdUYv7LoB8ePOkzQFW7supmpR9tUzFJ2h19Q0SRE0mgMx3+Zj1KaNxtMPxv5QZUvjWwd9nJFUmqY3MA6vQHavcDKrTikdRIylMiZM9y173YipXJMCbxL/s2J6TtE7bfFyQRN5aIN5Y4h//4YiD+My7y/bK1iggUaew49VT+ChWvBtNvKFC+pR9JTVS4pBobxoO+vQbm0A57SCxarNlQlS72OGIk4VZKsWc/7QfCg3gCNe2ecKubbbP9aPywX+h2fKESJ+K5z598ffICtry6Dr/+dQbwKuxCoEBRf0ZJWSSKIuZw4pa6+puTbUOSAevJcu7d/H19PBg32VVfO8v+fev8QGyx15o+nwOVO/qRAJDr8WRepM7/tx/gISnQkJYwJWv7kdRFhUsqkrYaNOJymB3z7KGNenR+DmguFthRsmowaMytMMfS/EDo2K2jnu+Cln8Bs+BVPxpfqNqF/Fr8A9g0CcSrFXNgsz+TNyARLRKJq9HLhd4ryMpMozEpgWwBrRsNWjcKWD8mKcU51R7gvp9lzvEjMSRtDej7+2DWDPMDsYeqXQDT+xMWXJX9SACIG660HVg5yA/wz63SGeaSr4HC5fxIaqPCJVWRUKh0Ll36oR/gD3fDn7ks82h740jey8grI1rFWRHV/imgVB3Q+Lti1lk1HGy35la/cd2al32cJwVMOtJG33rFSPfc6t2iL6dX4seRPU6AbxjPQmU0L1Tm+xPJh514O/w1Nj3YsnLiqKuSnPmXQJJv07FeKZ2eif22VkZEnI64zFYQpUP1eJEqzSST2MI/XFS4pDoL33Bh0eOH7SGVbQbT5+PoO0wfTXPCY/lnfiA8bOSj3eOu5DqDu2M8sS3vOz4NNLjavU5znmcBs8GfzZvI1iKqdoGp0pXvOwPlWwV7IVZCRyqApCv7xgkwfKPtc/gCntiJtWeDyrdgwcILGcnFCoI1w13eXwybImbFJsN2fxOof6UfCYhts13foQzXKGrJC7BOz/JMns+P5A1UuOQFxExq5DUnPVUofzGXpR+L+n7pCDvpIf4gHfEDoUOFytoENpItrdnP5FrCoE1klbYJFc8Dln7ocmCSKCcgSEiic2KlLq8R31C5vVYsxQNZaMj3YisLlU1T7H0mU7Ekh8o2cX2FpFFsEJMuf3+tYFk7yg8EA1Vo5XIIg45Uyhb96JtOmnpaj5aufA2X3mZ5EBUueQVpuDX2NphVX/sB/vA3YDHT7TVAOhRHg7R6H3UdzL7VfiA8bA+NOpe6ku7cjHhIGbFEYKQ/kKzUZj8Ls/l7f1JJh4pUYgHDYoaFnuEVM+RWql6eW/XFjAObgJ0slHfMBW2fyyvrH+3EmwjdlmNN4ILl8E7QjCeB+f8J/PWjpne5BU/+In4kIOb8AzTljycXdpKnZlu8SLJ9HkWFS56C32oxWpJeQukl0yXrwfT+yK6oo+LwLtC4n2cSRuFgnSXP599t4Rv8bwz2o/HH5r80ZwHV9k8u0U3Cs9K2f9mn/GVxPgnK6djITNnmLGZawsg2JE9QKNMQKFGTz2ryr81H2bsS2MO33YtBsnWxe4m95UaeV7wJXLDI9Wzha6BpT/DrucsPBoS0Nun+X4AXfoFy7CAv5u7OnKdYtKpr7VKxjR/Jm6hwyYtsmwn69kaYvcvsofUc6PhXoNVv+SiaSYY/Sj+9GvnWkSTuSsO04jVcmDc3L+hycWrzCNDifpf0dnAL/23/BS1gYXWQV8hKSMi2JMo2clUi0qpAolkl67iSTRE1QbX0jyv8uT/Anw8pr92/AUhbC9q3lo/5xo+xZxXM4e3+uXkLqtwJpvVDQJ2BwQgWee2lvHnqHyPqbB8u1htLEmGDLjmWCqhRV2WyjhAvKtOPF4bFq/uRvIsKl7zK0X2gyQ/ALPqfH+AvRrVu/KV8J/ochu1zQaOvjzwhTnJN2v8faM6zMe/OGi7WB0dWipIPJJOsrOxWfwOa/2qu/27JjuzTy0XYyDZTidqgkjVhJDlYvDuKVnK3IhWC8fIIBTErPMSCw952utvhHSxI1oPSWKTItmbaeitYNBp3CtvVvE5/mDYPR+8flRPiWTXtD4G53mZEvGVM+yd4MfM7njUDdMEV5O8adW0msUt1B/K1+YPoK0JTBBUueR3pUzL+7pPhVdvE7PwXgca32OOIEWE06X6Yxe/6gfCg/IVh2j1p29Xbi1OAZYyhQKXqu2iQNDZMr7IRYbb4bRD/jUYiMkogWEOyYixeCleAKVoeVKgkwJ9TFC5lPx8oVBpGIoV8D5MhYijvk4jNjJ8dsQk4fshWxeEI347zuWMHeJLYAzrKj4/vZ4HC4uTgDhUjYUL5igDn3AAjkdtAmwnOdteEdaP9QLDYpqY93mYR1tmPBIiUbf/waKb8HGr1kKt+1Byyk6hwUdwKUgyNNk7wA/xlESOoC193q95oWPG5c8uN0OrfhmZ5lUOz/x5I19ZwsQKmHa+8Gl5/6kIiURhJ5pXo1dqROuEpeQqbJ9fsF0CT24I1QJPuzdMeg+FrSrygZvewaPg7ULCEHwkIMZUbfyfMsk/8AP9sFoL2GiyLJSUTKlwUh/hBzHuBLwx/OuX5IitcSUKLtsPovnVeGE30A+Hhoi8sFvIXAU3nCxevmnMbm2zY+lEnYDL6nMhqfflnoCUfnOzYrSiphtsO6suC5ZdAzd7BRgOkz9LMv4KWfpgpEhEk0ibD9HiL/7ZefiRAJFl75LUwu37yA/zzpW3AxV9FXzSRoqhwUTIjX6Kxt2faN6aG18F0eSG6XAMRRnOfZ2H0OH/owk/ctUjuS9vHQPNfhlk/xg/mLjYC0+pBt9rMV8iPesR3Y8UXIL7lRn8mRYk10rYD59wE0/h2ZxsQJLsW2kgrln4SN8FiaXQzIGXOhcv4gQBZ9glowl2ZFmNUqSNM3y+jb46bwqhwUU6H+CIhe63TWWSkR18KlYPp+ry9aPHHxo5FxM6fnJ9MhBO5VB6h1cMwJWqApj4Gc2SnP5O72D5MUoHU9K4zh5Wl4kFEDF+o1NxOSSZsf6u6l7NYudU16Qw612LHfBYsf+NJ/XPE05TSfofF16pWHz8SIMcPgSY9ALPwdT/goMZ8bbzgFZe7pWSLChcle84UfanRw325pRIkUqR3yKy/2YtTup9MuFjfl/Z/Bq0dAbP0Az+aABQsDWp+D4w0tMxuxbTjRxYwH1uXXiO9RxQlwbBbQZJf1pgXKuK9Eq1JZSiIw7f0E1r1jR+ID/Zvbf4LlwBbsKQfDZC9K0CjroERs0EPmUIsWF7ihc+dfkTJCRUuSs7Y6MsLoBlPnKzssc6N7f4MtPhN5vyOcNk2y0Vfdi7wA+FDDa6HqdPPXfAC7EcSLnIhshUWLX4NlG/uR7Mgr+26MaAl71gvivTolqLkFlS5A0yD65yLdPFqfjRIePpZMwI0718w68f6sfhBZRq7PL6q5/uRgJFihSzNZal4LZiLP9N8ljBQ4aKERtpq0IR7YNZ96wf4CyeGSN3+DVTu5EcigCdrmvVXa2sdcfSlcBmYtk+AJDF27rMJJwBsQ8mWLPJqX5J9mP3wTmDpR6BFbyZ1l18l+bBiRYSK3OLVh0oWQbYv2D9h9izxg/HDVuy0/SMg5nhZc9OCIBt7CKrZC+aiD4Ei5f2IEgoqXJTwWP6p25vN6FsiyWyd/w4UrewHIkD2tcfxSmTbdD8QPlSxLQuEB0DLP4ZZPdSPJg4hl41K76eFb9nEPXMszQ8qSmywfiu1LoKpPQCo0w8oVtWfiQN83RDzRnGhzi03YarVB+b8l6Pb7g4HaZQ5WpzKTzXJtNtTLJykQ37ghnYpiAoXJXwO7wZN/T0g9vfpjb+ktXtb/hJKbkek20eydTL/5ahLnqnxrTBVuzrvFymlTDBseXf9a21PpBzDw/IarBpky0Cx7rv4VlYoKQWVqMFi5WInVmr0BCThNp6IGF/wXxbjvKjIpYioTb7t/JyLLMUDqaSc84zbZs8QTSZe4JmL3nfvgxIRKlyUyNn8A2jir2B2/OgH+Esp/iZdX+IvZQ8/EgFn2JYKFyukxJ5btqLmPpcQ3i9ngsq3hGn6c6DhDTmXXx7c6qqSln4Es2WKH1SUMyOff1TtBlPzIudFIr2i4o24E0u578LXc9UOwEaYWj/orgfSdywenMHUU6AaF7FoeS+66LSiwkWJEomSLHzTRUkkx8RD9a9kAfNidGFomail2SJfBCLFJt+1fADYNBm05P24lleGg724NuDXTHoiSaJgTiWn0shv5SCQOIhunJywf5MSP2x3bmloWI0/O9V50VCpfXSJ89EgvcoWvgEs+TDXtzqp7qUwnZ8P3nMmI3ydwfe/ZuG2xw/w7yG9jjr8hQXUIzl/t5WQUOGixIbDu6y5HBa8dmpLQzosd3sZaPgzdxwJvGqjmf8HzHsx4uRdgar3hGl0A2jR2zCbv/ejiQlJ51n+Xc05NwOlG/rRbJBy6tVDQHzD+nH8Gh3yJ5RURrYbULWL3RKVe5RvlXtCRZAqmZVfuehKHJoeng2q0MptC4nvTLw4sBk08Z7Tyrnl+2y3hoJsOJnHUOGixBZJsp3068x9j5r90jVujNa47vv7Im4bINhuxE1uh6nQ2lUzZEiWS1Ssi2aj64EGV589vCyVGuu/A61hEbNmuHrEpAiUvxhQqY2NophKbV1eVLwSS3NCFhLrR9tIJlZ+nRCi2Vr1d3yKhf+N8Y1sSHR4Al/nsiQcE/8e5vx/x8cHJw+hwkUJBuk6LUlp6S6x7Z8EzvuTexwx/FGVkuGpv4tqUrZh9Wa/gilcmgXMCzCHtvoziYsVXVJW3fA6oN5lITSz49dKDK5sNGYYsHWWbiklAVSkElChGSC5T+Vb8OPWQLmmfKVOoMoTSbSVhPFlnybMd0e+00a2YVr+Nn55LMKhHbyg4mvJ8s/8gIMKlYXp9iovOK7xI0osUeGiBIdk1Yvh0rwX7YrD9B/Bg1FEXdI5uh805xlgznNRrfKoaEVrEEeSuDv/pYRN4M2KbXtQvQdMvSuBugND6+B9iFeCG8aD1n9nozLJEG1KZUhclSVhtmxjGHvP4qQCC5VETNqU77GI4LXDWbB8DCPtKxIEmxvW7G6Y8/4AFKngR+OEbI1JcUJGawjGuov3eAcoXt2PKLFGhYuSvOxbC5ry6GmrnXChUnWtRT/tXQkseDOp8kRsJKbq+TB1WMDUHQCUqu/PnIW0NSxkxp0SMlkuvkp0WJ+OkjVgStVzny/Z2pEE0dINnGApVNo/M1HgaeDwLha4O0/d71oE2jrdleLnkudKdkiyK5rcAtP2sfiZ5qWzf4OLsmTNZRFH8Q5/A1rcx0cxWKAp2aLCRUl+Nk0CTX4QZttMPxAZVKYpTItfgnbxilIMsuioP5M8yN+AegNhavUFKncMPWFz50/uddw4Cdg8BWbfan9CyQ4b+SrJQrEcC5HSDb048QJFEqzj4ch6Jg7vBo4fdAJEHh/Zc+qxJNHbMb5JFeC+TTwRr4U5sNn/z4mNFYQNr4Zp9yQLwEZ+NF7wVLngddAPvFjKUi1F1brBXPhmYuQe5QFUuCgpAn+MJa9m6h+i3gaxFQlN7wKJP82id5O3UkcaPtbsCSOtBmr2Di90LTlEmyeDNk22Qgbb5qgBXgbsFoXkoEhUpUAJkHQEz59FJBYoBhOmeKGje/k/J/wRcySNP3/HecI+Ahw7YM8ZHqPj/Jk8xjd5/rEjMGIpz+eTUWyHgi0nbnQD0OYRoGwTPxpHpOHsuF+cVpFoc2s6/h1odg8faZQlXqhwUVKLE3yBX/iGLaE2B7f5wciwPVxEwEgTyIWvJU0OTHaIp43NjRFTsmoXAIXL+jMhIH+7bBtIY8ztc/h+LrBnKV9AMkyyihJjTgoW6Ssk22zxRgSiuN+KC3cWx1+q3h2mu0RZ4ugRo1hUuCipiaxA5zwLSNfZKAWHFTDn/gIk7QPmv8or3l3+TPJic2MqtAKqdoWp1s15TBSt6M+GiLyuO+cD238EbWMxs4PFzPb5yRuhUhIGG9FqehtMywdzTxisHeWSb9NW+gEHFSkP04mvLY1v5iONsuQGKlyU1ObQdl4tPc2C479RT6h2C0nKLQ/vdGXUKZYHIu0abKKv5MZU4ZvNIQjzwizeHruXON+dnQtd7owc71nGr3/kBoJKHkG2N5vfY6v9QqqWC4J960CTfgOzarAfyIA0lO3CoiXeFUxKJlS4KMnFiaPAT/9xyYdVOrtIQShGUwc2uRLq+f/hD310eQBUthlMqweA/IVA816C2TrDn0ktSHonSbSpUgd7byM0UsYbCfK+7V5qK1WwcwFo1wJ+vJgFzfJca7qnJA5SHm5a/AaQ7ukFS/rROCOf0R//CZrxFH8mD/hBB7GINxfwdad6dz+i5CYqXJTk46dXge+l5JAvKMWrAY1vh2l6B1Ciph3LESmhnvU0INb/0QoY6TYrK8PyLUCL/wcs/zLqfzPRsa+3CJgKbWAqnsd/e3OgVB0+E0XIXCpa+H2xJdp7V4N4xWv7Me3byOfW8+MtmhicolBF/hzJd0iM2nKrCktYO9JVJmbxqBHXYnPe74BWD/FCpbAfVXIbFS5KcrJlKmj0TSf3n23ORp3+MOfeA9S46OxRGBEws//BAuad6HMy0sPb9a8CrfralkzmJV8U60Rc5lwWMU2d22s5fiw9lkRIhhINOxvSyFO6Y0ulk73fZO9JBI+8znLP4gaHWOBIqa+S0NiE2/qXAy3uz/3+PeJVI41c147yA6cgFlOm0zOhLYiUuKLCRUlejuxxDRhl+yeD+KBS9VnA3AU0vvXse9GyhTT3eUCaw0WbxGt4xdjoWpvIK5MsSSLvxnH+bN7DJliWZQHjTdeM3Jes4zxOZDLIV9A/M4bItpOIG6kos94lO0+aqlH6Y3nOsX22nNhuU0kit+TfiLdJRuS8lByHABXg9z7dal7+rgIs5tI5fpBX8kv9Qd5FbPBx7h0wze6Nv2lcViRPbcaT7tqRJZpHLL5tZ3upvFMSEhUuSvIjK+5Fb4IWvgkj2wwektBu/atZSNzt8mFyQpJ4pQP1vFd4IjvVjj5SqHInGHHQtNtI7wCL30uKnkjxwkbIxFemhAgZvi9eA6ZYVb6vxmN8LNb3kk8j/ijJgIiig9tdBEgiQ/vWg1gU2y0vyePZNts/Me9BUrnW9E7+Ll4F5Gcxm5uIQF34GmjaE6dVB9oWIO3+DMjvmki9oZTTUOGipA6ypbBmhG2tj9XDkbGpIEnTOkn8a/iznCfDo2nOB0aSbjOIoEiRnBC7fXXOTQBPXjYXZu1IrbAJERvFKlbBRc6K8MRSpCyv3EtZy3zj7+1NtqskR0LeW3G0lQTP9G0qSTLOjuPe2E2Qz8/RfT4ic5Bv+93jwyxkJbonERnrQutvIkTFfZZFr76fmbHRlcY3OcEifZgSgTXDQD88cnoeS4GiQMsHYFo9bHuqKYmPChclNUlbA1r0FsC3jHbmVIAntMY3uu2ccs386BmQiWjFZyBp5CgOulFi7eHrDOAL+d1Aef650uV66Qf8b8/zz1CU5Mba8dfoBtP4tsSIrqTD31+a9OBp27Yn+x1J+wCJ9ClJgwoXJbWREsfVQ0ALXnPN4jJGYSSELQKm3hU5VwysH2PzYMy6b/1AdNime01+zgKKL/DStXnph6BlH8ckwqMo8UYcmc05NwKN+JZIiazixzLjCV68vJfpey9Q3YEw7f8v58WLkrCocFHyDulRmCXvskhY7wf5IlakvN3KsWIip7D2nmWg+a+4///IXj8YOTYKU7sfX/RvAWpd7Cqlln0CrBoUdbsCRQkSKlwBaHitEyyV2vvRBIG/OzT7b8BPr59WMWgFS9vHgQqt/YiSjKhwUfIekssgdt6L3gDWDM+Un2CjMI1vBxpcfapKJCuSB7H0A9C8V2B2L/SD0WEngkbXORFToSWwYQJoxecqYpSEweat1LsMpj5/N2r0CKYqLBoO7wbNfQ6Y/1KmCkG7hVVvgAqWFEKFi5K3kfyXxe+wiPlf5q7S4s3S6AaYpixicrrYbRgPLPgPaOXX/GWKjfmcdeZtfJMz5ZLKm40TQWI/vop/hm4nKXEk4cWKIBEWEStZKgJtDkuj62HaPJo4CcJKTFDhoigW/hqwCKFFbwIrB2WyoafyrVnA3AY0vB4oXM6PZkG8Qxa/C1r4RmYBFCVUpQuMCBi5Se8WqUwSk7vV32hirxIIVKIGULs/TJ2BiStWBGkdMf9lZzWQ0cdJqoTOuY0Fy8O57xejBIIKF0XJinhyLPmARchbMLt+8oN8QZTS3Pq8+pSk2mzdefnrtH6c84qIZRRGfpa00W/A4qkuTyhSHix5OutGgdYMd4nHx9L8sxUldOxWSuW2LFT6W8FiWzokKrIFtPIr0OK3YTZO8IMOKloZpvkvAUm4l7w1JWVR4aIoOSEJs4veAVZ8mikh165Kz7kF5pybnTPsmUivGFr8TkyjI1bEVLsApu7lLGIudZUcUj21aRILmG9ZOH0H2j6Hv9wn/P+hKJmRhHRT/UKgZh8WK5dE3jwzHoi3kuSkrRwMrPnmNIdru7Xa8tdAoxu0n1AeQYWLooSCGJKtGuxccLOWVVfpzALmJqD+NdmbnVnzOf5/l37MAminH4wNVKkdTD0WMXUGnNrLl6iRJPhu+I5/37Ewe5a4cSVPYtsvVO0CU7MXUKOni6qcMWKYIEjumdgYiFhZz59fOuJPOFxvsgEwze/Xjs15EBUuihIuskWz9H3Qondh9i7zg3IxLcQCYiAgUZiavc+cGyC5M6uHgpZ9wPcjT7sgRwuJhX6dfjC1+/EFvdspEzCxot84CbRpIt9/D+ycrxGZFMbmeVTuAFTpClONPwdVOyeOIVx27FrIi4NvQKuGAFumnea9IlhbftmqbXaP5q/kYVS4KEo0bP7BOuBi2WeZep9QkUquvFlaDFRq50ezIFGRFV+AlnwAs3myH4wddvLi1bUVMTV4pV2qrj/DiHU9Tw52K4xv2Dwj5pEgJX7YcvpqXWCqdnUdlyue51ofJDLHD9ntTVo7wop5s2e5P3EGRHyJWJGtUWntoORpVLgoSiyQSApfgGnJe3wRHpEpkkKlGsI0ut5VJZVp5EezkLaaxc/HLII+yZQQHEuoZD2gVi8YETESXi9c1p8R+DKwexmwdTpo2yy+nwPsmAMjnjVKQmHbVlRmYcLixIhAqdSWRWl9fzbBkT5B60azWBkFbBwPI1uw2SDJtmh8s4uwlDnHjyqKChdFiT0SSVn+mYukbJniBx1UsS2MCBjr0ZJNfxS5uEskZtnnwYkYyRGowBNezR5ulV65YxYhw8hWkpR2S2fj7XNBOxcAO3+CSVvjn6AEjY3cVWwJSJNQyUup2BoozeI3kfNTMiJRlI2SazUe2DA2U9+wM+HcpPs7sSJu0okeNVJyBRUuihIkEklZ/imLmI+ylFbzxFO1G4w49EpirXi0nIk4iBjBlsSWO9fmQphq57OQ6ZR5aykjUuVhRcwC0K5F/Dsu5dsSnqRWweC4f5ISFmJ4WK4xTOlz+H1o4pJny7dI7GqfrIgDtTQkle3TTZOBTT+wUNngT+YM8efNSFWQfB+k1F9RckCFi6LEix3zbTNFLPsUZh8LGo8VMZKLIlVJ9S7L3uROoh8rBzsDOpkUzpC8GEuoaFWgSodT2xFyy+53E6QkW37HXSxk9iwF7V3FxyudeNuzOtP2WV7DuriW4NdTEkqllL5kbZhS9dwWSNnGLFwr+2cmERJN2TbLbi2arTNBW2bAHD/gT54dW8bckIWK5IHJa6EoIaLCRVHiDn/lJCl2KYuYFV/BHNzkx/mMhMar93AW6zmJGHHqteWig6zhXdZmckEhna0h210iYiq0AcqfG+Kky3/z/o0sZFjMSNsC6dx7gP/u/XLjcdlC2L8+k2NxokOFSgFFKvLfX8G+T4YfU7GKfF/eRQ3Sz4lQKVYtebc9JIl250InvCWisnO+zYHKmIweKiQRpQZXue3Ssk38qKKEhwoXRclNJI9k82TQ8s/PLGKqnM8i5kpXTZFdTowkOG4YB1o73DWNjHMOiq1oqdDMbm2YcnxvbyxoCpbwzwgDMfk7tMPlCR3miVFuh+TxTtDh3fZvNUfSQMf2ucfHDoCkQkouYzzOs6z/d/awmMv50kb5DFCoNF8FC/HvWoxvxWHyFeS/h8fyF2UxUh6mOIsyvnfihO+LVXKurHJLteoWea1la3LXUpBs/e1exCJFtgCXR7wFaKOJsg0kHkN1+mnPICUmqHBRlEQhJxFjbdk7sIi5wkVicqoiET+MNSOckNk8JdeiGCRCS1yFSzeEkWoqeSy/t9xLqbYSX0Tgpa11ES8Wt5QmW3l8k9wk2dLL0KAwGkgEa83eTqyIK6/mrCgxRoWLoiQi6SJGukJL08csURSSxE1xDpX+MhXb8jc5myqTYweATfzv2FYAYxKmMaMtdS3mb5KYXKwqTNGKLqokWyySlGqjGzzpmfz+/1KyRaJUsn14YAvfWPDuW8vChD8zIlTSWKjsX4uMLStijTM+7Mufx0ttOwq13leCRIWLoiQDkgQpSbkrv4KRSp4MWBHAAsZIg7yaFwEFivkzZ0AmN+lltGEssHFyUrQCoELlTm3PFOXHInRY0LhcEn8rUJwny0Ju60fu5TWQlb9s50guSrJw4ghweA9wZLeLkNjHcu+O6eA29x5KbpD0wjog+UHb+EIem2aeISNVUDW6O08g+cyVbuhPKErwqHBRlGRD8hBYxNDqocCmKZmqi2xPmhoXukiM+GCU5JVwTkhSbLrPxqbvTxNFqQLlLwzDrw0VLOqiARKhKugFTXpuS778TuwIBUrw6+qSaW3OizH2/zHp/4/kH2WXwyNRruOugoqk0urYfuDoQbtlR0f32uorMfajY4f5eQf5nOTr8HMkLycHQ7bcxOZbVe7IQoVFivQ6knYCGglTcgkVLoqSzMgKfM1wFjFfu62grJ1zyzRmAdMbplZfH8I/S78a+fc2TgRtngRsnm7N5/JyGXNexZZvV25r2wcYcVmWz07Bkv6souQuKlwUJVWQJNwNY10kZs0wGEnCzICLxnRzIka6BIuoORuydbF9LrB1BmjTFBYyM3LuKaMkJ7L1U6U9jBgPWiflDtlHlBQll1Hhoiipys6fAOkJwyKGNk+GEWfTDJD4i4jxXXr4P1QTNEkE3TqTBc0ckCT7ym3XUnXNTRKkizkqtAAqtnF+PFVYrIiIzS7BW1ESDBUuipIXkIoSScpdI9GYUZlKrdOh8s1PCZmq54e34rYmZSyUts+zYkaql2jnIphDW/0TlNyAJHFZfHXEY0fe3wqt+THf5yvon6EoyYcKF0XJc/BXXqIk68eCWMxg08TTc2MkEZUnOaraBUZEjNykXDlcpCJmzzLbz4hsTyNpB+COtfN0bLAePyVrA2Ub2siJKePuUa6pLTNXlFRDhYui5HUkj2XLtFNCZvPUM277WLv2al2dkJEcCJkg+ZkRI2W90sfIeo2sAe3lx/vWWA8S29vomDjhKoJURdl+PlIlVrIuTGn/WMz85H04W9K1oqQQKlwURcmMREKkRHrjRFsija1SWXS6TwgVKssCpp0VMUaETCV+HEuXVLGg37/BeZYc3ML3G0H2no9tb6PNMHIfI8fX3IIKFgeKVoMpUQ0kPY1K1oApXsOZ8RWvzse1NHKiKBlQ4aIoSs6IL8mW6VbE0EYRMlNP21pKh8TSv1JbmIptAOkqXaFl9o0iY4V4pYhBW3pvI2vY5h/zONkxPi9VV3KO782xQ6CjaW7M+qgc4LEjoOP7T0tizglJdDUFijqbe8kbEc+XAkVgS4fF+K6Q3PMY34zci1OwbcAoZnriGsyPNVqiKGGhwkVRlPCQiX37bBYyP4C2TmNRMxMmbaU/eTrWDr5iKxYzImRauVt2DSMTCRE2J85QKSXVN8nkxqsoKYYKF0VRokfs57fOsJEZkuiM+L1I2XQ2UOkGMF1fAmr18SOKoiihocJFUZRg2LsC2LkI2LMctIcf713Oj/leEnCPHwda3g/T5Z/+yYqiKKGhwkVRFEVRlKRBrRIVRVEURUkaVLgoiqIoipI0qHBRFEVRFCVpUOGiKIqiKErSoMJFURRFUZSkQYWLoiiKoihJgwoXRVEURVGSBhUuiqIoiqIkDSpcFEVRFEVJGlS4KIqiKIqSNKhwURRFURQlaVDhoiiKoihK0qDCRVEURVGUJAH4f1Dt8HqqEg48AAAAAElFTkSuQmCC";
	
	public CustomerDtoTwo convertToCustomerDtoTwo(Customer customer) {
		CustomerDtoTwo customerDtoTwo = modelMapper.map(customer, CustomerDtoTwo.class);
		customerDtoTwo.setRefCountryId(customer.getCountry().getCountryId());
		customerDtoTwo.setRefStateId(customer.getState().getStateId());
		customerDtoTwo.setRefIndustryTypeId(customer.getIndustryType().getIndustryId());
		customerDtoTwo.setCountryName(customer.getCountry().getCountryName());
		customerDtoTwo.setStateName(customer.getState().getStateName());
		customerDtoTwo.setIndustryName(customer.getIndustryType().getIndustryName());
		customerDtoTwo.setCountryTelCode(customer.getCountry().getCountryTelCode());
		return customerDtoTwo;
	}

	@Override
	public void createBranchFaceListId(String branchCode) throws Exception {

		HttpClient httpclient = HttpClients.createDefault();

		try
		{
			String brCode = branchCode.toLowerCase();

			URIBuilder builder = new URIBuilder("https://centralindia.api.cognitive.microsoft.com/face/v1.0/largefacelists/"+brCode);


			URI uri = builder.build();
			HttpPut request = new HttpPut(uri);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", subscription.getKey().trim());

			// Creating API Body
			JSONObject json = new JSONObject();
			json.put("name", brCode);
			json.put("userData", "User-provided data attached to the face list.");
			json.put("recognitionModel", "recognition_02");
			// Request body
			StringEntity reqEntity = new StringEntity(json.toJSONString());
			request.setEntity(reqEntity);

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

	/*@Override
	public void trainBranchFaceListId(String branchCode) throws Exception {

		HttpClient httpclient = HttpClients.createDefault();
		try {
			String brCode = branchCode.toLowerCase();

			URIBuilder builder = new URIBuilder("https://centralindia.api.cognitive.microsoft.com/face/v1.0/largefacelists/"+brCode+"/train");


	            URI uri = builder.build();
	            HttpPost request = new HttpPost(uri);
	          //  request.setHeader("Content-Type", "application/json");
				request.setHeader("Ocp-Apim-Subscription-Key", "935ac35bce0149d8bf2818b936e25e1c");


	            // Request body
	            StringEntity reqEntity = new StringEntity("{}");
	            request.setEntity(reqEntity);

	            HttpResponse response = httpclient.execute(request);
	            HttpResponse response1 = httpclient.execute(request);
	            HttpEntity entity = response.getEntity();
	            HttpEntity entity1 = response1.getEntity();

	            if (entity != null) 
	            {
	                System.out.println(EntityUtils.toString(entity));
	            }


		} catch (Exception e) {

		}

	}*/


	//http://52.183.137.54/lngattendancesystem
}



