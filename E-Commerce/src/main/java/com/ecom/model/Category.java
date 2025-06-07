package com.ecom.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;

	private String imageName;

	private Boolean isActive;

	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private byte[] image;

	public String getName() {
		return name; // Return the actual field value
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsActive() {
		return isActive; // Return the actual field value instead of null
	}

	public void setName(String name) {
		this.name = name;
	}
}
