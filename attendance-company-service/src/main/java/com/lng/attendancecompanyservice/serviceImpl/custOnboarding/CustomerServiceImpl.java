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
import com.lng.attendancecompanyservice.entity.masters.LeaveType;
import com.lng.attendancecompanyservice.entity.masters.Login;
import com.lng.attendancecompanyservice.entity.masters.LoginDataRight;
import com.lng.attendancecompanyservice.entity.masters.State;
import com.lng.attendancecompanyservice.entity.masters.UserRight;
import com.lng.attendancecompanyservice.repositories.custOnboarding.CustomerRepository;
import com.lng.attendancecompanyservice.repositories.masters.BranchRepository;
import com.lng.attendancecompanyservice.repositories.masters.CountryRepository;
import com.lng.attendancecompanyservice.repositories.masters.CustLeaveRepository;
import com.lng.attendancecompanyservice.repositories.masters.IndustryTypeRepository;
import com.lng.attendancecompanyservice.repositories.masters.LeaveTypeRepository;
import com.lng.attendancecompanyservice.repositories.masters.LoginDataRightRepository;
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
	LoginDataRightRepository loginDataRightRepository;

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
			List<Customer> customerList3 = customerRepository.findCustomerByCustName(customerDto.getCustName());

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

						login = setCustomerToLogin(customer);

						if(login != null) {
							int loginId = saveLogin(login);
							if(loginId != 0) {
								List<UserRight> userRights = userRightRepository.assignDefaultModulesToDefaultCustomerAdmin(loginId);
							}
						}

						// saves to LoginDataRight table
						try {
							if(login != null) {
								Login login1 = loginRepository.findByRefCustId(branch.getCustomer().getCustId());
								LoginDataRight loginDataRight = new LoginDataRight();
								loginDataRight.setBranch(branch);
								loginDataRight.setLogin(login1);
								loginDataRightRepository.save(loginDataRight);
							}

						}catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
				//send mail
				customerDto.setCustCode(customer.getCustCode());				
				sendMailWithoutAttachments(customerDto, login.getLoginName());
				statusDto.setCode(200);
				statusDto.setError(false);
				statusDto.setMessage("successfully created");
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Customer mobile number or email already exist");			
			}

		} catch (Exception e) {
			statusDto.setCode(400);
			statusDto.setError(true);
			statusDto.setMessage("Oops..! Something went wrong..");		
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
			synchronized (this) {
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
			List<Customer> customerDtoList = customerRepository.findAllCustomerByCustIsActive(true);

			customerListResponse.setDataList(customerDtoList.stream().map(customer -> convertToCustomerDtoTwo(customer)).collect(Collectors.toList()));

			if(customerListResponse != null && customerListResponse.getDataList() != null) {
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
				customerResponse.status = new Status(false, 200, "successfully updated");
			}	else {
				customerResponse.status = new Status(false, 400, "Customer not found");
			}

		} catch (Exception e) {
			customerResponse.status = new Status(true, 500, "Oops..! Something went wrong..");
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
				customerResponse.status = new Status(false, 200, "successfully deleted");
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

	String Logo = "iVBORw0KGgoAAAANSUhEUgAAAdYAAAHoCAYAAAD0as6HAAAABGdBTUEAALGOfPtRkwAAACBjSFJNAACHDwAAjA8AAP1SAACBQAAAfXkAAOmLAAA85QAAGcxzPIV3AAAACXBIWXMAABJ0AAASdAHeZh94AAAAIXRFWHRDcmVhdGlvbiBUaW1lADIwMTk6MDQ6MjQgMTA6Mjc6NTbFaTbFAABYEElEQVR4Xu3dB3hUVeIF8DPpvUxC70gHqdKsWBCR3rvuqn8bghRFBAVFQERQQEXXdd2VGnpvInalSi/SeyeT3iaTmf+7j8suKpkkZObNvDfn931+eo+riyGZ88otJocCROQVslf/DlN0CILvqywTItIbP/lnIvKw7A1HkTRgMSzd5sG6+YxMiUhvWKxEXiDnuxNI6rsQjhwbHOlWWLoo5brjvPy7RKQnLFYiD8v58RQsPebDkZUrE8Cemg1Lxzmw7r4oEyLSCxYrkQeJR76W7vPgyPxfqd5gT85CYofZyD1wWSZEpAcsViIPsW4/pz7yFY9+8+NIzETi47OQ+/tVmRCRt2OxEnmAeMRr6TRXfeRbEPvVDFiUcrUdS5QJEXkzFiuRxnL3XYKl/Wz1UW9h5V1KU+9cbaeSZEJE3orFSqQh8Ug3UZSqJVMmhZd3LgWJj30F2+lkmRCRN2KxEmlEPMoVj3TFo93blXcmWf135J1PlQkReRsWK5EGxCNc8ShXPNItLttJC661c82/i4hcj8VK5Gbi0a14hCse5bpK3pFrSGw3G3nFuPslIvdgsRK5kXhkqz66PeP696K2Q1dgUe5c7YlFf19LRO7DYiVyE/GoVjyyFY9u3SV3/2UkdpxTpBnGROReLFYiNxCPaNVHtUeuycR9cnddgKVz4dbEEpH7sViJXEw8mhWPaMWjWq1Yt51DYpd5sKfnyISIPIXFSuRC6v6+Heeoj2i1lrv5DJK6z4c9M/8tEonI/VisRC5iT8tRH8mKR7OeIk7KSeqVAEf2Xzf1JyJtsFiJXMCeYYWl6zz1kayn5Ww6AUvv62e7EpH2WKxExSQevSZ1mw/rL6dl4nk5Xx+FZcBiOHLzZEJEWmGxEhWDeOQqHr3m/HhSJt4jZ/XvSP77UjhsLFciLbFYiW6Tw2pDUt9F6qNXb5W19ACSn10BR55dJkTkbixWotsgHrEmDViE7PVHZOK9shL2IvnFlXDYWa5EWmCxEhWReLSa9NRSZK86LBPvlzV7N1IGr4HD4ZAJEbkLi5WoCMRdX/JzK5C95IBM9CPzy9+Q+sp6liuRm7FYiQpJLdUXVyFr/l6Z6E/Gp1uR+vrXckRE7sBiJSoEcZeX+vJaZM3aJRP9ypixGaljN8kREbkai5WoEMQj1Ix/7ZAj/Ut//yekTvhejojIlVisRAVIGfW1+gjVaNKVYk1TCpaIXIvFSuRE2tvfImPar3JkPGljNyF9hnH/+4g8gcVKlI+0d39A2ns/ypFxpb6+EekzjXdHTuQpLFaiW0ib8hPS3vlOjgxOTMx6dT0yvjTOO2QiT2KxEv1J+kebkTbGx2bNKuUqNpDInK3/Wc9EnsZiJbpJxufbkDrSR9d52h3qOt1MHa/TJfIGLFYiKeM/O5EydJ169+az8sTOUsuRpcOdpYi8BYuVSJE5dzdSXlrl26V6g00p16eWImvV7zIgoqJgsZLPy1y4D8nPr1QfhdJ14vSeZHF6zzrvP72HyNuwWMmnZa04iORnlqmPQOmPHNY8JPVbiOxvjsmEiAqDxUo+K3vtYSQ/uUR99Em35si2Ian3AuT8eFImRFQQFiv5pOyNR5W7sUXqXRk558jMhaX7fOT8ekYmROQMi5V8Ts53J5S7sIVw5NhkQgVxpFth6ToX1q1nZUJE+WGxkk/J+ekULD0T4MjKlQkVliM1B5bOSrnuvCATIroVFiv5DOuWs+ojTUeGVSZUVPaUbFg6zkHuvksyIaI/Y7GST7DuOK/ebTnScmRCt8tuyURiu9nIPXhFJkR0MxYrGV7unovqXZY9NVsmVFz2axlKuc6C7cg1mRDRDSxWMrTcA5dxrf1s2JOzZEKuYr+cjsTHlXI9YZEJEQksVjKs3N+vqh/8jsRMmZCr5V1IvV6up5NlQkQsVjIk27FEWNrNgv1qhkzIXfLOJCPxsa+QdzZFJkS+jcVKhmM7laTeReVdTJMJuVveafk1V+5giXwdi5UMRdw1JbZVPuDP8e5Ja7bjiUh6YrEcEfkuFisZRt751OuPJJW7J9KeX3QIoiY/JkdEvovFSoaQdykN18Tyj5OcoeoJflEhMK/sj6DGZWVC5LtYrKR7eVczkNh+NvK4ptIjTJHBMC/vh6Cm5WVC5NtYrKRrYhcgS4fZsHEXII8whQfBvLgPglpUkAkRsVhJt8SmD4kd5iB3L/et9QRTWKBaqsH3VZYJEQksVtIle9r1k1Zyd/GkFU8whQQgNqEXgh+oIhMiuoHFSrpjzxBng86Ddds5mZCWTMFKqc7vhZBHqsmEiG7GYiVdEeeoJnVXSvWX0zIhLZmC/BE7pwdC2lSXCRH9GYuVdMORnQtLz/nI+eGUTEhTAX6I+aobQtrVlAER3QqLlXTBYbUhqd9C5Gw6IRPSlFKqsV92RWinOjIgovywWMnrOWx5SHpyMbLXHZUJacpfuVP9RyeEdq8nAyJyhsVKXs2RZ0fS08uQveJ3mZCm/EyImdkBYX0ayICICsJiJa/lsNuR/OxyZC/aLxPSlMmE6I/bI2xAIxkQUWGwWMkrORwOJL+4Clnz98qENCVK9cO2CP9bExkQUWGxWMnriFJNGbIWWbN2yYS0FjW5DcKfbSZHRFQULFbyOqmvrkfmP7fLEWktamJrRAxsIUdEVFQsVvIqKaM3ImPmVjkirUW+/TAihtwjR0R0O1is5DVSx32LjA9/kSPSWuQbrRD56n1yRES3i8VKXiFt0g9In/SjHJHWIkbej8hRreSIiIqDxUoel67cpaaN+06OSGvhw+5B1JiH5IiIisvkEFMwiTwk/ePNSB2xQY5IaxGDWiLqvTZyRESuwDtW8piMf25H6mtfyxFpLey5Zoic9KgcEZGr8I6VPCLjPzuRMnCVWLQqE9JS+NN3IWpGO5hMJpkQkavwjpU0lzl/D1JeYql6SugTjRA1/XGWKpGbsFhJU5kL9yH52RWAnaXqCaF9G6ib6pv8+KNP5C786SLNZK04iORnlgF5dpmQlkJ73qke/8ZSJXIv/oSRJrLXHkbyk0sAG0vVE0K71kXMF51h8uePPJG78aeM3C77m2NI6r8IDmueTEhLwe1rIebfXWEK8JcJEbkTi5XcKuf7E0jqtQCObJtMSEshbavDPKc7TIEsVSKtsFjJbXJ+Pg1LjwQ4snJlQloKfqQaYuf2hCkoQCZEpAUWK7mFdetZWLrNgyPDKhPSUlCrqjAv6AVTSKBMiEgrLFZyOevOC7B0ngtHWo5MSEvB91eGeXFvmEJZqkSewGIll8rdcxGWDrNhT8mWCWkpsGVFxC7uA7+wIJkQkdZYrOQyuQcuI7HDHNiTsmRCWgpqVh5xy/rCLyJYJkTkCSxWconcw1eR2F65U72WIRPSUmDjsjAv7we/qBCZEJGnsFip2GzHE2Fpp5Tq5XSZkJYCGpRB3Mr+8IsJlQkReRKLlYrFdioJiW1nIe9CqkxIS4H1SiF+9QD4mcNkQkSexmKl25Z3NuV6qZ5LkQlpKaB2CZhFqcaxVIm8CYuVbou4Q018XCnV00kyIS3514hH3Jon4F8yQibkzE8/HEVWFtdUkzZYrFRkeZfTca3dLPXdKmkvoKoZ8aJUS0fKhJxZs2ofXh++GK8NXYzsbO4CRu7HYqUiybuWgUSlVPMOX5MJacm/Uizi1j0J/3JRMiFnNqzbj3ffXg273YEd205h1CtLYLVy32pyLxYrFZpYn2ppPxu2g1dkQlryrxiDuPXKnWqFaJmQM5s2HsT4MddL9YYtv57Am68tgy2XJy2R+7BYqVDETkqJHecgd+8lmZCWxB1q3NonEKDcsVLBvt/0O94evRJ5tzhUX7xvHfP6cth4NjC5CYuVCmRPy1H3/s397bxMSEviXapaqlXNMiFnRHGOHbXCaXF+/+1hjHtzxS2Ll6i4WKzklD3Dqp5SI06rIe35lYyAWZRq9XiZkDObfz6GN15bitxCPOr9ZsMhTJTvX4lcicVK+RLnqIrzVK0/n5YJackvPhxxawYgsFYJmZAz27acxKhXlVK1Fv796brV+/He+LVwOFiu5DosVrolR44Nlt5KqX5/QiakJbGTUtxqpVTrlpIJOfPb9lMYOWwxcpTv26JatXwPpr73NcuVXIbFSn/hsNqQ1HcBcjYelwlpyS86BOaV/RFYv7RMyJndO89gxJBFxVqjunThb5gxdZMcERUPi5X+wGHLQ9KTi5G97qhMSEvidBpRqkGNy8qEnNm35xxeeXkhsrKKv/HDgnnb8Ml0lisVH4uV/suRZ0fS08uQveJ3mZCWTBFBMC/ri6Cm5WVCzhzYfwHDBy1AZobrtiqc+9VW/HPmD3JEdHtYrKRy2O1IeX4FshftlwlpyRSulOoSpVRbVpQJOXP490sYNjAB6ek5MnGdf3/xC/6j/EF0u1ispE7aSBm4Gplz98iEtGQKC4R5cR8E31dZJuTMsSOXMeSF+UhLy5aJ632u3LXO/WqLHBEVDYvVx6mlOnQtMr/aKRPSkikkALEJvRD8QBWZkDPHj13BYKVUU1KyZOI+n0z/Fovmb5cjosJjsfq41Nc2IPNzfnh4gilYKdX5vRDySDWZkDOnTl7Dy0qpJidlysT9pk3ZqM4YJioKFqsPS33zG2R8zMddnmAK8kfsnB4IaVNdJuTM2dMWDH5+HiyJGTLRhljaOvW9DVi9fLdMiArGYvVRqe98h/SpP8sRaSrADzFfdUNIu5oyIGfOn0tSS/Xa1XSZaEuU66Tx67BhzT6ZEDnHYvVBaZN+QPq7XFLgEUqpxn7ZFaGd6siAnLl4IQWDnpuHy5dTZeIZYj/hd8auxsYNB2RClD8Wq49Jn/YL0sZ9J0ekKX/lTvUfnRDavZ4MyJnLl1KVUp2LSxdTZOJZolzHvbFKPZKOyBkWqw9J/2QLUkdtlCPSlJ8JMTM7IKxPAxmQM1evpKmPfy+cT5aJdxDHzIkj6X7+kTuTUf5YrD4i45/bkTpigxyRpkwmRH/cHmEDGsmAnEm8lq6W6tkzFpl4F3Ek3RsjlmHrZh5QQbfGYvUBGV/tVNeqqrMwSFuiVD9si/C/NZEBOZNkyVCX1Jw+lSgT72S12tTTdLZvPSkTov9hsRpc5vw9SBm4CuBhzh4RNbkNwp9tJkfkTEpyplqqJ45flYl3E0fUiXIVp+sQ3YzFamBZi/cj+bkVLFUPiZrYGhEDW8gROZOamqWW6rGjV2SiD+JUHXG6zv6952VCxGI1rKyVh5D89DLAZpcJaSny7YcRMeQeOSJn0tOyMXRgAo4cviwTfRGn6wx7KQEHD1yQCfk6FqsBZa87guQnFsORmycT0lLkG60Q+ep9ckTOZGTkYNigBBw6cFEm+iRO2Rn20gLl4uCSTMiXsVgNJvubY0jqtxAOK0vVEyJG3o/IUa3kiJzJyrLilcELsH+vMe70UlOyMOSFBPWgAPJtLFYDyfnhJJJ6L4Aj2yYT0lL40HsQNeYhOSJnsrNy8erLi7Bn1zmZGEOynIAlDgwg38ViNYicn0/D0n0+HJm5MiEtRQxqiegJreWInMnOzsVrwxZj547TMjEWcVCAug73tHeuwyX3Y7EagHXrWVi6zYMjwyoT0lLYc80QOelROSJnxPrP0a8uNfz6T3FggDfuHEXaYLHqnHXnBVg6z4UjLUcmpKXwp+9C9AdtYTKZZEL5ETsWiVLd/MtxmRibODhAPUDgkmcPECDtsVh1LHfvJVg6zoE9JVsmpKXQJxohavrjLNVCsCmlOvb15fjlp2My8Q0XLyTjpWfn4sqVNJmQL2Cx6lTuwStIbD8bdkumTEhLoX0bqJvqm/z4I1QQm82Ot99Yge+/PSwT33LjPFmxBzL5Bn4q6JDtyDUktpsF+7UMmZCWQnveqR7/xlItmDgNZvzYldi00bePWjtzKlGdLSz2Qibj4yeDzthOWJD4uFKql3n16wkhnesg5ovOMPnzR6cg4vzSd8etwdfrDsrEt4k9kIcMTEBKSpZMyKj46aAjttPJaqnmXeBkCE8Ibl8LsV91gynAXyaUH4fDgffGr8XaVftkQsLRw5cx5MX5SEvjvAgjY7HqRN7ZFCQ+9hXyznD6vieEtK0O85zuMAWyVAsiSnXqpA1YtXyPTOhmhw9dUrc/FNs5kjGxWHUg72La9TvV00kyIS0FP1INsXN7whQUIBNyZtqUb7B00U45ols5sO+8up2j2NaRjIfF6uXyrqSrE5Vsx7374GejCmpVFeYFvWAKCZQJOfPRB5uwaP52OSJnxHaOYltHsb0jGQuL1YvZEzNhaT8btt/1cfCz0QTdWwnmRb1hCmWpFsanH3+H+XO2yhEVhtjWceTwxeqh6WQcLFYvZU/KwjWlVHP36/OMSr0LbFkR5qV94RceJBNy5ovPfsTsLzfLERXFti0n8caIperOVGQMLFYvJHZSSuw4B7Y9+j6jUq+CmpVH3DKlVCOCZULO/OeLX/Dl5z/LEd0OsSPVmJHL1R2qSP9YrF7Gnp6DxC5zkfvbeZmQlgIbl4V5eT/4RYXIhJyZ89UWfD7zBzmi4vjhu8PqDlVipyrSNxarF7FnWGHpOg+5W87KhLQU0KAM4lb2h19MqEzImflzt2Lm9G/liFxB7FA14a1V6uYapF8sVi/hyMqFpUcCrD8b84xKbxdYrxTiVw+AnzlMJuTMooTt+PiDTXJErrRh7QFMemcty1XHWKxewJFjg6XPQli/PyET0lJA7RIwi1KNY6kWxvLFOzHt/Y1w8HPfbVav2IMp765XN9sg/WGxepgjNw+W/guR8/VRmZCW/GvEI27NE/AvGSETcmbl8t2YMmkDS1UDy5fswrTJG+WI9ITF6kEOWx6SnlyCnDVHZEJaCqhqRrwo1dKRMiFn1q3Zj8nj1/ERpYYWLdiBj6fxkbvesFg9xJFnR/Izy5G9nCd/eIJ/pVjErXsS/uWiZELObFx3ABPGclKNJ8ybtRWff/K9HJEesFg9wGG3I+WFlchayJM/PMG/Ygzi1it3qhWiZULObNp4COPGsFQ96T//+pVrhXWExaoxMRkhZdAaZM7ZLRPSkrhDjVv7BAKUO1Yq2I/fH8Hbo1eoB5aTZ6m7W/2bu1vpAYtVQ2qpDl2LzH//JhPSkniXqpZqVbNMyJmffzyKN19bxg0LvMinH32nrh8m78Zi1VDayK+R+TlP/vAEv5IRMItSrR4vE3Jmy6/H8caIZdy/1guJ9cNLF/Li3JuxWDWSOuYbpH/Exzie4Bcfjrg1AxBYq4RMyJntW0/i9eFLYLXyxBVvJJY6TX1vA1Yu3SUT8jYsVg2kjv8O6VM48cATxE5KcauVUq1bSibkjDjG7LWhPMbM24lynTxxPdas4gRIb8RidbO0939C+kRuUu4JftEhMK/sj8D6pWVCzuzdfRavDlmE7GwevK0HYpb2u2+vVpdCkXdhsbpR+rRfkDaWi7s9QZxOI0o1qHFZmZAz+/eex/BBC5GVaZUJ6YEoV7EU6tuNh2RC3oDF6ibpM7cidRS3I/MEU0QQzMv6IqhpeZmQM4cOXMSwQQuQkZEjE9ITsRTqrdEr1KVR5B1YrG6Q8eUOpL66Xo5IS6ZwpVSXKKXasqJMyJnDv1/C0JcSkJ6WLRPSI7Ek6s2Ry7D552MyIU9isbpY5uxdSBm85vrsAtKUKSwQ5sV9EHxfZZmQM8eOXsHQFxOQmpIlE9KzXGseRr26FNu2nJQJeQqL1YUy5+9F8ourAG79pjlTSABiE3oh+IEqMiFnTh6/ipdfmI/k5EyZkBGI2dwjhy1WZ3eT57BYXSRryQEkP7cc4NZvmjMFK6U6vxdCHqkmE3Lm9KlEDHp+HpIsGTIhIxGzusXsbjHLmzyDxeoCWat+R/JTSwFu/aY5U6A/YmZ1R0ib6jIhZ86esWCwUqqWRJaqkYnZ3a8MXogD+y/IhLTEYi2m7HVHkDxgkXpgOWkswE8p1W4I7VBLBuTMhfPJGPzcPFy9kiYTMrL09BwMHZiAw4cuyoS0wmIthuxvjiGp30I4rCxVzSmlGvtlV4R2qiMDcubSxRQMUkr18uVUmZAvELO9h7yYgGNHLsuEtMBivU05P55EUu8FcGRz6zfN+St3qv/ohNDu9WRAzlxR7lBFqV68kCwT8iUpKVl4WSlXMWGNtMFivQ05v56Bpft8ODK59Zvm/EyImdkBYX0ayICcuXY1DYOfm4vz55JkQr5ITFQb/MJ8nDmdKBNyJxZrEVm3nYOl61w40rn1m+ZMJkTPaIewAY1kQM5YxIfp8+LD1CIT8mWJ19LVd+znzvIiy91YrEVg3XkBlk5z4Ejl1m+aE6X6YVuEP3WXDMiZ5KRM9UP01MlrMiG6/lpAzAq/eCFFJuQOLNZCyt13CZaOc2BP4dZvnhA1uQ3Cn20mR+TM9Xdq83GC79ToFsRENlGuVziRzW1YrIWQe+gKEtvPht3CXWo8IWpCa0QMbCFH5EyaOgt0Po4e5ixQyp945y7KVbyDJ9djsRbAduQaEh+fBftVLqj3hMi3H0bE0HvkiJwRSyvE3r+HD12SCVH+xLt3MaFJvIsn12KxOmE7YbleqpfTZUJaihzdCpGv3idH5Eym2Gnn5QU4eIA77VDhnTpxDUOUck3hntEuxWLNh+10slqqeRf4HsITIkberxYrFSwr6/r2dXt3n5cJUeGJU47EJhLiNQK5Bov1FvLOpcAiSvUMF9R7QvjQexA15iE5ImfEhuuvDV2M3TvPyISo6NRzeZVyzUjnigdXYLH+Sd7FNCS2nQXbSa7984SIQS0RPaG1HJEzN44I27HtlEyIbp94jTB8cIL6WoGKh8V6k7wr6Uhsr5Tqce5O4glhzzVD5KRH5YicsVptGPXqEh5qTS4lXieMGLII2VncVa44WKySPTETlvazYTvEtX+eEPZUE0R/0BYmk0kmlB9bbh7eHLkcm38+LhMi1xGHpL82bLH6moFuD4tVYU/OQmKH2cjdz7V/nhD6RCN1q0KWasFsNjvGjFqBn74/IhMi19u+9SRGv7pUfTJCRefzxWpPzVZ3VMrdzTMLPSG0T311U32TH6/xCpKXZ8e4N1fg+02/y4TIfTb/clx9MiKekFDR+PSnmT09B4ld5sG6g8sUPCGkRz3EfN6ZpVoIdrsD48euwjcbDsmEyP3Ek5Gxo1eoT0qo8Hz2E82eaUVS9/nI3cxlCp4Q0rkOYv/VBSZ/lmpBRKm+O241Nqw9IBMi7Xz3ze94Z8wq9YkJFY5Pfqo5snJh6TEfOT9ymYInBLevhdivusEU4C8Tyo/D4cD7E9djzcp9MiHS3sb1B5SLuzXqRR4VzOeK1ZFjg6XPQli/4zIFTwhpWx3mOd1hCmSpFkSU6tT3vsaKpbtkQuQ5a1ftw+SJ69TvS3LOp4rVkZsHy4DFyPn6qExIS8GPVEPs3J4wBQXIhJz56INNWLrwNzki8ryVS3erF3ssV+d8plgdtjwk/30pclZzRqUnBLWqCvOCXjCFBMqEnPlk+iYkzN0mR0TeQ1zsiYs+yp9PFKsjz47kZ5Yjayknf3hC0L2VYF7UG6ZQlmph/OOT7zH3q61yROR9xEXfpx99J0f0Z4YvVofdjpQXViJrISd/eEJgy4owL+0Lv/AgmZAz//rHT/jqX7/KEZH3mv3vzfjisx/liG5m6GIV7wFSBq9B5pzdMiEtBTUrj7hlSqlGBMuEnJn15a9qsRLpxZef/6x+39IfGbZY1VIdvg6ZX3LyhycENi4L8/J+8IsKkQk5M/erLfjs4+/liEg/xPctX138kWGLNfX1r5H5GSd/eEJAgzKIW9kffjGhMiFnFs3fjk+mfytHRPojJtstStguR2TIYk0duwkZMzbLEWkpsG4pxK1SStUcJhNyRsywnDZloxwR6de09zdi+eKdcuTbDFesaRO+R/r7fE/lCQG1S8C8ZgD848NlQs4sX7oLU9/bAC4JJCMQ38dTJm3A6uWc02KoYk1TClUUK2nPv0Y84tY8Af+SETIhZ9as3IMpE9ezVMlQxJaHk8avw4Y1vr0KwzDFmv7RZqSN5aJlTwioaka8KNXSkTIhZ8SHzrvj1nLfVTIk9SSmt9Zg00bfPYnJEMWa/tlWpI78Wo5IS/6VYhG37kn4l4uSCTmzaeNB9UOHpUpGJk7CeXv0Cvzw3WGZ+BbdF2vGlzuQOnz99Qf8pCn/ijGIW6/cqVaIlgk5Iw4of2vUSh6/RT5BnOE6ZuRy/PKT7+3NrutiFRs/iA0gWKraE3eocWufQIByx0oF+/H7Ixjz+nKWKvmU3Nw8jH51GbZuPiET36DbYs1csA/JL6wE+EhNc+JdqlqqVc0yIWc2/3wMb45cpl7BE/kaq9WG14cvwW/bfef8a10Wa9aSA0j+v2UAr/4151ciHGZRqtXjZULOiCv1119ZilxrnkyIfE92di5GDFmE3TvPyMTYdFesWat+R/JTSwFe/WvOLz5cvVMNrFVCJuSMuEIXV+riip3I12Vl5eKVlxdi/97zMjEuXRVr9oajSH5isXpgOWlL7KQUt3qAurMSFWzXb2fUK3RxpU5E12VmWDFs0AL8fvCiTIxJN8Wa8+1xJPVdCEcOr/615hcdAvPK/gisX1om5Mze3efUK3NxhU5Ef5Selo0hAxNw5PAlmRiPLoo158eTsPRMgIMfVJoTp9OIUg1qXFYm5MyB/RfwyuAFyMq0yoSI/iw1JQtDXkzA8WNXZGIsXl+sOb+egaX7fDgyWapaM0UEwbysL4KalpcJOSMebw1VrsTT03NkQkT5SU7KxMsvzMfpU4kyMQ6vLlbr9nOwdJ0LRzqv/rVmCldKdYlSqi0ryoScOXr48vVSTcuWCREVxJKYgcHPz8O5s0kyMQavLVbrrguwdJwDRyqv/rVmCguEeXEfBN9XWSbkjHic9fKL85GSkiUTIiqsq1fSMOjZubhwPlkm+ueVxZq77xIsHebAnsKrf62ZQgIQm9ALwQ9UkQk5c+rENfVxlnisRUS35/LlVAx6bh4uX0qVib55XbHmHrqCxPazYbfwg0prpmClVOf3Qsgj1WRCzpw9bcGg5+epj7OIqHguXkhWHwuLO1i986pitR1LhKWdUqpX+UGlNVOgP2JmdUdIm+oyIWfEO6FBz81F4rV0mRBRcZ09Y1HL1ZKo758rrylW2wkLEtt+hbxL+r9a0Z0AP6VUuyG0Qy0ZkDM3rqyvGODKmsjbiFnCg5/X9+sVryhW2+lkJD4+C3nnjfF8XVeUUo39sitCO9WRATkj3gG99Ow8XLqYIhMicrUTx6/qekKgx4tVlKlFlOoZ48wI0w1/5U71H50Q2r2eDMgZcYcqHv+KO1Yici+xhG3YS/pcwubRYhWPfa+1mwXbSYtMSDN+JsTM7ICwPg1kQM6Id6lGXG9H5M0OHbiIoS8tQEaGvpZdeqxY866kI1Ep1bwj12RCmjGZED2jHcIGNJIBOWOxZKizf88YcIcYIm93YN/569uEZulnoyCPFKs9MROW9rNhO3RVJqQZUaoftkX4U3fJgJxJSc7EkBfmq+tVicgz9uw6h9eGLtbNaVGaF6s9OQuJHecgd/9lmZCWoia3QfizzeSInElNzVI3fzh21JgbhRPpyY5tpzBy2GLk6OCEM02L1Z6aDUunucjddUEmpKWoCa0RMbCFHJEzaeJoqxfF0Va8ACTyFtu2nMQbI5Yi18vP5NasWO3pOUjsMk/dWJ+0F/n2w4gYeo8ckTMZyvfqsJeMfxgzkR798tMxjH19OWw2u0y8jybFas+0Iqn7fORuPiMT0lLk6FaIfPU+OSJnMpXv1eGDE9QJE0Tknb7/9jDGvbkCeXneWa5uL1ZHdi6SeiUg58dTMiEtRYy4Ty1WKlh2Vi5GDFmEvbtZqkTe7psNhzB+7CrY7Q6ZeA+3FqvDakNS30XI2XRCJqSl8KH3IOqth+WInBGzDUcMXYSdO07LhIi83Ya1BzDpnbVwOLyrXN1WrI7cPFj6L0b2+iMyIS1FDGqJ6Amt5YicEbMMXx++RJ11SET6snrFHkydtMGrytUtxeqw5SH570uRs/p3mZCWwp5rhshJj8oROSNmF4pZhls386kKkV4tXbQT06Z8I0ee5/JideTZkfzsCmQtPSAT0lLYU00Q/UFbmEwmmVB+bEqpvjlymTrLkIwjOjpU/hX5kkXzt+OT6ZvkyLNcWqwOu1KqL65EVsJemZCWQp9opG5VyFItmJiq/9YbK/Hjd3xVYSS16pTBghXPo217Hizhi+Z+tRWfz/xBjjzHZcUqnm+nDF6DrNm7ZUJaCu1TX91U3+TnttfmhiGm6L8zZhW+3XhIJmQE1aqXxIcf90JUVCheH9MeDz7C84V90X+++AVf/vNnOfIMl30Kp76yHplf/iZHpKWQHvUQ83lnlmohiKn5745bg43r+arCSCpXice0T/sgOiZMHQcE+OHtCZ1w7/3V1TH5li8+/VG5e90iR9pzySdxyqivkfHpVjkiLYV0roPYf3WByZ+lWhBRqu+NX4u1q/bJhIygXPlYtVTN5nCZXBcQ6I933uuCZi2qyIR8ySfTv8WCedvkSFvF/jROHbsJGdN+lSPSUnD7Woj9qhtMAf4yofyIVxVT3l2PVcv3yISMoHSZaMz4rC9KloyUyR8FBwfg3and0KBReZmQL5kx9RssXaj9k9RiFWvaxO+R/v5PckRaCnmsBsxzusOkXJWTc6JUp03eiOVLdsmEjCC+RASmK3eqZcpGy+TWQkODMGVGL9StV1Ym5CvE0tap723AyuXazv257WJNm/IT0sZ/L0ekpeCHqyJ2Xg+YggJkQs58/OG3WLRghxyREcSawzHj076oUNEsE+fCw4Mx9eNeqF6zlEzIV4hynTx+Hdat0m61ym0Va/pHm5E2xjvWC/maoFZVYV7YB6aQQJmQM59+9B3mz+H7fyOJig7F9Jm9UblqvEwKR8wWnvaJ8s9VKdo/R/on5ldMeHsNNm7QZtJikYs14/NtSB35tRyRloLurQTzot4whbJUC+OfM3/A7H9vliMygoiIYHyg3HlWq3F7d57qne5nfVC+QqxMyFeIch33xip8v8n9OwIWqVgz/v0bUoauu35vTZoKbFkR5qV94RceJBNyRqxj+/cXv8gRGUFomHhX2hN16hbvXWl8iUh1wpOY+ES+RaxhHztqBX764ahM3KPQxZo5ZzdSBq1mqXpAULPyiFumlKpytU4FE3epYh0bGYeY3fveB91Rv2EFmRTPjdnEYgIU+RZ1f/DXlmLzL8dl4nqFKtbMBfuQ/MJK5V6apaq1wEZlYV7eD35RITIhZ+bP3qq+VyXjCAzyx8Qp3XBXs8oycQ3xOFhMgBKPh8m35FrzMOqVJdi+9aRMXKvAYs1afhDJ/7dMuYf2zpPajS60XwP4xXBT8cJYuXQXPp7GSXVG4u/vh7ff7YyW99whE9cSE6A+/KQXIiN54eprxHGRI4ctxq7fzsjEdZwWa/bq35H85BLAxlL1lNTXNiBzIXcKKowGjSvy7sNA/PxMGDO+A1o9WFMm7lGjZml8OLO3uiSHfEtWVi5eeXkh9u4+JxPXyLdYszccRdKAxeqB5eRB4hi+Z5YhayU3jC9Ipcpx+OizvjDHsVz1TpTqyDGPo3WbujJxLzEh6v3pPRDKGfc+JyvTilcGL8DBAxdkUny3LNac704gqe9COJRbZfICNqVcn1yiXuyQc1XuKKHuxhMTe30zdtIfcerhsJFt0L5jA5loo2Hjinh3ancEceMVn5OenoOhLybg8O+XZFI8fynWnJ9OwdIzAQ7lFpm8h7jIERc7Od+fkAnl545qJTFDnHTCA691afDwR9C1e2M50pbYsH/C+10QyK1CfU5aWrZarsePXZHJ7ftDsVo3n4Gl2zw4MqwyIW8iLnYsPRKQ8/NpmVB+xAYC4sQTsUsP6cdzA1uhV99mcuQZ99xXHWPHd1QnTpFvSU7OxODn5+PUiWsyuT3//c6x7jgPSxelVNNZqt5MXPRYus+DdbtrX7YbUc1apdUt7CI441MX/vbMPXjy6bvlyLMeal0bo95qr77rJd+SZMnAoOfn4expi0yKTi1W6+6LsHScA3tqthqSd3Ok5sDSaS5y91yUCeWnVp0y+FCUKzfX8Gp9BjTDsy8+IEfeoW27ehgx6jH1nS/5lsRr6cqd6zxcOJ8sk6LxsydnXS9V5c+kH+L361r72cg9WPz3AUYnjgv74GMup/BWXXs0xktDHpYj79KxayMMHvaIHJEvuXw5FS89OxcXL6TIpPD8xOYDEa/cI4ekJ47ETCS2mwXb0eK9D/AF9eqXw9SPeqr7zZL3aNexPoaPbKPcFXrvbWGvfs3Ud7/key5dTFHvXK9cSZNJ4fi/pQhqXkH5KxOsP5ySMemFeOeavfowQjrU4g5NBShVOhr1G5THd98cgo2bnnjcI21q4423OyhX994/Sahh4wrqBu67d56VCfmKtNRs/PrzcTz4cC2EFfLCXC1W8RfB91aGw5oH66+u396J3Eu8c81efQShnWrDL5oTdZwpUzZavXv97pvfWa4e9MCDNfH2xM4ICNDPzNsmTSsjMzMH+/eelwn5ipTkLHXT/gcfqYXQ0ILL9b/FKgQ/WBX2pCzkbuc3jt44UrKRs+4oQrrUgV8k3yU6U7ZcDGrXLauWq7gLIW21vPcOTHi/qy7Xiop1rkmWTPx+kBMHfU1yUia2bT6Jh1rXQkiI8x26/nK5GPX+Ywj7m2cWZ1Px2I4nIrH9bORdzZAJ5Ud8QL47tRt32dGYuOubMFmfpSqId8HinXC7jnfKhHzJsaNXMGRggrqZhDN/KVbxjRP9cXuE9uI3jh7ZDl2BpcNs9ckDOdfi7jswUdw5BXGXHS2Is1QnT+te4NW+t1P3MX6zHR55tI5MyJccPnQJw15agIz0HJn81S1fcJj8/BDzz84I6VhbJqQnuXsvIZHrkgvl7vuqYfx7+r2D0ovadctg6oyehXo/pQdiV6Yx73TAfQ9Ulwn5kgP7zmP44ARkZt56Q6V8Zw6YAvwR+1U3BLd2zzmI5F65v52Hpes82Lk9ZYHEh+O4SfqaSKMn1WuWUjfpCDfYJh0BysXYO+91QfOWVWVCvmTv7vMYMWQRsm+xr77TTxJTcADMCb0RfL9rT+4nbYgZ3knd5/FAhUIQs1THTezM/WFd7PpB4r0RFWXMpWDiHf27U7qhUZMKMiFfsnPHaYwcvlg9NP1mBX6KmEIDEbuoD4KalZcJ6UnOD6dg6Z3AIwALodUjtfDWRG6+7ioVKprVU4bMBj98PkT5jJw8rSfq3llOJuRLtm05iVGvLIHV+r/P2EJ9gojlG+bl/RDYsIxMSE9yNh6HhYfWF8rDrevgjXHcfL24ypSNwozP+iK+RKRMjE1sl/nBx73Ugx/I94g1rm+OXA6b/Iwt9KW52NXHvLI/AmqXlAnpSc7q35H09DI4uG6zQG3a1sNodUcgluvtKFEyEtM/7YdSpaNk4hsiI0PUx97isH3yPT99fwSLFu5Q/7pIz7z848MRt3oAAqqaZUJ6kr14P1KeXwGHneVaEHGyycgxj7Nci8gcF67eqZavECsT3xITG4bpM3urj8HJt4hJbN173qX+dZFfJvmXiYR57RPwrxgjE9KTzLl7kDJ4DRwOh0woP+07NuCxYUUQHR2KaTP7oFLlOJn4JvH4W1xciMfh5BvqNyyHiVP+t2zvtmZpBCilGrdmAPxL+8b7E6PJ/PI3pI7YIEfkjDg2bPjrLNeCiMPkxWPQatX5qkgQj8FnfNZPfSxOxlajZim8P73XH9Zo31axCgF3xMG8egBMcWEyIT3J+GQLUt/8Ro7Ima7dG2PoiEdZrvkQR/FNmd5TPVSe/qdc+VhM/7QPYg0+K9qXVaxkVietiffrN7vtYhUC65RE3Mr+PFFFp9Kn/oy0Cd/LETnTvdddeGmYdx7G7Ulie8L3p/VA/YZcjncrlavEq+9cxWNyMpZSpSPVVx/muAiZ/E+xilUIalRWXYpj4okquiSKNU0pWCpYn37NMfDlh+SIxB7L4iCDxndVkgndSrUapTBVuavhIfvGISbpTf+0L0qXiZbJHxW7WAVxULp5UR+YwvS9ubavSnvzG6TP3CpH5Ey/J1vg+ZdayZHvEts/jp/E7fwK6+KFFORkcwc0IxDzCaZ+1AsVK+U/Sc8lxSqIbQ9j5/dUt0Ek/Ul9dT0y/v2bHJEzTzx1N5554X458j1iZ6qxEzrhvlY1ZELObP75GMa9sRJ2O2fi65149TFleo8CNwJxWbEKIa2rI+arbsrlrEv/taQFhwMpg1Yjc+5uGZAzT/3fvXj6uXvlyHeIdb2jxjyOh1vz5KvC2PXbGYx6dSlyueuZ7omlNGJJjTj+sCAub8DQjrUR83ln5d/MKZS6o1xRJz+/EllLDsiAnHn6ufvx5NN3y5HxiVnRYl1v2w71ZULOHDpwUT395M8btJP+iAvKMeM7qmc4F4Zbbi3DetdXD0vn+gQdyrMj+amlyF79uwzImecGtkL/J1vIkbENHvaIuq6XCnb82BUMeykBGRn5H4ZN+iBqbOQbbYv0lMZtz2zD/9YEUZPbyBHpidisP2nAYmRvPCoTcubFlx9Cn/7N5ciYXhz0IHr1ayZH5MzZMxYMeTEBKSlZMiE9E8vs2nduKEeF49aXoREDWyDynUfkiPREHDOX1Hshcr4/IRNyZpDyw9ezb1M5Mpannr0X/f/eUo7ImSuXU5VSnYfEa+kyIT37+zP3qMvsisrts4wih9+LiJG+O4NSz8QB6ZaeCcj59YxMyJmXhz+Cbj2byJEx9H2iOZ55nj+/hWGxZODlF+bj4oVUmZCe9eh1F/7vxQfkqGjcXqxC1JiHED6YV7x65Ei3IqnrPFh3nJcJ5cdkMmHYa4+iczdjvIdUd5sawt2mCiM1NQtDBybg9KlEmZCetW1fD0NGtJajotOkWIWodx9F+NPXj9QhfbGnZsPSaQ5y91yUCeVHlOurox5Dxy5FeyfjbcSvf2gxPlh8SWamFa8MXoCjhy/LhPTsgQdr4vUx7dWf5dulWbGKX2TU9McR2reBTEhP7ElZSOyolOuhKzKh/Ijv9RGj26Jdxztloi/ioHfx6y/OB4uvEEtpXh++BPv3XpAJ6dldzSrj7Xc7qTuLFYdmxSqY/PwQ81lHhHbh4nI9sl/NgKXdbNiO8XFXQcS6N3HV+1i7ejLRhwcfqYXRb7fnAe+FYLPZMeb15di+9aRMSM/q3lkOkz7ojqCg4u8eqGmxCqYAf8T8uxtCHuN2aHqUdykNiY/Pgu10kkwoP6KcRr/VHq0fqysT73b3fdXw1oTiX637ArE94fixK/HT90dkQnp2R7WSmDKjJ8JcdFCCR36CTMoVQey8Hgh+oLJMSE/yzqUgse0s5J3n7MeCiH113xzXAQ+3riUT79SsRRVMmNxV3baNnHM4HJjy7np8ve6gTEjPyleIxTQXH+3nsUtTU0ggYhf3RdDdFWVCepJ3KgnX2inlqtzBknPiDnDshM5o9bB3lmuDRuXV49+CeYBGocyc8S2WL9klR6RnJUtGqofRx8X/9UzV4vDoMx+/8CCYl/RFYOOyMiE9yTtyDYntZyPvWoZMKD+iXMdN9L4TYerWK4spM3ohNJRnhRbGf774BXO/4hGLRhATE4YPP+mNMmVjZOI6Hn+Z4hcdgrgV/RFYt5RMSE9sB68gscMcddYwORcQ6I/x73XBPfdVk4lniaOvPlA+WMLDg2VCziyavx2fz/xBjkjPxPf8Bx/3QpU7SsjEtbxiloJfXBjMqwfAv0a8TEhPbHsuwtJ5Luxp3HC8IOId5oT3u6LlPYU7JcNdxAeKuFqPjAyRCTmzevluTJuyUY5Iz8Qrj8nTeqBWnTIycT2vKFbBv1QE4lcp5VopViakJ9bt52DpOg/2DKtMKD9iOv/EKd3UCUOeULFyHGZ82gcxsWEyIWc2bTyESePXiSOLSefEK5nxk7ugURP3zu3xmmIV/CtEI26tUq5lo2RCemL95TQsPRLgyM6VCeVHXDWLNXNiQbqWxPuk6TNdP1nDqH796RjGvbFSXV5D+iaWv735Tgfcc191mbiPVxWrEFDFDPOaAfArES4T0hPr9yeQ1HcRHFYe7lyQkJBATP6wBxrfVUkm7lWqVBQ++kdflCrNC9fC2LnjNEaPWIrc3DyZkF6JTcReGfUYWrfRZk251xWrEFizBOJWKeUa67p1RaSd7PVHkPTEEjhs/EAqSEhoIN6f3kNd8uJO5rhwTP+sD8qWc/0MSCM6sP8CRgxdpG5ZSPr3wksPorOGh/R7ZbEKgfVLw7yiP0xRnLGoR9krDyHp6WVw5NllQvkRS13Ekpf6DcvJxLXEsoIZn/VFxUpxMiFnjh+7glcGLUAm5wsYwoCnWmp+nrDXFqsQdFc5mBf3hSmca+z0KHvRfiS/uAoOO8u1IGL6/9QZvdX9Sl1JzPr9cGZvVHXTsgKjOXvGgiEvJiAlhcvHjKBrj8bq3arWvLpYheB7KyE2oRdMIdwVRo+yZu9C6pC16jZw5Fx4xPW1da5aBhCmXJCKf59Yr0oFu3I5VSnVeUi8li4T0jNxStOw19rIkba8vliFkIfvQOzsHjBxH1NdyvhiB1Jf2yBH5Iy4wxT7lha3DENDAzFlek+X3wEblcWSgZdfmI+LF7j/tRGITVhGv9XOY6c06aJYhZB2NRHzRRfAXze/ZLpJxsdbkPrWJjkiZ6KiQtVyrVajpEyKRqyTFXv/NmzMfbgLIzU1S7lTnY/Tp3gcohGIWfbjJ3dVdzrzFF21VGiPeoiZ2UH5VfOsSD1Kn/wT0t7llnCFES0mHH3aVz3Oqiiu7+zUBc1aVJUJOZOZacUrgxfg2BEe4G8EteuWwXsfdvf4gRK6u/0LG9AI0R+0vb4wiXQn7Z3vkD7tFzkiZ8TOSGKJTOWqhdvqUxxRN3Z8R00WwBuBWEozcthi7N97QSakZ+LnZOqMXl6x97Uun6uGP9sMURNbyxHpTeqojUj/jCeEFIbZHI6PPuuLSpWdL5UR75LeeLs9HmpdWybkjM1mx5jXl2PHtlMyIT0T67PFjmLesk2nbl9YRrx8NyJHt5Ij0pvU4euR8Z/f5IicEdsPXl+HapbJH4mHNyPfaIs2j9eTCTkjtid8Z8wq/PT9EZmQnsWXiFDPVC1RMlImnqfrmUCiWMOH3SNHpCsOB1JeWo3M+XtlQM6ID40Z/+iH8hX+ekjF0BGPon3nhnJEzohlX+9PXI+N6w/IhPQsKjpUPaWpXHnvOrxF18UqRI9vjbDnmskR6Ypy55D83HJkLeOHXGGUVMr1I6Vcb96WcODLD6N7r7vkiAryyfRvsWLpLjkiPRPrtKd+1KvIE/y0YFKu4HS/cl/8JyQ/v1LdjID0R6xPjp3bAyHta8mEnLl4IQUvPTsb7To2xFPP3itTKsiX//wZX3z6oxyRnoklZVM/6okmTbU9HaqwDFGsgtiTNunvS5G9eL9MSE/EzlqxC3sj5JFqMiFnxJZ70dE8pKKwFs3fjg/f50HlRiBmv094vyvub1VDJt5H94+CbzApX+zYf3VBcDvv/WJT/hzZNiT1XoCcH0/KhJxhqRbe6uW7MW0KS9UI1Nnvb7Xz6lIVDFOsgnikaJ7TE0EP3SET0hNHZi4s3efDuvmMTIiKZ9PGg5g0fp2YK0cGMOTV1mjT7k458l6GKlbBFBwA88JeCLpXm8OjybUc6VZYus6DdScX7VPx/PrTMYx7Y5W6vIb079mBD+hmop7hilXwCwuCeUlfBDV17+HR5B72lGxYOs5B7r5LMiEqmp07TmP0iKXIzeVh+0bQ94nm+NvT+llaachiFfwig2Fe3g+Bd/LILD2yWzKR2H42cn+/KhOiwjmw/wJGDF2kbllI+texa0MMfPkhOdIHwxar4BcbCvOq/gioxUOe9ch+NQOWdrNgO85TR6hwjh29guGDFiAzwyoT0rOHW9fCq68/BpPO9oY3dLEK/iUjELdqAAKq3Ho7OPJueRfTkNhuNmynk2VCdGtnz1gwdGACUlOyZEJ61vKeOzBmfCd1eY3eGL5YBf9yUTCvHQD/8tEyIT3JO5MMy+OzkHeeh1DTrV2+lIohL85D4rV0mZCeNWhUHhMmd1WPQdQjnyhWIaBSLOLWDIBfqQiZkJ7YTlpwrZ1Srpf5wUl/ZElMx8svzMPFC7zwMoIaNUvh/Wk9ERIaKBP98ZliFQKqxyNu9QCY4rzjaCEqmrwj12BpPxv2xEyZkK9LTc3CkIEJOHPaIhPSM3E8othUPyIyRCb65FPFKgTWLYW4lf3hF63v3zhflXvgMhI7KOWazPdovi4z04pXBi/AsSNXZEJ6VrpMtHr8W6w5XCb65XPFKgQ1Kgvz0r4wRQTJhPQkd/dFWDrPhT0tRybka8RSmpHDFmP/Xm4kYgTmuHD1oPKSpaJkom8+WaxCUMuKMC/sDZOOn+P7Muu2c+r2h3blroV8iy03D2++tgw7tp2SCelZZGQIPvy4Nyrkc5C/HvlssQrBraqqx5WZgvQ588zXWX86BUvPBXBk58qEjE5sT/jO2NX4+cejMiE9C1VubKbM6InqNUvJxBh8uliFkMdqIOY/3YAAn/9S6JL12+NI6rsIDit32TE6ccLl+xPXY+N6HoxvBIHKDc3EKd1wZwPjbT3LNlGEdq6DmM86KV8Nfe3uQddlrz+C5L8vgcPGfWGN7JPp32LF0l1yRHomNn14e0InNG9ZVSbGwmKVwvo2QPRH7QGdbZ1F12UtO4TkZ1fAYbfLhIzky89/xrxZW+WI9Ex8xI4c0w6tHq4lE+Nhsd4k/O9NEPXeo3JEepOVsBfJL65SHxmScSyYtw1ffPajHJHeDR7+CNp18P4zVYuDxfonES+1RORYfZ2kQP+TNWsXUoasZbkaxMrluzFj6jdyRHr39HP3oVffZnJkXCzWW4h87X5EjLhPjkhvMv+5HamjNsoR6dWmjQcxefw65SJJBqRrolBFsfoCFms+ot56GOEDW8gR6U3G9F+R9va3ckR68+tPxzDujVXq8hrSv3Yd62Pw8IflyPhYrE5ETW6DsL81liPSm7T3flT/IH3ZueM0Ro9YitxczvI2glYP1cTINx/X3ZmqxWFy8GWUU2KWafLTy5C1YJ9MSG+i3n0UES/fLUfkzQ7sv6CeVMODyo2hWYsqmDytB4KCAmTiG1ishSDWRyb1X4zslYdkQrqiXClHT3sc4f/XVAbkjY4dvYKXnp3Lg8oNQmz8MG1mb4SG+t6e7HwUXAimAH/EzuqG4NZ3yIR0Rbl2FDOFM77aKQPyNmfPWDDkxfksVYOoVqMk3p/ewydLVWCxFpIpKADmhN4Ivr+KTEhXRLkOXIXMhL0yIG9x6WIKBj8/F5bEDJmQnlWsZMa0T3ojKipUJr6HxVoE4iSc2MW9EdS8gkxIV+wOJD+7HFkrDsqAPM2SmK7eqV6+lCYT0rOSJSPx4Sd9YI6LkIlvYrEWkV9EMMzL+iKwYRmZkK7Y7Eh+cgmy1x2RAXlKamoWhgxMwJnTFpmQnsXEhmHap31Qpmy0THwXi/U2+MWEwryyPwJql5QJ6YnDmoekfguRvem4TEhrmZlWDB+0EMeOXJEJ6VmEcsPx4Se9UblKvEx8G4v1NvnHhyNu9QAE3BEnE9ITR7YNSb0SkPMjD8vWWk6ODSOGLMKBfedlQnoWEhKoLqmpWau0TIjFWgz+ZSJhXjMA/hVjZEJ64sjMhaXHfFi3npUJuZstNw9vvrZM3QSC9C8w0B8TJndBw8YVZUICi7WYApRSjRPlWjpSJqQnjrQcWDrPhXXXBZmQu4jtCd8Zuxo//3hUJqRnfn4mjBnfES3vrSYTuoHF6gLicbB57RPwiw+XCemJPSUblg5zkLv/skzI1cQ+NO9PXI+N6w/IhPRM7E44YtRjeLh1bZnQzVisLhJYqwTiVvVXJzaR/tgtmUhsPxu2I9dkQq708YffYsXSXXJEevfi4IfRsWsjOaI/Y7G6UGCDMjAv7wdTZLBMSE/sV9KR+Pgs2E5w+Ycrffn5z5g/Z6sckd797em70e/J5nJEt8JidbGgZuVhXtQHprBAmZCe5F1IvV6uZ5JlQsUxf+5WfPEZTxgyiu697sKzA1vJEeWHxeoGwfdXRmxCL5iCfetEB6PIU0rVopSrKFm6fSuX78bHH2ySI9K7No/XxZBXW8sROcNidZOQR6ohZlY3IIBfYj0Sj4PFO9e8q9y/9nZs2ngQk8evE1s0kwHc16oGRr/VQZ0JTAXjp74bhXaojZh/dgH8+WXWI/+SETCF85F+UYnlNG+PXqkuryH9u6tZZYx7tzMCeJNQaPxKuVlYrzsR/XH76/PTSTfUx/lL+sAvzDePvbpd2dm5mPTOWthsdpmQntWpWxbvTu2GYL7WKhIWqwbCn2yMqKmPyRF5u6D7RKn2hV84S7WoxPZ2E9/vilBekOhe1TtKYOrHvRAezlUORcVi1UjE880R+c4jckTeSpSqeSlLtTjqN6yAKdN7IjSUj9H1qlz5WEyb2RvR0VyXfztYrBqKHH4vIl5/QI7I27BUXadRk4rqxuziDpb0Jb5EBKZ/2kf5M7dpvV0sVo1Fvfkgwge3lCPyFixV12vStDImfdCd7+d0JCYmDNNn9kHZcjxYpDhYrB4Q9e6jCH/mLjkiT2Opuk+zFlXUd66BQf4yIW8l3qVO/agXqtxRQiZ0u1isHmAymRA17XGE9WsgE/KUoHsrsVTdTJx+MmGyUq6BLFdvJZ4qvPdhd9SuW0YmVBwsVg8x+fkh+rNOCO3C0yE8JegelqpW7r2/OsZN4lpIbyR+T955rwsa31VJJlRc/C73IJO/H2L+0x3Bj9eUCWlFLdVlSqlGcCmBVh54sCbGTugEf26Y4jXETkpvvt1BvfAh1+F3uIeZAv1hntMdQQ9WkQm5W9DdFVmqHiLO7xwzjlvjeYthIx5F67Z15YhchcXqBUwhgTAv7KN+4JN7qaW6vB9L1YPEB/lo5S6J5epZLwx6EF17NpEjciUWq5cQ7/nE+77AxmVlQq7GUvUebdvVw+tjHme5ekj/v7XAgL9z2Z+7sFi9iF9UCOJW9Edg3VIyIVe5/viXpepN2nVsgFdGPcZttDXWqWsj9W6V3IfF6mX84sJgXj0A/jXiZULF9d9SjWSpepvOyof88NfasFw18kib2nhVvZjhF9ydWKxeyL9UBOJFuVaKlQndrsAWFViqXk685xvyCg/Qdrd77quGMeM68vG7BlisXsq/fDTi1irlWjZKJlRUolTFo3WWqvfr0acpBr78kByRqzVsXFFdqxrATTo0wWL1YgFVzEq5PgG/khEyocJSS1VMVGKp6ka/J1vg+ZdayRG5Sq06ZfA+D0TQFIvVywXUiEfcKuWuK5bHNxVWUHNZqlEhMiG9eOKpu/HM8/fLERVX5arx+OCjXgjnpD1NsVh1IPDO0jCv6A9TFH84CiJK1byCpapnTz17L/7+zD1yRLerTNkoTPukN2Jiw2RCWmGx6kTQXeVgXtIXJu5rmy+WqnH834sPcJ1lMcTFR2DazL4oWYpzNDyBxaojwWJ/2wW9YArh+ZZ/xlI1HrHWsk//5nJEhRUVHYoPlTvVChXNMiGtsVh1JvihOxA7u4e6xzBdF9S0/PUdlViqhjNo2MPqjGEqnNCwIEyZ3hPVqpeUCXkCi1WHQtrVRMy/ugA8JeR6qa7sD79olqpRDXnlEe5pWwjiMPlJU7uhXv1yMiFP4SezToV2r4eYzzoqv4O+u9ibpeobxC5Bw197FB27NJQJ/Zk4iu+dSV3QtDlPyfIGLFYdC+vXENHTHhefPDLxHSxV3yLKdcTotmjX8U6Z0A3ix3/UmMdxf6saMiFPY7HqXPgzTRE10be2g2Op+iaxFd/IN9uhzeM8P/RmQ15tjbYd6ssReQMWqwFEvHw3It/wjR1r1GVHLFWfJR55vvF2B3UzeQKeeeF+9OjNyV3ehsVqEJGjWiF8mLEX1aulumoAS9XHiXId804ntHq4lkx8U58BzfDU/90rR+RNWKwGEj2+NcKebyZHxhLYhHeq9D8BAX4YN7ET7vPR94odOjfAS0MeliPyNixWg4me2hahAxrJkTGIUlX3S47hfsn0P+KklvHvdVGPQ/MlD7WurU7k4pmq3svkUMi/JoNw5NmR9NRSZC/aLxP9CmxcFnGrB7BUKV85OTaMHLYYWzefkIlxtbi7Kt77sAcCuUGMV2OxGpTDloekfguRveqwTPSHpUqFlZ2di9eGLsb2rSdlYjz1G5bDtE/6IiSUx795OxargTmUK3lLjwTkfHNMJvqhlqqYqMTj8qiQsrNy8crLC7Fzx2mZGEeNmqXw8ef9EBHJOQZ6wHesBmYKDkBsQk8E3VdZJvrAUnUuMytLuUPLliO6QdzJvT+9Bxo0Ki8TY6hYOQ4ffNKbpaojLFaD8wsLgnlxH3VTBT1gqTonCnXzli3YsnUrcnJyZEo3hIYGYcqMXqhXv6xM9K1U6Uj1TFWzOVwmpAcsVh/gFxmsnv4SWL+0TLxTYMMyiBNLaliqtySKdPPmzcjMyEBaWpparlarVf5duiE8PBgffNQbderqu1zNceGY/mlflC4TLRPSCxarjxBlJdaBBtQqIRPvopaqmKhkDpMJ3UwUqCjSdKVUb0hNTcXWbduQm5srE7pBPDb9cGZv1Kzt3ReT+RG//g8+7oWKleJkQnrCYvUh/iUj1PIKqOJdByCzVJ0Txbllyxa1SP8sOTkZ27Zvh81mkwndEKmUk3iMWq2Gvs4mDQ0NxJTpPVCjpj4vCojF6nP8y0bBvPYJ+FXwjsdLLFXnRGFuU+5KU25RqjdYLBb1f2PLy5MJ3RAdE4YZn/ZF1Tu880nNn4n1qROndEP9hhVkQnrEYvVBAZViEL/mCfiXjpSJZwQ0YKk6I4pyq3I3aklKkkn+EpVy/W3HDuSxXP8iJlYp13/0ReWq8TLxTuL0nrHjO6J5y6oyIb1isfqogGpxMCulZorzTKmJUo1fw1LNjyjIHUpRWhITZVKwK1evYufOnbDb7TKhG8Ss2o8+66suXfFGYnfCkW+0VbcrJP1jsfqwwDolr8/C1XhjezE7OZ53qvkSxfjbb7/hqlKURXXp8mXs2rWL5XoLcfERmKGUa/kKsTLxHoOHP4L2nRvKEekdi9XHBTUqC/PSvjBFBMnEvUSpxq15An4eulP2dqIQxV3n5StXZFJ0Fy5exO7du8FN1f6qZMlIfPSPfihTNkYmnvfUs/eiV19jnkrlq1ishKCWFWFe1AcmN+9BGngnS9UZUYS79+zBxUuXZHL7zl+4gD379rFcb6FU6Sh1e0BvWB/ao9ddeOb5++WIjILFSqrgB6ogdm4PmILcc2qGWqpiNjJL9ZZEAe5RSvX8+fMyKb6zZ87gwIEDLNdbKFM2Wrlz7YtSpaJkor3HO9yJISNayxEZCYuV/ivksRqImdUdCHDttwVL1TlRfPv378fZc+dk4jonT53CwUOH5IhuVq58rPrONb5EhEy088CDNTHyzXY8U9WgWKz0B6EdayPmH52V7wzX/MCLUjWL2b8s1XwdOHgQp06770SWEydO4PfD+j0+0J0qVDKr5Sq2D9RK0+ZV8Pa7nRDg4gtY8h78naW/COtTH9Eftb++BqAYbpSqfzw3EM/PIeVu8uRJ958hevToURxR/qC/qlwlXl2KE6vBRvd17yyHd6d2Q1BQgEzIiFisdEvhf2+CqPfayFHRBdYtpa6TZanm7/CRIzh2/Lgcud9h5a71uIb/f3pS5Y4SmD6zN6Kj3XcARLXqJTH1o54IC9NmBj55DouV8hXxUgtEvvWQHBWeWqprn4B/CZZqfkTBHVGKVWvifasWd8h6VK1GKUz7tA+i3FCuYu3sh5/0RlQUT27yBSxWcipyxP2IGHGfHBWMpVqwEx6eULT/wAGcOXNGjuhmNWuVxocfu/ZQcbF2drpS2GKDCvINLFYqUNRbDyN8YAs5yh9LtWCi0A7s3y9HnrN33z6cc8MsZCOoXbeMencpznUtLrFPsbgL9qYNKcj9WKxUKFGT2yDs703k6K9YqgU7e/asWmje4MZmFBcuXJAJ3axuvbLq+9DQYrwPvX7gei91chT5FhYrFYpYbxf9UTuE9q4vk/9hqRZMbPywZ+9er9qsQfxaxL7Cl1yw05MRiaPbpkxXyvU2diQLDg7A5Gk9UKtOGZmQL2GxUqGZ/PwQ83knhHSuIxMgoE5JlmoBRHF56969duXX9NvOnbhSjL2JjaxRk4pqQYaEFL5c1TNV3++q/rPkm0zKDzv3O6MicVhtSOqVANuZVMStU0q1JCdl5Edspi+Of/P202b8/f3RrGlTxMfzseWtbN18Aq8NXQyr8r3vjHqm6oSOaN2mrkzIF7FY6bY4snJhT7fyTtUJcezbdh0dPh4QEIDmzZrBbDbLhG62+edjGPnKEuRab/37KfZTeXV0W3Tu2kgm5Kv4KJhuizgJh6Wav8TERGzfvl03pSrYbDZs27YNScnJMqGbtby3Gsa/11V91HsrLw5+iKVKKhYrkYslJSVhmyhVHR42nquU69atW5GSkiITutl9D1THuEmd/7LP74CnWqLfkwUvSSPfwGIlciFRSFuVuz5x96dXubm52LJlC1LT0mRCNxMn04yd0An+/tc/Prv2aIwXXnpQ/Wsige9YiVwkNTUVm5VCslqtMtG3kOBgtGzZEhERnJx2KxvXHcCWzccx+q0O6qQlohtYrEQukJ6ejl82b4Y1J0cmxhAaGqqWa3gYj/0jKiw+CiYqpoyMjOt3qgYrVSErK0t9LCz+TESFw2IlKoZMpXA2K3eq2dnZMjGezMxM/KqUq5H/G4lcicVKdJtE0ah3cz5QOJnKXfmWrVuRY8C7ciJXY7ES3QZRMOJOVTwG9hVpaWlquRplchaRu7BYiYpIFIsomHQfKtUbxMxnsZxILMkholtjsRIVwX/XeCoF46uSk5PVHZr0vFaXyJ1YrESFJIpE3K2l+HCp3mARu0uJctXRlo1EWmGxEhWCKJCt27er2xXSdYkWi64OGSDSCouVqACiOMTRb5bERJnQDdeuXsXOnTu9/lg8Ii2xWImcEIXx22+/qUfA0a1dunyZ5Up0ExYrUT5EUYjCEIeVk3MXL13C7t27wR1SiVisRLckCkIUhSgMKpzzFy5g7969LFfyeSxWoj8RxbBnzx61KKhozpw9i30HDrBcyaexWIluIgph3759OHvunEyoqE6fOoWDhw7JEZHvYbES3eTAwYM4feaMHNHtOnHiBH4/fFiOiHwLi5VIOqTcZZ08eVKOqLiOHj2q/kHka1isRIrDR47g2PHjckSuIu5aj/PrSj6GxUo+TxTqEaVYyT3E+1Y+CSBfwmIln3bi1Cn1ETC51/4DB/jumnwGi5V81unTp3Fg/345IncTs63PcbY1+QAWK/mks2K9JUtVU+qmG3v24ALXB5PBsVjJ55w/fx57uEOQR4iv+a5du3CJO1qRgbFYyadcvHgRu7inrUfZla/9bzt34gr3YCaDYrGSzxCb6e9U7pZYqp4nDjjYIU4NunZNJkTGwWIlnyCOfRPHv/FoM+/x33NuLRaZEBkDi5UMLzExEdu3b1c/yMm72Gw2bN22DUnJyTIh0j8WKxlaUlIStolS5Z2q11LLdetWpKSkyIRI31isZFjJyge1uBsSH9zk3XJzc7FlyxakpqXJhEi/WKxkSKmpqepdkPjAJn2wKr9Xm5VyTU9PlwmRPrFYyXDEB/MWpVStVqtMSC+sOTnq711GZqZMiPSHxUqGkpGRod715Cgf0KRPWVlZ6mNh8WciPWKxkmFkKnc5mzdvRnZ2tkxIr8Tv5a9KufL3kvSIxUqG8N+7HH4QG0amePqgXCjx6QPpDYuVdE988IpS5Xs540lXypXvy0lvWKyka+IDV3zwig9gMiZ1hve2bZzhTbrBYiVdE+/ixB9kbGJSGiczkV6wWEnXYmJi0KxpU/j78VvZqAICAtC8WTNERUXJhMi78dOIdC8uLg5NlXL1Y7kajrhgEhdOsbGxMiHyfvwkIkMoUaIE7mrShOVqIP7+/miq3KmKCyciPeGnEBlGqVKl0LhRI5hMJpmQXokLJHGhVCI+XiZE+sFiJUMpU6YMGjVsyHLVMT/l965J48YoWbKkTIj0hcVKhlOuXDk0qF+f5apD4vesoXJhVLp0aZkQ6Q+LlQypQoUKqFe3rhyRHohSFRdE4sKISM9YrGRYlStXRt06deSIvF39O+9UL4iI9I7FSoZWtWpV1K5dW47IW9WtVw8VK1aUIyJ9Y7GS4VW74w7UqF5djsjb1FEufKpWrixHRPrHYiWfULNmTdyhFCx5l5o1avD3hQyHxUo+Q70zqlJFjsjT1CcJSrESGQ2LlXxKnTp1+C7PC1RRLnD47puMisVKPkUs6VBnn5YvLxPSWuVKlThbmwyNxUo+R10v2aABypUtKxPSirigqVevHjfvIENjsZJP+u8OP6VKyYTcTd0RS7mgYamS0bFYyWeJjd6bNGmCUtyT1u3KlC6NhixV8hEsVvJpN8o1nkeTuU1JcepQ48Y80o98Br/Tyeep5342bQozD9N2OfWcXJYq+Rh+txMpAgIC0Lx5c8SyXF3GHBeHu+66S71wIfIlLFYiSS3XZs0QFR0tE7pd4u6/edOmCGCpkg9isRLdJDAwEC2VO9eoyEiZUFFFR0WhmXKBIi5UiHwRi5XoT4KCgtCiRQtERETIhAorSilV8bUTFyhEvorFSnQLwcHBaKHcuYaHhcmEChIRHq5+zcSFCZEvY7ES5SM0NFS9+woNCZEJ5SdMKdWWLVuqFyREvo7FSuREmHLHKgojhOWaL/E1ulu5AOHXiOg6FitRAcLF3ZhSHLwb+ytRpuLxr7i7J6LrWKxEhSAmMvH94R8FKRca4oJDXHgQ0f+wWIkKScx4FZtIcMbr9ZnTolQ5c5ror1isREUQEx2tbiLhy2s0xYWFuHvnWl+iW2OxEhWR2PawWdOm8PfB/W9v7E4Vzd2piPLFYiW6DXFxcerG/b60uby4kBA7KnE/ZSLnWKxEt0k9uaVJE58oV/UEIKVU48xmmRBRflisRMVQSpw12qiRoQ/wFhcO4gKiRHy8TIjIGRYrUTGVKVMGjRo2NGS5+in/TU0aN0bJkiVlQkQFYbESuUC5cuXQoH59Q5Wr+G9ppNyNly5dWiZEVBgsViIXqVChAu6sV0+O9E2UqrhQKFu2rEyIqLBYrEQuVKlSJdStU0eO9Kv+nXeqFwpEVHQsViIXq1q1KmrXri1H+lOvbl1UrFhRjoioqFisRG5Q7Y47UKNGDTnSjzrKBUGVKlXkiIhuB4uVyE1qKsV6h1KweqG3Xy+Rt2KxErmRXu4Aq1evrss7bCJvxGIlcjMxmamSF7+zFO+Ea9WsKUdEVFwsViI3E0tX7hSzbMuXl4n3qFypknpXTUSuw2Il0oC6LrRBA5TzonWhFStUQL169Qy1qQWRN2CxEmlEFFjDhg1RulQpmXiOKPj6BtspishbsFiJNCQ2tG/SpAlKeXDv3TKlS6sFz1Ilcg8WK5HGbpSrOHZOayXFaTyNG/vUObJEWuNPF5EHiPNNxVFsZg0PDY8X58eyVIncjj9hRB4SEBCA5s2bI1aDchUHlDe96y610InIvVisRB6klmuzZoiOipKJ64m74mbK/0cAS5VIEyxWIg8LDAxEixYtEOWGco2JibleqkqBE5E2WKxEXiAoKAgtmjdHRHi4TIpPFLW4GxbFTUTaYbESeYng4GD1zjU8LEwmt08UtChqUdhEpC0WK5EXCQ0NVcs1NCREJkUXppRqy5Yt1aImIu2xWIm8TJhyxyqKMeQ2ylX8s3crxXw7/ywRuQaLlcgLhYu7TqUgi3LX+d+7XeXPROQ5LFYiLxUREVHo96RB4v2s8r91xftZIioeFiuRF1Nn9iqF6Wxmb5Dy98TdrShiIvI8FiuRl4uJjlaXzdxqLep/18BGRsqEiDyNxUqkA2Lbw2ZNm8L/pn1+b2yJGK0ULxF5D5NDIf+aiLzc1atXsX3HDvXIN3EXazab5d8hIm/BYiXSmctXrqgn1JSIj5cJEXkP4P8BE2RYOCxJ7UYAAAAASUVORK5CYII=";

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



