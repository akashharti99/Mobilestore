package com.mobile.store.service.implementation;





import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.mobile.store.dto.Customer;
import com.mobile.store.dto.CustomerOrder;
import com.mobile.store.dto.Item;
import com.mobile.store.dto.MobileCover;
import com.mobile.store.helper.AES;
import com.mobile.store.helper.MyEmailSender;
import com.mobile.store.repository.CustomerOrderRepository;
import com.mobile.store.repository.CustomerRepository;
import com.mobile.store.repository.ItemRepository;
import com.mobile.store.repository.MobileCoverRepository;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;



@Service
public class CustomerServiceImpl implements CustomerService{

	@Autowired
	Customer customer;
	
	@Autowired
    MyEmailSender emailSender;
	
	@Value("${razorpay.key}")
    private String key;
	
	@Value("${razorpay.secret}")
	private String secret;
	
	
	@Autowired
    CustomerRepository customerRepository;
	
	@Autowired
	CustomerOrderRepository customerOrder;
	
	@Autowired
	MobileCoverRepository mobileCoverRepository;
	
	@Autowired
	ItemRepository itemRepository;
	
	@Override
	public String loadRegister(ModelMap map) {
		map.put("customer", customer);
        return "customer-register.html";
	}

	@Override
	public String loadRegister(@Valid Customer customer, BindingResult result, HttpSession session) {
		 if (!customer.getPassword().equals(customer.getConfirmpassword())) {
	            result.rejectValue("confirmpassword", "error.confirmpassword", "* Password Missmatch");
	        }
	        if (customerRepository.existsByEmail(customer.getEmail())) {
	            result.rejectValue("email", "error.email", "* Email should be Unique");
	        }
	        if (customerRepository.existsByMobile(customer.getMobile())) {
	            result.rejectValue("mobile", "error.mobile", "* Mobile Number should be Unique");
	        }

	        if (result.hasErrors()) {
	            return "customer-register.html"; 
	        }else {
	            int otp = new Random().nextInt(100000, 1000000);
	            customer.setOtp(otp);
	            customer.setPassword(AES.encrypt(customer.getPassword(), "123"));
	            customerRepository.save(customer);
	            emailSender.sendOtp(customer);

	            session.setAttribute("success", "Otp Sent Success");
	            session.setAttribute("id", customer.getId());
	            return "redirect:/customer/otp";
	        }	}

	@Override
	public String submitOtp(int id, int otp, HttpSession session) {
		Customer customer=customerRepository.findById(id).orElseThrow();
		if (customer.getOtp() == otp) {
			customer.setVerified(true);
            customerRepository.save(customer);
            session.setAttribute("success", "Account Created Success");
            return "redirect:/";
        } else {
            session.setAttribute("failure", "Invalid OTP");
            session.setAttribute("id", customer.getId());
            return "redirect:/customer/otp";
        }
	}

	@Override
	public String loadHome(HttpSession session) {
		 if (session.getAttribute("customer") != null) {
	            return "customer-home.html"; 
	        }else {
	            session.setAttribute("failure", "Invalid Session, Login Again");
	            return "redirect:/login";
	        }
	}

	@Override
	public String viewMobileCover(HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {
            List<MobileCover> mobileCover = mobileCoverRepository.findAll();
            if (mobileCover.isEmpty()) {
                session.setAttribute("failure", "No Products Found");
                return "redirect:/customer/home";
            } else {
            	Customer customer=(Customer)session.getAttribute("customer");
            	map.put("items", customer.getCart().getItems());
                map.put("mobileCover", mobileCover);
                return "customer-mobilecovers.html";
            }
        } else {
            session.setAttribute("failure", "Invalid Session, Login Again");
            return "redirect:/login";
        }
	}

