package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDetails;
import com.ecom.repository.ProductRepository;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private CartService cartService;

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

	@GetMapping("/")
	public String index(Model m) {

		List<Category> allActiveCategory = categoryService.getAllActiveCategory().stream().limit(6).toList();
		List<Product> allActiveProducts = productService.getAllActiveProducts("").stream().limit(12).toList();
		m.addAttribute("category", allActiveCategory);
		m.addAttribute("products", allActiveProducts);
		return "index";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/products")
	public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "16") Integer pageSize,
			@RequestParam(name = "showAll", defaultValue = "false") Boolean showAll) {

		List<Category> categories = categoryService.getAllActiveCategory();
		m.addAttribute("paramValue", category);
		m.addAttribute("categories", categories);

		// If showAll is true or if we're showing All Categories explicitly, show all
		// products
		if (showAll || "all".equalsIgnoreCase(category)) {
			List<Product> products = productService.getAllActiveProducts(category);
			m.addAttribute("products", products);
			m.addAttribute("productsSize", products.size());

			// Set pagination attributes to simulate a single page with all products
			m.addAttribute("pageNo", 0);
			m.addAttribute("pageSize", products.size());
			m.addAttribute("totalElements", (long) products.size());
			m.addAttribute("totalPages", 1);
			m.addAttribute("isFirst", true);
			m.addAttribute("isLast", true);
		} else {
			// Normal pagination flow
			Page<Product> page = productService.getAllActiveProductPagination(pageNo, pageSize, category);
			List<Product> products = page.getContent();
			m.addAttribute("products", products);
			m.addAttribute("productsSize", products.size());

			m.addAttribute("pageNo", page.getNumber());
			m.addAttribute("pageSize", pageSize);
			m.addAttribute("totalElements", page.getTotalElements());
			m.addAttribute("totalPages", page.getTotalPages());
			m.addAttribute("isFirst", page.isFirst());
			m.addAttribute("isLast", page.isLast());
		}

		return "product";
	}

	@GetMapping("/product/{id}")
	public String product(@PathVariable int id, Model m) {
		Product productById = productService.getProductById(id);
		m.addAttribute("product", productById);
		return "view_product";
	}

	/* THAT IS USER PROFILE IMAGE */

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDetails user, HttpSession session) {
		try {
			boolean existsEmail = userService.existsEmail(user.getEmail());

			if (existsEmail) {
				session.setAttribute("errorMsg", "Looks Like You Are Already Registered ");
			} else {
				UserDetails savedUser = userService.saveUser(user);

				if (!ObjectUtils.isEmpty(savedUser)) {
					session.setAttribute("succMsg", "HOORAY! SIGN UP SUCCESSFULLY");
				}
			}
		} catch (RuntimeException e) {
			session.setAttribute("errorMsg", e.getMessage());
		} catch (Exception e) {
			session.setAttribute("errorMsg", "SERVER ERROR");
		}

		return "redirect:/register";
	}

	
	/* THAT IS FORGOT PASSWORD */
	@GetMapping("/forgot-password")
	public String showForgotPassword() {
		return "forgot_password.html";
	}

	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {

		UserDetails userByEmail = userService.getUserByEmail(email);
		if (ObjectUtils.isEmpty(userByEmail)) {
			session.setAttribute("errorMsg", "INVALID EMAIL");
		} else {

			String resetToken = UUID.randomUUID().toString();
			userService.updateUserResetToken(email, resetToken);

			// Generate URL : for Token Generate In Password Reset

			String Url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

			Boolean sendMail = commonUtil.sendMail(Url, email);
			if (sendMail) {
				session.setAttribute("succMsg", "PLEASE CHECK YOUR EMAIL FOR PASSWORD RESET");
			} else {
				session.setAttribute("errorMsg", "OOPS! SERVER ERROR EMAIL NOT SEND");
			}

		}

		return "redirect:/forgot-password";
	}

	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {

		UserDetails userByToken = userService.getUserByToken(token);
		if (userByToken == null) {
			m.addAttribute("msg", "YOUR LINK IS EXPIRED");
			return "message";
		}
		m.addAttribute("token", token);
		return "reset_password";
	}

	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String token, @RequestParam String password,
			@RequestParam String confirmPassword, HttpSession session, Model m) {

		UserDetails userByToken = userService.getUserByToken(token);
		if (userByToken == null) {
			m.addAttribute("errorMsg", "YOUR LINK IS EXPIRED");
			return "message";
		}

		// Check if passwords match
		if (!password.equals(confirmPassword)) {
			m.addAttribute("errorMsg", "PASSWORDS DO NOT MATCH");
			return "reset_password";
		}

		userByToken.setPassword(passwordEncoder.encode(password));
		userByToken.setResetToken(null);
		userService.updateUser(userByToken);
		session.setAttribute("succMsg", "PASSWORD CHANGE SUCCESSFULLY");
		m.addAttribute("msg", "PASSWORD CHANGE SUCCESSFULLY");
		return "message";
	}

	@GetMapping("/addCart")
	public String addToCart(@RequestParam("pid") Integer productId, @RequestParam("uid") Integer userId,
			HttpSession session) {
		try {
			Cart cart = cartService.saveCart(productId, userId);
			if (cart != null) {
				session.setAttribute("succMsg", "Product added to cart successfully!");
			}
		} catch (Exception e) {
			session.setAttribute("errorMsg", "Error adding product to cart: " + e.getMessage());
		}
		return "redirect:/product/" + productId;
	}

	@GetMapping("/search")
	public String searchProduct(@RequestParam String ch, Model m) {
		List<Product> searchProducts = productService.searchProduct(ch);
		m.addAttribute("products", searchProducts);

		List<Category> categories = categoryService.getAllActiveCategory();
		m.addAttribute("categories", categories);

		// Add dummy pagination attributes to avoid Thymeleaf errors
		m.addAttribute("paramValue", "");
		m.addAttribute("pageNo", 0);
		m.addAttribute("pageSize", searchProducts.size());
		m.addAttribute("totalElements", searchProducts.size());
		m.addAttribute("totalPages", 1);
		m.addAttribute("isFirst", true);
		m.addAttribute("isLast", true);

		return "product";
	}
}
