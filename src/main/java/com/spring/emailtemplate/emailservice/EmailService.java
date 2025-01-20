package com.spring.emailtemplate.emailservice;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.spring.emailtemplate.constants.Constants;
import com.spring.emailtemplate.entity.EmailTemplate;
import com.spring.emailtemplate.repository.EmailRepository;

import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private EmailRepository emailrepository;

	@Value("${spring.mail.username}")
	private String sender;

	@Autowired
	private JavaMailSender javaMailSender;

	public EmailTemplate sendEmail(EmailTemplate emailDetails) {
		try {
			List<String> recipient = emailDetails.getRecipients();
			
			String userHome = System.getProperty("user.home");
			String filePath = userHome + Constants.FILE_PATH + emailDetails.getFileName();
			MimeBodyPart attachmentPart = new MimeBodyPart();
			File file = new File(filePath);
			
			if (file.exists()) {
				attachmentPart.attachFile(file);
			} else {
				throw new RuntimeException("File not found at: " + filePath);
			}

			for (String recipients : recipient) {
				MimeMessage message = javaMailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message, true);

				helper.setFrom(sender);
				helper.setTo(recipients);
				helper.setSubject(emailDetails.getSubject());
				helper.setText(emailDetails.getBody(), true);

				helper.addAttachment(file.getName(), file);

				javaMailSender.send(message);

				emailDetails.setStatus("SENT");
				emailDetails.setSentDate(new Date());
				emailrepository.save(emailDetails);
			}
		} catch (Exception e) {
			emailDetails.setStatus("FAILED");
			emailDetails.setSentDate(new Date());
			e.printStackTrace();
		}
		return emailrepository.save(emailDetails);
	}
	
	 public void trackEmail(long emailId) {
	        Optional<EmailTemplate> emailOptional = emailrepository.findById(emailId);

	        if (emailOptional.isPresent()) {
	            EmailTemplate email = emailOptional.get();
	            email.setRead(true);
	            email.setReadDate(new Date());
	            emailrepository.save(email);
	        } else {
	            throw new RuntimeException("Email not found for ID: " + emailId);
	        }
	    }
	
	
	public List<EmailTemplate> sentEmailInfo(EmailTemplate emailInfo) {
		return emailrepository.findAll();
	}
}