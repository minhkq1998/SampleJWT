package com.example.test.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.model.User;
import com.example.test.payload.request.DeleteRequest;
import com.example.test.payload.request.EditRequest;
import com.example.test.payload.request.LoginRequest;
import com.example.test.payload.request.SignupRequest;
import com.example.test.payload.response.JwtResponse;
import com.example.test.payload.response.MessageResponse;
import com.example.test.repository.UserRepository;
import com.example.test.security.jwt.JwtUtils;
import com.example.test.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail()));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsById(signUpRequest.getId())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Id is already taken!"));
		}
		
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getId(),
							 signUpRequest.getUsername(), 
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()));

		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	@PostMapping("/edit")
	public ResponseEntity<?> editUserInfo(@Valid @RequestBody EditRequest editRequest, @RequestHeader String token) {
		if(jwtUtils.validateJwtToken(token)) {
			if (!userRepository.existsById(editRequest.getId())) {
				return ResponseEntity
						.badRequest()
						.body(new MessageResponse("Error: User is not found!"));
			}
			Optional<User> userResponse = userRepository.findById(editRequest.getId());
			User user = userResponse.get();
			// Update user's account
			if(editRequest.getNewusername() != null && !editRequest.getNewusername().equals("")) {
				if (userRepository.existsByUsername(editRequest.getNewusername())) {
					return ResponseEntity
							.badRequest()
							.body(new MessageResponse("Error: Username is already taken!"));
				}
				user.setUsername(editRequest.getNewusername());	
			}
			if(editRequest.getEmail() != null && !editRequest.getEmail().equals("")) {
				if (userRepository.existsByEmail(editRequest.getEmail())) {
					return ResponseEntity
							.badRequest()
							.body(new MessageResponse("Error: Email is already in use!"));
				}
				user.setEmail(editRequest.getEmail());
			}
			
			
			userRepository.deleteById(editRequest.getId());
			userRepository.save(user);
			
			return ResponseEntity.ok(new MessageResponse("User updated successfully!"));

		}
		return ResponseEntity
				.badRequest()
				.body(new MessageResponse("Error: User is not found!"));	
	}
	
	@PostMapping("/delete")
	public ResponseEntity<?> deleteUser(@Valid @RequestBody DeleteRequest deleteRequest) {
		if (!userRepository.existsById(deleteRequest.getId())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: User is not found!"));
		}

		// delete user
		userRepository.deleteById(deleteRequest.getId());;

		return ResponseEntity.ok(new MessageResponse("User has deleted!"));
	}
}
