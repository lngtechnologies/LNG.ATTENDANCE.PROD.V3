package com.lng.attendancecompanyservice.serviceImpl.custOnboarding;


import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;
import com.lng.attendancecompanyservice.entity.masters.Branch;
import com.lng.attendancecompanyservice.entity.masters.Login;
import com.lng.attendancecompanyservice.repositories.custOnboarding.CustomerRepository;
import com.lng.attendancecompanyservice.repositories.masters.BranchRepository;
import com.lng.attendancecompanyservice.repositories.masters.LoginRepository;
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
		try {

			Customer customer1 = customerRepository.findCustomerByCustEmail(customerDto.getCustEmail());
			Customer customer2 = customerRepository.findCustomerByCustMobile(customerDto.getCustMobile());
			Customer customer3 = new Customer();

			if(customer1 == null && customer2 == null) {

				if(customer3.getCustCode() != customerDto.getCustCode()) {

					Customer customer = saveCustomerData(customerDto);

					Branch branch = setCustomerDetailsToBranch(customer);

					if(branch != null) {
						int custId = saveBranch(branch);

						Login login = setCustomerToLogin(customer);

						if(login != null) {
							int loginId = saveLogin(login);
						}
					}			
				} else {
					statusDto.setCode(400);
					statusDto.setError(true);
					statusDto.setMessage("Customer Code Already Exist");
				}
				//send mail
				customerDto.setCustCode(customerDto.getCustCode());
				sendMailWithoutAttachments(customerDto);
				statusDto.setCode(200);
				statusDto.setError(false);
				statusDto.setMessage("Successfully Saved");

			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Customer Mobile Number or Email Already Exist");
			}

		} catch (Exception e) {

		}
		return statusDto;
	}

	//Send email without Attachments
	public void sendMailWithoutAttachments(CustomerDto customerDto) {

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
				"                                                                                        <img src=\"http://40.112.180.100/welcomekit/images/lng_logo.png\" class=\"swu-logo\" style=\"outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; max-width: 100%; clear: both; display: block; border: none; width: 230px; height: auto; padding: 15px 0px 0px 0px;\" width=\"230\">\r\n" + 
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
				"                                                                                            <img src=\"http://40.112.180.100/welcomekit/images/welcome_img.png\" valign=\"bottom\" align=\"center\" class=\"text-center\" style=\"outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; width: auto; max-width: 100%; clear: both; display: block; margin: 0 auto; Margin: 0 auto; float: none; text-align: center;\">\r\n" + 
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
				"																														<p style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; font-size: 13px; line-height: 21px; margin-bottom: 5px; Margin-bottom: 5px; text-align: justify; color: #777777;\">: <a href=\"https://www.lngattendancesystem/SignIn\" style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; text-align: left; line-height: 1.3; color: #f7931d; text-decoration: none;\" target = \"_blank\">https://www.lngattendancesystem/SignIn </a> </p>\r\n" + 
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
				"																														<p style=\"font-family: Helvetica, Arial, sans-serif; font-weight: normal; padding: 0; margin: 0; Margin: 0; font-size: 13px; line-height: 21px; margin-bottom: 5px; Margin-bottom: 5px; text-align: justify; color: #777777;\">: "+ customerDto.getCustName() +" </p>\r\n" + 
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
				"                                                                                                                  <center style=\"color:#ffffff;font-family:sans-serif;font-size:16px;font-weight:bold;\">Click le Button</center>\r\n" + 
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
				"																											<img src=\"http://40.112.180.100/welcomekit/images/social-media-icon.png\" alt=\"\" style=\"outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; clear: both; width: 124px; max-width: 600px; height: auto; display: block; padding-top: 6px;\" width=\"124\">																											\r\n" + 
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
			ex.printStackTrace();
		}
	}

	// Save to customer table
	private Customer saveCustomerData(CustomerDto customerDto) {
		CustomerResponse customerResponse = new CustomerResponse();
		Customer customer = modelMapper.map(customerDto, Customer.class);


		String custCode = customerRepository.generateCustCode();

		customer.setCustIsActive(true);
		customer.setCustCreatedDate(new Date());
		customer.setCustCode(customerDto.getCustCode() + custCode);
		customer.setCustLogoFile(base64ToByte(customerDto.getCustLogoFile()));			
		try {
			
				customer = customerRepository.save(customer);
				customerResponse.data = customerDto;
				
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
		branchRepository.save(branch);
		return branch.getCustomer().getCustId();
	}

	//Set Customer to Login 
	private Login setCustomerToLogin(Customer customer){
		Login login = new Login();
		login.setRefCustId(customer.getCustId());
		login.setLoginName(customer.getCustName());
		login.setLoginMobile(customer.getCustMobile());
		login.setLoginIsActive(true);
		login.setLoginCreatedDate(new Date());
		return login;
	}

	//save to Login Table
	private int saveLogin(Login login){
		//CustomerDto customerDto = new CustomerDto();
		String randomPassword = loginRepository.generatePassword();
		login.setLoginPassword(Encoder.getEncoder().encode(randomPassword));
		loginRepository.save(login);
		String mobileNo = login.getLoginMobile();
		String mobileSmS = "Welcome to Attendance System, Your Login Password is : "+ randomPassword;	
		String s = messageUtil.sms(mobileNo, mobileSmS);
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
	public CustomerResponse getCustomerByCustomerId(int custId) {
		CustomerResponse customerResponse = new CustomerResponse();
		try {
			Customer cust = customerRepository.findCustomerByCustId(custId);
			if(cust != null) {
				CustomerDto custDto = convertToCustomerDto(cust);
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
			if(customer != null) {
				customer.setCountry(customer.getCountry());
				customer.setState(customer.getState());
				customer.setIndustryType(customer.getIndustryType());
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
				//customer.setCustLogoFile(base64ToByte(customerDto.getCustLogoFile()));
				customer.setCustGSTIN(customerDto.getCustGSTIN());
				customerRepository.save(customer);
				customerResponse.status = new Status(false, 2000, "Successfully Updated");
			}	else {
				customerResponse.status = new Status(false, 4000, "Cannot Update");
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
		try {
			Customer customer = customerRepository.findCustomerByCustId(custId);
			customer.setCustIsActive(false);
			try {
				customerRepository.save(customer);
				customerResponse.status = new Status(false, 200, "Successfully Deleted");
			} catch (Exception e) {
				e.printStackTrace(); 
				customerResponse.status = new Status(true, 5000, "Something went wrong");
			}
		} catch (Exception e) {
			e.printStackTrace();
			customerResponse.status = new Status(true, 4000, "Customer Not Found");
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


}
