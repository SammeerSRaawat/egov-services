package org.egov.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.models.Property;
import org.egov.models.PropertyRequest;
import org.egov.models.TaxCalculation;
import org.egov.models.User;
import org.egov.notification.config.PropertiesManager;
import org.egov.notification.model.EmailMessage;
import org.egov.notification.model.EmailMessageContext;
import org.egov.notification.model.EmailRequest;
import org.egov.notification.model.SmsMessage;
import org.egov.notification.model.TaxCalculationResponse;
import org.egov.notificationConsumer.NotificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
/**
 * 
 * @author Yosadhara
 *
 */
public class NotificationService {

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	NotificationUtil notificationUtil;

	/**
	 * This method is to send email and sms Demand acknowledgement
	 * 
	 * @param propertyrequest
	 */
	public void demandAcknowledgement(PropertyRequest propertyrequest) {

		for (Property property : propertyrequest.getProperties()) {

			Map<Object, Object> propertyMessage = new HashMap<Object, Object>();
			propertyMessage.put("acknowledgementNo", property.getPropertyDetail().getApplicationNo());
			propertyMessage.put("upicNo", property.getUpicNumber());
			propertyMessage.put("assessmentDate", property.getAssessmentDate());
			propertyMessage.put("tenantId", property.getTenantId());

			for (User user : property.getOwners()) {

				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				String emailAddress = user.getEmailId();
				String mobileNumber = user.getMobileNumber();
				String message = notificationUtil.buildSmsMessage(propertiesManager.getDemandAcknowledgementSms(),
						propertyMessage);
				SmsMessage smsMessage = new SmsMessage(message, mobileNumber);
				EmailMessageContext emailMessageContext = new EmailMessageContext();
				emailMessageContext.setBodyTemplateName(propertiesManager.getDemandAcknowledgementEmailBody());
				emailMessageContext.setBodyTemplateValues(propertyMessage);
				emailMessageContext.setSubjectTemplateName(propertiesManager.getDemandAcknowledgementEmailSubject());
				emailMessageContext.setSubjectTemplateValues(propertyMessage);
				EmailRequest emailRequest = notificationUtil.getEmailRequest(emailMessageContext);
				EmailMessage emailMessage = notificationUtil.buildEmailTemplate(emailRequest, emailAddress);
				kafkaTemplate.send(propertiesManager.getSmsNotification(), smsMessage);
				kafkaTemplate.send(propertiesManager.getEmailNotification(), emailMessage);
			}
		}
	}

	/**
	 * This method to send email and sms for Demand approval
	 * 
	 * @param propertyrequest
	 */
	public void demandApprove(PropertyRequest propertyrequest) {

		for (Property property : propertyrequest.getProperties()) {

			Map<Object, Object> propertyMessage = new HashMap<Object, Object>();
			propertyMessage.put("acknowledgementNo", property.getPropertyDetail().getApplicationNo());
			propertyMessage.put("upicNo", property.getUpicNumber());
			propertyMessage.put("assessmentDate", property.getAssessmentDate());
			propertyMessage.put("tenantId", property.getTenantId());
			propertyMessage.put("landOwner", property.getPropertyDetail().getLandOwner());

			for (User user : property.getOwners()) {

				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				String emailAddress = user.getEmailId();
				String mobileNumber = user.getMobileNumber();
				String message = notificationUtil.buildSmsMessage(propertiesManager.getDemandApproveSms(),
						propertyMessage);
				SmsMessage smsMessage = new SmsMessage(message, mobileNumber);
				EmailMessageContext emailMessageContext = new EmailMessageContext();
				emailMessageContext.setBodyTemplateName(propertiesManager.getDemandApproveEmailBody());
				emailMessageContext.setBodyTemplateValues(propertyMessage);
				emailMessageContext.setSubjectTemplateName(propertiesManager.getDemandApproveEmailSubject());
				emailMessageContext.setSubjectTemplateValues(propertyMessage);
				EmailRequest emailRequest = notificationUtil.getEmailRequest(emailMessageContext);
				EmailMessage emailMessage = notificationUtil.buildEmailTemplate(emailRequest, emailAddress);
				kafkaTemplate.send(propertiesManager.getSmsNotification(), smsMessage);
				kafkaTemplate.send(propertiesManager.getEmailNotification(), emailMessage);
			}
		}
	}

