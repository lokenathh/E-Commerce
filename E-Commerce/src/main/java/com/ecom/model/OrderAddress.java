package com.ecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

@Data
@Entity
public class OrderAddress {

	
	@jakarta.persistence.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer Id;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String mobileNo;
	
	private String address;
	
	private String city;
	
	private String state;
	
	private String pincode;
	

}
