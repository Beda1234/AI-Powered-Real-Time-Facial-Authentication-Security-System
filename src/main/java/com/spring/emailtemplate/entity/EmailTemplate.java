package com.spring.emailtemplate.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Email_Template_Table")
public class EmailTemplate {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ElementCollection
	@CollectionTable(name = "email_recipients", joinColumns = @JoinColumn(name = "email_template_id"))
	@Column(name = "recipient_email")
	private List<String> recipients;
	private String subject;
	@Lob
	private String body;
	private String status;
	private Date sentDate;
	private String fileName;
	
	private boolean isRead;
	private Date readDate;
}
