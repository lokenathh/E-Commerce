package com.ecom.util;

public enum OrderStatus {

	IN_PROGRESS(1,"In Progress"),
	ORDER_RECIVED(2,"Order Recived"),
	PRODUCT_PACKED(3,"Product Packed"),
	OUT_FOR_DELIVERY(4, "Out For Delivery"),
	DELIVERED(5,"Delivered"),
	CANCEL(6,"Cancelled");
	
	private Integer Id;
	
	private String name;

	private OrderStatus(Integer id, String name) {
		Id = id;
		this.name = name;
	}

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
