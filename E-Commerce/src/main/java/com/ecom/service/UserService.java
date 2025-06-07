package com.ecom.service;

import com.ecom.model.UserDetails;

public interface UserService {

	
	public UserDetails saveUser(UserDetails user);
	
	public UserDetails getUserByEmail(String email);

	public void updateUserResetToken(String email, String resetToken);

	public UserDetails getUserByToken(String token);
	
	public UserDetails updateUser(UserDetails user);
	
	public UserDetails updateUserprofile(UserDetails user);
	
	public Boolean existsEmail(String email);
}
	