	/**
	 * this method is to send email and sms to tansfer fee for demand
	 * 
	 * @param propertyrequest
	 */
	public void demandTransferFee(PropertyRequest propertyrequest) {

		for (Property property : propertyrequest.getProperties()) {

			Map<Object, Object> propertyMessage = new HashMap<Object, Object>();

			propertyMessage.put("acknowledgementNo", property.getPropertyDetail().getApplicationNo());
			propertyMessage.put("upicNo", property.getUpicNumber());
			propertyMessage.put("assessmentDate", property.getAssessmentDate());
			propertyMessage.put("tenantId", property.getTenantId());

			for (User user : property.getOwners()) {

				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				String emailAddress = user.getEmailId();
				String mobileNumber = user.getMobileNumber();
				String message = notificationUtil.buildSmsMessage(propertiesManager.getDemandTransferfeeSms(),
						propertyMessage);
				SmsMessage smsMessage = new SmsMessage(message, mobileNumber);
				EmailMessageContext emailMessageContext = new EmailMessageContext();
				emailMessageContext.setBodyTemplateName(propertiesManager.getDemandTransferfeeEmailBody());
				emailMessageContext.setBodyTemplateValues(propertyMessage);
				emailMessageContext.setSubjectTemplateName(propertiesManager.getDemandTransferfeeEmailSubject());
				emailMessageContext.setSubjectTemplateValues(propertyMessage);
				EmailRequest emailRequest = notificationUtil.getEmailRequest(emailMessageContext);
				EmailMessage emailMessage = notificationUtil.buildEmailTemplate(emailRequest, emailAddress);
				kafkaTemplate.send(propertiesManager.getSmsNotification(), smsMessage);
				kafkaTemplate.send(propertiesManager.getEmailNotification(), emailMessage);
			}
		}
	}

	/**
	 * This method is to send email and sms for Demand rejection
	 * 
	 * @param propertyrequest
	 */
	public void demandReject(PropertyRequest propertyrequest) {

		for (Property property : propertyrequest.getProperties()) {

			Map<Object, Object> propertyMessage = new HashMap<Object, Object>();

			propertyMessage.put("acknowledgementNo", property.getPropertyDetail().getApplicationNo());
			propertyMessage.put("upicNo", property.getUpicNumber());
			propertyMessage.put("assessmentDate", property.getAssessmentDate());
			propertyMessage.put("tenantId", property.getTenantId());

			for (User user : property.getOwners()) {

				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				String emailAddress = user.getEmailId();
				String mobileNumber = user.getMobileNumber();
				String message = notificationUtil.buildSmsMessage(propertiesManager.getDemandRejectSms(),
						propertyMessage);
				SmsMessage smsMessage = new SmsMessage(message, mobileNumber);
				EmailMessageContext emailMessageContext = new EmailMessageContext();
				emailMessageContext.setBodyTemplateName(propertiesManager.getDemandRejectEmailBody());
				emailMessageContext.setBodyTemplateValues(propertyMessage);
				emailMessageContext.setSubjectTemplateName(propertiesManager.getDemandRejectEmailSubject());
				emailMessageContext.setSubjectTemplateValues(propertyMessage);
				EmailRequest emailRequest = notificationUtil.getEmailRequest(emailMessageContext);
				EmailMessage emailMessage = notificationUtil.buildEmailTemplate(emailRequest, emailAddress);
				kafkaTemplate.send(propertiesManager.getSmsNotification(), smsMessage);
				kafkaTemplate.send(propertiesManager.getEmailNotification(), emailMessage);
			}
		}
	}