	@Override
	public String addToCart(HttpSession session, int id) {
		if (session.getAttribute("customer") != null) {
            MobileCover mobileCover = mobileCoverRepository.findById(id).orElseThrow();
            if (mobileCover.getStock() <= 0) {
                session.setAttribute("failure", "Out of Stock");
                return "redirect:/customer/mobilebrands";
            } else {
                mobileCover.setStock(mobileCover.getStock() - 1);
                mobileCoverRepository.save(mobileCover);

                Customer customer = (Customer) session.getAttribute("customer");

                List<Item> items = customer.getCart().getItems();
                if (items.isEmpty()) {
                    Item item = new Item();
                    item.setMobilebrand(mobileCover.getMobilebrand());
                    item.setName(mobileCover.getName());
                    item.setDescription(mobileCover.getDescription());
                    item.setImageLink(mobileCover.getImageLink());
                    item.setPrice(mobileCover.getPrice());
                    item.setQuantity(1);
                    items.add(item);

                    customer.getCart().setItems(items);

                    session.setAttribute("success", "Added to Cart Success");
                } else {
                    boolean flag = true;

                    for (Item item : items) {
                        if (item.getName().equalsIgnoreCase(mobileCover.getName())) {
                            item.setPrice(item.getPrice() + mobileCover.getPrice());
                            item.setQuantity(item.getQuantity() + 1);
                            flag = false;
                        }
                    }

                    if (flag) {
                        Item item = new Item();
                        item.setMobilebrand(mobileCover.getMobilebrand());
                        item.setName(mobileCover.getName());
                        item.setDescription(mobileCover.getDescription());
                        item.setImageLink(mobileCover.getImageLink());
                        item.setPrice(mobileCover.getPrice());
                        item.setQuantity(1);
                        items.add(item);

                    }
                    customer.getCart().setItems(items);

                    session.setAttribute("success", "Added to Cart Success");
                }

                customer.getCart()
                        .setPrice(customer.getCart().getItems().stream().mapToDouble(x -> x.getPrice()).sum());
                customerRepository.save(customer);

                session.setAttribute("customer", customerRepository.findById(customer.getId()).orElseThrow());
                return "redirect:/customer/mobilebrands";
            }
        } else {
            session.setAttribute("failure", "Invalid Session, Login Again");
            return "redirect:/login";
        }
	}

	@Override
	public String removeFromCart(HttpSession session, int id) {
		if (session.getAttribute("customer") != null) {
            MobileCover mobileCover = mobileCoverRepository.findById(id).orElseThrow();
            Customer customer = (Customer) session.getAttribute("customer");
            List<Item> items = customer.getCart().getItems();
            if (items.isEmpty()) {
                session.setAttribute("failure", "No Item in Cart");
            } else {
                Item item2 = null;
                for (Item item : items) {
                    if (item.getName().equals(mobileCover.getName())) {
                        item2 = item;
                        break;
                    }
                }
                if (item2 == null) {
                    session.setAttribute("failure", "No Item in Cart");
                } else {
                    mobileCover.setStock(mobileCover.getStock() + 1);
                    mobileCoverRepository.save(mobileCover);
                    session.setAttribute("success", "Item Removed Success");
                    if (item2.getQuantity() > 1) {
                        item2.setQuantity(item2.getQuantity() - 1);
                        item2.setPrice(item2.getPrice() - mobileCover.getPrice());
                        customer.getCart().setPrice(customer.getCart().getItems().stream().mapToDouble(x -> x.getPrice()).sum());
                        itemRepository.save(item2);
                        customerRepository.save(customer);
                    } else {
                        customer.getCart().getItems().remove(item2);
                        customer.getCart().setPrice(customer.getCart().getItems().stream().mapToDouble(x -> x.getPrice()).sum());
                        customerRepository.save(customer);
                        itemRepository.delete(item2);
                    }
                    
                }
            
            }
            // customer.getCart().setPrice(customer.getCart().getItems().stream().mapToDouble(x -> x.getPrice()).sum());
            
            session.setAttribute("customer", customerRepository.findById(customer.getId()).orElseThrow());
            return "redirect:/customer/mobilebrands";
        } else {
            session.setAttribute("failure", "Invalid Session, Login Again");
            return "redirect:/login";
        }
	}

	@Override
	public String viewCart(HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {
            Customer customer = (Customer) session.getAttribute("customer");
            if (customer.getCart().getItems().isEmpty()) {
                session.setAttribute("failure", "No Item in Cart");
                return "redirect:/customer/home";
            } else {
                map.put("cart", customer.getCart());
                return "customer-cart.html";
            }
        } else {
            session.setAttribute("failure", "Invalid Session, Login Again");
            return "redirect:/login";
        }
	}
	
	
	@Override
    public String checkout(HttpSession session, ModelMap map) {
        if (session.getAttribute("customer") != null) {
            Customer customer = (Customer) session.getAttribute("customer");
            if (customer.getCart().getItems().isEmpty()) {
                session.setAttribute("failure", "No Item in Cart");
                return "redirect:/customer/home";
            } else {
                CustomerOrder order = new CustomerOrder();
                List<Item> newItems = new ArrayList<>();
                for (Item item : customer.getCart().getItems()) {
                    Item newItem = new Item();
                    newItem.setName(item.getName());
                    newItem.setPrice(item.getPrice());
                    newItem.setQuantity(item.getQuantity());
                    newItem.setMobilebrand(item.getMobilebrand());
                    newItem.setDescription(item.getDescription());
                    newItem.setImageLink(item.getImageLink());
                    newItems.add(newItem);
                }

                order.setItems(newItems);
                order.setTotalAmount(customer.getCart().getPrice());
                order.setCustomer(customer);
                order.setOrderDateTime(LocalDateTime.now());
                order.setPaymentId("Pending Payment"); // Placeholder
                
                customerOrder.save(order);

                map.put("totalAmount", customer.getCart().getPrice());
                map.put("customer", customer);
                map.put("orderId", order.getId());
                map.put("cart", customer.getCart());

                session.setAttribute("customer", customerRepository.findById(customer.getId()).orElseThrow());
                return "booking-confirmation-page.html";
            }
        } else {
            session.setAttribute("failure", "Invalid Session, Login Again");
            return "redirect:/login";
        }
    }

