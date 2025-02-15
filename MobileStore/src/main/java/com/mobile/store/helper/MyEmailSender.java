package com.mobile.store.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.mobile.store.dto.Customer;

import jakarta.mail.internet.MimeMessage;

@Service
public class MyEmailSender {
	
	@Autowired
	JavaMailSender mailSender;

	@Autowired
	TemplateEngine templateEngine;

	public void sendOtp(Customer customer) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom("hartiakash05@gmail.com", "The Cover House Mobile Store Site");
			helper.setTo(customer.getEmail());
			helper.setSubject("Otp for Account Creation");

			Context context = new Context();
			context.setVariable("customer", customer);

			String text = templateEngine.process("customer-email.html", context);
			helper.setText(text, true);
			
			mailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("**************" + customer.getOtp() + "***********************");
	}

}
