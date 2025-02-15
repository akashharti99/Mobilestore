package com.mobile.store.service.implementation;


import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.mobile.store.dto.Customer;
import com.mobile.store.dto.MobileCover;
import com.mobile.store.helper.AES;
import com.mobile.store.helper.MyEmailSender;
import com.mobile.store.repository.CustomerRepository;
import com.mobile.store.repository.MobileCoverRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class MainServiceImpl implements MainService{
	
	@Value("${admin.email}")
    private String adminEmail;

    @Autowired
    MyEmailSender emailSender;

    @Value("${admin.password}")
    private String adminPassword;
    
    @Autowired
    CustomerRepository customerRepository;
    
    @Autowired
    MobileCoverRepository mobileCoverRepository;
	
    @Autowired
    MobileCover mobileCover;

	@Override
	public String loadHome(ModelMap map) {
		List<MobileCover> mobilecover=mobileCoverRepository.findAll();
		if (!mobilecover.isEmpty()) {
            map.put("mobilecover", mobilecover);
        }
		return "home.html";
	}

	@Override
	public String loadLogin() {
		return "login.html";
	}

	@Override
	public String login(String email, String password, HttpSession session) {
	    if (email.equals(adminEmail) && password.equals(adminPassword)) {
	        session.setAttribute("admin", "admin");
	        session.setAttribute("success", "Login Success");
	        return "redirect:/admin/home";
	    } else {
	        Customer customer = customerRepository.findByEmail(email);
	        if (customer == null) {
	            session.setAttribute("failure", "Customer not found");
	            return "redirect:/login";
	        }
	        if (customer.getPassword() == null) {
	            session.setAttribute("failure", "Password is not set for this account");
	            return "redirect:/login";
	        }
	        String decryptedPassword;
	        try {
	            decryptedPassword = AES.decrypt(customer.getPassword(), "123");
	        } catch (Exception e) {
	            session.setAttribute("failure", "Error decrypting password");
	            return "redirect:/login";
	        }
	        if (decryptedPassword.equals(password)) {
	            if (customer.isVerified()) {
	                session.setAttribute("customer", customer);
	                session.setAttribute("success", "Login Success");
	                return "redirect:/customer/home";
	            } else {
	                customer.setOtp(new Random().nextInt(100000, 1000000));
	                customerRepository.save(customer);
	                emailSender.sendOtp(customer);
	                session.setAttribute("success", "OTP Sent Successfully");
	                session.setAttribute("id", customer.getId());
	                return "redirect:/customer/otp";
	            }
	        } else {
	            session.setAttribute("failure", "Invalid Password");
	            return "redirect:/login";
	        }
	    }
	}

	

	@Override
	public String logout(HttpSession session) {
		 session.removeAttribute("admin");
			session.removeAttribute("customer");
			session.setAttribute("success", "Logged out Successfully");
			return "redirect:/"; 
	}

	

}
