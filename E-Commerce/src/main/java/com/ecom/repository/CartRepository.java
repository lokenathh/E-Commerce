package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecom.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    // This method should be added if not already present
    Cart findByProductIdAndUserId(Integer productId, Integer userId);
    
    public Integer countByUserId(Integer userId);
    
    public List<Cart> findByUserId(Integer userId);

	void deleteAllByUserId(Integer userid);
}