	/**
	 * This method is to send email and sms for Property acknowledgement
	 * 
	 * @param properties
	 */
	public void propertyAcknowledgement(List<Property> properties) {

		Map<Object, Object> propertyMessage = new HashMap<Object, Object>();
		for (Property property : properties) {

			propertyMessage.put("acknowledgementNo", property.getPropertyDetail().getApplicationNo());
			propertyMessage.put("upicNo", property.getUpicNumber());
			propertyMessage.put("assessmentDate", property.getAssessmentDate());
			propertyMessage.put("tenantId", property.getTenantId());

			for (User user : property.getOwners()) {

				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				String emailAddress = user.getEmailId();
				String mobileNumber = user.getMobileNumber();
				String message = notificationUtil.buildSmsMessage(propertiesManager.getPropertyAcknowledgementSms(),
						propertyMessage);
				SmsMessage smsMessage = new SmsMessage(message, mobileNumber);
				EmailMessageContext emailMessageContext = new EmailMessageContext();
				emailMessageContext.setBodyTemplateName(propertiesManager.getPropertyAcknowledgementEmailBody());
				emailMessageContext.setBodyTemplateValues(propertyMessage);
				emailMessageContext.setSubjectTemplateName(propertiesManager.getPropertyAcknowledgementEmailSubject());
				emailMessageContext.setSubjectTemplateValues(propertyMessage);
				EmailRequest emailRequest = notificationUtil.getEmailRequest(emailMessageContext);
				EmailMessage emailMessage = notificationUtil.buildEmailTemplate(emailRequest, emailAddress);
				kafkaTemplate.send(propertiesManager.getSmsNotification(), smsMessage);
				kafkaTemplate.send(propertiesManager.getEmailNotification(), emailMessage);
			}
		}
	}

	/**
	 * This method is to send email and sms for Property approval
	 * 
	 * @param properties
	 */
	public void propertyApprove(List<Property> properties) {

		Map<Object, Object> propertyMessage = new HashMap<Object, Object>();
		for (Property property : properties) {

			propertyMessage.put("acknowledgementNo", property.getPropertyDetail().getApplicationNo());
			propertyMessage.put("upicNo", property.getUpicNumber());
			propertyMessage.put("assessmentDate", property.getAssessmentDate());
			propertyMessage.put("tenantId", property.getTenantId());
			// total Propertytax calculation logic
			String taxCalculations = property.getPropertyDetail().getTaxCalculations();
			Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
			TaxCalculationResponse taxCalculationResponse = gson.fromJson(taxCalculations,
					TaxCalculationResponse.class);
			Double propertyTax = 0.0;
			for (TaxCalculation taxcalculation : taxCalculationResponse.getTaxCalculationList()) {
				propertyTax = propertyTax + taxcalculation.getPropertyTaxes().getTotalTax();
			}
			propertyMessage.put("propertyTax", propertyTax);
			propertyMessage.put("effectiveDate", property.getAssessmentDate());
			propertyMessage.put("municipalityName", property.getTenantId());
			for (User user : property.getOwners()) {

				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				String emailAddress = user.getEmailId();
				String mobileNumber = user.getMobileNumber();
				String message = notificationUtil.buildSmsMessage(propertiesManager.getPropertyApproveSms(),
						propertyMessage);
				SmsMessage smsMessage = new SmsMessage(message, mobileNumber);
				EmailMessageContext emailMessageContext = new EmailMessageContext();
				emailMessageContext.setBodyTemplateName(propertiesManager.getPropertyApproveEmailBody());
				emailMessageContext.setBodyTemplateValues(propertyMessage);
				emailMessageContext.setSubjectTemplateName(propertiesManager.getPropertyApproveEmailSubject());
				emailMessageContext.setSubjectTemplateValues(propertyMessage);
				EmailRequest emailRequest = notificationUtil.getEmailRequest(emailMessageContext);
				EmailMessage emailMessage = notificationUtil.buildEmailTemplate(emailRequest, emailAddress);
				kafkaTemplate.send(propertiesManager.getSmsNotification(), smsMessage);
				kafkaTemplate.send(propertiesManager.getEmailNotification(), emailMessage);
			}
		}
	}

