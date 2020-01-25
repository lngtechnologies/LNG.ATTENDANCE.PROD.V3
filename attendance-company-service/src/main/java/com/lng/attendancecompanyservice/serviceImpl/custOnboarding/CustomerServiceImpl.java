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

	//String Logo = "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";

		String Logo = "iVBORw0KGgoAAAANSUhEUgAAAi4AAAJiCAMAAADXDrn6AAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAACuUExURf////6mKv+qMv6zR/+cEP6fGf6jIf+WAhh20v6XBf6sOf/9+v/lv/6ZCv/ox/+wQP/rzf/37P7Ecf/+/f/58P/Ulv/Xnv/Rj/62T//w2v/79f/Kfv/t1P/fsf/cqv/89/7Bav/y3//HeP6/ZP/OiP/apP/26P+8Xv/iuP/04/66Wf/Mg/+5VCV+1TOG1+jx+/v9/keS23Cq48bd9Njo+Ie46PP4/ZzF7Fqd37HR8JexuAsAAE4KSURBVHja7F3pkqJMEBxowAC5RG6E5QaB93++BUEVbRC8nan888VEfDu7NNlVmdXV1M8PAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJ4MOxDzRDOcBoriVxLBUMKiBUvRDElKyCwyX3FiQ/dyXg5WS1i1v4V0w3taGJkcs2fGLFAMhwol1nN5u4bF/LVQZddQColhFw8Dy0hFqLlyCqv7m2iiKRVJTWQAdcT04CMQUlQm4gpW+3vxb+OV2QhNBJqQzOwoS1LVvhQ3qrq1ZJF3Ez0OowqRzCjtaMnXRBWW/ssQuGVB4LMORaKo1FwxsG/83cud9imVApE0/q9gTB3izJd4HVHzJQonM5AfJ7xlP/avUy0+iX3zIoaxFQ/v4sOTj6hlxCVPCFPRcuvZ/tfe5LoTIeKod0J4I5+K9Ub3OfaCKJXjbV5dJ1nJrhYWEkMqNryXD8RSjNF5LqBR+HqiAD6eKnyJhHOmOO4WVgZwnoDkUupTRZDCHCpmAIxCSDK6b14LQ4bsA8CEFd4he1QhfA/SDwBrWN2sp2uZKIH8A8CnIN08VSt0pgewKAAsAkM6qaywXCnDmgDwsGKyF1YSOJQBTOIK4YjQlgQYQKpxJ1zhYguWBDDkgxJ01CuspIFfBgyCj46emUXQQwIYMUIlc5KDDCiuAAaxdNFJJc4BvQIYCSzh8TyIVkRYEMAg1qeBBXlwbAgYxspgTpIQlPgBI9hEwrE3OodiHGAE+TELMeCEAGOwtWMTP3IhsADGJEt58EKUv4H1AIwZZ+VQvSUMuDIKGNW32eFYSAJ5CxiFXB0kSyXDcgDGIJqHSx8RVPoB42Q5OGcqBOMMmEgW2rB/lhZoXMBEslg+tYBkBBhyQ+YpWbZR7Y0MWBUAFkF2ShbbERYLAr6eA8Ai9dkTsqx1ula6BvQpAHCwnX0Flyrtn5+crD10CCoXgMMulrQNCsqqljCN3s2gvR+ABX+4XlYEPz8rpc5KJPRWArCwDnYIyT8/S6POSrQGh0QArGgJ9wqXzOsf86a9xYerQwAsvH0PLq3XAWXbBBpOhmUB4LCRDgpX7fIQBXkIcCUPoabSLzaCN4MzRQAW+T4PMW7DncYPMVDEBWCRFvuyXPyv4w4bwgetAVjo+yJu1VTjVg13uA/u3VbTZhwN77quXsMwjOY/9U8uz8ubAKrPz0Wwl7hMvrNH9GIhGJ8lcVM5TzTHLxDJ0NSEyXqkVEVhrHn8BtTXg6F1699mn7Rpy5U+pK1lK3pGeOeoPZYmzd3Um3/wqh8YWtrs04QWSnu7S9u4ho+IBw5k3BWTuKz0ZMhVDwgtbYFFbXpcpDfekV9brhFJ7Qnng8lyOlvP4OHE9JZgv2+uRLvly3eq5V3/FjcuiGdRBKNvYCDjXHj70KLvMoDfpKR3qBYrUSRq8Q4QhcEDZ6bZ0X13JdplH5mo43/5akOUuiG6kSkUQ3CoyrJIURTH6WbYK36WVYgjGGp6oCIyTYazjisQmb1q2YkGo15eQn5t+kl8YiZDCM6MQiPJRev6MbmdWqKrlX4hEfT134ycHE7ehzVl3O2+NvvsRIz/wjJukETM9HHhKHL0XN7eboWXQTPr9XRmJzbMRDDtAp8DOo3bZZ9G49Luy6iiZ9Oo0oz1TB5rYlIxKffWCz9eGmbpXIDv1ovYNVUulUbAvGaR7FyZkoAoLovdJ84JVuXEqQZ9GOG7kJiOicjpliXaZZ+Aq/dx/AqtZ8XSdQHazH99kT1bbpIQ4SNNM1oH5O8uHnd1XKrNPm5tTJjnt24v+ethhTHL/PX1MyvxSSyL6cyFQ/l9IpK2h0RUPTv0ng/bw8hZKXTfqBls3qhwYYZFf3sgxtppNxLr7ELtto407JPruLZXUFfrHp9wQdLCS3DJ+LN2adU5Ipo/RJrnJqJ/bjFqXpnI+ygjstUzTJThjD8ZY+Ru93Q2qCnNoScmovXp8BqcNtA/8i1sDHRBcVb6e6N3dOG02GI3TXNPrPpb4Uh5g5Vi+YNXys6Vi7zEVu5f+qbA0u82dd4Wy2qjQuWj71uLOJLLbmrwVnvT9s7Div8NlmMTc5f/8D9z+3fvn7k2AfB1miCskTziH+7Vz+cLnw0Llm+aFZzq6NxiE/GfEL572aK0AVWrl6Ea3OKr+DQUKzPltDFYYPlCX7q6ZAz6/UnJa7e7kLR5KWoKuYN7SunHhnBWYCmGCreC+aVzpVeadJ6UfvlUnrKzrW0eWEkjsmX3WbEe8ukKUSMH40ryzW1IQUz8nRBjd5fOpNY/W8SIbPEu7Awx1TxZypBtlrTZccUO5NzT4tAvEEcSDEO1v5ulajAEKaEqU0pt1/nymrMd0T97Orr8ndY67RS+3+6HpjYnDTxpgC7ftTcxC5lDRdt4hl7Z8kmsVBwjzOura7qmNE/cPpU5/zyzn2iF6BfOXdm0spXV2x+T+pEjfCBdG5i3ZE4y6clAFqImWk9V3l0VmcUSXM5juMLR+eBZtEnjs3IMyn8ZW/g2hlL8QcUMHRJtJVxsmBBx1XigICcl1xvg1sNdBHfQhjBD/SmN/uv8LMQQ2m86ttbbh+u0ytofFrkJTnnQ11tP0hAvWSjl2p9dylpECovngTEdz3p4pNmW/RBDx7/mMkHcxcw2Rti1vqDxVTK1wL7yqyW1wMe/b+5KYAk8hXsmU04785Tkwf1WS69f8KWcX6F6113dv9Mqq/ohCXzFQMR2zwrXCrpWhK2ysMWYYlnL+OaSZ4JGJf/QpCH260uU8v1n1nZnVsouiNYGWsLHzZi9hS1Whu9Cc4Z7EpaiYb7nBlrTUBk+sgd36/QehI2+vHSnSj1LZNUBpMBaohQtbmBLgI8shD6YhSztbVQ5oQz/sE8x2EYvSLL+N98hSFtvu1e2Mj1UzhfxmYEetcCpgiXLoLFUXZ9ZfAYEZDxKyyz1XrVXCL9WwwTt29kr28ZP4w20gT/kYcaWVA0FrGQZUMaWIbGLjwKj5I8JMmuX7Ive73RJVhsymC6h5sKCxZZn1Wqgz2AksC4NbBv0wKBG0SEWnwihetAxVp8wdPyF3x3atC+U7F66yw6UW6yBV5mNPLOHyyqCguWXHDKLzwWL9PTxhGG8b2OL2EpKTj0U/vHllhwvPccuB4i4Njn8VM+7uUIxnGSaWaSETmkYup7ouq4ZhuEoUYU4gmYfwhj14YThvqvpriv8I/vAFrwSwfvn/T0BrHvMcJEFp/C28Y05iCJQFGrupKPmlSV6Whkh4h7HxZre/SWZddLbGsUXuWq+1aF716zV7hZXRLKLAdkyWHH6VwrT0pCtS7d8BSyKPfFGb2FbuaaY5K3fiinub1/pK7rvGRfWsSVbH9mCSxXpQPO1MrhwOYMTuJf7SIxmvjUGKY86EEx5zZduqRnTyt3dCHavcEcn38QW/+fAFhIrLPC6ghrUaQGuoaW6SHKqMScJsWT0jE/ErXgjI2crG06/NymlUe9AfvM1bAmPbOHU6SKXGHrCJS4PSReSTo6mnxsSmSE+8/B/udGjmZyhontFqnyahT//Q/kT2aLhl7Ea2ugiJmQQ5x8QWibcjOOb1+R2m4/nnWeS9/avuKdhm3G/jC0S7umdef5Z9TEb8XxO8Cqe9loEyeFfvOksrZhBmeuNOuP4F59GWPTBHqmrt+zZkuDZsszm+Wf3cq13M2B7b8SflIUIhX9T0dPSqukC3LxvNk9P5r3/I+iDtVzqtGGhqbfg2GKjWf55g/nfz0WcXEzZtab23p225ENyck5K7nLWPRP5KSMWzjcQ3YstLovVLStuTtlfxZw8n1e5eTTlTI//iIs5W31qkGHuEjG9EtWnDXCZwZYt1ucO3Wn0MHnoTO7z1ytyTPifvStdUJTZoSAgouCGTbvv2m4z0z3zLfO9/4tdBSmqSKpgrhulld9OD1ohOTk5STUL9Dtl77Iik0SXwPH+rD4Klu1orW/xrodalxm+xfVsHfm2PZRu4ci9e9l5KNtZ9EHx6IfaZpoL+15Jj1BbN92CxZdOlJYXCejFuNwh+iuVhzirDdGrGfyZs1jzolJVGbuKnky1DUJfdHA7n4De/N6Ca1uaCCzcMZ8cdjNK5t2mwMtGfTiWiDvM/vn2X/rRyXVrBMaUt9w6O83NYT9HbYBAXCZlzT7EP3MjkKDJ1nrLwSxa78/mMBGPFpfNax2dJ8O9pY1FgCESWpg7A1ptR5yEerL8dMN2NowpB0+1kzlayFE6v879KjpPhnqLg5FIPiKEYW6VDq+wFwSWlVQCxMk4mwkoPdEwdBAd6JokJmxBGOotHtbTwIalmdAyEjWdvUVTvl9w1s7EvcazrFtww1hgxZTpwnZGOb3FxBqwY/hJi363hqJyyFpKWkn0gyzGFzQ+5LSI+ifpZ2/bQU5vsZAXpr9ACiLqd+pMBaBFD2TeqeQeMgql6IJTuS2i50j6Wdn2kudSOUaJhrpYMbUS4MKG9Pk9MyeVZN+U2o8qwVWSmKY5+RZMwLuCzBx9UbAoDxmu9gTWyZJhLOTOSDumCd0zbQMGzB72EyANpQlMRPRtnv05P1h3m9qTWGqQFWbwlcRfbs9Q/y3dLkFKaYtepAG9ZQbBHn3BhFt+AWeJKj8x6jV60n6x6OjPALPWINU0DW6wtwURwrgwBnWTyNtZ8DHL860+3wjJ3kIKEnIXRaTzvLBNWOt0Svm8BY7W02LMDTe0VF3tGU3sMFUZA0yrzBAu7xg9N2nk8pbJh2hjR4vbH9JH2rPeWeiKHMZbSve1fYMpoV3HhoSLj21Y6AJvQWbUGmtRmop7KU+9Hl/EXjvSBZh5+Nxx02dmYpc9YIijAc54CFPNnLw9kza3UzvRntv8oCwi7aT6LmNGPdcvYSX0IFcmgjNqZtJGaHJeMW/Q0Z7fam8CHqYrEQcTwVwSKQ7YxuQgF98CZ9Qoed2Sx7UY2mtYRyDVsKSB+Z3wpS+3CP9iwly6Qb4oFNnNoScQx2vxW/tz7VWsJ1ANziUBbyGE9ZrEMRzYsGkiDRDAyyAzaolgyuVHYqenvY65fN6uKsUml3emUzQzbdhX3CJnbaX7RHBGzSHllb8XdGiX2iuZH/BeHMeUoLEaSVziSmhStT/AR/rIGwFYPDijZpJ0vBZpzA7ai1mLz2rPi07BtEJfr8Z5c2rrkKVFEi5g8eCMWnIvVl20WK470V7O6tyM1Cj4hneDSSwBBnPniLekV/3AGbXkXqxANCLf9rUXNP+Np4axCt01WzKhYujZUGy5Qr5Ueh8HVMGQJfC1qUhUNtZe1NYHHoIJChwVHVri0tcRNrfuZVe+0FvIJ9YiOe5hrb2ujSzZAm4nTCGN+PE+EFJ/jeCOXdqjTO4MSVPAZD5pAzr/r8+Lu0ZBSe5QP2etSdaxwNteQ4JDtZ/hLR5JMSM+bNFH2suby+mK6IVkoiJiPy72eyYiWkBegLRPAW9JCuh350WG+f5f60/lAbwzk0YZkxLBMEKYS+hfnreQJfA1Lr9gLifKVc4BBicZvMLFXr+aYlwa4IUfIrlknIFyyQxJh8fNyX2509URDP5SOUXTNIRSbkKzbGwTMEQdJLXuM7yFzJBseVRUY6h8hLGNJUHjtRniirjIX1sI43LIZOzBHAnpUg85TG55rPwDVJ94HP4oELyb6ExJ3EVG0N4QyM4WRWCOhKwDcjn31cz7yjsQe0cryENxJA0hs2+1iGfAVlHTy+orgjkSopfiFNBVlYc4NkRLaqMoL1dE55Ia2vPAQfb1LJgL5kiItwTOs46R37mkbhSDsIt8Ic4/tZINcTgy4MF2CGoGz1veca5yq5xCZGMsf1cLoeFtM0BkYDc4FJ5I9r/jecseDS0r5RAZ1sNqSb0ApEOYipw6qZFMIPvrmVlsbpvnLehwiKGolhwxH6FgnMf7S1QVUXQu6JnXkLE7VhmYTjhkXBbLwWagfCGXIVtOyJ7Ah1k4MqRPSI3UxSk8EWu04sXMqbSa5WJUSOVc+1Du+khh3UKlojVeN7HPzACXtAiGrN5FvMUZ1JQb5DZsvuax/tJgtNy6Pc5RQ7OazFmKzCWrdxFvKbvKB/7E/Hm2ZOSeFtY85aQq2oFPIIfO1DXpXhJh76ZyT3MWF8AYDwvQa5PGrUMHSqJcpH3B+H8qXnp1goJEe12U5S5cYc9x9yiG84M+fr9qAz6kD9FWuSMIPmTocS/x4G+xDOnmLx7zJGHoIOD2HRlnRzKKKyLwAk5pLbqkXlkGA9OFzdlHPEdNp0945kGCzs0g/9NV057jRba9U5K5awLeR+ilwhBAKH8DDAxhVZFOH/vawoPkCNTeU9VQvMTA7hPn/hPUIc51hoRrq4IjhRS+06SjUwOH7K73uls4bmRgs1JaJX17W9AH2bIcIFtoOmK5ZRun/uHm97Y670sNzGhZd2bH6+F/mnR3QATwq2I6d4U/P1zoMVWnfbnNdF5j7j5WpTVO9YSrSxImTEV0AEpNBsSEC9y3e/SW5nughJYXGliCYtwTD65onOuXoJh7C2ULAwEMPlM2vgG9ZX1Sw3TVgV9aUBuPQ4Qh/0aiBUa5HMRV0QJ/9CmkIFfRxUjqvC8mPnaibsxNbUmf8dqEK+E2YoJuhQdGkMAOrfN3LKnjvpyAWTyoPAqLaCtZ+g/iWg1SLjRiTQnsYtgFdmJ2ySIpVUtfw+biEfVb2ZTmczGcu4Q9HwqJT0poF3qWhjuNZJFUT531yT5//bro3w8esYW4d4oCJZ/USGM0+rA24jMuYxz92qXEWw7KU472z9/fKpe5S/o9vkuX/0BDkQDR/sNJEbqycXEW7gCWdBP/8ZTk8mh/fa9Ufl/6R9jurXeHqN2kz79j2QAx1eHSCOq8WxY6Q5LOX2azyhu+f1Fv+VapVP7Srusvd0hHDXo99hxZnlsVsv87dAWzm4K5nptEm4bqMB4z0dfRW35eq6pFQcJNzKUzyMyBiQIu/qGx8AqdIUl3p+1x++4Avtj28+gt335o1/YX/dav4il2mC0SKuYohcfFuany5/yv/fTeuv37A7unRbR/j95S+fs6f2t/x/CyoXfv15kCmVdE0wnSQJuO6RuODmPujqkXBS4nb/n+zw34l8ZtH/zEmVh9AmPesotomvNlhXKxAC8NXEqU5mWgfOUIXL6f3OW/q/09mt/d3jy4xD4ytvUaSuHxelmpxuMYBy4WpV/fKV85A5fK1/X+nr+7kxLzhFzK8Q3RyBxazxHxuQY6Q2KkpYEJ61tSyoUYuFzK0DFW697lmhaXbmW+JdSugKF746WieIYkrft/7/JvOnpd4HKVIpryFxJfyjfmXOLaq4/sLBzCIrrGgzVnXjgt0jy04ZSaAi6Vyo/r/lWf/My3e/I6XXotEVlBV7RVbIeR/+lWkb58hCKj8MDlWkU0ZUH09lq3e3KDkp60THuDuhNbKqdQMlBLpUUY1DZ3VRQlwOX75/X/8vZwW+zSpIPLnHYFKlfxRFEsfRfPkKRH0D5MW7WhKfuMgMtVcS51oMtd+3aLCk6pRo+BiGe7KBDmdaLnWBcJrFD1VFGEAZefEj76kMYiU4QPhMElEf8PGURbigBwi3+1oqVECyf7HXrLtx8SPvqCKotmTp7gsmMJm3Qq8vn3n6miiAIulX8lfPSWxwSX6h8FlwAjetv8+89UUXSyX5G3fMn47KdGZjkJLqM/CS4dC6mKVoJbW5WrJMDlCqKo+1vNoijaKYG8guBC9RbZOFLHYS5v2+GLAxcpcW4YC+JW9MyxV2iVzdkrxnaSotCx5sNcU8FcCrhcTbdwVytRIsopze2fbScILgbsOdYE1/wqiQsFXK6oW7ijuZSIbuvAtvfM4QeXEeIMc763LJSrUMDlt5RP36UmEduJQIoY1LmQ4FIrQ23diO8t5Y7ylQS4SIlztd4pu8xIRb1Hq2zO5XlvMEXNTK63OIpxoYDLDVqL97A5VRfPbWeNVdk2Uv6AIjrsG2L3SqjVYhhwuUVr8U5VdLyyx4SzRX2w9KnBeFpqhmTK9xZdLbk82o8zcLlRa/HWNqaUC0sbDi6+8YdFth7AuWNbpaJ8wOWnnI/fSFYuTCykuQgWdCQs3gLEHAFwUXRuaH+fvUXK1mIEdK1zlggQXgTy/+SWKpaha2YAl7JSLVDARcrWohZx+PHAoW5bgKIDq8WSj+wAFTPge4u9Ub5CAZcvOZ9/Ytmkih4lM4zEtoCiIx9htN7mSdhfd/jeoqaKjvYZAxc5KZeoXdQlKMYDcj0QL5JCu5uW0HX4rSLbVMPzFHCRFeeGLZ9zmqgjIaBmcfl/pu8YUsEfglSkriyigYucrcUo18RCl4MNVXRjPkXXTQ+obQTeoivZwhG4fKvI3FqMiJZ4iGPmIFNvoLdcQoPLqYndshTOzQlcfsv6FU6kyhnozhGgO+RX0bu0nnIn8Ba1mZsGLrLi3HDW7LyipW8i7SKguDX7mCOdROGCPrTaV8gAF0lbi1rU4DkTc29IDIBAlzCzH6muQEeUitTMIg1c5GwtntzBTFg3HVlONeIKXRjFVEkTdhYVn3sCLl8kuPyS9TuMknjhJhVSYl1uL3qaCi6uwFsSvKOAi8SUS4ROm8QzgBpl7fAOvmWyLceJLvCWklqG+pkAF0lbixG6jdvLJ/4F3KEHLvH1Yu3kPiVc2NuqiM4HXGRtLWoRB3eWWg6wi2J0HqM7sdiyaOYJvMVQ3kIBl69Pab/FgZAupxIIkC5N7oqOIMW5dEXBpancJQEu0lIumtb3yEqfccLWCUgXgoVLLKErYv+RRfEvZ/9VngDnhj5ynikykJ29vsWjT1y2dzgpK4YuJ3CRtbV4LoacSLAwQ3aw+i6XdKEVU2YH2eWtFJc4cJG2tXi0jvM/9q50LXEliIZddkTBgKLsoICggpd5/xe7kvRSSVXHMKMk6U7/mg/m3vkghzqnTi0tZOhKAAeciWoXnadjakxdiAWCS9rmAoTLMcEf41H4KDdFogDwlFdt0YXTIuVnDKs0uCiES4J1ruPRlV0fZUnt6Fmq2ug8haSFb9NYGlzUwiW5pUXLrRfZEjjfc1EORCWQJefS4BJSuCS2tMjTm5lIqJGXdpNX7QjLeeRMYBKdBpd3QEUfSf4gbcFFXaoKiPIi9pedLApAKJsGl5DC5T3Rn6QoQkqzRDRGDVUNcXA0oPD0GISWsumeyx4Il+SWFk/nQaQ6z+S9WgVFMfoGenLjaiY1dMMJlwSXFpnVwi7TuisR9SJ0AwDXwh6tMuoEoQWXuA0WLgkuLTIGYrZbBW6DsghvxZMXwabLXDXI/je+FA2FS6ItF9fSdUNKz7NTgZ+MwqPrw4TpMTi41FLhoonOddouW86fplQGM0LPnt1tNvNMLgYGl4rZaPmEwiXJpUWW+BRlHEFxoKOaRoOmSzs4uBi+EhUKlySXFnkaPRRclEcTqbaid8HTvdsKDC5Fszt0PcLlmPAP0xMrw6bUjGG/rmiJg/Ek2w0MLtNUuGiic53HXu+LvAjtXUbOfoHgortAz6V+bTJaPMIl0aVFTjauM/dcCtV2yQy3a8hFgd3/hq/n9giXRJcWT+dUP5wKdil+n0az8qKHfvKpRac4O49wSXZp8XRaQo3kqEAwRw//GnNR8GmmwkUTy8VyfH9XjfTLVMY7U6TRt+XQcBmYLFxeIVqSXVp0zpqvFXNm5lE1eqJYzv4YGi0Fk5dFeYRLwkuLzinwAvOkRE0vFhV2/iI0XEze0PGfBy0JLy2ezoMoKhZkBUCmR6gCwHLum0JouBjc6LL3oCXxloubDhWE5GXS5VFKmJlCtzZCo8XgWrRXuGigc51WbtdImUrpUpEJEpIucuw+FbrnCZeklxa5NumKxDjD48xa6bow6VJJhe6ZwiXxpUVuqzjSxSkNDXlEeVG5LmX3gonr0MHF3FuufMLlqMNnGnDpshSG7WnoqC3fJhtXwqfRvVS4aKNzncZKm/+BPdsamB0ZK4LFJCxazO2LOnrRctDiQ+V4wagias1TUOVpKoRrMSxcOqlw0aO06J48W+zvVADcOLMulfKsnQmPL17TboyydcHUy38/vGhJfmnROb0v8VoV0mXKIwcvCj4o7tLrhoXLOhUu2lgurgtXkSJlyZMhns50Fb0uoSsApi669AkXDUqLzmnz5LkimGYA6tJDhRQppqZL0Nn5hIsOpUUuZd3kuS6Y5h5kvxW6TXceNrgYOkbvFy4alBaF0nWg0ZA3JOZKpTx7t+rv6nZ1TnjXxczLorc+4aKH5WI53bluEnQnlW4hQOlWBIWFOsVUuGikcx2d0uTJMysHjQCHoHL0UMFRimZdMwsABx9atCgtckeuzUMKULpKT9d9o4r7LvOVtKVbIVy0KC2654UhYC7nh1ZgFXuOVrpEr8uEii6Z30w+4vqTRcLlqM8vIcMQsJR9TGshaPEaIFaOJsahSd9u9YsP5fgazx/tzi9ctNG5lvVUZtBYSaWRkVHhWhEucH2xMLhwMXr/9Rs+xvFB+IWLJqVFnvm4vdy2aLz8QpBw7msKSx9vLBxOL8xF1vY0SXqIHSMh4aJJaZH7Jy+SdRxh2gKt+x26p59QurWXi08AOJPHcWOk7eZKy9KixWXtSrCOS0tdUAJo01ta8NVGhZvM5fOiT0clxIqRPOsK9bJcWGI0EKyTERB5UCVG7htY1k6IgPP7Ht3O7ZyOESMh4aJLaVGIkJ5gHVtA5Im9XaRLANjTHbSi8ejcSl5sGAkJF21Ki+6pl+o3ItUZc4jw/S19hXbN4Um1WUT1og/X5HiLBSNh4fKmFVpGPDGqCMf2xCrKipFNuzFfr7ejqhdtmc0RA0bCwkUjy4WZc25qnBcVox7Yrz2g6eWW6McllncML/SQWGvJJnJGQsJFJ8uFSRYn2Z3L7acDYMZO6YpRgxiCLkbYR8cIKWpG+oPQok9pUSTKM5EY1Z2X7sDlMxN6gLGLJU2fqFA/XU4zcN/9Pbrns8PCRaPSonvWrGLUkTp2CBbrIoZxB6jHuGWuFXFPN+eBTWSZCCFcjpqh5ZRH34rMOCcgwks9fuutLkDmpx2iu2522RSWl4EjYqQdFi5XW93gkmddlraUpkVgu9TpPNpfMSr3Lbz3snzh1aiCkKJhJCxcdNO5ToqTlQCY8jya2y5zxaKWPF74soh+e6H8fUfASHssXHQqLbqn5cmjZzyPzsq3qdR4TlQSK3FY0/0hntnbhet6hHDRqrQofBWn8/Japj01sOV9QEOgQeRL+Vi0XUpCunq/qHB4x2h51w4tp4SoIwHQ40nyUL5NqddHLF2uYzICAATn5r/LkQEhXDZb/eAyZvVoFwDOhsIV4JF72nib4jW7rags3QBCuhwjEcJFs9KiexasDOjMGOWFM6d06Vrky22CtqLbR7cFMuIyjPRJCJc3DdFy8lhGwnfLCNuFu3Q2fUdaDrdMdRTLVKN2QC7CSIRw0ay0KF0657G+yMw3GzQfXSXNu1HA1Z5RE9IFGIkQLvpZLs4pMAZqyvnoAhcxBC7ypO1S5HiD5y7Kj7WDhPTbjEQJF91Ki+6pcgbKyF0JZTlOj7JjN9t58r36QsWhiKcXPZb8rzISJVy0Ky2655lPyOfFCNkcjnuUyRrAiAgkRcV1atHhBRLSbzISJVyOWqLllP7aLMrwPT8PwL7v09sXGkSLZZmIOBHjxUNIv8ZI/12ZYbmcTo3ddTWXHlwDPOtnWr/6k+ZbYtixG4NP560R/w4j7Qm0aGm5sAc/5iGFeXAD4LA90B0sXcxQvXheMfLHo0Jff56R/OsK9VoU5T9dZuDWZN9+F8weNuj7N6eYdxqXHHb9S8vupCl+miUo4aJhaZGdO8YaA1kyugN7k2v0nrkxLjsOYlIB+IaQrjaHH/3lU8JFx9IiO/fMq+9Ky/YeTLwO6JHnCa4jdcnR2DjAxUdIP8pIpHDZfGoLlzYsGbnmXBt08Hfp/oU1bt+dki29sSSkn2MkUrhoark4Z8EYyO2cFKGjxd7u0FZtEzss4xjvL9zt/ArjhxjpeGWO5eIcm8WBtnT41yCtUUwZZXByPYn3lfQ+QvqZkWpSuOhZWmSnyZq4hzIk2CU2G2AR7S5uY0MBy991/FwXr8rwO/X/vuTjg0TLQWO0WBU2CbKQyW+Ts5JFlJndHpY63vlux/1iV0RIm38cqaaFi37d3PBkWG1nLa+eb4J6T5ucYewTr+XiVTAKQ0j/yEikcNHXcmG0khEMJNtdisFweSY6ppoxKxiFIqR/YSRauLxrjRYrz0JKzgOXDEiciN7LHtECk03C9Wg77MH+LSPRwkXX0iI/HCNNubqlCO5OXJBrT1v+tm4El1ZcPy8ipL9kpC0pXLQtLbJzy2vMWQ9cmkq4jDBcbAwXfi1fIgjprxiJFi5vlvZwAU97wfhJDKUp4NIgykjZSKddz0loiKLg2YxECxetLZfTmXOMeOGyVsLFGZKvEUZv9nLLdH/BXTuTkWjhorflcjoj7r8CuJTlpjEMl1sMlwGnsMRcvrgnhMc5Sz4UwuX10wC4tP1wAYvp8JiRA5cBIWuL6G8ljJDOYCRauOhcWmSnxYnjLLgsidpzJln3o5ENtiGf94FGy9FK4fI9GZUtpF2aCfjke7JpMgwjKYSL7pYLe/BTH1xuA+EyR3ApYLjYSfjonySlfL92ilhXaILlwqPL3VlwGSG4VBIKF0U2/N3aqZ1CuGjbza2Gi/09XHoILgwbTQUZtexWbD/9B5nhBDOSwnHRvLToh0tT9DndwgqhTdaMakQPtwIuT5k4KxmakIIYaa9Ay7tlHlycB9uHbLL+Hi5jDKys/AcaYN46MYQUwEhvV4ZaLl642FKGwF0bC7KBoUUsWljQcOnG/WZgmpBUI9UqKvpjGQiXLIeL4A+63+WBaLOc0L7Lfew93q1Cu1Ij1Z8bYy0XDpepDA5FDhcRHujmyxGxUqxN99JN4jRydJ7tRoxUK4LLZm8OXMCgWZ7DRbRHrcjW7jlxwdU93hnEYdiN+7fwoQgafkbavRpaWuSnx+HSlgZtHoSHO3xrkeW7zIgpE89E0o0HLp3Yfw3bt6swjPRHoXN3psBlxOGyklOMBXFPhGqKsUr0/M9Qm4PQRHfx/x52CkLyMtK7wZaLc645XDrS4i+KhYV43TJLm+sYLkt6bMSOffcLjxwKQgKMtNsYbLlYjFbGIDg4xJIF4WFJr1Yu4EjSolt17cvc9voDZ68iJMFICotuaw5cLN4etZRpcgWEB7Swwy0PZHBvyzO+UY3DpZ2Mr2L3fhXMSP+ZrXNPp8wM/4bcstEE4aFFLxuDWy7Z3+zT2zpeYjgvfT4huYx0MD64WPmSa/g/eMdXazJzolYZ5oikOU+OGS1gwTL28WX/ehXASEfjg4tVLLkO7ly2ZL+Adf5z+haRF8LBhQS18sDFTs7X8akkpKvNq2Grf6iTZQ7uTVkI2SG4QeKmRG7tHhMObo7cNDZMFlx2yqqQ2cUifpo8PBTEaPM9ZBN0p9WT373rgTiC+6PaCenFlOfj9Qy0mEVFJ6ECmyebHAsrSVbUjSOPaCrNW16qeOBSSdZXomiCoZjINLRYi1IZcEmGWzCCTSpkwwscY8yz2yY6ZEn63tvPkIxzCAGV19fjYWsaWk7awr0CeCKEyBK20+XI29Keicuil/iyaQaXYuK+lY/Nt2ixjDz33JZ1KsrlKgsdoj9qQi6nqxK7XB7IvZerUszb6eijrDka6PzD0+FaVS7W7QU2vKykMOZsVMU+XQP8/zndJersDmlGRJwZf7Ryy/s1TI87dNGoQlSICpSt+5iEGVjy/AmEy9ZMuCx5b+RItsaVQUBAa7ttllBhDzdLXTi+lPc36oSXjZloORWFZsCn6/DkmQcEdIdEVrhvvrKj53Y9kVk1Yr1LKvgEOHZHQ+HyLLqXMqKdpQLMN1QFcGlqSswx3lPXdvYSsL9Ded5Te853+qJ7KSeyHBtq1TJp686I6zy7lPHSLyWi+1Khd5X50YehcLHqnDjaIlBM4JXhme/HXpk2gbxVfuL/fT4p7XRkPq3yXz5NhUuGbxbrCqpZwYBA+3Q9/JKXtx4g3CaJ/XYUcvfNVLRYTS49aqK5uwsbJifkLEC1TAwykpl0LuoLyP/tHIxdzkGfF64zrkVUqMEqwIq+L61IXC+SozpeFjG6aO9vzjF1XeBpiwJPgRsvPdh08D97V9rkKI5EAQMKbIMPbJfv+zwiZnv2iNj//8d2dKEUkmqHGpdjoPJ9mJjquqIhW3r58mXmyb68s2dJjfa2Ke+LelYBPqW7v//YaKEp8chTEUD1tQ1Mbbp24eVgmbp8snXVT4r+pZrC4mf4588Nl1OhsQwLyS0SxUYhzNj8dEczW9KKjPL7826NdTpxvvyOh4sHTo8rOAfaZZ0uT6wGqZNlT6fGf/tQp1vX+xnpHQK//vODw2VXJM394uy4QCU2ts7suFp6G7WqkeyjZzH0qPczyuFQj1+//eBoodaVPXix7OxYfCq8jIvYKk8XO9iqRnGdWkec+Ne/f7Ej5td//+H9aETFBoBASm4n2Hq4tVoYdEojuO7YNp+uVT+3rov0/vbbD48VlhAFQCJhqZHmp/uwNjJuyP/husWOmmHNM2mEhnvxMscyNTrDTHplr0mHlgWweWgpA4z1CR6IemNfCC/9QjAJgVayJNYlNbHFY6fxnDEUbjJ8zg3BpEh6O6kUTNogE+4k1tSobdHuNMuLrCsO/pZrpRFfF15WBY0RYXKBWolhYfiwJEz8gILpta+4NOyCRdQbZ+Ws3cuD4AFNKsbk7kM5aS5kmpFt4lirdn2viE8QFvfGWtKQNQEL9vbWqtHeNoRMO4mu4PsxNWoM2oUscpNlgKVRJIRIczO/7qmsuRRBPAh3+Jwbgov6tx8JrruhvSNSODEmSLEcuWRsSDdG1i2DcNeAqhGiwFE1Aj1l0huAIqOZGp0809jA503NwdcmIxCEe3zODcFK0YyjTHwu0N0d2MoA5TFkC5Bc6ePfkes2Cks1KDmTRriPsn/SGFBXqgLIeHhYJr88/t576xGV0Cl6R7xOKOR/za5rVI3SjqcbuRV5ySyOumvtHVIILTXSysd02sYIpkaG/5K9+56VvGhlIzG2Y5TUuDUNUcZBDVw4Ss4RA//lgNgsLwfr+HdN1JsA8nPH59wQjJUskknOMYWtZbFN1z1aLd+aSiOVvqG+4whRa4CqESMvgaCnRV3wXg4X37MYG/jVM0iMQUFcqJvhg24GRqBA1BLVnhWsCxpcl4bGzLoZS+c0Yvj3GYvSTUKsvHOSvNygZdIY8kJFWkO9uxdXm1EHQPLSJDwVs+hL8hKBMQqbxNb5WjI2JEJauVlMDHskLw0CKAPkkrw8oQeubStKG8aGTB0lJQcmKi8N5bosCJI55yuFVmLsB6ZfYRgbBAF6mJ31mxTOq0PUG/NEcd2xsKpkkG4YO/boVxjN9sGgSw2+M8tthGWjJiFQSx9mgqJSvlLQDWNCHSUvpuebEP9WIjUzFYTJCB90MwB0XabJtT19x57p1w1sxgYaLx39NhI30A5k2oi6YwJUtK04CPbwBW9tyktgCRfKVmaWMiOm0g3CEqhoV6GrXOFQubVNeTnYwiUcWHOjBRB5ETVHruzd3jzlcTKAzR8meTlYBsDL/OjDVOr6+gJYRK0xBfPjpkJTC+DmEN/S+tq3hkvUEQPjxYe5okRbfNDNwAeIjLG4Q4aQvAwtnhcr12U5dtvsH9mD0EHUHBnw6t+EYXdVkJfu1WKROlrEXrkiYGJuDMDbqEHohGCXfMA1NWqCY3MYRiGZdIwdnj3bkSNGdYxSc/leXOt5zAgNLbAxkValk4FSXk6EpEujQpQMvJU1XOgZcjBHMbDcaINPuhF4ANF1KTS1hXCpUNrRNtOgkyVfkn0l4O5KRBxid1qDAKuMLA168nd+EecCMcPlaXFlFutGfHMIZq9eG8gRf5a87Pm9QfkKTWZYuARGKh3OvYP9eMl0J+9BZVzJGR9145SXvkiAp/xomNiDgqy8k/0TQ887gyQ75dcc478f+KSboryolYmxkm0ftj4jeWo4yEuc63ZwQXbrvk0CoTCD5usF19R2PF+e2+U4mjH7ztwoM10vGUovzUEMhiUvxYv1eXE6IK6oGDpzI+2brkrQueCTbgSg54WdGlvOedcWm2VxG12Jq26kMZ4Wkt2mYQ3N1w9+G3U5h3GRl3AzT10s2NtEho1hFNZ/PwCCYwDf5I7fNTlPpc0KQBEVLfsnnp5+Jolcmt5dMbpeGoEAju7v8dvowjnM3REud4fnhdUQbnCSFM+6dgkqu03BApgYWBiEHVYVGnoufYWE853jM+UZDcIl9WzMPokfjz5MpVln/IpZ62K9T75UN3JkTW1P9+yGXKrrYi7doFQalHSmnIGIwR1tR7i0zE2wgNw+zQa1NhaOmoKhnBhGseLq/Ym/6aMjKMhtSdzSC3RnRvPix6rBMYgaowtW6nmdiNkXqEfKt7egCU+d76I1nr444CgJNUp1zUAnJFv10Z5XAKa8FdERFMT3XLfRxNMFG3G80BaUBEcDNQEXaL6e8dnsa94L4hJ2STYjbrKrcR5+vOQ+mjAbgrWWtfQYbaG5kW+bCFQ4t10VpT4eL83GPIG30YRbEe7szeeRIyjS0dEZSCX28lDHC7KXJmAKb6N5yDSSNRfZtq7jZey6jVIq+mWlflhxvGBy1JDbSOVGLELu3iZkSp2rzEgCpyhzlPpNaeo7ai9NuY1CyEKXXHo58Gpj5CS7H8RtY9C0l5QNAMm7OHqsMbkRHJPQY8WfjKcyztvociPOEoGnlycFZcFZUg3BVbuN1nxAi89EN+dtlJx7znuKJuRJuYckX8L1NYj6ohOpHYxC2c2YEHf65DZ6jF2fYaE3NNfYHMScKUTNsZVtHgx7doHsuH3SeRtF58RZgfwDo8hYiXULsYekEehqq2SowykdMA5zcyt1ZD0ln0h1moUq5kXMo8qrETVGHmv2pTs7BURZOnYFRXvt+gzLlzu+UQrYYCmgGVhou0EyVkVk0kvurhuRrsvMy+U42C+Q3rzijzJ83HXHDqwf8bikdmVs9ep2MZDnhXxWCdBKAYJK0/srwGlStUeP9azCXHrKAmXqbk+zzWeQWTY7qmap0aPG2C5u3Ks9Tpr0wnLpGSW7yU1OtLTcPNvo8+MFmGKSmFem6Q8Lb/i8a45NSGBB58hqPSfmehEJ896Ml/Tw+fGy8Y2ugE6ApaMmYCi7ghho52E4Z2S3I+qF/sISFc576u6V2W7Cy9HLBAe/NwBLnezuGcfg3dJiGN0pJRXAgwN2BbRzmYSREFum64621phKpTo/p//tcSZDyHRbJVyeitqWxJcAbINF1BVjvf53YNr9k2m0QnrpJlXihcsrcCJDOlPXEfbA1hzzUK3y5JcT6XkZy3KEc254rxIuoq4IxZdeXvDocIdPvO5kV9v6PGUnRMBms/B3HmZVwkUk5jt4HfH6Yt5ToYOoK3YJGIPJDXF3UTgS1aFxq0q4CPV2bGZHN5z40gBMZQt8cY0kO0pzo7kgu3630vEiWvWhjSrgpWmaayVYO6o3uvpe1oxJdUeWUQvN5dquEi6ixUirBYgRHjTJitHKUG8E+pCnHrW9jFhdWgz56a0qHS8Lz7iOBKNh2XQL6UutMdET3C47DbhUJ5KizK8SLulOsWZlwlMMGOlLvXPpSB/y1KYDfc4pdWuLpOh5qnS8iLWd58ich8noyxWfeZ2x181LXXYCbFnHUVuUDuNK8SKar6/EnOHxQPWl7jinRKsW9yhfpT0gLTmobjuuFC7S+A/7ApKuuqJ8XEpeZxz0PuYuk9aoaS7rxMJFWel4SYTxv9M26cuITgdHuvttyIJvt0XPSmvCW+p4ES6p/Uel40XWnjVxt8fPnF2kOqgRL3+V4RumjT71KSyZOl7mkXj/UaV4kUM6rsTsss9SAuduIl6IEeu6mH/zb+mXOjuK42UqpbrFkXyF7eodBcIfxYYEYR/sd4CJF9G3b8FskWSnhw85evR46Q+4OhvuwkrhIlrSvBwWnFKRgH2A/0e8EB9wTu13EqQSnbgTEo348SLSm2PF40Wa9AaxSXfZzwxxStCrwW1p7+gYbfEBUAVjSugLp2apvqgERBXZSyKjYQmLR4G4VunBFWNrwHdcRW9JI7oEjqpj1cBwdEtpyiTcl8dqyZE06Xre2nRnevkU4+Xl4KW95C0iaIm9nENZOervxPEyqCbtqqkL2jReUZzuUKOEj17vF2IQaUnpt7MX7RctaPZMC9MtT3S5PqpVjqRJV1w95d2ec2qIwXh5qRpC3jg4sqVrL1Rw2TKq/TjKYZZBtXgpZoxtoFdKptAsXgJ0v7wKJ62++47jRftVE1pa3MALaF/NVgeuI+0ekyk0ni+vhBQ6lm88zODvygOaRp+04bmtr15HM5hWRUsYL8h3X4G8B4bsvAWzhExLyRLJtEG6w2XyxeyIK/9FvAhSvWH5EQ6BfwEecITXe3AoLTeb0t0QsG0k2Q0rXkfKBXyF2xpjcQV17uCwQfwFJpG8l7lQ3FI+7rS4DhPa0gjTmvugWimAJH3IhYAjRsRLzvRd3MD3FzGK4QiMd2FfmpKwp06nG7xGsnHF48VX1VHtW4MROEUTbIZ9SQ6tK63fH6QRief6x+TDe2gNrBWTafg30KpObRkvk4TorSuIqhjDRR7v/b3HUi4fnucwC15lFcMFduzvrfFypRfcAQc1/2XiIhXzt6Hjl2aC9ejxcNJsuIeK4RKBHzi0xsuSxmMbBZgvohC1ordboK+lUsAyIclSW844Pldku1obvTYsJpARcqbZeoQGmK8pLq1Su/E70SpNwB1SLR8mNdFoUvU6WniO86WQdDc0/UrRkPkVLLSFmG/GLNEn4FK2u9qEmuO2VzFckq7n4C9K0h3T+/cyx7dfFavSJKY3Y1g61E6ExHOtYWg5S6vSl7MrXpSkm9Eygd/H918Ny7DspncxnI9vGWw8ioju3usRcvkfe1e6pqoOBEGAfEG2AQFFQdzX93++a3DOHTokbIIDDvl3vrMpU3RXV1d3VrDRvK+bjqxs3QPsL8r/AEkbUnhc1lyP5or0rA7n6EpHm0d9aiqA9InOd3DpyLSu+AL1I6DXZSTdPUlI0jgR24TmlixF707ccjWqdbQkOSPLXtTQxHXxAhNc9q9nJF2TlITyyHgrn4Re2lWo43VDbkzqdjOyhTvxYalTt9UI6a4QGUxbjDBLRZ0xwFQ8HqrGc6P09VQ7+hQ7GLfS21rtE/CxhGpdvCgAA/CWG+fn1TgTxmvc1iMWKohkP0G6cC762zyy7Io/KRljEzknhO6hCNhuXWMdvXZhBcZQpJ/fm6W4FMcdMKXHljNiWGFwUbqdEDhQd/kSIVfX4fhq7XSELBAyNln4YTHzXfT0d+aja6r4XBRmX47LcDpczGBR3NRHSFxnBXzlKxRr4+UO6NgXUPuyLYD1jURPnIxdpEoldNny/H2VQvul4miBqX9eQmg5U8GKhfrVETVfNwXzJEBxWaTzeMZ2XBvEO6GVX4/CPjFdT3RwbhRkNzLCqzNcsbCtDRd62BtefgM8DHoq7cj7kfOyX+g5T6TI6XPPt3rSqUGEXNgaUzWbJWS9C+J6XT8d0RiPswU11rLeCfeQBjNxpDCc4pXeEsk+/wSzjv2tJkag00MGE3yQjraCXT8d0W+CDQpyBXypqadC9+Z4/p1snVF4aeFRRV3z3P8/0R3QGQPJRx26tm/14YKpxsURbATHNxcCRhkvuGdThdyOJTbDmaCuee6//0mkPsme8FsHpCPXeh0vU1iRS/CLTaOdPcKDOgFjpUUxw+m+r+KaVHVE1lies+Uz3sJr0RriRYDX9ymjQFdyPK43hMdwrHd8rC3FohYGUmdZvoJtIUYt4GUFObMzurwrowWdKzAc4y3lAql8PCod7UAkFNfCrgW8uCG8+H4yjjVWRYtTKs+ht7E/GyMATEJVdHBv3lZoIO6yzBk+SEhGMK5p5hzg/fh3URTz/O8hmLwrWt8Q0qZUOgqzfAXbwkJugpcc4FfQcWWNHgZ2bAFokQuyTIzzc8cdH8JutxQnT4QIpqNDE7jkJ6jWsEKSxwBTVhMVFjw/9/O+cUB0ISPoa0rTUQKqo0b0BaEkBwcdrtbURj2XPkv2anTGMeX8zhQqq3Uhfx5I8znz64uM1HCdSRzYFNaTRni551LqDDJePJqk2JUOtXSWcX7mA3j5aiF3YsW8UguJDiQdZfmKGAqbRvQFWfl+8xkGGHHc5JEpNKgoXkBcMt4zjuN7PUGd1EukTeRR+IkAfUmoiyJq3CGc73ZQAQZdR8/L9wnn/FUFdORQStuPSVebg0yMjCy3/lKIkuhApejWDC8Kg7TrKkV5R9EufexaWa3AQgtP8427c3oHlM1WJ+kpe7EVITfzZniRGaJkuIWN7tG2SzgjLW/xLxDLmjI5jy5lD0ZH7+Gc6mOlY7HZDzX/vkenicDLyq0rqnMp/XmVd6UU+uQBWtTSCPR817tqO35R9GU9QfJC0DEQ3ZqpdbyvdICPB+/+9jbVM/1w+TQ3ixZtWlBhdbcL3jagNLgySDXvgWoabLasdeasenDmwH/uT9t2PVw448dDC+9C3Rh17DrzEVKzpMlLXbcnuCEqaAgXNGF+rQVFh+TbH7XVuUl5y42FFl7plGoycqca6I6KbPNUntOgZLRrihfOolSdkv+U/V8EzEyqlr/TV0wt/1NPltntqlGCjB2sptUZpLuuMLWa4gWzJaNvd3cWMH8uJS3yNcS1QgUNV7xRhUv3C1UJMjxYTV/h5nYHTkrVPCe23p+adUEgWv6tK0oiuXhXDhctKkdx2Rdx4PaOjhG9M+wAL0K7UR+4JoHhJNNwT/2bRvKHWo8M+XMyq1JsY5P7Y4S7SDuj55BqTzVE6NINEjDTaIwXmZdOc4BB8z+yI3PGED+5FxJCaYYjqqRXvuO3PL4EikMbObVpXeFenxg1P1yPbh4wE/8PNKttxlIUhRdaTZm7rIumue9Zt0RuT8xKz1FKvrPlET6/UE6TlMq10IUe/eiU7acrdx4jUnMvZzkbFToEzzkS502fnzDZBIYb/WdOLuUVutBgkUeFhPQA64HuseF59MHdx9mV9Xxszp+OcZUOwba42dR6UScDk+16QqppwG+J5+b6Al7QrkBa0XOpXNl+Ku01Wdu5DLOS7MuT4PziZlM35VEE4EPEGNsAeJlKr+BFLeJhCydXV2rBBzpi3D2ro2LoleonXofgWRS91TJ/gBCPMSK8KdsueuAl1F7BC14WpZjQzzk9seR/mBZzYeqdPLTQLruiosiw3/tN9pCc+1j9P8z9w8tKOIqv4KXM023v5DxivA+KMTGzuY853hXaZXfjcCHymuHo3d9lBwXD8+lIh0NlJVzUl/BSNpUW+ozXT7vZHwGWLzb348UW2mW345e1ZauaO0mrc6aw6EC8bBT0WoApM0RtbowIpuziwQeZSKmFFrqlJHEy+anLTamFr7aFkBWyok4WL4sX8YJvpUWy6bBimJhEA2YytLO9pGf/vHejwizJ9i0rgbjyyzyXLdw7XGO5ehEvaFIht5gOkyWJJ2/lflBo4d4MFlNKnsgJrl5+7+gbibuK0NVlZ8ef+uhlvGCnikSw2FtMG5+sJb49LBnveOcNTKy4dUelflK64136tYdBMk2Sz1IajJ4v4wUp1Uw8syjhUGuszh1Pvwwi0rgez+0ssrWS9aliPynt+1q/aC0j/r28WWumQW72Ol6QVFW13fgnfjWGVeu09M7mpcfRZsUVqzh3mh61ih2ClEZqv+orM8vxgiOhBbzgbfW34hI7WomBQhGt+W67DwL/+3jBfuns5pZ03f/mdArtZM++MewHkGtX8zoEm99HiyDoBmsTBNDn8KEVvCh+nVwytX3HajjDsvy1p3ngPyfO/do0yeVW2mkHePLrntUKeEFBG3hBaFJ7sP5yDhJLGQpc7AKPM3s5i7usqvmmDhexB2oUodtleFlCb3rj0+z2kdCO94kklscaLM4d7/xL/pnLqUDeZnP9r1wTl6fup+WH2AvtsgpeEvcitoEX7LygvYULM/L2zu4uaRNRVWVZVlRVnVjz64PG+A8G/IsPMbwVoJkjt6zUqmhJxQ2xJ0o3wUue7x5By/g6PU7awAuSlx84M5IbdCjbZ8KkLVy0pO45tTeuQ4IXp7g+QtZspqF2APNpU2m5MSpqlob5daf5mUYuWq69QsvTIbpzi/EibkKpHbwgxfskt+V50mBSb6MNFy3P+ii/Yy6E96DZ0ytqCzDBp0QYvXjkU2FXg4wpNa5rijx0sWf+d6LX5YUkGE/k80t+b+o53j6Bw5xL5oMtJj2dbqv3qp9o6Z2fg7TQ85ohjCfYy3XDXuEwgx8ZicrY3NatmIiY69p6jJbn1D+Dw8N44rg+bg8w+DTgdVJuXCYtcO5cOcjVe9UpWrReesWIsqLmPza0p0tfZwO1eKSBLqwLvVIdSmL+lMNd9V71N1p6mrWJXZSRQmE8ERe20iZekOgNj/VetqUSs8GuiEyxeq9amM4Rc2txX14Z6TkgzSizs4R3I7aKFyQ7wxpKM6/lCZl9N8/0hqv3qp+FhtTjlylltvnXAsYTHMwk1PKRoqGYLKdxBbUSb5mbAhbMv3riiFAzK1XTe/00loh1KQQVT66zXdt4QcpyCFfZLJwqmVhklzlsmx2vf57apnZ9f4sIU5Fy6ZKKJ+IiwK0DBklxv1+l8FBpCxteMkPLhvmXMe9+17TF6/T/BdJl1h3hUxhP5Cgy2scLUpL+Lgiyk2puLfZoFcfByxPnnoaR/QDibdpXZ3wNKp4sbRV1ccRlH3nvZl+R3sseM30s2HFJ5H1XwhaxLwzikKzJWNpOrZyWFhLq5mhBv2jM0avci2dfrOIG7FAs8cw/5FG/fw66MfsnmSe/MowazFTNJUIfj5iLZ1WmaZxrm3jDAQ6Pxx4wQvKQroDyMEL59hhFeHEQlaTzifwCYnowWr8IaqwY5lzBE3KGA/i5Zl/QQ+or4VVYnkGXaqXO7WKLneLvlZeY7/n3RCrX3NaRI3HClmYjtd78q+A6BV2B3p7NhGntoeKJqiclHXw7eKlnYEjBb7xnF/9a72NzTOubeS1nAwlGZGJWG942gfTG+Zxx0KUIDN7HconAeQxebDIpV/+d1dIscurakjU20ZjueWoDd5fshfzX90F6x8iGTzGnItCrHC29pG5Q/DB4fcz6HrxjsP74gEptAXLCqWHOvGTGXwyatlucgd68TaQVw2fy4Ez4kA/bspfPnPottCVlaxt3l9PXpndtIiWJMfunu+LqDBNurCTaJ/aEoZ50O/k11xKgt3/uorLocd8IkYXaOMp8GS9afv2OenASm3U1eGD54g9LJ+uiksiIhAEfouXmd55+UQxOPJdd+Unm0exTW40mWUs8vYVAM7Pj20lrXu3zwDLlLu0oSEQpWRxYAZ1PSCKzQqLuicM3v+yhk/Gi47JNa5UxuS59fdOA0hzNKHDu2quL93iui4ifea1NUSnKm2AbznFTt2D+W9rUI5mcS5MNGS+axhZq+Ty3vsT6qsTSGm7s88HbXi21lSDHvS6lgPrjm1skdLGvsxzaIaWynGO8tOnU2AelTWrVnwrCYqug/9q7zuVUwSAq7cuAdKVJEemC7/98V4qN+mE0lrvnRyaTmUwmsO6ePdueA7Zc+5L7qm0bR2ja8Uthq2FuShzBPLTjQggHi4MjlTRCHA36A4MDH4dq+7SZ9JnRjaOddjAsio+PZOma7M8HAxlDMpo1NrjnD/qOTflrQrz4ElQlVcbtNaNbBzNNG4m4JBuKxn2qsZjZkBOwxrg8MzzzUEmfzBfdAltVbaad8nznRALpYnQ1oHpE2orID3Qswz2izmjilw439FeDR9x3XdBYlYJ2l8E4ZFvz12gcb177ZasgPslWhJQfZBfO6Pw4GnYt26rqFn7d1bjKwVDtD9fSZtvBJsVRToLm0yQ/jfg+GpI+7CDkcd3JH/5NqzoJoS2+DxWDEaLlREr9k8c4PoMNm3LUKsuFt7cVzhhRRDLqnq6pS7qAvvSE6a58LmT7n9u2HQxt2FgGIJ10LkUj39tWRhTkzUQ5TBi5ilCLEdT3ntT2ytCRtmlZp72Qi0085nhOScWQfk++YupjJHRSp6ZGLK2uvdmrxfdi7ff1Ga46GXSIWYMW0pOz2sTUu6kxKHRHdVYxnPCiyBtLHqorefziuyGSffF4R3VqRAWmvyC1ExNM3kiNoU1tvClrPRlBBXszoX0OrGn4Lspb7Xk8tP9Rr90tQugprse/ZKh7TXoDU6EKcaJyiTGlZo7VzCuxhY1Wi/8Ail+ONrRF63XQDiaUjv3u0WUcba9RL0yVcPr21vq0FxzdUZ5U1O5bM6IeVaq0A7/3p7fZsobfocZd7nZu3PAFcowgqRjtekucxH/8AkKVMIwJvV+YIxF910g7RxToYMbsyM1tYLn4QyLDUKqOdZlNxNnBQBdjDLk+6kl7i/8KK4+jeqSEjuTCRMGMHJml9AspUjwfPd1QJN/I8AjnSsYqWgjjm+xr10LtF4AqR8q76oo6h4ywknGVk+z09DlxCUlhEYt4AUGxxMzAGz1iw9F4tq9Yy8Aqsv8T3X4yQlPnaSpEwF/xTee+Jv2B90mYgZZZE3R2nThiFmuFGpqzmjTz8ey7LsBKOzCS0aT6h9T8mekOfdCvHXZ5Q+0RMp7AHVJfPSKwr1D+IExz05Q4EjH0nX9oavNrvbeD1sBA2ulD9wYHp6uzdf7WNekNH+XE2zbisem4Z9kWAriWwfKZTXcNxp5fGGJJ1b1hohtRC7n3q2EL/oQZZKjOsME0Bvhhl+CSsXEXCSH81pj00vFs840czbl/Z5zi/qQK2MX1azSiq46zpEtwCT2+U06hKbud8JZGcyBe72mYaCLBaobrCR4spEX9y52nF4q689luWs3nd7sFxrTdjtNPRC/yTY55kbMh46miQT1cLxRbsI+O7HmMNuxVt/yumxHRgfyrcUaaC41s19VgV4mc6ZGaS8Tf9c6wh8nCTzNcn4Mw16tYVJQFRcolJNHdJEL89TijQB6CwZnX7V7mPS0KwtSkJJJERzD0GQxCBEmSxAMoy2Sa0wzXkxCHhpAU6KbxaaH0ZESc5zykq5tFUh4YnmjNGBhVLN4z1Py3HZ9SPBldmuF6RluBWYxUV1yz1uZO73DdkxExxf6h44wsQ1KHUC0M3XN5XpblXQ3r+C2fubFu2GpoSg+af6VVjGNM9XC9YK/BJKZKRxU9of3TQ13GZE/gzxLjA4fTsBzL6Ww9GwJpwUqqPer2yfYNWKAocQL0WbZCRDgGsK93DpgOWAK+iyktgTn7baunDsCa7pb3P2U4DfduQVJrlKYMRjCLxWQVPZH0hsX0XqVjVHn1ERZDhxnW9qF13eIDxnJPohShisWcnh1/YPsc/G7Fq+gbbGWRVIngRx8mfS34qr2V1JRrA7oNSUcPpCUL2X5T5ot8TFtZOJUuOVlxBIxKHUYZhNj8JPf2upiqV3evm+820Ih/n6A5kcUUUEr8dXmgmms9T4govSk0K2n7xZIPyHepNjMHHXd0bO3ldF1GheLQI7CJqXpCpJGtBma7uOgY9RXXf/nyF4GKZFw5NtHqmV3WBLn/cdhXd8fYg1szga3Xr+siP9suFomnvszLCJLNYzsJJ2q6MpgAKMujg1LlUxi10S+UgTFXwdTKR7/OCuqvuQxjGiL2ut5tpp7coPTmx0c/FNu6VnS+uzh46hA1vbpWrEp/YzPl4YEZ900s4zygywQWvNmnZUr1soXzVqbBNXUsF2SVyawsLzCfKcwwVBDPOTdgXU1AsaYLjuXJFYKaxkin0eiRxYaEHzecYC3q6sObdGkujbI5qzOW8s2QGqdB3vwXqKuLl9HoseOHjFlkp7eytNzIp35tNSzi8kDj5+1YsbxAum4QJApgt39JfJkbi0n0sXUdKI+u3+6ej8tmSzSnu5tGHJUGke6Kc69PbGVdbdFuwgal/4/RVBcrga6WZ7LxSXpGUnXxtnluvZOzstnSTw9Ns+Wl1RIRJFfeCiiqOxN3UQxF1INOCGSlCGzllRZzZL6n7MIxpAmXwUhh5DrPvdGxdjItOPTdN6YPOvCVl1pMXY8mbXF1cjI4NzQZsgwtceYkj2uHVRw+jnyKoIfrjSDzvx6yXeVGzGXhZBKH+KUAmuDMsGQlGS87O2VOg+wysWTe040gpM7zSv1EenLFIeAPYdVyukBp53wj8e6tHgnliAhJchJlmmmaqqpq24VRIbLtQA3TZuUCJlVmpUKElv43Q9J0MBDqxeUnD1rX8Yukm1NdaOh/T2yb3EigrhZJLUUtf02RmqZsfgNv5b0lPEOq/AkK4yulRcn+dsULy/l4Ow4BL8f6tC+VVLNNSy+Tnt4JXso7MlSCPixbMmohlSVV71atX8uxnZPPKFQjSp1bGgC8DZa8XcelHyKMO1lsInqGbz7EbASC8g3XqQg2BKBPxsYNmtSoXAuk9GprWXzftg6akPJA80TwJ99lMvy5sa4sNSojmpuYXdZ1EK1VlizNIJIrp+9tI87kPQi0XxyYROO8YxeZRYY/rr6uAY/wv4OS2dQpMSIyeB6AaViu4VOI/VHhUQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA8Hz8A/s282ssG5iSAAAAAElFTkSuQmCC";
	
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



