package com.mobile.store.service.implementation;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.mobile.store.dto.MobileCover;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

public interface AdminService {

	String loadHome(HttpSession session);

	String addMobileCover(HttpSession session, ModelMap map);

	String addMobileCover(HttpSession session, @Valid MobileCover mobileCover, BindingResult result,
			MultipartFile image);

	String viewMobileCover(HttpSession session, ModelMap map);

	String deleteMobileCover(HttpSession session, int id);

	String editMobileCover(HttpSession session, int id, ModelMap map);

	String updateMobileCover(HttpSession session, @Valid MobileCover mobileCover, BindingResult result,
			MultipartFile image);

	String viewMobileCoverOrders(HttpSession session, ModelMap map);

}
