package com.revcart.auth.service;

import com.revcart.auth.entity.User;
import com.revcart.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public String sendVerificationOTP(String email) {
        String otp = generateOTP();
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setVerificationOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        
        sendEmail(email, "Email Verification OTP", "Your OTP is: " + otp);
        return otp;
    }
    
    public boolean verifyEmail(String email, String otp) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && otp.equals(user.getVerificationOtp()) && 
            user.getOtpExpiry().isAfter(LocalDateTime.now())) {
            user.setEmailVerified(true);
            user.setVerificationOtp(null);
            user.setOtpExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    
    public String sendPasswordResetOTP(String email) {
        String otp = generateOTP();
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setVerificationOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        
        sendEmail(email, "Password Reset OTP", "Your password reset OTP is: " + otp);
        return otp;
    }
    
    public boolean resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && otp.equals(user.getVerificationOtp()) && 
            user.getOtpExpiry().isAfter(LocalDateTime.now())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setVerificationOtp(null);
            user.setOtpExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    
    public User updateProfile(Long userId, User updatedUser) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setName(updatedUser.getName());
        user.setPhone(updatedUser.getPhone());
        user.setAddress(updatedUser.getAddress());
        return userRepository.save(user);
    }
    
    private String generateOTP() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
    
    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            // Log error
        }
    }
}