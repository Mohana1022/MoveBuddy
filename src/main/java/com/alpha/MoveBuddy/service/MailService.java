package com.alpha.MoveBuddy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
 
	 @Autowired
	    private JavaMailSender javamailsender;
	    
	 
	 public void sendMail(String tomail, String subject, String message) {
		 
		 SimpleMailMessage mail = new SimpleMailMessage();
		 
		 mail.setTo(tomail);
		 mail.setFrom("vasishtareddy11@gmail.com");
		 mail.setText(message);
		 mail.setSubject(subject);
		 
		 javamailsender.send(mail);
	 }


}
