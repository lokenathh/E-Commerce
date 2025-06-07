package com.ecom.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.UserDetails;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails saveUser(UserDetails user) {
		// Check if email already exists
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new RuntimeException("Email already exists");
		}

		user.setRole("ROLE_USER");
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		UserDetails saveUser = userRepository.save(user);
		return saveUser;
	}

	@Override
	public UserDetails getUserByEmail(String email) {

		return userRepository.findByEmail(email);
	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		UserDetails findByEmail = userRepository.findByEmail(email);
		findByEmail.setResetToken(resetToken);
		userRepository.save(findByEmail);
	}

	@Override
	public UserDetails getUserByToken(String token) {
		return userRepository.findByResetToken(token);

	}

	@Override
	public UserDetails updateUser(UserDetails user) {
		return userRepository.save(user);

	}

	@Override
	public UserDetails updateUserprofile(UserDetails user) {

		UserDetails dbUser = userRepository.findById(user.getId()).get();

		if (!ObjectUtils.isEmpty(dbUser)) {
			dbUser.setName(user.getName());
			dbUser.setMobileNumber(user.getMobileNumber());
			dbUser.setAddress(user.getAddress());
			dbUser.setCity(user.getCity());
			dbUser.setState(user.getState());
			dbUser.setPincode(user.getPincode());
			return userRepository.save(dbUser);
		}

		return dbUser;
	}

	@Override
	public Boolean existsEmail(String email) {
		
		return userRepository.existsByEmail(email);
	}

}
