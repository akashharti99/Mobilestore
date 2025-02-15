package com.mobile.store.service.implementation;



import org.springframework.ui.ModelMap;


import jakarta.servlet.http.HttpSession;

public interface MainService {


	String loadLogin();

	String login(String email, String password, HttpSession session);


	String loadHome(ModelMap map);

	String logout(HttpSession session);




}
