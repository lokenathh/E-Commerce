package com.ecom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecom.repository.UserRepository;

@Service
public class UserDetailsServiceimpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    System.out.println("Attempting to load user: " + username);
	    com.ecom.model.UserDetails user = userRepository.findByEmail(username);

	    if (user == null) {
	        System.out.println("User not found in database");
	        throw new UsernameNotFoundException("USER NOT FOUND");
	    }
	    
	    System.out.println("User found: " + user.getEmail() + " with role: " + user.getRole());
	    return new CustomUser(user);
	}

}