    @Override
    public String confirmOrder(HttpSession session, int id) {
        if (session.getAttribute("customer") != null) {
            Customer customer = (Customer) session.getAttribute("customer");
            List<Integer> itemIds = customer.getCart().getItems().stream().mapToInt(Item::getId).boxed().collect(Collectors.toList());
            customer.getCart().getItems().clear();
            customerRepository.save(customer);
            itemRepository.deleteAllById(itemIds);

            CustomerOrder order = customerOrder.findById(id).orElseThrow();
            order.setOrderDateTime(LocalDateTime.now());
            order.setPaymentId("Paid - Cash on Delivery"); // Marking as paid via COD
            customerOrder.save(order);
            
            session.setAttribute("success", "Order Placed Successfully");
            return "redirect:/customer/home";
        } else {
            session.setAttribute("failure", "Invalid Session, Login Again");
            return "redirect:/login";
        }
    }
	
	

	@Override
	public String viewOrders(HttpSession session, ModelMap map) {
		if(session.getAttribute("customer") != null) {
	           Customer customer = (Customer) session.getAttribute("customer");
	           List<CustomerOrder> orders = customerOrder.findByCustomerAndPaymentIdIsNotNull(customer);
	           if(orders.isEmpty()) {
	               session.setAttribute("failure", "No Orders Found");
	               return "redirect:/customer/home";
	           }
	           else{
                orders.sort((o1, o2) -> o2.getOrderDateTime().compareTo(o1.getOrderDateTime()));
	               map.put("orders", orders);
	               return "customer-order-history.html";
	           }
	       }else {
	           session.setAttribute("failure", "Invalid Session, Login Again");
	           return "redirect:/login";
	       }
	    }

	@Override
	public String requestPasswordReset(String email, HttpSession session) {
		Customer customer = customerRepository.findByEmail(email);
		if (!customerRepository.existsByEmail(email)) {
			session.setAttribute("failure", "No account found with the given email");
			return "redirect:/login";
		}

	    int otp = new Random().nextInt(100000, 1000000); // Generate OTP
	    customer.setOtp(otp);
	    customerRepository.save(customer);

	    emailSender.sendOtp(customer); // Send OTP to email
	    session.setAttribute("success", "Password reset OTP sent to your email.");
	    return "reset-password.html";
	}

	@Override
	public String verifyOtpAndResetPassword(int otp, String newPassword, String confirmPassword, HttpSession session) {
		Customer customer = customerRepository.findByOtp(otp)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

	    if (!newPassword.equals(confirmPassword)) {
	        session.setAttribute("failure", "Passwords do not match");
	        return "redirect:/customer/verify-reset-password"; // Redirect back to reset password page
	    }

	    customer.setPassword(AES.encrypt(newPassword, "123")); // Encrypt and update password
	    customer.setOtp(0); // Invalidate OTP
	    customerRepository.save(customer);

	    session.setAttribute("success", "Password reset successfully. You can now log in.");
	    return "redirect:/login";
	}

	@Override
	public String viewMobileBrands(HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {
	        List<MobileCover> mobileCover = mobileCoverRepository.findAll();
	        if (mobileCover.isEmpty()) {
	            session.setAttribute("failure", "No Products Found");
	            return "redirect:/customer/home";
	        } else {
	            List<String> mobileBrands = mobileCover.stream()
	                .map(MobileCover::getMobilebrand)
	                .distinct()
	                .collect(Collectors.toList());

	            map.put("mobileBrands", mobileBrands); // Add to the map

	            return "customer-mobilebrands.html";
	        }
	    } else {
	        session.setAttribute("failure", "Invalid Session, Login Again");
	        return "redirect:/login";
	    }

	}

	@Override
	public String viewMobileCoversByBrand(String mobilebrand, HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {
	        List<MobileCover> filteredMobileCovers = mobileCoverRepository.findByMobilebrand(mobilebrand);
	        if (filteredMobileCovers.isEmpty()) {
	            session.setAttribute("failure", "No products found for the selected brand.");
	            return "redirect:/customer/mobile-cover/mobilebrand";
	        } else {
	            map.put("mobileCovers", filteredMobileCovers);
	            map.put("selectedBrand", mobilebrand);
	            return "customer-mobilecoverss.html";
	        }
	    } else {
	        session.setAttribute("failure", "Invalid Session, Login Again");
	        return "redirect:/login";
	    }
	}

	

	}



