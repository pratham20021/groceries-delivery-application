package com.revcart.auth.controller;

import com.revcart.auth.dto.JwtResponse;
import com.revcart.auth.dto.LoginRequest;
import com.revcart.auth.dto.SignupRequest;
import com.revcart.auth.entity.User;
import com.revcart.auth.exception.ResourceNotFoundException;
import com.revcart.auth.repository.UserRepository;
import com.revcart.auth.security.JwtUtils;
import com.revcart.auth.security.UserPrincipal;
import com.revcart.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            throw new IllegalArgumentException("Email and password are required");
        }
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getEmail(),
                user.getRole()));
    }
    
    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<JwtResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }
        
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setPhone(signUpRequest.getPhone());
        user.setAddress(signUpRequest.getAddress());
        user.setRole(signUpRequest.getRole() != null ? signUpRequest.getRole() : User.Role.CUSTOMER);
        
        User savedUser = userRepository.save(user);
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signUpRequest.getEmail(), signUpRequest.getPassword()));
        
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getEmail(),
                savedUser.getRole()));
    }
    
    @PostMapping("/send-verification-otp")
    public ResponseEntity<Map<String, String>> sendVerificationOTP(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        String otp = userService.sendVerificationOTP(email);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully", "otp", otp));
    }
    
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        if (email == null || otp == null) {
            throw new IllegalArgumentException("Email and OTP are required");
        }
        boolean verified = userService.verifyEmail(email, otp);
        if (verified) {
            return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
        } else {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        String otp = userService.sendPasswordResetOTP(email);
        return ResponseEntity.ok(Map.of("message", "Password reset OTP sent successfully", "otp", otp));
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");
        if (email == null || otp == null || newPassword == null) {
            throw new IllegalArgumentException("Email, OTP, and new password are required");
        }
        boolean reset = userService.resetPassword(email, otp, newPassword);
        if (reset) {
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } else {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }
    }
    
    @GetMapping("/oauth2/google")
    public ResponseEntity<?> getGoogleOAuth2Url() {
        return ResponseEntity.ok(Map.of("url", "http://localhost:8080/api/oauth2/authorization/google"));
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
    
    @PutMapping("/users/{id}/role")
    public ResponseEntity<Map<String, Object>> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String role = request.get("role");
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required");
        }
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        try {
            user.setRole(User.Role.valueOf(role.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User role updated successfully", "user", user));
    }
}