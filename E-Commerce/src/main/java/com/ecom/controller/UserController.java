package com.ecom.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDetails;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDetails userDetails = userService.getUserByEmail(email);
			m.addAttribute("user", userDetails);
			Integer countCart = cartService.getCountCart(userDetails.getId());
			m.addAttribute("countCart", countCart);
		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}

	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid);

		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "PRODUCT ADD TO CARD FAILED");
		} else {
			session.setAttribute("succMsg", "PRODUCT ADD TO CARD");
		}

		return "redirect:/product/" + pid;
	}

	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {

		UserDetails user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
		if (carts.size() > 0) {
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/cart";
	}

	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
		cartService.updateQuantity(sy, cid);
		return "redirect:/user/cart";
	}

	private UserDetails getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDetails userDetails = userService.getUserByEmail(email);
		return userDetails;
	}

	@GetMapping("/orders")
	public String orderPage(Principal p, Model m) {
		UserDetails user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
		if (carts.size() > 0) {
			Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 49 + 99;
			m.addAttribute("orderPrice", orderPrice);
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/order";
	}

	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p) {
		/* System.out.println(request); */
		UserDetails user = getLoggedInUserDetails(p);
		orderService.saveOrder(user.getId(), request);
		return "redirect:/user/success";
	}

	@GetMapping("/success")
	public String loadSuccess(Model m, Principal p) {
	    UserDetails user = getLoggedInUserDetails(p);
	    List<ProductOrder> orders = orderService.getOrderByUser(user.getId());
	    
	    if (!orders.isEmpty()) {
	        // Get the latest order
	        String latestOrderId = orders.get(orders.size() - 1).getOrderId();
	        LocalDate latestOrderDate = orders.get(orders.size() - 1).getOrderDate();
	        
	        // Find all orders from the same date (likely from the same checkout session)
	        List<ProductOrder> latestOrders = new ArrayList<>();
	        Double itemsTotal = 0.0;
	        
	        for (ProductOrder order : orders) {
	            if (order.getOrderDate().equals(latestOrderDate)) {
	                latestOrders.add(order);
	                itemsTotal += order.getPrice() * order.getQuantity();
	            }
	        }
	        
	        Double totalPrice = itemsTotal + 49 + 99; // Adding delivery and tax
	        
	        m.addAttribute("o", orders.get(orders.size() - 1)); // For basic info
	        m.addAttribute("orderItems", latestOrders);
	        m.addAttribute("itemsTotal", itemsTotal);
	        m.addAttribute("totalOrderPrice", totalPrice);
	    }
	    
	    return "/user/success";
	}

	@GetMapping("/user-orders")
	public String myOrder(Model m, Principal p) {
		UserDetails loginUser = getLoggedInUserDetails(p);
		List<ProductOrder> orders = orderService.getOrderByUser(loginUser.getId());
		m.addAttribute("orders", orders);
		return "/user/my_orders";
	}

	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		Boolean updateOrder = orderService.updateOrderStatus(id, status);
		if (updateOrder) {
			session.setAttribute("succMsg", "PRODUCT GOT CANCELLED");
		} else {
			session.setAttribute("errorMsg", "OOPS! SERVER ERROR PRODUCT NOT FOUND");

		}

		return "redirect:/user/user-orders";
	}

	@GetMapping("/profile")
	public String profile() {
		return "/user/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDetails user, HttpSession session) {
		UserDetails updatedUser = userService.updateUserprofile(user);

		if (updatedUser != null) {
			session.setAttribute("succMsg", "Profile Updated Successfully");
		} else {
			session.setAttribute("errorMsg", "Failed to update profile");
		}

		return "redirect:/user/profile";
	}

	@PostMapping("change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		UserDetails loggedInUserDetails = getLoggedInUserDetails(p);

		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());
		if (matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserDetails updateUser = userService.updateUser(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg", "OOPS! SERVER ERROR");
			} else {
				session.setAttribute("succMsg", "Password Updated Successfully");
			}
		} else {
			session.setAttribute("errorMsg", "Current Password Incorrect");
		}

		return "redirect:/user/profile";
	}

}
