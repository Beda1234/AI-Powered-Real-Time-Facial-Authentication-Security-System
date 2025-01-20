package com.spring.emailtemplate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.spring.emailtemplate.entity.EmailTemplate;

@Repository
public interface EmailRepository extends JpaRepository<EmailTemplate, Long>{

}
