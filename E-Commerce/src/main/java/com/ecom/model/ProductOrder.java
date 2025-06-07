package com.ecom.model;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ProductOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String OrderId;
	
	private LocalDate orderDate;
	
	@ManyToOne
	private Product product;
	
	private Double price;
	
	private Integer quantity;
	
	@ManyToOne
	private UserDetails user;
	
	private String status;
	
	private String paymentType;
	
	@OneToOne(cascade = CascadeType.ALL)
	private OrderAddress orderAddress;

	public void setOrderGroupId(String orderGroupId) {
		// TODO Auto-generated method stub
		
	}

	public String getOrderGroupId() {
		// TODO Auto-generated method stub
		return null;
	}

}