	/**
	 * This method is to send email and sms for Property rejection
	 * 
	 * @param properties
	 */
	public void propertyReject(List<Property> properties) {

		Map<Object, Object> propertyMessage = new HashMap<Object, Object>();
		for (Property property : properties) {

			propertyMessage.put("acknowledgementNo", property.getPropertyDetail().getApplicationNo());
			propertyMessage.put("upicNo", property.getUpicNumber());
			propertyMessage.put("assessmentDate", property.getAssessmentDate());
			propertyMessage.put("tenantId", property.getTenantId());

			for (User user : property.getOwners()) {

				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				String emailAddress = user.getEmailId();
				String mobileNumber = user.getMobileNumber();
				String message = notificationUtil.buildSmsMessage(propertiesManager.getPropertyRejectSms(),
						propertyMessage);
				SmsMessage smsMessage = new SmsMessage(message, mobileNumber);
				EmailMessageContext emailMessageContext = new EmailMessageContext();
				emailMessageContext.setBodyTemplateName(propertiesManager.getPropertyRejectEmailBody());
				emailMessageContext.setBodyTemplateValues(propertyMessage);
				emailMessageContext.setSubjectTemplateName(propertiesManager.getPropertyRejectEmailSubject());
				emailMessageContext.setSubjectTemplateValues(propertyMessage);
				EmailRequest emailRequest = notificationUtil.getEmailRequest(emailMessageContext);
				EmailMessage emailMessage = notificationUtil.buildEmailTemplate(emailRequest, emailAddress);
				kafkaTemplate.send(propertiesManager.getSmsNotification(), smsMessage);
				kafkaTemplate.send(propertiesManager.getEmailNotification(), emailMessage);
			}
		}
	}

	/**
	 * This method is to send email and sms for property revision petition
	 * acknowledgement
	 * 
	 * @param properties
	 */
	public void revisionPetitionAcknowldgement(List<Property> properties) {

		Map<Object, Object> propertyMessage = new HashMap<Object, Object>();
		for (Property property : properties) {

			propertyMessage.put("acknowledgementNo", property.getPropertyDetail().getApplicationNo());
			propertyMessage.put("upicNo", property.getUpicNumber());
			propertyMessage.put("assessmentDate", property.getAssessmentDate());
			propertyMessage.put("tenantId", property.getTenantId());

			for (User user : property.getOwners()) {

				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				String emailAddress = user.getEmailId();
				String mobileNumber = user.getMobileNumber();
				String message = notificationUtil
						.buildSmsMessage(propertiesManager.getRevisionPetitionAcknowledgementSms(), propertyMessage);
				SmsMessage smsMessage = new SmsMessage(message, mobileNumber);
				EmailMessageContext emailMessageContext = new EmailMessageContext();
				emailMessageContext
						.setBodyTemplateName(propertiesManager.getRevisionPetitionAcknowledgementEmailBody());
				emailMessageContext.setBodyTemplateValues(propertyMessage);
				emailMessageContext
						.setSubjectTemplateName(propertiesManager.getRevisionPetitionAcknowledgementEmailSubject());
				emailMessageContext.setSubjectTemplateValues(propertyMessage);
				EmailRequest emailRequest = notificationUtil.getEmailRequest(emailMessageContext);
				EmailMessage emailMessage = notificationUtil.buildEmailTemplate(emailRequest, emailAddress);
				kafkaTemplate.send(propertiesManager.getSmsNotification(), smsMessage);
				kafkaTemplate.send(propertiesManager.getEmailNotification(), emailMessage);
			}
		}
	}

