package com.lng.attendancecompanyservice.serviceImpl.custOnboarding;

import java.net.URI;
import java.util.Base64;
import java.util.Date;
import java.util.List;
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
import com.lng.attendancecompanyservice.entity.masters.IndustryType;
import com.lng.attendancecompanyservice.entity.masters.Login;
import com.lng.attendancecompanyservice.entity.masters.State;
import com.lng.attendancecompanyservice.entity.masters.UserRight;
import com.lng.attendancecompanyservice.entity.masters.LeaveType;
import com.lng.attendancecompanyservice.repositories.custOnboarding.CustomerRepository;
import com.lng.attendancecompanyservice.repositories.masters.BranchRepository;
import com.lng.attendancecompanyservice.repositories.masters.CountryRepository;
import com.lng.attendancecompanyservice.repositories.masters.CustLeaveRepository;
import com.lng.attendancecompanyservice.repositories.masters.IndustryTypeRepository;
import com.lng.attendancecompanyservice.repositories.masters.LeaveTypeRepository;
import com.lng.attendancecompanyservice.repositories.masters.LoginRepository;
import com.lng.attendancecompanyservice.repositories.masters.StateRepository;
import com.lng.attendancecompanyservice.repositories.masters.UserRightRepository;
import com.lng.attendancecompanyservice.service.custOnboarding.CustomerService;
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
	MailProperties mailProperties;


	ModelMapper modelMapper = new ModelMapper();

	MessageUtil messageUtil = new MessageUtil();

	Encoder Encoder = new Encoder();

	//Sms sms = new Sms();

	//Email email = new Email();


	/*
	 * @Bean public BCryptPasswordEncoder getEncoder() { return new
	 * BCryptPasswordEncoder(); }
	 */


	@Override
	@Transactional(rollbackOn={Exception.class})
	public StatusDto saveCustomer(CustomerDto customerDto) {

		StatusDto statusDto = new StatusDto();
		Login login = new Login();

		try {

			List<Customer> customerList1 = customerRepository.findCustomerByCustEmail(customerDto.getCustEmail());
			List<Customer> customerList2 = customerRepository.findCustomerByCustMobile(customerDto.getCustMobile());

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

						login = setCustomerToLogin(customer);

						if(login != null) {
							int loginId = saveLogin(login);
							if(loginId != 0) {
								List<UserRight> userRights = userRightRepository.assignDefaultModulesToDefaultCustomerAdmin(loginId);
							}
						}
						
					}
				}
				//send mail
				customerDto.setCustCode(customer.getCustCode());				
				sendMailWithoutAttachments(customerDto, login.getLoginName());
				statusDto.setCode(200);
				statusDto.setError(false);
				statusDto.setMessage("Successfully Saved");
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Customer Mobile Number or Email Already Exist");			
			}

		} catch (Exception e) {
			statusDto.setCode(400);
			statusDto.setError(true);
			statusDto.setMessage(e.getMessage());		
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
		String subject = "LNG Attendance System";
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
				"																														<p style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; font-size: 13px; line-height: 21px; margin-bottom: 5px; Margin-bottom: 5px; text-align: justify; color: #777777;\">: <a href=\"http://52.183.143.13/lngattendancesystemv1\" style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; line-height: 1.3; color: #f7931d; text-decoration: none;\" target = \"_blank\">https://www.lngattendancesystem/SignIn </a> </p>\r\n" + 
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

		try {
			String custCode = customerRepository.generateCustCode();
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


	// Convert byteArray to image and storing in file system
	/*
	 * public void byteToImage() { String path = "F:\\custLogo";
	 * ByteArrayInputStream bis = new ByteArrayInputStream(); BufferedImage bImage2
	 * = ImageIO.read(bis); ImageIO.write(bImage2, "jpg", new File("output.jpg") );
	 * }
	 */
	

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
			createBranchFaceListId(branch.getBrCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return branch.getCustomer().getCustId();
	}

	//Set Customer to Login 
	private Login setCustomerToLogin(Customer customer){
		Login login = new Login();
		try {
			
			login.setRefCustId(customer.getCustId());
			login.setLoginName("admin@"+customer.getCustCode());
			login.setLoginMobile(customer.getCustMobile());
			login.setLoginIsActive(true);
			login.setLoginCreatedDate(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return login;
	}

	//save to Login Table
	private int saveLogin(Login login){
		//CustomerDto customerDto = new CustomerDto();
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
			List<Customer> customerDtoList = customerRepository.findAllCustomerByCustIsActive(true);

			customerListResponse.setDataList(customerDtoList.stream().map(customer -> convertToCustomerDtoTwo(customer)).collect(Collectors.toList()));

			if(customerListResponse != null && customerListResponse.getDataList() != null) {
				customerListResponse.status = new Status(false, 2000, "Success");
			}else {
				customerListResponse.status = new Status(true, 4000, "Not Found");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			customerListResponse.status = new Status(true, 5000, "Something went wrong");
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
				customerResponse.status = new Status(true, 4000, "Not found");
			}
		} catch (Exception e) {
			customerResponse.status = new Status(true, 5000, "Something went wrong");
		}
		return customerResponse;
	}


	//Updates the Customer details
	@Override
	public CustomerResponse updateCustomerByCustomerId(CustomerDto customerDto) {
		CustomerResponse customerResponse = new CustomerResponse();
		try {
			Customer customer = customerRepository.findCustomerByCustId(customerDto.getCustId());
			Country country = countryRepository.findCountryByCountryId(customerDto.getRefCountryId());
			State state = stateRepository.findByStateId(customerDto.getRefStateId());
			IndustryType industryType = industryTypeRepository.findIndustryTypeByIndustryId(customerDto.getRefIndustryTypeId());
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
				customerResponse.status = new Status(false, 2000, "Successfully Updated");
			}	else {
				customerResponse.status = new Status(false, 4000, "Customer Not Found");
			}

		} catch (Exception e) {
			customerResponse.status = new Status(false, 5000, "Something went wrong");
		}
		return customerResponse;
	}

	//Deletes the customer which means the custIsActive will set to zero(0)
	@Override
	public CustomerResponse deleteCustomerByCustomerId(int custId) {
		CustomerResponse customerResponse = new CustomerResponse();

		Customer customer = customerRepository.findCustomerByCustId(custId);
		try {
			if(customer != null) {
				customer.setCustIsActive(false);
				customerRepository.save(customer);
				customerResponse.status = new Status(false, 200, "Successfully Deleted");
			} else {
				customerResponse.status = new Status(true, 400, "Customer Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace(); 
			customerResponse.status = new Status(true, 5000, "Something went wrong");
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
					customerListResponse.status = new Status(false, 2000, "Success");
				}else {
					customerListResponse.status = new Status(true, 4000, "Not Found");
				}
			}else {
				customerListResponse.status = new Status(true, 4000, "Data too long or too less");
			}
		}
		catch (Exception e) {

			customerListResponse.status = new Status(true, 5000, "Something went wrong");
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

	String Logo = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgICAgMCAgIDAwMDBAYEBAQEBAgGBgUGCQgKCgkICQkKDA8MCgsOCwkJDRENDg8QEBEQCgwSExIQEw8QEBD/2wBDAQMDAwQDBAgEBAgQCwkLEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBD/wAARCAK7BQADASIAAhEBAxEB/8QAHQABAAICAwEBAAAAAAAAAAAAAAgJAQcDBQYCBP/EAGIQAAEDAwIEAgQIBwgNCQQLAAABAgMEBQYHEQgSITFBUQkTImEUGDJWcYGW0xUjOEJ1kbIzQ1JiobGz0hYXJDQ2N1NydpKVtMEZVFdjdIKTtdEmNXOUJTlEZoWio8Ph8PH/xAAcAQEBAAIDAQEAAAAAAAAAAAAAAQYHAgQIBQP/xAA/EQEAAQMCBAMDCgQFAwUAAAAAAQIDBAYRBTFBURIhYQcywRMiUmJxgZGhsfAUI0LRFXLC4fEWJNIXM5KTov/aAAwDAQACEQMRAD8AtQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA4nTIxquc5GtRN1VTL5WxtV8jka1qbqq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AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//Z";

	public CustomerDtoTwo convertToCustomerDtoTwo(Customer customer) {
		CustomerDtoTwo customerDtoTwo = modelMapper.map(customer, CustomerDtoTwo.class);
		customerDtoTwo.setRefCountryId(customer.getCountry().getCountryId());
		customerDtoTwo.setRefStateId(customer.getState().getStateId());
		customerDtoTwo.setRefIndustryTypeId(customer.getIndustryType().getIndustryId());
		customerDtoTwo.setCountryName(customer.getCountry().getCountryName());
		customerDtoTwo.setStateName(customer.getState().getStateName());
		customerDtoTwo.setIndustryName(customer.getIndustryType().getIndustryName());
		return customerDtoTwo;
	}

	@Override
	public void createBranchFaceListId(String branchCode) throws Exception {

		HttpClient httpclient = HttpClients.createDefault();
		
		try
		{
			String brCode = branchCode.toLowerCase();
			
			URIBuilder builder = new URIBuilder("https://centralindia.api.cognitive.microsoft.com/face/v1.0/facelists/"+brCode);


			URI uri = builder.build();
			HttpPut request = new HttpPut(uri);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", "935ac35bce0149d8bf2818b936e25e1c");

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
}



