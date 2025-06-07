package com.ecom.service;

import java.util.List;
import com.ecom.model.Cart;

public interface CartService {
	Cart saveCart(Integer productId, Integer userId);

	List<Cart> getCartsByUser(Integer userId);

	public Integer getCountCart(Integer userId);

	void updateQuantity(String sy, Integer cid);
}