	/**
	 * This method is for sending email and sms for property revision petition
	 * hearing
	 * 
	 * @param properties
	 */
	public void revisionPetitionHearing(List<Property> properties) {

		Map<Object, Object> propertyMessage = new HashMap<Object, Object>();
		for (Property property : properties) {

			propertyMessage.put("acknowledgementNo", property.getPropertyDetail().getApplicationNo());
			propertyMessage.put("upicNo", property.getUpicNumber());
			propertyMessage.put("assessmentDate", property.getAssessmentDate());
			propertyMessage.put("tenantId", property.getTenantId());

			for (User user : property.getOwners()) {

				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				String emailAddress = user.getEmailId();
				String mobileNumber = user.getMobileNumber();
				String message = notificationUtil.buildSmsMessage(propertiesManager.getRevisionPetitionHearingSms(),
						propertyMessage);
				SmsMessage smsMessage = new SmsMessage(message, mobileNumber);
				EmailMessageContext emailMessageContext = new EmailMessageContext();
				emailMessageContext.setBodyTemplateName(propertiesManager.getRevisionPetitionHearingEmailBody());
				emailMessageContext.setBodyTemplateValues(propertyMessage);
				emailMessageContext.setSubjectTemplateName(propertiesManager.getRevisionPetitionHearingEmailSubject());
				emailMessageContext.setSubjectTemplateValues(propertyMessage);
				EmailRequest emailRequest = notificationUtil.getEmailRequest(emailMessageContext);
				EmailMessage emailMessage = notificationUtil.buildEmailTemplate(emailRequest, emailAddress);
				kafkaTemplate.send(propertiesManager.getSmsNotification(), smsMessage);
				kafkaTemplate.send(propertiesManager.getEmailNotification(), emailMessage);
			}
		}
	}

	/**
	 * This method is for sending email and sms for property revision petition
	 * endorsement
	 * 
	 * @param properties
	 */
	public void revisionPetitionEndorsement(List<Property> properties) {

		Map<Object, Object> propertyMessage = new HashMap<Object, Object>();
		for (Property property : properties) {

			propertyMessage.put("acknowledgementNo", property.getPropertyDetail().getApplicationNo());
			propertyMessage.put("upicNo", property.getUpicNumber());
			propertyMessage.put("assessmentDate", property.getAssessmentDate());
			propertyMessage.put("tenantId", property.getTenantId());

			for (User user : property.getOwners()) {

				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				String emailAddress = user.getEmailId();
				String mobileNumber = user.getMobileNumber();
				String message = notificationUtil.buildSmsMessage(propertiesManager.getRevisionPetitionEndorsementSms(),
						propertyMessage);
				SmsMessage smsMessage = new SmsMessage(message, mobileNumber);
				EmailMessageContext emailMessageContext = new EmailMessageContext();
				emailMessageContext.setBodyTemplateName(propertiesManager.getRevisionPetitionEndorsementEmailBody());
				emailMessageContext.setBodyTemplateValues(propertyMessage);
				emailMessageContext
						.setSubjectTemplateName(propertiesManager.getRevisionPetitionEndorsementEmailSubject());
				emailMessageContext.setSubjectTemplateValues(propertyMessage);
				EmailRequest emailRequest = notificationUtil.getEmailRequest(emailMessageContext);
				EmailMessage emailMessage = notificationUtil.buildEmailTemplate(emailRequest, emailAddress);
				kafkaTemplate.send(propertiesManager.getSmsNotification(), smsMessage);
				kafkaTemplate.send(propertiesManager.getEmailNotification(), emailMessage);
			}
		}
	}

	/**
	 * This method is for sending email and sms for property alteration
	 * acknowledgement
	 * 
	 * @param properties
	 */
	public void alterationAcknowledgement(List<Property> properties) {

		Map<Object, Object> propertyMessage = new HashMap<Object, Object>();
		for (Property property : properties) {

			propertyMessage.put("acknowledgementNo", property.getPropertyDetail().getApplicationNo());
			propertyMessage.put("upicNo", property.getUpicNumber());
			propertyMessage.put("assessmentDate", property.getAssessmentDate());
			propertyMessage.put("tenantId", property.getTenantId());

			for (User user : property.getOwners()) {

				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				String emailAddress = user.getEmailId();
				String mobileNumber = user.getMobileNumber();
				String message = notificationUtil.buildSmsMessage(propertiesManager.getAlterationAcknowledgementSms(),
						propertyMessage);
				SmsMessage smsMessage = new SmsMessage(message, mobileNumber);
				EmailMessageContext emailMessageContext = new EmailMessageContext();
				emailMessageContext.setBodyTemplateName(propertiesManager.getAlterationAcknowledgementEmailBody());
				emailMessageContext.setBodyTemplateValues(propertyMessage);
				emailMessageContext
						.setSubjectTemplateName(propertiesManager.getAlterationAcknowledgementEmailSubject());
				emailMessageContext.setSubjectTemplateValues(propertyMessage);
				EmailRequest emailRequest = notificationUtil.getEmailRequest(emailMessageContext);
				EmailMessage emailMessage = notificationUtil.buildEmailTemplate(emailRequest, emailAddress);
				kafkaTemplate.send(propertiesManager.getSmsNotification(), smsMessage);
				kafkaTemplate.send(propertiesManager.getEmailNotification(), emailMessage);
			}
		}
	}

