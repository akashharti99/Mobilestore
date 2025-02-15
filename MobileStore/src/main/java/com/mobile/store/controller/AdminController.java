package com.mobile.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mobile.store.dto.MobileCover;
import com.mobile.store.service.implementation.AdminService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	AdminService adminService;
	
	@GetMapping("/home")
	public String loadHome(HttpSession session) {
		return adminService.loadHome(session);
	}
	
	@GetMapping("/add-mobilecover")
	public String addMobileCover(HttpSession session,ModelMap map) {
		return adminService.addMobileCover(session,map);
	}
	
	@PostMapping("/add-mobilecover")
	public String addMobileCover(HttpSession session,@Valid MobileCover mobileCover,BindingResult result,@RequestParam MultipartFile image) {
		return adminService.addMobileCover(session, mobileCover,result,image);
	}
	
	@GetMapping("/manage-mobilecover")
	public String viewMobileCover(HttpSession session,ModelMap map) {
		return adminService.viewMobileCover(session,map);
	}
	
	@GetMapping("/delete-mobilecover/{id}")
	public String deleteMobileCover(HttpSession session,@PathVariable int id) {
		return adminService.deleteMobileCover(session,id);
	}
	
	@GetMapping("/edit-mobilecover/{id}")
	public String editMobileCover(HttpSession session,@PathVariable int id,ModelMap map) {
		return adminService.editMobileCover(session,id,map);
	}
	
	@PostMapping("/edit-mobilecover")
	public String updateMobileCover(HttpSession session,@Valid MobileCover mobileCover,BindingResult result,@RequestParam MultipartFile image) {
		return adminService.updateMobileCover(session,mobileCover,result,image);
	}
	
	@GetMapping("/viewmobilecoverorders")
	public String viewMobileCoverOrders(HttpSession session,ModelMap map) {
		return adminService.viewMobileCoverOrders(session,map);
	}
	
}
