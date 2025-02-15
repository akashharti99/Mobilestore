package com.mobile.store.service.implementation;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.mobile.store.dto.CustomerOrder;
import com.mobile.store.dto.MobileCover;
import com.mobile.store.helper.CloudinaryHelper;
import com.mobile.store.helper.MyEmailSender;
import com.mobile.store.repository.CustomerOrderRepository;
import com.mobile.store.repository.MobileCoverRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class AdminServiceImpl implements AdminService{
	
	@Autowired
	MobileCover mobileCover;
	
	@Autowired
	CloudinaryHelper cloudinaryHelper;
	
	@Autowired
	MyEmailSender emailSender;
	
	@Autowired
	MobileCoverRepository mobileCoverRepository;
	
	@Autowired
	CustomerOrderRepository customerOrderRepository;

	@Override
	public String loadHome(HttpSession session) {
		if (session.getAttribute("admin") != null)
			return "admin-home.html";
		else {
			session.setAttribute("failure", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@Override
	public String addMobileCover(HttpSession session, ModelMap map) {
		if(session.getAttribute("admin")!=null) {
			map.put("mobileCover", mobileCover);
			return "add-mobilecover.html";
		}
		else {
			session.setAttribute("failure", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@Override
	public String addMobileCover(HttpSession session, @Valid MobileCover mobileCover, BindingResult result,
			MultipartFile image) {
		if(session.getAttribute("admin")!=null) {
			if(result.hasErrors()) {
				return "add-mobilecover.html";
			}
			else {
				mobileCover.setImageLink(cloudinaryHelper.saveImage(image));
				mobileCoverRepository.save(mobileCover);
				session.setAttribute("success", "Mobile cover Added Success");
				return "redirect:/admin/home";
			}
		}else {
			session.setAttribute("failure", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@Override
	public String viewMobileCover(HttpSession session, ModelMap map) {
		if(session.getAttribute("admin")!=null)
		{
			List<MobileCover> mobileCover=mobileCoverRepository.findAll();
			if(mobileCover.isEmpty()) {
				session.setAttribute("failure", "No Mobile cover Added Yet");
				return "redirect:/admin/home";
			}
			else {
				map.put("mobileCover", mobileCover);
				return "admin-mobilecover.html";
			}
		}
		else {
			session.setAttribute("failure", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@Override
	public String deleteMobileCover(HttpSession session, int id) {
		if(session.getAttribute("admin")!=null) {
			mobileCoverRepository.deleteById(id);
			session.setAttribute("success", "Mobile Cover Deleted Success");
			return "redirect:/admin/manage-mobilecover";
		}
		else {
			session.setAttribute("failure", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@Override
	public String editMobileCover(HttpSession session, int id, ModelMap map) {
		if(session.getAttribute("admin")!=null) {
			MobileCover mobileCover=mobileCoverRepository.findById(id).orElseThrow();
			map.put("mobileCover", mobileCover);
			return "edit-mobilecover.html";
		}else {
			session.setAttribute("failure","Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@Override
	public String updateMobileCover(HttpSession session, @Valid MobileCover mobileCover, BindingResult result,
			MultipartFile image) {
		if(session.getAttribute("admin")!=null) {
			if(result.hasErrors()) {
				return "edit-mobilecover.html";
			}else {
				byte[] picture;
				try {
					picture=new byte[image.getInputStream().available()];
					image.getInputStream().read(picture);
					
					if(picture.length>0)
						mobileCover.setImageLink(cloudinaryHelper.saveImage(image));
					else
						mobileCover.setImageLink(mobileCoverRepository.findById(mobileCover.getId()).orElseThrow().getImageLink());
				}catch (IOException e) {
					e.printStackTrace();
				}
				mobileCoverRepository.save(mobileCover);
				session.setAttribute("success", "Mobile cover Updated Success");
				return "redirect:/admin/manage-mobilecover";
				
			}
		}else {
			session.setAttribute("failure", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@Override
	public String viewMobileCoverOrders(HttpSession session, ModelMap map) {
		if(session.getAttribute("admin")!=null)
		{
			List<CustomerOrder> mobileCoverorders=customerOrderRepository.findAll();
			if(mobileCoverorders.isEmpty()) {
				session.setAttribute("failure", "No Mobile cover Added Yet");
				return "redirect:/admin/home";
			}
			else {
				mobileCoverorders.sort((o1, o2) -> o2.getOrderDateTime().compareTo(o1.getOrderDateTime()));
				map.put("mobileCoverorders", mobileCoverorders);
				return "admin-mobilecoverorders.html";
			}
		}
		else {
			session.setAttribute("failure", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

}
