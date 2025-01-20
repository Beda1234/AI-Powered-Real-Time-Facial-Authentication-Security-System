package com.spring.emailtemplate.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.emailtemplate.emailservice.EmailService;
import com.spring.emailtemplate.entity.EmailTemplate;

@RestController
@RequestMapping("/api/email")
public class EmailController {
	
	@Autowired
	private EmailService emailservice;
	
	@PostMapping("/send")
	public ResponseEntity<EmailTemplate> sendEmail(@RequestBody EmailTemplate emailTemplate) {
		EmailTemplate response = emailservice.sendEmail(emailTemplate);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/emailInfo")
	public ResponseEntity<List<EmailTemplate>> emailInfo(EmailTemplate emailTemplate){
		List<EmailTemplate> emailInfo = emailservice.sentEmailInfo(emailTemplate);
		return ResponseEntity.ok(emailInfo);
	}
}
