package com.mobile.store.service.implementation;

import java.util.List;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.mobile.store.dto.Customer;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

public interface CustomerService {

	String loadRegister(ModelMap map);

	String loadRegister(@Valid Customer customer, BindingResult result, HttpSession session);

	String submitOtp(int id, int otp, HttpSession session);

	String loadHome(HttpSession session);

	String viewMobileCover(HttpSession session, ModelMap map);

	String addToCart(HttpSession session, int id);

	String removeFromCart(HttpSession session, int id);

	String viewCart(HttpSession session, ModelMap map);

	String checkout(HttpSession session, ModelMap map);

	String confirmOrder(HttpSession session, int id);

	String viewOrders(HttpSession session, ModelMap map);

	String requestPasswordReset(String email, HttpSession session);

	String verifyOtpAndResetPassword(int otp, String newPassword, String confirmPassword, HttpSession session);

	String viewMobileBrands(HttpSession session, ModelMap map);

	String viewMobileCoversByBrand(String mobilebrand, HttpSession session, ModelMap map);


	

	

//	String addToCartItem(HttpSession session, int id);
//
//	String removeFromCartItem(HttpSession session, int id);

}