	/**
	 * This method is for sending email and sms for approve property alteration
	 * 
	 * @param properties
	 */
	public void approvePropertyAlteration(List<Property> properties) {

		Map<Object, Object> propertyMessage = new HashMap<Object, Object>();
		for (Property property : properties) {

			propertyMessage.put("acknowledgementNo", property.getPropertyDetail().getApplicationNo());
			propertyMessage.put("upicNo", property.getUpicNumber());
			propertyMessage.put("assessmentDate", property.getAssessmentDate());
			propertyMessage.put("tenantId", property.getTenantId());

			for (User user : property.getOwners()) {

				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				String emailAddress = user.getEmailId();
				String mobileNumber = user.getMobileNumber();
				String message = notificationUtil.buildSmsMessage(propertiesManager.getAlterationApproveSms(),
						propertyMessage);
				SmsMessage smsMessage = new SmsMessage(message, mobileNumber);
				EmailMessageContext emailMessageContext = new EmailMessageContext();
				emailMessageContext.setBodyTemplateName(propertiesManager.getAlterationApproveEmailBody());
				emailMessageContext.setBodyTemplateValues(propertyMessage);
				emailMessageContext.setSubjectTemplateName(propertiesManager.getAlterationApproveEmailSubject());
				emailMessageContext.setSubjectTemplateValues(propertyMessage);
				EmailRequest emailRequest = notificationUtil.getEmailRequest(emailMessageContext);
				EmailMessage emailMessage = notificationUtil.buildEmailTemplate(emailRequest, emailAddress);
				kafkaTemplate.send(propertiesManager.getSmsNotification(), smsMessage);
				kafkaTemplate.send(propertiesManager.getEmailNotification(), emailMessage);
			}
		}
	}

	/**
	 * This method is for sending email and sms for reject property alteration
	 * 
	 * @param properties
	 */
	public void rejectPropertyAlteration(List<Property> properties) {

		Map<Object, Object> propertyMessage = new HashMap<Object, Object>();
		for (Property property : properties) {

			propertyMessage.put("acknowledgementNo", property.getPropertyDetail().getApplicationNo());
			propertyMessage.put("upicNo", property.getUpicNumber());
			propertyMessage.put("assessmentDate", property.getAssessmentDate());
			propertyMessage.put("tenantId", property.getTenantId());

			for (User user : property.getOwners()) {

				propertyMessage.put("name", user.getName());
				propertyMessage.put("tenantId", user.getTenantId());
				String emailAddress = user.getEmailId();
				String mobileNumber = user.getMobileNumber();
				String message = notificationUtil.buildSmsMessage(propertiesManager.getAlterationRejectSms(),
						propertyMessage);
				SmsMessage smsMessage = new SmsMessage(message, mobileNumber);
				EmailMessageContext emailMessageContext = new EmailMessageContext();
				emailMessageContext.setBodyTemplateName(propertiesManager.getAlterationRejectEmailBody());
				emailMessageContext.setBodyTemplateValues(propertyMessage);
				emailMessageContext.setSubjectTemplateName(propertiesManager.getAlterationRejectEmailSubject());
				emailMessageContext.setSubjectTemplateValues(propertyMessage);
				EmailRequest emailRequest = notificationUtil.getEmailRequest(emailMessageContext);
				EmailMessage emailMessage = notificationUtil.buildEmailTemplate(emailRequest, emailAddress);
				kafkaTemplate.send(propertiesManager.getSmsNotification(), smsMessage);
				kafkaTemplate.send(propertiesManager.getEmailNotification(), emailMessage);
			}
		}
	}
}
